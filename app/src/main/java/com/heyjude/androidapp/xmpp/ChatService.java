package com.heyjude.androidapp.xmpp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.heyjude.androidapp.activity.ChatActivity;
import com.heyjude.androidapp.constant.ChatConstants;
import com.heyjude.androidapp.db.DBHelper;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Global;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by bhavesh on 13/7/15.
 */
public class ChatService extends Service {

    public static final String TAG = "ChatServcice";

    // Constants for Various Actions for Notifying activity
    public static String CONFERENCE_NAME = "@conference.54.251.97.231";
    private static final int NOTIFY_NEW_MESSAGE = 1;
    private static final int NOTIFY_MESSAGE_SENT = 2;
    private static final int NOTIFY_VIEWS_TO_UPDATE_UNREAD_COUNT = 3;
    private static final int NOTIFY_MESSAGE_DELIVERED = 4;


    // List of callbacks to whome we need to send the message
    private ArrayList<ChatCallBacks> mCallBacks;

    //Create Instance of binder
    private ChatBinder mBinder = new ChatBinder();

    //Handler
    private ChatHandler mHandler;

    private ChatHelper mChatHelper;
    private static ChatUtils chatUtils;

    //Shared Preferences
    public static SharedPreferences sharedPreferences;
    public static final String PREF_NAME = "HeyJudeLogin";
    private boolean hasLoggedIn;

    /**
     * hasInit is used avoid to initiate com.heyjude.androidapp.xmpp connectivity multiple times.
     */
    private boolean hasInit = false;
    private static DBHelper dbHelper;

    //private UserInfoModel userInfoModel;

    public static final String KEY_CHAT_MESSAGE = "message";
    public static LoginData loginData;


    @Override
    public void onCreate() {
        super.onCreate();

        ChatLog.e("ChatService-OnCreate");
        mCallBacks = new ArrayList<>();
        mHandler = new ChatHandler(this);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        hasLoggedIn = sharedPreferences.getBoolean("hasLoggedIn", false);

        /**
         * Check whether user logged in app or not.
         */
        if (hasLoggedIn) {
            init();
        } else {
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**
         * Check whether user logged in app or not.
         */
        if (hasLoggedIn) {
            if (!hasInit)
                init();
            return START_NOT_STICKY;
        } else {
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

    }

    private void init() {
        hasInit = true;
        mChatHelper = ChatHelper.getInstance(this);
        mChatHelper.registerConnectivityChangeReceiver();
        mChatHelper.setChatListener(mChatListener);
        chatUtils = new ChatUtils(this);
        dbHelper = DBHelper.getInstance(this);

        //Initialize User Info
        //userInfoModel = PreferenceUtility.instance(this).getUserInfo();

        getApplicationContext().registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    private BroadcastReceiver connectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ChatConstants.hasNetworkConnection(context)) {
                //clearTypingStates()
            } else {
                /*if (PreferenceUtility.instance(ChatService.this).isLoggedIn()) {
                syncContacts();
                }*/
            }
        }
    };

