/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.heyjude.androidapp.gcm;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.heyjude.androidapp.R;
import com.heyjude.androidapp.constant.ChatConstants;
import com.heyjude.androidapp.db.DBHelper;
import com.heyjude.androidapp.xmpp.ChatMessage;
import com.heyjude.androidapp.xmpp.ChatUtils;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GCMIntentService extends IntentService {

    public GCMIntentService() {
        super("GCMIntentService");
    }

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    private static String postID = "";

    //Like notification
    public static int NOTIFICATION_ID = 2;


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onHandleIntent(Intent intent) {

        DBHelper dbHelper = DBHelper.getInstance(this);
        Bundle extras = intent.getExtras();
        Log.e(">>>>GCM", "Push Notification");

        //CHAT NOTIFICATION MESSAGE.

        // sendNotification("Dummy Data");

        try {
            ChatMessage messageData = new ChatMessage();
            messageData.flag = extras.getString(ChatConstants.FLAG);
            messageData.to = extras.getString("sender");
            messageData.taskid = extras.getString("taskid");
            messageData.newtimestamp = extras.getString("newtimestamp");
            dbHelper.add(messageData);
            ChatUtils.generateNotification(getApplicationContext(), messageData);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!extras.isEmpty()) { // has effect of unparcelling Bundle

            /** Filter messages based on message type. Since it is likely that
             * GCM will be extended in the future with new message types, just
             * ignore any message types you're not interested in, or that you
             * don't recognize.
             */

        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setContentTitle(getResources().getString(R.string.app_name));
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.ic_applogo); // stat_notify_chat taken from Android SDK (API level 17)
        //android.R.drawable.stat_notify_chat
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());

        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        Uri soundUri = null;
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(soundUri);

        //Look up the notification manager service.
        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(12, builder.build());

    }
}