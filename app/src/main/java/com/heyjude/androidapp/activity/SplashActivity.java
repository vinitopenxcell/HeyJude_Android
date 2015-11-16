package com.heyjude.androidapp.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Global;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SplashActivity extends BaseActivity {

    private String TAG = "SplashActivity";

    private TextView hangoutTvOne;
    private TextView hangoutTvTwo;
    private TextView hangoutTvThree;
    private static int SPLASH_TIME_OUT = 2000;

    //Shared Preferences
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /**
         * Initializing the Facebook SDK so we can use Facebook.
         */
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        /**
         *Setting up the Preferences for Checking whether user is already Logged in or not,
         */
        sharedPreferences = getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        int counter = sharedPreferences.getInt(Global.PREF_LOGIN_COUNTER, 0);
        counter++;

        Log.e(TAG, "Counter Value:" + counter);

        editor.putInt(Global.PREF_LOGIN_COUNTER, counter);
        editor.commit();

        /**
         *Getting Keyhash value for Facebook. Which we need to supply while setting up Facebook
         */
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e(TAG, "KeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        hangoutTvOne = (TextView) findViewById(R.id.firstDot);
        hangoutTvTwo = (TextView) findViewById(R.id.secondDot);
        hangoutTvThree = (TextView) findViewById(R.id.thirdDot);

        //Animation Start...!!!
        waveAnimation();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                boolean hasLoggedIn = sharedPreferences.getBoolean(Global.PREF_HAS_LOGGED_IN, false);

                if (HeyJudeApp.hasNetworkConnection()) {

                    Util.getCurrentLocation(SplashActivity.this);
                    if (hasLoggedIn) {

                        Gson gson = new Gson();
                        String json = sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, "");
                        LoginData loginData = gson.fromJson(json, LoginData.class);

                        Intent intent = new Intent();
                        intent.setClass(SplashActivity.this, HomeActivity.class);
                        intent.putExtra(Global.PREF_LOGIN_DATA_OBJECT, loginData);
                        startActivity(intent);
                        SplashActivity.this.finish();

                    } else {
                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                } else {
                    Util.showAlertDialog(SplashActivity.this);
                }
            }
        }, SPLASH_TIME_OUT);

    }

    public void waveAnimation() {
        PropertyValuesHolder tvOne_Y = PropertyValuesHolder.ofFloat(TextView.TRANSLATION_Y, -20.0f);
        PropertyValuesHolder tvOne_X = PropertyValuesHolder.ofFloat(TextView.TRANSLATION_X, 0);
        ObjectAnimator waveOneAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvOne, tvOne_X, tvOne_Y);
        waveOneAnimator.setRepeatCount(-1);
        waveOneAnimator.setRepeatMode(ValueAnimator.REVERSE);
        waveOneAnimator.setDuration(300);
        waveOneAnimator.start();

        PropertyValuesHolder tvTwo_Y = PropertyValuesHolder.ofFloat(TextView.TRANSLATION_Y, -20.0f);
        PropertyValuesHolder tvTwo_X = PropertyValuesHolder.ofFloat(TextView.TRANSLATION_X, 0);
        ObjectAnimator waveTwoAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvTwo, tvTwo_X, tvTwo_Y);
        waveTwoAnimator.setRepeatCount(-1);
        waveTwoAnimator.setRepeatMode(ValueAnimator.REVERSE);
        waveTwoAnimator.setDuration(300);
        waveTwoAnimator.setStartDelay(100);
        waveTwoAnimator.start();

        PropertyValuesHolder tvThree_Y = PropertyValuesHolder.ofFloat(TextView.TRANSLATION_Y, -20.0f);
        PropertyValuesHolder tvThree_X = PropertyValuesHolder.ofFloat(TextView.TRANSLATION_X, 0);
        ObjectAnimator waveThreeAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvThree, tvThree_X, tvThree_Y);
        waveThreeAnimator.setRepeatCount(-1);
        waveThreeAnimator.setRepeatMode(ValueAnimator.REVERSE);
        waveThreeAnimator.setDuration(300);
        waveThreeAnimator.setStartDelay(200);
        waveThreeAnimator.start();
    }
}