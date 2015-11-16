package com.heyjude.androidapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.animation.AnimatorClass;
import com.heyjude.androidapp.apirequest.Request;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.gcm.GCMUtils;
import com.heyjude.androidapp.requestmodel.Key;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Global;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "LoginActivity";

    private EditText editEmail, editPwd;
    private Button btnLogin, btnSignUp, btnTwitter, btnFacebook;
    private TextView txtForgetPwd;

    private HashMap<String, String> loginFieldMap, facebookFieldMap, twitterFieldMap;

    private CallbackManager callbackManager;

    private int flag_login = 0;
    private int LOGIN_MOBILE = 1;
    private int LOGIN_FACEBOOK = 2;
    private int LOGIN_TWITTER = 3;

    //Shared Preferences
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    /* Any number for uniquely distinguish your request */
    private final int WEBVIEW_REQUEST_CODE = 100;

    private Twitter twitter;
    private RequestToken requestToken;

    private String consumerKey = null;
    private String consumerSecret = null;
    private String callbackUrl = null;
    private String oAuthVerifier = null;

    private GoogleCloudMessaging gcm;

    private String email, pwd, regId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        sharedPreferences = getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        /* initializing twitter parameters from string.xml */
        initTwitterConfigs();

		/* Enabling strict mode */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_login);

        init();

        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnTwitter.setOnClickListener(this);
        txtForgetPwd.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);

        getDeveiceToken();

    }

    private void getDeveiceToken() {

        //Get Device Token From the GCM
        try {
            if (TextUtils.isEmpty(sharedPreferences.getString(Global.PREF_DEVICE_TOKEN, ""))) {
                GCMUtils utils = new GCMUtils();
                utils.setmContext(this);
                if (utils.checkPlayServices()) {
                    gcm = GoogleCloudMessaging.getInstance(this);
                    regId = utils.getRegistrationId();

                    Log.e(TAG, "Reg Id: " + regId);

                    if (regId.isEmpty()) {
                        utils.registerInBackground();
                    }

                } else {
                    Log.i(TAG, "No valid Google Play Services APK found.");
                }
            } else {
                Log.i(TAG, "Device Token Already Registered : RegID" + sharedPreferences.getString(Global.PREF_DEVICE_TOKEN, "Not Available"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void init() {

        Util.getCurrentLocation(this);

        Log.e(TAG, "Location3" + Util.latitude + " " + Util.longitude);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPwd = (EditText) findViewById(R.id.editPwd);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnTwitter = (Button) findViewById(R.id.btnTwitter);
        txtForgetPwd = (TextView) findViewById(R.id.txtForgetPwd);
        btnFacebook = (Button) findViewById(R.id.btnFacebook);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("data", "" + resultCode);
        String verifier;

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == WEBVIEW_REQUEST_CODE) {
                verifier = data.getExtras().getString(oAuthVerifier);

                Log.e("Varifier", "" + verifier);

                if (verifier != null) {
                    try {

                        AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

                        long userID = accessToken.getUserId();
                        final User user = twitter.showUser(userID);

                        String screenName = user.getScreenName();
                        String imageURL = user.getProfileImageURL();

                        Log.d("Image URL from Twitter", "" + imageURL);

                        URL url = new URL(imageURL);
                        Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                        File f = new File(Environment.getExternalStorageDirectory(), String.valueOf(System.currentTimeMillis()) + ".jpg");

                        FileOutputStream outStream;
                        try {

                            outStream = new FileOutputStream(f);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                            outStream.flush();
                            outStream.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        twitterFieldMap = new HashMap<String, String>();
                        twitterFieldMap.put(Request.FIELD_DEVICE_TOKEN, sharedPreferences.getString(Global.PREF_DEVICE_TOKEN, ""));
                        twitterFieldMap.put(Request.FIELD_TWITTERID, String.valueOf(userID));
                        twitterFieldMap.put(Request.FIELD_TWITTER_SCREEN_NAME, screenName);
                        twitterFieldMap.put(Request.FIELD_IMAGE, imageURL);
                        twitterFieldMap.put(Request.FIELD_PLATFORM, getString(R.string.lbl_android));
                        twitterFieldMap.put(Request.FIELD_LAT, Util.latitude);
                        twitterFieldMap.put(Request.FIELD_LON, Util.longitude);

                        if (sharedPreferences.getString(Global.SESSION_KEY, null) != null) {

                            flag_login = LOGIN_TWITTER;
                            getTwitterWSResponse();
                        } else {
                            flag_login = LOGIN_TWITTER;
                            getSessionKey();
                        }


                    } catch (Exception e) {

                    }
                } else {
                    Util.dismissProgressDailog();
                }


            } else {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.e("Cancled", "Cancled");
        }


        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnLogin:

                email = editEmail.getText().toString();
                pwd = editPwd.getText().toString();

                if (isValidData(email, pwd)) {

                    Util.showProgressDailog(LoginActivity.this);

                    loginFieldMap = new HashMap<String, String>();

                    loginFieldMap.put(Request.FIELD_DEVICE_TOKEN, sharedPreferences.getString(Global.PREF_DEVICE_TOKEN, ""));
                    loginFieldMap.put(Request.FIELD_EMAILID, email);
                    loginFieldMap.put(Request.FIELD_PWD, pwd);
                    loginFieldMap.put(Request.FIELD_PLATFORM, getString(R.string.lbl_android));
                    loginFieldMap.put(Request.FIELD_LAT, Util.latitude);
                    loginFieldMap.put(Request.FIELD_LON, Util.longitude);

                    if (sharedPreferences.getString(Global.SESSION_KEY, null) != null) {

                        flag_login = LOGIN_MOBILE;
                        getLoginWSResponse();
                    } else {
                        flag_login = LOGIN_MOBILE;
                        getSessionKey();
                    }
                }


                break;

            case R.id.btnSignUp:
                Util.HideKeyBoard(LoginActivity.this);
                startActivityWithAnim(new Intent(LoginActivity.this, RegisterActivity.class));
                break;

            case R.id.btnTwitter:
                Util.showProgressDailog(LoginActivity.this);
                loginToTwitter();

                break;

            case R.id.btnFacebook:
                loginToFacebook();

                break;

            case R.id.txtForgetPwd:
                startActivityWithAnim(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                break;

        }

    }

    private boolean isValidData(String email, String pwd) {
        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            editEmail.setError(getString(R.string.alert_enter_email));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError(getString(R.string.alert_enter_valid_email));
            isValid = false;
        } else if (TextUtils.isEmpty(pwd)) {
            editPwd.setError(getString(R.string.alert_enter_password));
            isValid = false;
        }
        return isValid;
    }

    public void getSessionKey() {

        if (getApplicationContext().hasNetworkConnection()) {
            RestClient.getInstance().getApiService().getSessionKey(Request.KEY, new Callback<Key>() {

                @Override
                public void success(Key key, Response response) {

                    if (key.getStatus().equalsIgnoreCase(Request.SUCCESS)) {

                        HeyJudeApp.getInstance().setSessionKey(key.getToken());

                        if (flag_login == LOGIN_FACEBOOK) {
                            getFacebookWSResponse();
                        } else if (flag_login == LOGIN_MOBILE) {
                            getLoginWSResponse();
                        } else if (flag_login == LOGIN_TWITTER) {
                            getTwitterWSResponse();
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                    Util.showToast(LoginActivity.this, getString(R.string.lbl_retrofit_error));
                }
            });
        } else {
            Util.showAlertDialog(LoginActivity.this);
            Util.dismissProgressDailog();
        }
    }

    public void getTwitterWSResponse() {

        if (getApplicationContext().hasNetworkConnection()) {

            RestClient.getInstance().getApiService().twitterlLogin(twitterFieldMap, new Callback<LoginData>() {

                @Override
                public void success(LoginData loginData, Response response) {

                    if (loginData.getData().getStatus().equals(getString(R.string.response_fail))) {

                        Util.dismissProgressDailog();
                        Util.showToast(LoginActivity.this, getString(R.string.alert_incorrect_username_pass));
                        editEmail.setText("");
                        editPwd.setText("");
                        editEmail.requestFocus();
                    }

                    if (loginData.getData().getStatus().equals(getString(R.string.response_success))) {

                        Util.dismissProgressDailog();

                        Util.HideKeyBoard(LoginActivity.this);

                        editor.putBoolean(Global.PREF_HAS_LOGGED_IN, true);
                        Gson gson = new Gson();
                        String json = gson.toJson(loginData);
                        editor.putString(Global.PREF_RESPONSE_OBJECT, json);
                        editor.putInt(Global.PREF_LOGIN_COUNTER, 1);


                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra(Global.PREF_LOGIN_DATA_OBJECT, loginData);
                        startActivity(intent);
                        editor.putString(Global.PREF_LOGIN_WITH, getString(R.string.lbl_twitter));
                        AnimatorClass.appearLeftAnimation(LoginActivity.this);

                        editor.commit();
                        finish();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                    Util.dismissProgressDailog();
                    Util.showToast(LoginActivity.this, getString(R.string.lbl_retrofit_error));
                    Log.e("Response from Error", error.toString());
                }
            });
        } else {
            Util.showAlertDialog(LoginActivity.this);
            Util.dismissProgressDailog();
        }


    }

    public void getFacebookWSResponse() {

        if (getApplicationContext().hasNetworkConnection()) {
            RestClient.getInstance().getApiService().facebooklLogin(facebookFieldMap, new Callback<LoginData>() {

                @Override
                public void success(LoginData loginData, Response response) {

                    Log.e("Response of Facebook", "" + response);

                    if (loginData.getData().getStatus().equals(getString(R.string.response_fail))) {

                        Util.dismissProgressDailog();
                        Util.showToast(LoginActivity.this, getString(R.string.alert_incorrect_username_pass));
                        editEmail.setText("");
                        editPwd.setText("");
                        editEmail.requestFocus();

                    }

                    if (loginData.getData().getStatus().equals(getString(R.string.response_success))) {


                        Util.dismissProgressDailog();
                        Util.HideKeyBoard(LoginActivity.this);

                        editor.putBoolean(Global.PREF_HAS_LOGGED_IN, true);
                        Gson gson = new Gson();
                        String json = gson.toJson(loginData);
                        editor.putString(Global.PREF_RESPONSE_OBJECT, json);

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra(Global.PREF_LOGIN_DATA_OBJECT, loginData);
                        startActivity(intent);
                        editor.putString(Global.PREF_LOGIN_WITH, getString(R.string.lbl_facebook));
                        editor.putInt(Global.PREF_LOGIN_COUNTER, 1);
                        AnimatorClass.appearLeftAnimation(LoginActivity.this);

                        editor.commit();
                        finish();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Util.dismissProgressDailog();
                    Util.showToast(LoginActivity.this, getString(R.string.lbl_retrofit_error));
                    Log.e("Response from Error", error.toString());

                }
            });
        } else {
            Util.showAlertDialog(LoginActivity.this);
            Util.dismissProgressDailog();
        }


    }

    public void getLoginWSResponse() {

        if (getApplicationContext().hasNetworkConnection()) {
            RestClient.getInstance().getApiService().logIn(loginFieldMap, new Callback<LoginData>() {

                @Override
                public void success(LoginData loginData, Response response) {

                    Util.dismissProgressDailog();

                    if (loginData.getData().getStatus().equals(getString(R.string.response_fail))) {

                        Util.showToast(LoginActivity.this, getString(R.string.alert_incorrect_username_pass));

                        editEmail.setText("");
                        editPwd.setText("");
                        editEmail.requestFocus();
                    }

                    if (loginData.getData().getStatus().equals(getString(R.string.response_success))) {

                        Util.HideKeyBoard(LoginActivity.this);

                        editor.putBoolean(Global.PREF_HAS_LOGGED_IN, true);
                        Gson gson = new Gson();
                        String json = gson.toJson(loginData);
                        editor.putString(Global.PREF_RESPONSE_OBJECT, json);


                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra(Global.PREF_LOGIN_DATA_OBJECT, loginData);
                        startActivity(intent);
                        editor.putString(Global.PREF_LOGIN_WITH, getString(R.string.lbl_application));
                        editor.putInt(Global.PREF_LOGIN_COUNTER, 1);
                        AnimatorClass.appearLeftAnimation(LoginActivity.this);

                        editor.commit();
                        finish();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                    Util.dismissProgressDailog();
                    Util.showToast(LoginActivity.this, getString(R.string.lbl_retrofit_error));
                    Log.e("Response from Error", error.toString());

                }
            });
        } else {
            Util.showAlertDialog(LoginActivity.this);
            Util.dismissProgressDailog();
        }


    }

    private void initTwitterConfigs() {
        consumerKey = getString(R.string.twitter_consumer_key);
        consumerSecret = getString(R.string.twitter_consumer_secret);
        callbackUrl = getString(R.string.twitter_callback);
        oAuthVerifier = getString(R.string.twitter_oauth_verifier);
    }

    private void loginToTwitter() {

        if (getApplicationContext().hasNetworkConnection()) {
            final ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);

            final Configuration configuration = builder.build();
            final TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            try {
                requestToken = twitter.getOAuthRequestToken(callbackUrl);

                final Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
                startActivityForResult(intent, WEBVIEW_REQUEST_CODE);

            } catch (TwitterException e) {
                e.printStackTrace();
            }
        } else {
            Util.showAlertDialog(LoginActivity.this);
            Util.dismissProgressDailog();
        }

    }


    private void loginToFacebook() {

        if (getApplicationContext().hasNetworkConnection()) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));

            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                @Override
                public void onSuccess(LoginResult loginResult) {
                    Util.showProgressDailog(LoginActivity.this);

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    try {

                                        Log.d("OBJECT REST", "" + object);

                                        String user_id = object.getString("id");
                                        String user_name = object.getString("name");
                                        String user_email = object.getString("email");

                                        URL image_value = new URL("https://graph.facebook.com/" + user_id + "/picture");

                                        Log.e("Facebook Response:", "" + user_id + " " + user_name + " " + user_email + " " + image_value.toString());

                                        facebookFieldMap = new HashMap<String, String>();

                                        facebookFieldMap.put(Request.FIELD_DEVICE_TOKEN, sharedPreferences.getString(Global.PREF_DEVICE_TOKEN, ""));
                                        facebookFieldMap.put(Request.FIELD_EMAIL, user_email);
                                        facebookFieldMap.put(Request.FIELD_IMAGE, image_value.toString());
                                        facebookFieldMap.put(Request.FIELD_FACEBOOKID, user_id);
                                        facebookFieldMap.put(Request.FIELD_PLATFORM, getString(R.string.lbl_android));
                                        facebookFieldMap.put(Request.FIELD_LAT, Util.latitude);
                                        facebookFieldMap.put(Request.FIELD_LON, Util.longitude);
                                        facebookFieldMap.put(Request.FIELD_NAME, user_name);

                                        if (sharedPreferences.getString(Global.SESSION_KEY, null) != null) {

                                            flag_login = LOGIN_FACEBOOK;
                                            getFacebookWSResponse();
                                        } else {
                                            flag_login = LOGIN_FACEBOOK;
                                            getSessionKey();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender, birthday");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    Log.e("Facebook Cancel", "Facebook Cancel");
                }

                @Override
                public void onError(FacebookException exception) {

                    Util.showToast(LoginActivity.this, exception.toString());
                }
            });

        } else {
            Util.showAlertDialog(LoginActivity.this);
            Util.dismissProgressDailog();
        }
    }
}