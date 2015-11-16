package com.heyjude.androidapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.adapter.DrawerAdapter;
import com.heyjude.androidapp.animation.AnimatorClass;
import com.heyjude.androidapp.apirequest.Request;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.dialog.PagerDailog;
import com.heyjude.androidapp.fragment.CurrentTaskFragment;
import com.heyjude.androidapp.fragment.HistoryTaskFragment;
import com.heyjude.androidapp.fragment.HomeFragment;
import com.heyjude.androidapp.fragment.CouponsFragment;
import com.heyjude.androidapp.fragment.TwoForOneDiningFragment;
import com.heyjude.androidapp.fragment.UpdateProfile;
import com.heyjude.androidapp.model.DrawerOption;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Constants;
import com.heyjude.androidapp.utility.Global;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class HomeActivity extends BaseActivity {

    private ListView lsDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private int pos = 0;
    private DrawerLayout drawerLayout;
    private boolean drawerClose;
    private Fragment fragment;

    private HashMap<String, String> logoutFieldMap;

    private SharedPreferences sharedPreferences;
    private LoginData loginData;

    private String TAG = "HomeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

       /* getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // // set an enter transition
            // getWindow().setEnterTransition(new Explode());
            // // set an exit transition
            // getWindow().setExitTransition(new Explode());

            getWindow().setSharedElementEnterTransition(new ChangeBounds());
            getWindow().setSharedElementExitTransition(new ChangeBounds());
        }*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_menu);

        sharedPreferences = getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);

        TextView toolbarTtitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTtitle.setText(getString(R.string.app_name));

        Gson gson = new Gson();
        sharedPreferences = getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);

        drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);

        startAndBindToChatService();

        if (!(sharedPreferences.getString(Global.PREF_LOGIN_WITH, "").equals(getString(R.string.lbl_facebook)) ||
                sharedPreferences.getString(Global.PREF_LOGIN_WITH, "").equals(getString(R.string.lbl_twitter)))) {

            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                    R.string.app_name, R.string.app_name) {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    if (drawerClose) {
                        switch (pos) {

                            case 0:
                                if (!(fragment instanceof HomeFragment)) {
                                    changeFragment(new HomeFragment(), true, false);
                                }
                                break;
                            case 1:
                                if (!(fragment instanceof CurrentTaskFragment))
                                    changeFragment(new CurrentTaskFragment(), true, false);
                                break;
                            case 2:
                                if (!(fragment instanceof HistoryTaskFragment))
                                    changeFragment(new HistoryTaskFragment(), true, false);
                                break;

                            case 3:

                                if (!(fragment instanceof TwoForOneDiningFragment))
                                    changeFragment(new TwoForOneDiningFragment(), true, false);
                                break;

                            case 4:

                                if (!(fragment instanceof CouponsFragment))
                                    changeFragment(new CouponsFragment(), true, false);
                                break;

                            case 5:

                                /*Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hey Jude");

                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey Jude Application");
                                startActivity(Intent.createChooser(sharingIntent, "Share via"));*/
                                if (!(fragment instanceof UpdateProfile))
                                    changeFragment(new UpdateProfile(), true, false);
                                break;

                            case 6:
                                logout();
                                break;

                            default:
                                Toast.makeText(HomeActivity.this, "Coming Soon", Toast.LENGTH_LONG).show();
                                break;

                        }
                    }
                    drawerClose = false;

                }
            };
        } else {

            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                    R.string.app_name, R.string.app_name) {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    if (drawerClose) {
                        switch (pos) {

                            case 0:
                                if (!(fragment instanceof HomeFragment)) {
                                    //changeFragment(new HomeFragment(), true, false);
                                    changeFragment(new HomeFragment(), true, false);
                                }
                                break;
                            case 1:
                                if (!(fragment instanceof CurrentTaskFragment))
                                    changeFragment(new CurrentTaskFragment(), true, false);
                                break;
                            case 2:
                                if (!(fragment instanceof HistoryTaskFragment))
                                    changeFragment(new HistoryTaskFragment(), true, false);
                                break;

                            case 3:

                                if (!(fragment instanceof TwoForOneDiningFragment))
                                    changeFragment(new TwoForOneDiningFragment(), true, false);
                                break;

                            case 4:

                                if (!(fragment instanceof CouponsFragment))
                                    changeFragment(new CouponsFragment(), true, false);
                                break;

                            case 5:

                                /*Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hey Jude");

                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey Jude Application");
                                startActivity(Intent.createChooser(sharingIntent, "Share via"));*/
                                logout();
                                break;

                            default:
                                Toast.makeText(HomeActivity.this, "Coming Soon", Toast.LENGTH_LONG).show();
                                break;

                        }
                    }
                    drawerClose = false;

                }
            };
        }


        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        lsDrawer = (ListView) findViewById(R.id.lsDrawer);
        lsDrawer.setOnItemClickListener(slidingListClickListener);
        loadDrawerListData();

        /*//Getting Object from the previous Activity
        loginData = (LoginData) getIntent().getSerializableExtra(Global.PREF_LOGIN_DATA_OBJECT);

        if (loginData != null) {
            HeyJudeApp.getInstance().setUser(loginData.getData());
        }*/

        String menuFragment = getIntent().getStringExtra(Constants.FROM);

        if ((sharedPreferences.getInt(Global.PREF_LOGIN_COUNTER, 0) == 1) && (menuFragment == null)) {
            PagerDailog dailog = new PagerDailog(HomeActivity.this);
            dailog.show();
        }


        if (menuFragment != null) {
            if (menuFragment.equals(Constants.CHAT_UTILS)) {
                fragment = new CurrentTaskFragment();
            }
        } else {
            fragment = new HomeFragment();
        }

       /* Bundle bundle = new Bundle();
        bundle.putSerializable(PREF_LOGIN_DATA_OBJECT, loginData);
        fragment.setArguments(bundle);*/

        changeFragment(fragment, false, false);

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void loadDrawerListData() {

        DrawerOption newTask, currentTask, history, twoForOneDining, coupons, share, settings = null, logout;

        newTask = new DrawerOption(getString(R.string.lbl_new_task), R.drawable.menu_newtask_selector);
        currentTask = new DrawerOption(getString(R.string.lbl_current_task), R.drawable.menu_currenttask_selector);
        history = new DrawerOption(getString(R.string.lbl_task_history), R.drawable.menu_history_selector);
        twoForOneDining = new DrawerOption(getString(R.string.lbl_two_for_dining), R.drawable.menu_dining_selector);
        coupons = new DrawerOption(getString(R.string.lbl_counpons), R.drawable.menu_coupons_selector);
        //share = new DrawerOption("Share", R.drawable.menu_share_selector);

        if (!(sharedPreferences.getString(Global.PREF_LOGIN_WITH, null).equals(getString(R.string.lbl_facebook)) ||
                sharedPreferences.getString(Global.PREF_LOGIN_WITH, null).equals(getString(R.string.lbl_twitter)))) {
            settings = new DrawerOption(getString(R.string.lbl_profile_settings), R.drawable.menu_profile_selector);
        }

        logout = new DrawerOption(getString(R.string.lbl_logout), R.drawable.menu_logout_selector);

        List<DrawerOption> optionList = new ArrayList<DrawerOption>();
        optionList.add(newTask);
        optionList.add(currentTask);
        optionList.add(history);
        optionList.add(twoForOneDining);
        optionList.add(coupons);
        // optionList.add(share);

        if (!(sharedPreferences.getString(Global.PREF_LOGIN_WITH, null).equals(getString(R.string.lbl_facebook)) ||
                sharedPreferences.getString(Global.PREF_LOGIN_WITH, null).equals(getString(R.string.lbl_twitter))))
            optionList.add(settings);
        optionList.add(logout);

        DrawerAdapter adapter = new DrawerAdapter(this, optionList);
        lsDrawer.setAdapter(adapter);

        lsDrawer.setItemChecked(0, true);
    }

    AdapterView.OnItemClickListener slidingListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            drawerClose = true;
            pos = position;
            lsDrawer.setItemChecked(position, true);
            drawerLayout.closeDrawers();
        }
    };

    private void changeFragment(Fragment fragment, boolean shouldAnimate, boolean popAnimate) {

        this.fragment = fragment;

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (shouldAnimate)
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        if (popAnimate)
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    private void logout() {

        //User is logout From: Application,Twitter or Facebook
        String logutFrom = sharedPreferences.getString(Global.PREF_LOGIN_WITH, null);

        Util.showProgressDailog(HomeActivity.this);

        if (logutFrom.equals(getString(R.string.lbl_twitter))) {

            CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();

        } else if (logutFrom.equals(getString(R.string.lbl_facebook))) {
            LoginManager.getInstance().logOut();
        }


        String session_key = HeyJudeApp.getInstance().getSessionKey();

        logoutFieldMap = new HashMap<String, String>();

        logoutFieldMap.put(Request.FIELD_DEVICE_TOKEN, sharedPreferences.getString(Global.PREF_DEVICE_TOKEN, ""));
        logoutFieldMap.put(Request.FIELD_USERID, loginData.getData().getId());
        logoutFieldMap.put(Request.SESSION_HEADER, session_key);

        getLogoutResponse();

    }

    private void getLogoutResponse() {

        RestClient.getInstance().getApiService().logout(logoutFieldMap, new Callback<LoginData>() {

            @Override
            public void success(LoginData loginData, Response response) {


                Util.dismissProgressDailog();

                if (loginData.getData().getStatus().equals(getString(R.string.response_fail))) {
                    Toast.makeText(getApplicationContext(), getString(R.string.lbl_retrofit_error), Toast.LENGTH_SHORT).show();
                }

                if (loginData.getData().getStatus().equals(getString(R.string.response_success))) {

                    if (mChatService != null) {
                        mChatService.logout();
                    }
                    stopChatService();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();

                    Util.HideKeyBoard(HomeActivity.this);
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    AnimatorClass.appearLeftAnimation(HomeActivity.this);
                    finish();
                }
            }

            @Override
            public void failure(RetrofitError error) {

                Util.dismissProgressDailog();
                Util.showToast(HomeActivity.this, getString(R.string.lbl_retrofit_error));
                Log.e(TAG, "Response from Error:" + error.toString());

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_call:
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + Uri.parse(Constants.JUDE_PHONE_NO)));
                startActivity(intent);
                break;
        }
        return true;

    }

    /*@Override
    public void onBindToChatService(ChatService chatService) {
        super.onBindToChatService(chatService);
    }*/

    @Override
    protected void onDestroy() {

        /*if (mChatService != null) {
            mChatService.logout();
        }*/
        stopChatService();
        Log.e(">>>>>>>>>", "onDestroy");
      /*  if (!ChatActivity.ISVISIBLE) {
            if (mChatService != null) {
                mChatService.logout();
            }
            stopChatService();
        } else {
            unbindFromChatService();
        }*/


        super.onDestroy();
    }
}
