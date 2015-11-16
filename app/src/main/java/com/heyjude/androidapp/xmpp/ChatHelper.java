package com.heyjude.androidapp.xmpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Global;

import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.iqlast.LastActivityManager;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration.SecurityMode;
import static org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration.builder;


/**
 * This is main class in order to established connection with com.heyjude.androidapp.xmpp and send/receive message etc...
 * Once the operation is completed then it will call-back to chatservice where handler will manage
 * tose call back and update the chat activity.
 */
public class ChatHelper {

    public static final int Online = 1;
    public static final int Connected = 2;
    public static final int Offline = 3;

    //http://54.251.97.231:9090/login.jsp

    public static String CONFERENCE_NAME = "@conference.54.251.97.231";
    private static final String HOST = "heyjudeapp.com";
    private static final String SERVICE_NAME = HOST;
    private static final int PORT = 5222;


    private static final int PACKET_REPLY_TIMEOUT = 15000;
    private static final int ROSTER_REFRESH_INTERVAL = 2000; //2 secs
    private static final int ROSTER_REFRESH_COUNT = 3;

    private Context mContext;
    private int mStatus = Offline;

    private static ChatHelper mChatHelperInstance;

    private XMPPTCPConnection mConnection;
    private Roster mRoster;
    private DeliveryReceiptManager mDeliveryReceiptManager;

    private HashMap<String, Presence.Type> presenceMap;

    private ChatListener mChatListener;

    private boolean isRefreshingRoster;

    private boolean isLogoutRequested;

    private BasicNameValuePair mLastPresenceChange, mLastXmppMessage;

    private boolean isStartupReceiverCall = true;


    private ArrayList<String> groupList;
    private static SharedPreferences sharedPreferences;
    private static LoginData loginData;


    public static ChatHelper getInstance(Context context) {
        if (mChatHelperInstance == null) {
            mChatHelperInstance = new ChatHelper(context);
        }

        Gson gson = new Gson();
        sharedPreferences = context.getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);

