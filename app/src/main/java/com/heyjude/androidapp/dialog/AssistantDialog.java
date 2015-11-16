package com.heyjude.androidapp.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.heyjude.androidapp.R;
import com.heyjude.androidapp.customview.CustomDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by dipen on 27/6/15.
 */
public class AssistantDialog extends CustomDialog {

    private Button btn_reedem;
    private ImageView iv_logo;
    private ProgressBar progressBar;
    private TextView tv_name, tv_description;
    private Context mContext;
    private String name, description, imageURL = "";

    public AssistantDialog(Context context, String name, String description, String imageURL) {
        super(context);

        mContext = context;
        setContentView(R.layout.dialog_assistant);
        this.name = name;
        this.description = description;
        this.imageURL = imageURL;

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        init();
    }

    private void init() {
        btn_reedem = (Button) findViewById(R.id.btn_assist_reedem);
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_name = (TextView) findViewById(R.id.tv_name);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        bindValues();
    }

    private void bindValues() {
        tv_description.setText(description);
        tv_name.setText(name);

        Picasso.with(mContext).load(imageURL).into(iv_logo, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    public void ButtonClickListner(View.OnClickListener reedemClickListener) {
        btn_reedem.setOnClickListener(reedemClickListener);
    }
}
