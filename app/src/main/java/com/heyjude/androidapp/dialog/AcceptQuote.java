package com.heyjude.androidapp.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.activity.ChatActivity;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.customview.CustomDialog;
import com.heyjude.androidapp.model.Data;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Constants;
import com.heyjude.androidapp.utility.Global;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dipen on 11/9/15.
 */
public class AcceptQuote extends CustomDialog implements View.OnClickListener {

    private Context mContext;
    private String vendorid;
    private TextView tvYes, tvNo;
    private ImageView ivcancel;
    private String taskid;
    private SharedPreferences sharedPreferences;
    private LoginData loginData;
    private String TAG = "AcceptQuote";
    private String judeId;

    public AcceptQuote(Context context, String vendorID, String taskid, String judeId) {
        super(context);

        mContext = context;
        setContentView(R.layout.dialog_accept_quote);
        this.vendorid = vendorID;
        this.taskid = taskid;
        this.judeId = judeId;

        Gson gson = new Gson();
        sharedPreferences = context.getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);

        init();
    }

    private void init() {
        tvYes = (TextView) findViewById(R.id.tvYes);
        tvNo = (TextView) findViewById(R.id.tvNo);
        ivcancel = (ImageView) findViewById(R.id.ivCancel);

        tvYes.setOnClickListener(this);
        tvNo.setOnClickListener(this);
        ivcancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tvYes:

                /*VendorRatingDialog vendorRating = new VendorRatingDialog(mContext, vendorid, vendorName, taskid);
                vendorRating.show();*/

                acceptQuoteWS("yes");

                dismiss();
                break;
            case R.id.tvNo:
                acceptQuoteWS("no");
                dismiss();
                break;
            case R.id.ivCancel:
                dismiss();
                break;
        }

    }

    private void acceptQuoteWS(final String flag) {

        if (HeyJudeApp.hasNetworkConnection()) {

            RestClient.getInstance().getApiService().acceptQuote(
                    loginData.getData().getId(),
                    vendorid,
                    taskid,
                    flag,
                    new Callback<Data>() {
                        @Override
                        public void success(Data data, Response response) {
                            Log.e(TAG, "SUCCESS");
                            if (flag.equalsIgnoreCase("yes")) {
                                ChatActivity.sendChatMessages(Constants.ACCEPT_QUOTE_YES, "1", false);
                            } else if (flag.equalsIgnoreCase("no")) {
                                ChatActivity.sendChatMessages(Constants.ACCEPT_QUOTE_NO, "1", false);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(TAG, "FAIL");
                            Util.showToast(mContext, mContext.getString(R.string.lbl_retrofit_error));
                        }
                    }
            );

        } else {
            Util.showAlertDialog(mContext);
        }

    }
}