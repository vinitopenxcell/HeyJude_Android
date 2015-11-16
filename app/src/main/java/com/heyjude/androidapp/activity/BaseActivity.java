package com.heyjude.androidapp.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.heyjude.androidapp.R;
import com.heyjude.androidapp.animation.AnimatorClass;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.xmpp.ChatService;
import com.instabug.wrapper.support.activity.InstabugAppCompatActivity;

/**
 * Created by aalap on 19/6/15.
 */
public abstract class BaseActivity extends InstabugAppCompatActivity {


    private static final String TAG = "heyJude";

    protected boolean isBound;
    protected ChatService mChatService;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }


    protected void setToolbar(final Activity activity, String name) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView toolbarTtitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTtitle.setText(name);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                AnimatorClass.disappearLeftAnimation(activity);
            }
        });
    }

    @Override
    public HeyJudeApp getApplicationContext() {
        return (HeyJudeApp) super.getApplicationContext();
    }

    protected void setTitle(Activity context, String title) {
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(title);

    }

    protected void startActivityWithAnim(Intent intent) {
        super.startActivity(intent);
        AnimatorClass.appearLeftAnimation(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimatorClass.disappearLeftAnimation(this);
    }

    //ziya
    public void startAndBindToChatService() {
        startChatService();
        if (!isBound) {
            bindToChatService();
        }
    }

    private void startChatService() {
        Log.e(TAG, "startChatService");
        Intent startServiceIntent = new Intent(this, ChatService.class);
        startService(startServiceIntent);
    }

    private void bindToChatService() {
        Intent bindIntent = new Intent(this, ChatService.class);
        bindService(bindIntent, mServiceConnection, BIND_AUTO_CREATE);
    }


    public void stopChatService() {
        Log.i(TAG, "stopChatService ");
        if (isBound) {
            unbindFromChatService(); // unbind service before stopping
        }
        Intent stopServiceIntent = new Intent(this, ChatService.class);
        stopService(stopServiceIntent);
    }

    public void unbindFromChatService() {
        isBound = false;
        onUnbindFromChatService();
        Log.i(TAG, "unbindFromChatService");
        unbindService(mServiceConnection);
    }

    /*
      Service connection object for binding the service
       */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected");
            isBound = true;
            mChatService = ((ChatService.ChatBinder) service).getService();
            onBindToChatService(mChatService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("BaseCoreActivity", "onServiceDisconnected");
            isBound = false;
            mChatService = null;
        }
    };

    public void onBindToChatService(ChatService chatService) {

    }

    public void onUnbindFromChatService() {

    }


}
