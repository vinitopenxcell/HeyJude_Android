package com.heyjude.androidapp.xmpp;

import android.util.Log;

/**
 * Created by bhavesh on 13/7/15.
 */
public class ChatLog {

    private static final boolean IS_CHAT_LOG_SHOW = true;
    public static final String TAG = "ChatLog";

    public static void e(String message) {
        if (IS_CHAT_LOG_SHOW)
            Log.e(TAG, message);
    }

    public static void i(String message) {
        if (IS_CHAT_LOG_SHOW)
            Log.i(TAG, message);
    }

    public static void d(String message) {
        if (IS_CHAT_LOG_SHOW)
            Log.d(TAG, message);
    }

    public static void v(String message) {
        if (IS_CHAT_LOG_SHOW)
            Log.v(TAG, message);
    }

    public static void w(String message) {
        if (IS_CHAT_LOG_SHOW)
            Log.w(TAG, message);
    }
}
