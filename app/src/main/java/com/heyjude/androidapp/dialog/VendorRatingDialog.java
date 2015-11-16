package com.heyjude.androidapp.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.activity.HomeActivity;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.customview.CustomDialog;
import com.heyjude.androidapp.model.User;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Global;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dipen on 24/9/15.
 */
public class VendorRatingDialog extends CustomDialog implements View.OnClickListener {

    private Context mContext;
    private String vendorid;
    private EditText etComment;
    private RatingBar ratingBar;
    private Button btn_send, btn_cancel;
    private TextView tvVendorName;
    private String vendorName;

    private String TAG = "VendorRatingDialog";
    private String taskid;

    private SharedPreferences sharedPreferences;
    private LoginData loginData;

    public VendorRatingDialog(Context context, String vendorID, String name, String taskid) {
        super(context);

        mContext = context;
        setContentView(R.layout.dialog_chat_rating_comment);
        this.vendorid = vendorID;
        this.vendorName = name;
        this.taskid = taskid;

        Gson gson = new Gson();
        sharedPreferences = context.getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        init();
    }

    private void init() {

        etComment = (EditText) findViewById(R.id.etVendorComment);
        ratingBar = (RatingBar) findViewById(R.id.ratingVendor);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        tvVendorName = (TextView) findViewById(R.id.tvVendorName);

        tvVendorName.setText(mContext.getString(R.string.lbl_provide_rating_in_vendor) + vendorName);

        btn_send.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_send:


                if (TextUtils.isEmpty(etComment.getText().toString())) {
                    etComment.setError(mContext.getString(R.string.lbl_enter_reviews));

                } else {

                    Util.showProgressDailog(mContext);
                    if (HeyJudeApp.hasNetworkConnection()) {

                        Log.e(TAG, "Comment:" + etComment.getText().toString() + "&Rating:" + String.valueOf(ratingBar.getRating()));

                        RestClient.getInstance().getApiService().addUserReview(
                                taskid,
                                vendorid,
                                loginData.getData().getId(),
                                etComment.getText().toString(),
                                String.valueOf(ratingBar.getRating()), new Callback<User>() {
                                    @Override
                                    public void success(User user, Response response) {
                                        Log.e(TAG, "Success");

                                        Util.dismissProgressDailog();

                                        if (user.getStatus().equals("Success")) {
                                            Util.showToast((Activity) mContext, user.getMessage());

                                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                            builder.setTitle("Thanks");
                                            builder.setMessage("Thanks for providing reviews for Vendor");
                                            builder.setPositiveButton("Ok", new OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    mContext.startActivity(new Intent(mContext, HomeActivity.class));
                                                    dismiss();
                                                }
                                            });
                                            builder.show();
                                        } else {
                                            Util.showToast((Activity) mContext, user.getMessage());
                                            dismiss();
                                        }
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Log.e(TAG, "Fail");

                                        Util.dismissProgressDailog();

                                        Util.showToast((Activity) mContext, mContext.getString(R.string.lbl_retrofit_error));
                                    }
                                }
                        );
                    } else {
                        Util.showAlertDialog(mContext);
                    }
                }

                break;

            case R.id.btn_cancel:
                dismiss();
                break;

        }

    }
}