    /**
     * Compulsory method to override when extending the service class.
     * Return Binder instance
     *
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * ChatBinder Class that will keep track of Service instance and bind them to relavent component
     */
    public class ChatBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }
    }

    /**
     * adding & removing of adding #mCallBacks listner
     *
     * @param mCallBack
     */
    public void addChatCallBacksListener(ChatCallBacks mCallBack) {
        if (mCallBacks != null) {
            mCallBacks.add(mCallBack);
        }
    }

    public void removeChatCallBaskListener(ChatCallBacks mCallBack) {
        if (mCallBacks != null) {
            mCallBacks.remove(mCallBack);
        }
    }

    /**
     * This listner will fire from chatHelper.java and get the result on appropriate methods
     * and return to handler.
     */
    private ChatListener mChatListener = new ChatListener() {

        @Override
        public void connected() {
            ChatLog.i("chatlistener authenticated ");
            super.connected();
        }

        @Override
        public void authenticated() {
            super.authenticated();
            //mChatHelper.joinGroup();
            ChatLog.i("chatlistener authenticated ");
        }


        @Override
        public void onMessageSentSuccessfully(org.jivesoftware.smack.packet.Message sentMessage) {
            super.onMessageSentSuccessfully(sentMessage);
            ChatLog.i("chatlistener onMessageSent ");
            Message updateMsg = new Message();
            updateMsg.what = NOTIFY_MESSAGE_SENT;
            Bundle bundle = new Bundle();
            bundle.putString(KEY_CHAT_MESSAGE, sentMessage.getBody().toString());
            updateMsg.setData(bundle);
            mHandler.sendMessage(updateMsg);
        }

        @Override
        public void onMessageReceived(org.jivesoftware.smack.packet.Message receivedMessage) {
            super.onMessageReceived(receivedMessage);
            ChatLog.i("chatlistener onMessageReceived ");
            Message updateMsg = new Message();
            updateMsg.what = NOTIFY_NEW_MESSAGE;
            Bundle bundle = new Bundle();
            bundle.putString(KEY_CHAT_MESSAGE, receivedMessage.getBody().toString());
            updateMsg.setData(bundle);

            /**
             * Update Database
             */
            //String jsonBody = Utility.decodeToBase64(receivedMessage.getBody().toString());
            //ChatUserDetails userDetails = (ChatUserDetails) Utility.getObjectFromJsonString(jsonBody, ChatUserDetails.class);
            //DBHelper.instance(ChatService.this).add(userDetails, DBMessages.MSG_DELIVERY_STATUS_DELIVERED, DBMessages.MSG_UNREAD);

            mHandler.sendMessage(updateMsg);
        }

        @Override
        public void onMessageDelievred(String receipt) {
            super.onMessageDelievred(receipt);
            ChatLog.i("chatlistener onMessageDelievred ");
            Message updateMsg = new Message();
            updateMsg.what = NOTIFY_MESSAGE_DELIVERED;
        }

        @Override
        public void disconnected(boolean isLogoutRequested) {
            super.disconnected(isLogoutRequested);
            if (isLogoutRequested) {
                mChatHelper.clear();
            }
        }
    };


    /**
     * for updating the unread/read count status to relavent views
     */
    public void updateViewsOnMessageRead() {
        Message updateMsg = new Message();
        updateMsg.what = NOTIFY_VIEWS_TO_UPDATE_UNREAD_COUNT;
        mHandler.sendMessage(updateMsg);
    }

    /**
     * It will handle all messages.
     */
    private static class ChatHandler extends Handler {

        private final WeakReference<ChatService> mService;

        public ChatHandler(ChatService service) {
            mService = new WeakReference<>(service);
            Gson gson = new Gson();
            sharedPreferences = service.getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);

            loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatService service = mService.get();
            Bundle mBundle = msg.getData();
            switch (msg.what) {
                case NOTIFY_NEW_MESSAGE:

                    if (mBundle != null) {
                        String jsonBody = mBundle.getString(KEY_CHAT_MESSAGE);
                        // jsonBody = ChatUtils.decodeFromBase64String(jsonBody);
                        Log.i(TAG, "handleMessage " + jsonBody.toString());

                        ChatMessage chatMessage = (ChatMessage) ChatUtils.getObjectFromJsonString(jsonBody, ChatMessage.class);
                        Log.i(TAG, "handleMessage " + chatMessage.chatmsg);

                        chatMessage.to = chatMessage.from;
                        chatMessage.from = String.valueOf(loginData.getData().getId());
                        chatMessage.isdeliver = "false";
                        chatMessage.isread = "false";
                        chatMessage.ispush = "false";
                        chatMessage.user_id = String.valueOf(loginData.getData().getId());
                        chatMessage.msg_id = ChatUtils.generateRandomUUID();
                        // chatMessage.receiver_id = Constants.RECEIVER_ID;
                        chatMessage.sender_name = String.valueOf(loginData.getData().getUserName());

                        /*if (chatMessage.flag.isEmpty() && chatMessage.showutcdate.isEmpty()) {
                            for (int i = 0; i < chatMessage.venodr_list.size(); i++) {
                                chatMessage.flag = chatMessage.venodr_list.get(i).flag;
                                chatMessage.showutcdate = chatMessage.venodr_list.get(i).showdate;
                            }
                        }*/

                        //ENTER CHAT ENTRY INTO DATABASE.
                        if (!dbHelper.IsMessageExists(chatMessage.newtimestamp)) {
                            dbHelper.add(chatMessage);
                        } else {
                            dbHelper.update(chatMessage);
                        }

                        //WE ARE NOT STORING USER TYPE IN DATABASE.
                        if (ChatActivity.ISVISIBLE
                                && ChatActivity.taskid.equalsIgnoreCase(chatMessage.taskid)) {

                            ChatActivity._StrReceiverId = chatMessage.to;

                            for (ChatCallBacks serviceUpdateListener : service.mCallBacks) {
                                serviceUpdateListener.onMessageReceived(chatMessage);
                            }
                        } else {
                            /**
                             * Based on the type set the alert message and update the chatmessage object
                             * and then send to generateNotification method.
                             */
                            ChatUtils.generateNotification(service, chatMessage);
                            /*if (!dbHelper.IsMessageExists(chatMessage.newtimestamp)) {
                                //GENERATE NOTIFICATION IN CASE ACTIVITY IS NOT OPENED.
                                Log.e(">>>>Chat Service", "Push Notification");
                                ChatUtils.generateNotification(service, chatMessage);
                            }*/

                        }
                    }
                    break;
                case NOTIFY_MESSAGE_SENT:
                    if (mBundle != null) {
                        String jsonBody = mBundle.getString(KEY_CHAT_MESSAGE);
                        //jsonBody = ChatUtils.decodeFromBase64String(jsonBody);
                        Log.i(TAG, "handleMessage " + jsonBody.toString());
                        ChatMessage userDetails = (ChatMessage) ChatUtils.getObjectFromJsonString(jsonBody, ChatMessage.class);
                        for (ChatCallBacks serviceUpdateListener : service.mCallBacks) {
                            serviceUpdateListener.onMessageSentSuccessfully(userDetails);
                        }
                    }
                    break;

                case NOTIFY_MESSAGE_DELIVERED:
                    break;

                case NOTIFY_VIEWS_TO_UPDATE_UNREAD_COUNT:
                    for (ChatCallBacks serviceUpdateListener : service.mCallBacks) {
                        serviceUpdateListener.updateOnMessageRead();
                    }
                    break;

            }
        }
    }


    @Override
    public void onDestroy() {
        ChatLog.i("onDestroy ChatService");

        //Unregister
        mChatHelper.unregisterConnectivityChangeReceiver();
        super.onDestroy();
    }

    /**
     * It will call from activity in order to send message.
     *
     * @param chatDetail
     */
    public void sendMessage(ChatMessage chatDetail) {
        org.jivesoftware.smack.packet.Message xmppMessage = new org.jivesoftware.smack.packet.Message();
        String jsonBody = ChatUtils.getJsonStringFromObject(chatDetail);
        //jsonBody = ChatUtils.encodeToBase64String(jsonBody);
        xmppMessage.setBody(jsonBody);
        xmppMessage.setTo(chatDetail.to);
        xmppMessage.setFrom(chatDetail.from);
        xmppMessage.setStanzaId(chatDetail.msg_id);
        xmppMessage.setType(org.jivesoftware.smack.packet.Message.Type.chat);
        ChatLog.i("dispatching message " + " TO: " + chatDetail.to + "From :" + chatDetail.from + " jsonBody: " + jsonBody);
        mChatHelper.sendMessageToContact(xmppMessage);
    }

    /**
     * Logout/Disconnect XMPP Service
     */
    public void logout() {
        if (mChatHelper != null) {
            mChatHelper.disconnect();
        }
    }
}