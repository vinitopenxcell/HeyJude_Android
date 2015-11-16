package com.heyjude.androidapp.utility;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.heyjude.androidapp.model.User;
import com.instabug.library.Instabug;
import com.usebutton.sdk.Button;

/**
 * Created by aalap on 19/6/15.
 */
public class HeyJudeApp extends Application {

    private static HeyJudeApp context;
    private User user;
    //Shared Pref Variables
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    public void onCreate() {

        super.onCreate();
        Instabug.initialize(this, Constants.INSTABUG_KEY);

        Button.getButton(this).start();
        context = this;
        sharedPreferences = getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }


    public static HeyJudeApp getInstance() {
        return context;
    }

    /*public User getUser() {
        return user;
    }*/

    public void setUser(User user) {
        this.user = user;
    }

    public static boolean hasNetworkConnection() {

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase(Constants.WIFI))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase(Constants.MOBILE))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public String getSessionKey() {
        return sharedPreferences.getString(Global.SESSION_KEY, null);
    }

    public void setSessionKey(String sessionKey) {


        editor.putString(Global.SESSION_KEY, sessionKey);
        editor.commit();

        Log.e("Key from Server:", "" + sharedPreferences.getString(Global.SESSION_KEY, null));

    }
}
