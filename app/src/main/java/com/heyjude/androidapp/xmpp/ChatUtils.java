package com.heyjude.androidapp.xmpp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.activity.ChatActivity;
import com.heyjude.androidapp.activity.HomeActivity;
import com.heyjude.androidapp.constant.ChatConstants;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Constants;
import com.heyjude.androidapp.utility.Global;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatUtils {

    Context activity;
    //Message notification
    //DONT ASSIGN 2 BECAUSE 2 IS ALREADY ASSIGNED IN GcmIntentService.
    public static int MESSAGE_NOTIFICATION_ID = 1;
    public static int MESSAGE_GROUPNOTIFICATION_ID = 3;
    public static Map<String, ArrayList<String>> mapTaskMessages = new HashMap<String, ArrayList<String>>();
    public static ArrayList<String> privateTaskMessages = new ArrayList<String>();

    public static SharedPreferences sharedPreferences;
    public static LoginData loginData;


    public ChatUtils(Context context) {
        this.activity = context;
        Gson gson = new Gson();
        sharedPreferences = context.getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);
    }

    public static ChatMessage getTextMessage(String task_id, String sender_id, String receiverId, String flag, String senderName,
                                             String message, String Isdeliver, String isRead) {
        //here sender_id equal to user_id
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.msg_id = generateRandomUUID();
        chatMessage.taskid = task_id;
        chatMessage.from = sender_id;
        chatMessage.sender_name = senderName;
        chatMessage.to = receiverId;
        chatMessage.chatmsg = message;
        chatMessage.flag = flag;
        chatMessage.showutcdate = getCurrentDateFormat();
        // chatMessage.showutcdate = "2015-10-02:23:05:03";
        chatMessage.status = ChatConstants.STATUS_TYPE_PROCESS;
        chatMessage.isdeliver = Isdeliver;
        chatMessage.isread = isRead;
        chatMessage.user_id = sender_id;
        chatMessage.ispush = "false";
        Long tsLong = System.currentTimeMillis() / 1000;
        chatMessage.newtimestamp = tsLong.toString();

        return chatMessage;
    }

    public static Object getObjectFromJsonString(String jsonData, Class modelClass) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(jsonData, modelClass);
    }

    public static String getJsonStringFromObject(Object modelClass) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(modelClass);
        // return gson.fromJson(jsonData, modelClass);
    }


    public static String generateRandomUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getCurrentDateFormat() {
        // return DateFormat.format("kk:mm a", System.currentTimeMillis()).toString();
        return ChatConstants.getCurrentUTCDate();
    }


    /*public static void generateNotification(Context context, ChatMessage messageData, String alert) {

        Log.i("", "==Inside generateNotification Method==");

        Uri soundUri = null;
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (messageData.flag.equalsIgnoreCase(ChatConstants.FLAG_ASSIGNED_TASK)) {

            if (assignTaskMessages == null) {
                assignTaskMessages = new ArrayList<>();
            }
            assignTaskMessages.add(alert);

        } else {
            if (mapTaskMessages == null) {
                mapTaskMessages = new HashMap<>();
            }

            if (privateTaskMessages == null) {
                privateTaskMessages = new ArrayList<>();
            }

            //privateTaskMessages.add(messageData.sender_name + "@" + " " + messageData.chatmsg);
            privateTaskMessages.add(alert);

            mapTaskMessages.put(messageData.taskid, privateTaskMessages);
        }
        NotificationCompat.Builder builder;
        if (messageData.flag.equalsIgnoreCase(ChatConstants.FLAG_ASSIGNED_TASK)) {
            builder = createAssignTaskNotificationBuilder(context, messageData);
        } else {
            builder = createGroupMessageNotificationBuilder(context, messageData);
        }

        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        builder.setSound(soundUri);
        //Look up the notification manager service.
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Pass the notification to the NotificationManager.
        nm.notify(MESSAGE_GROUPNOTIFICATION_ID, builder.build());
    }*/

    public static void generateNotification(Context context, ChatMessage messageData) {

        Gson gson = new Gson();
        sharedPreferences = context.getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);

        Log.i("", "==Inside generateNotification Method==");

        Uri soundUri = null;
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (mapTaskMessages == null) {
            mapTaskMessages = new HashMap<>();
        }

        if (privateTaskMessages == null) {
            privateTaskMessages = new ArrayList<>();
        }

        //privateTaskMessages.add(messageData.sender_name + "@" + " " + messageData.chatmsg);
        if (messageData.flag.equalsIgnoreCase(ChatConstants.FLAG_TEXT_MESSAGE))
            privateTaskMessages.add("Hey " + loginData.getData().getUserName() + ", I have just sent you a message - Jude");
        else if (messageData.flag.equalsIgnoreCase(ChatConstants.FLAG_PAYMENT_MESSAGE))
            privateTaskMessages.add("Hey " + loginData.getData().getUserName() + ", I have just sent you a payment request - Jude");
        else if (messageData.flag.equalsIgnoreCase(ChatConstants.FLAG_MAP_MESSAGE))
            privateTaskMessages.add("Hey " + loginData.getData().getUserName() + ", I have just sent you a map - Jude");
        else if (messageData.flag.equalsIgnoreCase(ChatConstants.FLAG_VENDOR_MESSAGE))
            privateTaskMessages.add("Hey " + loginData.getData().getUserName() + ", I have just sent you vendor(s) details - Jude");

        mapTaskMessages.put(messageData.taskid, privateTaskMessages);

        NotificationCompat.Builder builder;

        builder = createGroupMessageNotificationBuilder(context, messageData);
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        builder.setSound(soundUri);
        //Look up the notification manager service.
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Pass the notification to the NotificationManager.
        nm.notify(MESSAGE_GROUPNOTIFICATION_ID, builder.build());
    }

    private static NotificationCompat.Builder createGroupMessageNotificationBuilder(Context context, ChatMessage messageData) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(getActiveGroupMessageCount() > 1 ? "Messages" : "Message");

        if (getActiveGroupMessageCount() > 1) {
            builder.setContentText(privateTaskMessages.size() + " messages from " + mapTaskMessages.size() + " Tasks.");
        } else {
            builder.setContentText(privateTaskMessages.get(0));
        }

        builder.setSmallIcon(R.mipmap.ic_launcher); // stat_notify_chat taken from Android SDK (API level 17)
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        builder.setLargeIcon(largeIcon);
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());

        if (mapTaskMessages.size() > 1) {
            if (Build.VERSION.SDK_INT >= 19) // KITKAT
            {
                PendingIntent pi = createCurrentTaskNotificationMessageActivityPendingIntent(context);
                if (pi != null) {
                    pi.cancel();
                }
            }

            PendingIntent msgPendingIntent = createCurrentTaskNotificationMessageActivityPendingIntent(context);
            builder.setContentIntent(msgPendingIntent);
        } else {
            if (Build.VERSION.SDK_INT >= 19) // KITKAT
            {
                PendingIntent pi = createChatActivityPendingIntent(context, messageData);
                if (pi != null) {
                    pi.cancel();
                }
            }
            PendingIntent msgPendingIntent = createChatActivityPendingIntent(context, messageData);

            builder.setContentIntent(msgPendingIntent);
        }


        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        if ((mapTaskMessages != null) && (mapTaskMessages.size() > 0)) {
            for (int i = privateTaskMessages.size() - 1; i >= 0; i--) {
                inboxStyle.addLine(privateTaskMessages.get(i));
            }
        }

        if ((mapTaskMessages != null) && (getActiveGroupMessageCount() >= 1)) {
            if (getActiveGroupMessageCount() > 1)
                inboxStyle.setSummaryText(privateTaskMessages.size()
                        + " messages from " + mapTaskMessages.size()
                        + " Tasks.");
            builder.setStyle(inboxStyle);
        }
        return builder;
    }

    private static int getActiveGroupMessageCount() {
        int cnt = 0;
        if (privateTaskMessages != null) {
            cnt = privateTaskMessages.size();
        }
        return cnt;
    }

    /**
     * IT WILL OPEN SINGLE CHAT ACTIVITY
     *//*
    private static PendingIntent createAssignTaskChatActivityPendingIntent(Context context, ChatMessage messageData) {

        *//**
     * Call for Refreshing the Data of CurrentTaskFragment.java
     *//*
        Intent intent = new Intent();
        intent.setAction(Constants.REFRESH_DATA);
        context.sendBroadcast(intent);

        Intent msgIntent = null;
        msgIntent = new Intent(context, ChatActivity.class);
        msgIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        msgIntent.putExtra(Constants.REQUEST_ID, messageData.taskid);
        msgIntent.putExtra(Constants.JUDE_ID, messageData.to);
        msgIntent.putExtra(Constants.FROM, Constants.NOTIFICATION);
        *//*msgIntent.putExtra(Constant.EXTRA_GROUPID, messageData.group_id);
        msgIntent.putExtra(Constant.EXTRA_SUBJECT_NAME, messageData.group_name);
        msgIntent.putExtra(Constant.EXTRA_MEMBERSID, messageData.member_id);
        msgIntent.putExtra(Constant.EXTRA_IS_NOTIFICATION, true); *//*


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        //Add the back stack to the stack builder. This method also adds flags
        //that start the stack in a fresh task.
        //stackBuilder.addParentStack(ParentGroupChatActivity.class);
        //Add the Intent that starts the Activity from the notification.
        stackBuilder.addNextIntent(msgIntent);

        //Get a PendingIntent containing the entire back stack.
        PendingIntent msgPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return msgPendingIntent;
    }*/

    /**
     * IT WILL OPEN SINGLE CHAT ACTIVITY
     */
    private static PendingIntent createChatActivityPendingIntent(Context context, ChatMessage messageData) {

        /**
         * Call for Refreshing the Data of CurrentTaskFragment.java
         */
        Intent intent = new Intent();
        intent.setAction(Constants.REFRESH_DATA);
        context.sendBroadcast(intent);

        Intent msgIntent = null;
        msgIntent = new Intent(context, ChatActivity.class);
        msgIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        msgIntent.putExtra(Constants.REQUEST_ID, messageData.taskid);
        msgIntent.putExtra(Constants.JUDE_ID, messageData.to);
        msgIntent.putExtra(Constants.FROM, Constants.NOTIFICATION);
        /*msgIntent.putExtra(Constant.EXTRA_GROUPID, messageData.group_id);
        msgIntent.putExtra(Constant.EXTRA_SUBJECT_NAME, messageData.group_name);
        msgIntent.putExtra(Constant.EXTRA_MEMBERSID, messageData.member_id);
        msgIntent.putExtra(Constant.EXTRA_IS_NOTIFICATION, true); */


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        //Add the back stack to the stack builder. This method also adds flags
        //that start the stack in a fresh task.
        //stackBuilder.addParentStack(ParentGroupChatActivity.class);
        //Add the Intent that starts the Activity from the notification.
        stackBuilder.addNextIntent(msgIntent);

        //Get a PendingIntent containing the entire back stack.
        PendingIntent msgPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return msgPendingIntent;
    }


    /**
     * IT WILL OPEN CURRENT TASK SCREEN
     */
    private static PendingIntent createCurrentTaskNotificationMessageActivityPendingIntent(Context context) {

        /**
         * Call for Refreshing the Data of CurrentTaskFragment.java
         */
        Intent intent = new Intent();
        intent.setAction(Constants.REFRESH_DATA);
        context.sendBroadcast(intent);

        //Start DalMainActivity in the message threads position.
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

            }
        }, 2000);

        Intent msgIntent = null;
        msgIntent = new Intent(context, HomeActivity.class);
        msgIntent.putExtra(Constants.FROM, Constants.CHAT_UTILS);

        msgIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //msgIntent.putExtra(DalMainActivity.KEY_PAGE_INDEX, DalMainActivity.MESSAGES_POSITION);x

        android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(context);
        //Add the back stack to the stack builder. This method also adds flags
        //that start the stack in a fresh task.
        //stackBuilder.addParentStack(HomeActivity.class);
        //Add the Intent that starts the Activity from the notification.
        stackBuilder.addNextIntent(msgIntent);

        //Get a PendingIntent containing the entire back stack.
        PendingIntent msgPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return msgPendingIntent;
    }


    public static <K extends Comparable, V extends Comparable> Map<K, V> sortByKeys(Map<K, V> map) {
        List<K> keys = new LinkedList<K>(map.keySet());
        Collections.sort(keys);

        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K, V> sortedMap = new LinkedHashMap<K, V>();
        for (K key : keys) {
            sortedMap.put(key, map.get(key));
        }

        return sortedMap;
    }

    public static String encodeToBase64String(String text) {
        String str = Base64.encodeToString(text.getBytes(), Base64.NO_WRAP);
        str = str.replace("+", "%2B");
        return str;
    }

    public static String decodeFromBase64String(String text) {
        text = text.replace("%2B", "+");
        String str = new String(Base64.decode(text, Base64.NO_WRAP)).toString();

        return str;
    }

}