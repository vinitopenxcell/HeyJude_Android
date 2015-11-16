package com.heyjude.androidapp.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.heyjude.androidapp.R;
import com.heyjude.androidapp.apirequest.Request;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;


public class TermsOfUseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);

        setToolbar(TermsOfUseActivity.this, "Terms And Condition");

        WebView webView = (WebView) findViewById(R.id.wvTermsAndCondition);
        webView.getSettings().setJavaScriptEnabled(true);

        if (HeyJudeApp.hasNetworkConnection()) {
            webView.loadUrl(Request.TERMS_AND_CONDITION);
        } else {
            Util.showAlertDialog(TermsOfUseActivity.this);
        }
    }
}
