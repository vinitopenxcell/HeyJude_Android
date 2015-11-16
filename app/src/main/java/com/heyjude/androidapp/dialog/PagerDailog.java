package com.heyjude.androidapp.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.adapter.PagerIndicatorAdapter;
import com.heyjude.androidapp.customview.CustomDialog;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Global;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by dipen on 1/10/15.
 */
public class PagerDailog extends CustomDialog {

    private String[] content;
    private String[] title;
    private CirclePageIndicator circlePageIndicator;
    private ImageView ivCancel;

    private SharedPreferences sharedPreferences;
    private LoginData loginData;

    public PagerDailog(Context context) {
        super(context);

        setContentView(R.layout.dialog_pager);

        Gson gson = new Gson();
        sharedPreferences = context.getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);

        ivCancel = (ImageView) findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        if (loginData.getData().getUserName().contains(" ")) {
            String[] splited = loginData.getData().getUserName().split(" ");
            title = new String[]{context.getString(R.string.indicator_title1) + " " + splited[0]
                    , context.getString(R.string.indicator_title2)
                    , ""
                    , ""};
        } else {
            title = new String[]{context.getString(R.string.indicator_title1) + loginData.getData().getUserName()
                    , context.getString(R.string.indicator_title2)
                    , ""
                    , ""};
        }

        content = new String[]{
                context.getString(R.string.indicator_content1),
                context.getString(R.string.indicator_content2),
                context.getString(R.string.indicator_content3),
                context.getString(R.string.indicator_content4)};

        PagerIndicatorAdapter adapter = new PagerIndicatorAdapter(context, content, title);
        ViewPager myPager = (ViewPager) findViewById(R.id.myfivepanelpager);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        myPager.setAdapter(adapter);
        circlePageIndicator.setViewPager(myPager);

    }
}