        return mChatHelperInstance;
    }

    public void clear() {
        mChatHelperInstance = null;
    }


    public static ChatHelper getHelper() {
        return mChatHelperInstance;
    }


    public ChatHelper(Context mContext) {
        this.mContext = mContext;
        establishConnection();
    }


    public void setChatListener(ChatListener chatListener) {
        this.mChatListener = chatListener;
    }

    /**
     * Configure connection over com.heyjude.androidapp.xmpp.
     */
    private void configureConnection() {
        mLastPresenceChange = null;
        mLastXmppMessage = null;

        if (mConnection != null) {
            mConnection.disconnect();
            mConnection = null;
        }

        //Add custom extension provider to read Typing status elements: composing and paused.
//        ProviderManager.addExtensionProvider(TypingStatusDataExtension.ELEMENT_NAME, TypingStatusDataExtension.NAMESPACE, new TypingStatusDataProvider());
        ProviderManager.addExtensionProvider(DeliveryReceipt.ELEMENT, DeliveryReceipt.NAMESPACE, new DeliveryReceipt.Provider());
        ProviderManager.addExtensionProvider(DeliveryReceiptRequest.ELEMENT, new DeliveryReceiptRequest().getNamespace(), new DeliveryReceiptRequest.Provider());


        XMPPTCPConnectionConfiguration.Builder mConnectionConfigBuilder = builder();
        mConnectionConfigBuilder.setHost(HOST);
        mConnectionConfigBuilder.setPort(PORT);
        mConnectionConfigBuilder.setServiceName(SERVICE_NAME);
        mConnectionConfigBuilder.setResource(null);

        //SETTING DEBUGGER ENABLES - PANKAJ
        mConnectionConfigBuilder.setDebuggerEnabled(true);

        //Disable DIGEST-MD5 since it is not supported by the server
        SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");

        mConnectionConfigBuilder.setSecurityMode(SecurityMode.disabled);


        mConnection = new XMPPTCPConnection(mConnectionConfigBuilder.build());
        mConnection.setPacketReplyTimeout(PACKET_REPLY_TIMEOUT);
        mConnection.addConnectionListener(mConnectionListener);

        if (presenceMap == null) {
            presenceMap = new HashMap<>();
        } else {
            presenceMap.clear();
        }
    }


    /**
     * It will use to established connection.
     */
    private void establishConnection() {
        configureConnection();
        new EstablishConnectionTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * To disconnect from com.heyjude.androidapp.xmpp.
     */
    public void disconnect() {
        new LogoutTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void requestRoster() {
        if (!isRefreshingRoster) {
            new FetchRosterTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private boolean fetchRosterPresence(Roster mRoster) {
        if (mConnection != null && mConnection.isConnected() && mConnection.isAuthenticated() && mRoster != null) {
            for (RosterEntry entry : mRoster.getEntries()) {
                String userName = getUsernameFromJId(entry.getUser());
                presenceMap.put(userName, mRoster.getPresence(entry.getUser()).getType());
            }
            return true;
        }
        return false;
    }

    /**
     * It is used to send message via com.heyjude.androidapp.xmpp to concern user or in group.
     *
     * @param dispatch_Message
     */
    private void dispatchMessageViaXmpp(Message dispatch_Message) {
        if (mConnection != null && dispatch_Message != null) {
            try {
                /**
                 * Checking if to contains proper com.heyjude.androidapp.xmpp user address with @HOST if not attach it.
                 *
                 */
//                if (!dispatch_Message.getTo().contains("@"))
//                    dispatch_Message.setTo(dispatch_Message.getTo() + "@" + HOST);
                ChatLog.i("dispatchMessageViaXmpp");

                DeliveryReceiptRequest.addTo(dispatch_Message);
                mConnection.sendStanza(dispatch_Message);

                //Sent Callback for successfull
                mChatListener.onMessageSentSuccessfully(dispatch_Message);

                //Update Status delivery status to done
                //   DBHelper.instance(mContext).updateDeliveryStatus(dispatch_Message.getStanzaId().toString(), DBMessages.MSG_DELIVERY_STATUS_DELIVERED);

            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

//    public void dispatchTypingStatus(XMPPTypingState xmppTypingState, String toUserName) {
//        if (mConnection != null && mConnection.isConnected() && mConnection.isAuthenticated()) {
//            Message xmppTypeStateMessage = new Message();
//            xmppTypeStateMessage.setTo(getJIdFromUsername(toUserName));
//            xmppTypeStateMessage.setFrom(mConnection.getUser());
//            xmppTypeStateMessage.setType(Message.Type.chat);
//            xmppTypeStateMessage.addExtension(new TypingStatusExtension(
//                    xmppTypingState.mTypingState == XMPPTypingState.TypingState.COMPOSING));
//            xmppTypeStateMessage.addExtension(new TypingStatusDataExtension(xmppTypingState.mId,
//                    xmppTypingState.mChatType));
//            try {
//                mConnection.sendStanza(xmppTypeStateMessage);
//            } catch (SmackException.NotConnectedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    //    public void sendMessageToList(String message,
//                                  ArrayList<Contact> userList) {
//        if (message != null && userList != null && mConnection != null && mConnection.isConnected() && mConnection.isAuthenticated()) {
//            new SendProjectMessageTask(message, userList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }
//    }
//
//    public void sendMessageToList(com.workhive.pojoclasses.Message message,
//                                  ArrayList<Contact> userList) {
//        if (mConnection != null && mConnection.isConnected() && mConnection.isAuthenticated()) {
//            new SendProjectMessageTask(message, userList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }
//    }
//

    /**
     * It will check com.heyjude.androidapp.xmpp connection and then fire SendMessageTask.
     *
     * @param xmppmessage
     */
    public void sendMessageToContact(Message xmppmessage) {
        if (mConnection != null && mConnection.isConnected() && mConnection.isAuthenticated()) {
            new SendMessageTask(xmppmessage).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public HashMap<String, Presence.Type> getPresenceMap() {
        return presenceMap;
    }

    /**
     * Joining process in group over com.heyjude.androidapp.xmpp.
     */
   /* public void joinGroup() {
        Log.e("Chat Helper", "Join Group Method STarted..");
        groupList = new ArrayList<>();
        if (mConnection.isConnected() && mConnection.isAuthenticated()) {
            if (groupList.size() > 0) {
                // Get the MultiUserChatManager
                MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(mConnection);
                try {
               *//* List<String> rooms = manager.getJoinedRooms("schoolapp" + "_" + user.getId() + "@" + HOST);
                    Log.e("Room Name", ""+rooms.toString());*//*
                    // Create a MultiUserChat using an XMPPConnection for a room
                    MultiUserChat userChat;
                    for (String group : groupList) {
                        userChat = manager.getMultiUserChat(group + CONFERENCE_NAME);
                        DiscussionHistory history = new DiscussionHistory();

                        */

    /**
     * GET THE PUSH NOTIFICATION COUNT AND PASS TO DISCUSSION HISTORY, IT WILL RETURN THAT NUMBERS OF MESSAGES.
     *//*
                        int pushcount= DBHelper.getInstance(mContext).getPushCount(group, "true");
                        history.setMaxStanzas(pushcount);
//                        userChat.join("" + user.getId(), null, history, SmackConfiguration.getDefaultPacketReplyTimeout());
                        userChat.join("" + "user.getId()", null, history, SmackConfiguration.getDefaultPacketReplyTimeout());
//
                        Log.e(">>>>>>>>>>>>>>", " ChatHelper push count=" + pushcount);
                        Log.e(">>>>>>>>>>>>>>", " ChatHelper group Id=" + group);
                    }

                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
    private Long getLastActive(String user) {
        try {
            if (!TextUtils.isEmpty(user) && mConnection != null && mConnection.isConnected() && mConnection.isAuthenticated()) {
                LastActivity mLastActivity = LastActivityManager.getInstanceFor(mConnection)
                        .getLastActivity(user);
                return mLastActivity.lastActivity;
            }
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return -1L;
    }

//    public String getLastActiveForUser(String userName) {
//        String mJId = getJIdFromUsername(userName);
//        if (mJId != null && presenceMap != null && presenceMap.containsKey(userName)) {
//            long mLastActive = getLastActive(mJId);
//            return Utils.getLastActiveFormattedString(mLastActive);
//        } else {
//            return Utils.getLastActiveFormattedString(-1L);
//        }
//    }

    public void registerConnectivityChangeReceiver() {
        mContext.registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void unregisterConnectivityChangeReceiver() {
        try {
            mContext.unregisterReceiver(connectivityChangeReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private boolean isDelayedPacket(Stanza packet) {
        DelayInformation delayInformation = packet.getExtension("delay",
                "urn:com.heyjude.androidapp.xmpp:delay");
        DelayInformation delayInformation1 = packet.getExtension("x",
                "jabber:x:delay");

        return delayInformation != null || delayInformation1 != null;
    }

    public int getConnectionStatus() {
        return mStatus;
    }

    public static String getJIdFromUsername(String username) {
        if (!TextUtils.isEmpty(username)) {
            return new StringBuilder().append(username).append("@").append(HOST).toString();
        }
        return null;
    }

    public static String getUsernameFromJId(String fullUserJid) {
        if (!TextUtils.isEmpty(fullUserJid) && fullUserJid.contains("@")) {
            return fullUserJid.substring(0, fullUserJid.indexOf("@"));
        }
        return null;
    }

    private class EstablishConnectionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                ChatLog.e("EstablishConnectionTask : connect");
                mConnection.connect();
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof SmackException.AlreadyConnectedException) {

                    mStatus = Connected;
                    /**
                     * If its already connnected then it will do login process on com.heyjude.androidapp.xmpp.
                     */
                    new LoginTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
            return null;
        }
    }

    /**
     * Login process over com.heyjude.androidapp.xmpp.
     */

    private class LoginTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            /**
             * UNCOMMENT AND CHECK USER OBJECT IS NULL OR NOT.
             */
//            if (user != null) {
            try {
                if (!mConnection.isAuthenticated()) {
                    /**
                     *  IN PLACE OF user.getId() PASS YOUR USER ID.
                     */
                    mConnection.login(loginData.getData().getId() + "@" + HOST, md5(loginData.getData().getId()));
                } else {
                    ChatLog.i("user not authenticated");
                    mStatus = Online;
                        /*updateStatus();*/

                        /*requestRoster();*/

                    if (mChatListener != null) {
                        mChatListener.authenticated();
                        mChatListener.connectionStatusChanged(mStatus);
                    }

//                        mConnection.addAsyncStanzaListener(mStanzaListener, new StanzaFilter() {
//                            @Override
//                            public boolean accept(Stanza stanza) {
//                                return true;
//                            }
//                        });
                }

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                ChatLog.i("XMPP exception");
                if (e instanceof SmackException.AlreadyLoggedInException) {
                    ChatLog.i("XMPP exception inside already logged in");
                    mStatus = Online;
                        /*updateStatus();*/

                        /*requestRoster();*/
                    if (mChatListener != null) {
                        mChatListener.authenticated();
                        mChatListener.connectionStatusChanged(mStatus);
                    }

//                        mConnection.addAsyncStanzaListener(mStanzaListener, new StanzaFilter() {
//                            @Override
//                            public boolean accept(Stanza stanza) {
//                                return true;
//                            }
//                        });
                }
            }
//            }
            return false;
        }
    }

    /**
     * Logout process over com.heyjude.androidapp.xmpp.
     */
    private class LogoutTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isLogoutRequested = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mConnection != null) {
                mConnection.disconnect();
            }
            return null;
        }
    }

    private class FetchRosterTask extends AsyncTask<Void, Void, Roster> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isRefreshingRoster = true;
        }

        @Override
        protected Roster doInBackground(Void... params) {
            if (mConnection.isConnected() && mConnection.isAuthenticated()) {
                mRoster = Roster.getInstanceFor(mConnection);
                return mRoster;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Roster roster) {
            super.onPostExecute(roster);
            if (roster != null) {
                ChatLog.d("Refreshing rooster");
                roster.addRosterListener(mRosterListener);
                roster.addRosterLoadedListener(mRosterLoadedListener);
            } else {
                isRefreshingRoster = false;
            }
        }
    }


    /**
     * Send Messsage task in order to send message to other users.
     */
    private class SendMessageTask extends AsyncTask<Void, Void, Void> {
        private Message xmppMessage;

        public SendMessageTask(Message xmppmessage) {
            this.xmppMessage = xmppmessage;
        }

        @Override
        protected Void doInBackground(Void... params) {
            dispatchMessageViaXmpp(xmppMessage);
            return null;
        }
    }


    /**
     * Listner which will provide you listed call backs.
     */
    private ConnectionListener mConnectionListener = new ConnectionListener() {
        @Override
        public void connected(XMPPConnection connection) {
            ChatLog.i("mConnectionListener Connected");
            mStatus = Connected;
            if (mChatListener != null) {
                mChatListener.connected();
                mChatListener.connectionStatusChanged(mStatus);
            }

//            Roster roster = Roster.getInstanceFor(connection);
//            roster.addRosterLoadedListener(mRosterLoadedListener);

            mDeliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(connection);
            mDeliveryReceiptManager.autoAddDeliveryReceiptRequests();
            mDeliveryReceiptManager.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
            mDeliveryReceiptManager.addReceiptReceivedListener(mReceiptReceivedListener);


            /*mConnection.addAsyncStanzaListener(mStanzaListener, new StanzaFilter() {
                @Override
                public boolean accept(Stanza stanza) {
                    return true;
                }
            });*/

            mConnection.addSyncStanzaListener(mStanzaListener, new StanzaFilter() {
                @Override
                public boolean accept(Stanza stanza) {
                    return true;
                }
            });

            new LoginTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            /*updateStatus();*/
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            ChatLog.i("mConnectionListener authenticated");
            mStatus = Online;
            if (mChatListener != null) {
                mChatListener.authenticated();
                mChatListener.connectionStatusChanged(mStatus);
            }

        }

        @Override
        public void connectionClosed() {
            ChatLog.i("mConnectionListener connectionClosed");
            mStatus = Offline;
            if (mChatListener != null) {
                mChatListener.disconnected(isLogoutRequested);
                mChatListener.connectionStatusChanged(mStatus);
            }
            isLogoutRequested = false;
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            ChatLog.i("mConnectionListener connectionClosedOnError");
            mStatus = Offline;
            if (mChatListener != null) {
                mChatListener.disconnected(false);
                mChatListener.connectionStatusChanged(mStatus);
            }

            establishConnection();
            /*if (isUserLoggedIn()) {
                establishConnection();
            }*/
        }

        @Override
        public void reconnectionSuccessful() {
            ChatLog.i("mConnectionListener reconnectionSuccessful");
        }

        @Override
        public void reconnectingIn(int seconds) {
            ChatLog.i("mConnectionListener reconnectingIn");
        }

        @Override
        public void reconnectionFailed(Exception e) {
            ChatLog.i("mConnectionListener reconnectionFailed");
        }
    };

    private RosterLoadedListener mRosterLoadedListener = new RosterLoadedListener() {
        @Override
        public void onRosterLoaded(Roster roster) {
            ChatLog.i("onRosterLoaded(): " + roster.getEntries().size());
            mRoster = roster;
//            waitAndFetchRosterPresence(roster);
            roster.addRosterListener(mRosterListener);
        }
    };

    private RosterListener mRosterListener = new RosterListener() {
        @Override
        public void entriesAdded(Collection<String> addresses) {
            if (presenceMap != null) {

            }
            for (String mAddress : addresses) {
                String userName = getUsernameFromJId(mAddress);
                Presence.Type mPresence = mRoster.getPresence(mAddress).getType();
                presenceMap.put(userName, mPresence);
                if (mChatListener != null) {
                    mChatListener.presenceChanged(userName, mPresence);
                }
            }
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {

        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {

        }

        @Override
        public void presenceChanged(Presence presence) {
            if (mRoster != null && !isRefreshingRoster) {
                Presence bestPresence = mRoster.getPresence(presence.getFrom());
                String userName = getUsernameFromJId(bestPresence.getFrom());

                //Added this check as presenceChanged was getting called twice for every roster presence change.
                if (mLastPresenceChange == null || !mLastPresenceChange.getName().equals(userName) || !mLastPresenceChange.getValue().equals(bestPresence.getType().toString())) {
                    mLastPresenceChange = new BasicNameValuePair(userName, bestPresence.getType().toString());
                    presenceMap.put(userName, bestPresence.getType());
                    if (mChatListener != null) {
                        mChatListener.presenceChanged(userName, bestPresence.getType());
                    }
                }
            }
        }
    };

    private ReceiptReceivedListener mReceiptReceivedListener = new ReceiptReceivedListener() {
        @Override
        public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
            ChatLog.i("MessageDelivered from " + fromJid + " to " + toJid + " : " + receiptId);
            mChatListener.onMessageDelievred(receiptId);
        }
    };

    /**
     * Here you will receive the messages, which you send to chat service and chat service will
     * send to activity.
     */
    private StanzaListener mStanzaListener = new StanzaListener() {
        @Override
        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
            ChatLog.i("ProcessPacket" + packet);
            if (packet instanceof Message && packet != null && mConnection != null && !packet.getFrom().equals(mConnection.getUser())) {
                Message message = (Message) packet;
                if (message.getBody() != null) {
                    mChatListener.onMessageReceived(message);
//                    parseMessageAndProcess(message, isDelayedPacket, isSelfCallFromDifferentRes);
                } else {


//                    processUserTypingCalls(message, isDelayedPacket, isSelfCallFromDifferentRes);
                }
            }
        }
    };

//    private void parseMessageAndProcess(Message message) {
//        try {
//            String decodedMessage = Utility.decodeToBase64(message.getBody());
//            if (!TextUtils.isEmpty(decodedMessage)) {
//                ChatLog.i("ChatHelper - Received: " + decodedMessage);
////                JSONObject parentJObj = new JSONObject(decodedMessage);
//                mChatListener.onMessageReceived(message);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private void processUserTypingCalls(Message message, boolean isDelayedPacket, boolean isSelfCallFromDifferentRes) {
//        if (!isSelfCallFromDifferentRes && !isDelayedPacket && mChatListener != null) {
//            TypingStatusDataExtension typingStatusDataExtension = (TypingStatusDataExtension) message
//                    .getExtension(TypingStatusDataExtension.NAMESPACE);
//            if (typingStatusDataExtension != null) {
//                XMPPTypingState.ChatType mChatType = typingStatusDataExtension.getChatType();
//                String mId = typingStatusDataExtension.getId();
//                for (ExtensionElement packetExtension : message.getExtensions()) {
//                    if (packetExtension.getElementName().equalsIgnoreCase("composing")) {
//                        mChatListener.userTypingProcessed(getUsernameFromJId(message.getFrom()), mId, true, mChatType);
//                        break;
//                    } else if (packetExtension.getElementName().equalsIgnoreCase("paused")) {
//                        mChatListener.userTypingProcessed(getUsernameFromJId(message.getFrom()), mId, false, mChatType);
//                        break;
//                    }
//                }
//            }
//        }
//    }

    /**
     * IT WILL CHECK INTERNET CONNECTIVITY AND IF NETWORK STATUS CHANGE IT WILL ESTABLISHED CONNECTION AGAIN!
     */
    private BroadcastReceiver connectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ChatLog.i("Network Status Change called");
            /*if (Util.hasNetworkConnection(context) && SchoolApp.instance().isUserLoggedIn()) {
                establishConnection();
            }*/
        }
    };

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}