package com.heyjude.androidapp.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.heyjude.androidapp.R;
import com.heyjude.androidapp.apirequest.Request;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;


public class PrivacyPolicyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        setToolbar(PrivacyPolicyActivity.this, "Privacy Policy");

        WebView webView = (WebView) findViewById(R.id.wvPrivacyPolicy);
        webView.getSettings().setJavaScriptEnabled(true);

        if (HeyJudeApp.hasNetworkConnection()) {
            webView.loadUrl(Request.PRIVACY_POLICY);
        } else {
            Util.showAlertDialog(PrivacyPolicyActivity.this);
        }
    }

}
