package com.heyjude.androidapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.adapter.ChatAdapter;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.constant.ChatConstants;
import com.heyjude.androidapp.customview.PinnedSectionListView;
import com.heyjude.androidapp.db.DBHelper;
import com.heyjude.androidapp.model.chatHistory.ChatHistory;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Constants;
import com.heyjude.androidapp.utility.Global;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;
import com.heyjude.androidapp.xmpp.ChatCallBacks;
import com.heyjude.androidapp.xmpp.ChatMessage;
import com.heyjude.androidapp.xmpp.ChatService;
import com.heyjude.androidapp.xmpp.ChatUtils;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ChatActivity extends BaseActivity implements View.OnClickListener {

    private static EditText editMessage;
    private static TextView tvError;

    private ImageView ivSend;
    private LinearLayout chatFooter;

    //ziya
    private static ChatAdapter adapter;
    private ArrayList<ChatMessage> messages;
    private static String _StrUserID;

    public static String _StrReceiverId;
    public static String taskid;

    //chat
    private ChatUtils chatUtils;
    private static DBHelper dbHelper;
    private static PinnedSectionListView stickyList;

    // Chat Service instance
    private static ChatService mChatService;
    public static boolean ISVISIBLE;

    private Bundle bundle;
    private SharedPreferences sharedPreferences;
    private static LoginData loginData;

    private String judeid, isFrom, newMessage, TAG = "ChatActivity";
    private boolean isFromHistory = false, isFromFragment = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.bundle = savedInstanceState;
        setContentView(R.layout.activity_chat);

        Gson gson = new Gson();
        sharedPreferences = getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);

        _StrUserID = loginData.getData().getId();

        setToolbar(ChatActivity.this, "Hey Jude");

        bundle = getIntent().getExtras();
        if (bundle != null) {
            isFrom = bundle.getString(Constants.FROM);
        }

        init();

        /**
         * Bind Chat Service
         */
        startAndBindToChatService();

        if (!isFromFragment) {
            if (bundle != null) {
                isFromFragment = bundle.getBoolean(Constants.FROM_FRAGMENT, false);
            }
        }

        if (isFromFragment) {
            if (bundle != null) {
                newMessage = bundle.getString(Constants.CHAT_MESSAGE);
            }

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    sendChatMessages(newMessage, "5", false);
                    isFromFragment = false;
                }
            }, 1000);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    newMessage = Constants.AUTOMATED_CHAT_MESSAGE;
                    sendChatMessages(newMessage, "1", false);
                }
            }, 3000);

        }
    }

    private void init() {

        chatUtils = new ChatUtils(this);
        dbHelper = DBHelper.getInstance(this);
        chatUtils.mapTaskMessages.clear();
        chatUtils.privateTaskMessages.clear();
        messages = new ArrayList<>();

        editMessage = (EditText) findViewById(R.id.editChatMsg);
        ivSend = (ImageView) findViewById(R.id.ivSend);
        tvError = (TextView) findViewById(R.id.empty);
        chatFooter = (LinearLayout) findViewById(R.id.chatFooter);

        if (isFrom != null) {
            if (isFrom.equals(Constants.HISTORY_TASK)) {
                chatFooter.setVisibility(View.GONE);
                isFromHistory = true;
            }
        }

        // HIDE VIRTUAL KEYBOARD
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        stickyList = (PinnedSectionListView) findViewById(R.id.lvChat);
        stickyList.initShadow(false);
        ivSend.setOnClickListener(this);

        adapter = new ChatAdapter(ChatActivity.this, messages, _StrUserID, bundle, isFromHistory, _StrReceiverId);
        stickyList.setAdapter(adapter);

        if (!isFromFragment) {
            if (bundle != null) {
                taskid = bundle.getString(Constants.REQUEST_ID);
                judeid = bundle.getString(Constants.JUDE_ID);
                _StrReceiverId = judeid;

                if (TextUtils.isEmpty(judeid)) {
                    chatFooter.setVisibility(View.GONE);
                }

            }

            if (isFrom != null) {

                if (isFrom.equals(Constants.CURRENT_TASK)) {
                    new FetchFromDatabaseTask().execute("");
                } else if (isFrom.equals(Constants.HISTORY_TASK)) {
                    new FetchFromDatabaseTask().execute("");
                } else if (isFrom.equals(Constants.NOTIFICATION)) {
                    _StrReceiverId = bundle.getString(Constants.JUDE_ID);
                    new FetchFromDatabaseTask().execute("");
                }

            }

        }
    }


    @Override
    public void onBindToChatService(ChatService chatService) {
        super.onBindToChatService(chatService);
        this.mChatService = chatService;
        this.mChatService.addChatCallBacksListener(mChatCallBacks);
    }


    @Override
    public void onClick(View v) {

        newMessage = editMessage.getText().toString().trim();
        if (TextUtils.isEmpty(newMessage)) {

            editMessage.setError("Please Enter your Chat Message");
            return;
        }
        sendChatMessages(newMessage, "5", true);
    }


    /**
     * @param newMessage
     * @param flag
     * @param temp       If boolean TEMP is true then only Send the message via XMPP.
     */
    public static void sendChatMessages(String newMessage, String flag, boolean temp) {

        ChatMessage chatMessage = new ChatMessage();
        // IS READ WILL BE TRUE WHILE I AM SENDING MESSAGE.
        String IsDeliever = "false";
        String IsRead = "true";
        String type = flag; //"5"== Simple Text from User's Side


        if (temp) {
            chatMessage = ChatUtils.getTextMessage(taskid, _StrUserID, _StrReceiverId, type,
                    loginData.getData().getUserName(), newMessage, IsDeliever, IsRead);
            mChatService.sendMessage(chatMessage);
        } else {
            _StrReceiverId = "";
            chatMessage = ChatUtils.getTextMessage(taskid, _StrUserID, _StrReceiverId, type,
                    loginData.getData().getUserName(), newMessage, IsDeliever, IsRead);
        }

        /**
         * Sending data to service
         */

        adapter.addItems(chatMessage);
        dbHelper.add(chatMessage);
        editMessage.setText("");
        tvError.setVisibility(View.GONE);
        stickyList.setVisibility(View.VISIBLE);
    }

    /**
     * Chat Callbacks
     */
    ChatCallBacks mChatCallBacks = new ChatCallBacks() {

        @Override
        public void onMessageReceived(ChatMessage chatUserDetail) {
            super.onMessageReceived(chatUserDetail);
            Log.e(TAG, "onMessageReceived >>>>>>" + chatUserDetail.chatmsg);

            if (chatFooter.getVisibility() == View.GONE)
                chatFooter.setVisibility(View.VISIBLE);

            // checking if activity open for same session on which message received
            if (chatUserDetail != null) {
                if (chatUserDetail.user_id.equalsIgnoreCase(_StrUserID)) {
                    chatUserDetail.isread = ChatConstants.STATUS_TYPE_READ;
                    adapter.addItems(chatUserDetail);
                    //adapter.notifyDataSetChanged();
                    //For Updating the Data base.
                    dbHelper.update(chatUserDetail);
                } else {
                    // send notification
                    ChatUtils.generateNotification(getApplicationContext(), chatUserDetail);
                }
            }
        }

        @Override
        public void onMessageSentSuccessfully(ChatMessage chatUserDetail) {
            super.onMessageSentSuccessfully(chatUserDetail);
            Log.e(TAG, "onMessageSentSuccessfully ");
            if (chatUserDetail != null) {
                if (chatUserDetail.from.equalsIgnoreCase(_StrUserID)) {
                    chatUserDetail.status = ChatConstants.STATUS_TYPE_SENT;
                    adapter.updateItem(chatUserDetail);
                    dbHelper.update(chatUserDetail);

                }
            }
        }
    };

    // FETCH COMMENT HISTORY AND DISPLAY
    public class FetchFromDatabaseTask extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ChatActivity.this, null, getString(R.string.lbl_loading), true);
            progressDialog.setCancelable(true);
            Log.e(TAG, "FetchData");
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {

                dbHelper.updateUnAssignTask(taskid, _StrReceiverId);

//                _StrReceiverId = Constants.RECEIVER_ID;
                ArrayList<ChatMessage> temp = dbHelper.getAllRecords(_StrReceiverId, _StrUserID, taskid);

                //messages = makeChatDateAsSection(temp);
                messages.addAll(temp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onProgressUpdate(String... str) {

        }

        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            try {
                if (messages.size() == 0) {
                    stickyList.setEmptyView(tvError);
                    fetchChatHistoryFromServer();
                }

                adapter.notifyDataSetChanged();

                adapter = new ChatAdapter(ChatActivity.this, messages, _StrUserID, bundle, isFromHistory, _StrReceiverId);
                stickyList.setAdapter(adapter);

                dbHelper.updateISReadStatus(taskid, "true");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void fetchChatHistoryFromServer() {

        if (HeyJudeApp.hasNetworkConnection()) {

            final ProgressDialog progressDialog = ProgressDialog.show(ChatActivity.this, "", getString(R.string.lbl_loading), false, false);

            RestClient.getInstance().getApiService().getChatHistory(
                    loginData.getData().getId(),
                    taskid,
                    judeid,
                    new Callback<ChatHistory>() {
                        @Override
                        public void success(ChatHistory chatHistory, Response response) {

                            progressDialog.dismiss();
                            Log.e(TAG, "SUCCESS");

                            if (chatHistory != null) {
                                if (chatHistory.getData().size() > 1) {
                                    for (int i = 0; i < chatHistory.getData().size(); i++) {

                                        //  ChatMessage chatMessage = (ChatMessage) ChatUtils.getObjectFromJsonString(chatHistory.getData().get(i).getBody(), ChatMessage.class);
                                        ChatMessage chatMessage = chatHistory.getData().get(i).getBody();
                                        // messages.add(chatMessage);
                                        chatMessage.from = String.valueOf(loginData.getData().getId());

                                        chatMessage.user_id = String.valueOf(loginData.getData().getId());
                                        chatMessage.msg_id = ChatUtils.generateRandomUUID();
                                        chatMessage.to = _StrReceiverId;
                                        chatMessage.isread = "true";
                                        //messages.add(chatMessage);
                                        adapter.addItems(chatMessage);
                                        dbHelper.add(chatMessage);
                                    }
                                }
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            progressDialog.dismiss();
                            Log.e(TAG, "FAIL: " + error.toString());
                        }
                    }
            );


        } else {
            Util.showAlertDialog(ChatActivity.this);
        }

    }

    private ArrayList<ChatMessage> makeChatDateAsSection(ArrayList<ChatMessage> list) {
        ArrayList<ChatMessage> mList = new ArrayList<ChatMessage>();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                if (!list.get(i).showutcdate.subSequence(0, 10).toString().equalsIgnoreCase(list.get(i - 1).showutcdate.subSequence(0, 10).
                        toString())) {
                    ChatMessage chatDate = new ChatMessage();
                    chatDate.showutcdate = list.get(i).showutcdate;
                    mList.add(chatDate);
                }
            } else {
                ChatMessage chatDate = new ChatMessage();
                chatDate.showutcdate = list.get(i).showutcdate;
                mList.add(chatDate);
            }
            mList.add(list.get(i));
        }
        return mList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ISVISIBLE = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ISVISIBLE = false;
    }

    @Override
    protected void onDestroy() {
        unbindFromChatService();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
        intent.putExtra(Constants.FROM, Constants.CHAT_UTILS);
        startActivity(intent);
    }
}