package com.heyjude.androidapp.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.customview.CustomDialog;
import com.heyjude.androidapp.model.ChangePassword;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Global;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dipen on 27/6/15.
 */
public class ChangePasswordDialog extends CustomDialog implements View.OnClickListener {

    private EditText etOldPassword, etNewPassord, etConfirmPassword;
    private TextView tvOk, tvCancel;
    private Context mContext;

    private SharedPreferences sharedPreferences;
    private LoginData loginData;
    private String TAG = "ChangePasswordDialog";

    public ChangePasswordDialog(Context context) {

        super(context);

        setTitle("Change Password");
        this.mContext = context;
        setContentView(R.layout.dialog_change_password);

        Gson gson = new Gson();
        sharedPreferences = context.getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);

        init();
    }


    private void init() {

        etOldPassword = (EditText) findViewById(R.id.etOldPassword);
        etNewPassord = (EditText) findViewById(R.id.etNewPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        tvOk = (TextView) findViewById(R.id.tvOk);
        tvCancel = (TextView) findViewById(R.id.tvCancel);

        tvOk.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tvOk:

                if (TextUtils.isEmpty(etOldPassword.getText()))
                    etOldPassword.setError(mContext.getString(R.string.alert_enter_old_password));
                else if (TextUtils.isEmpty(etNewPassord.getText()))
                    etNewPassord.setError(mContext.getString(R.string.alert_enter_new_password));
                else if (TextUtils.isEmpty(etConfirmPassword.getText()))
                    etConfirmPassword.setError(mContext.getString(R.string.alert_confirm_new_password));
                else if (!etConfirmPassword.getText().toString().equals(etNewPassord.getText().toString())) {
                    etConfirmPassword.setError(mContext.getString(R.string.alert_not_matches_with_new_password));
                    etConfirmPassword.setText("");
                    etConfirmPassword.requestFocus();
                } else {

                    if (HeyJudeApp.hasNetworkConnection()) {

                        Util.showProgressDailog(mContext);

                        //Call the webservice
                        RestClient.getInstance().getApiService().changePassword(
                                loginData.getData().getId(),
                                etOldPassword.getText().toString(),
                                etNewPassord.getText().toString(),
                                new Callback<ChangePassword>() {
                                    @Override
                                    public void success(ChangePassword changePassword, Response response) {

                                        Util.dismissProgressDailog();
                                        Log.e(TAG, "SUCCESS");

                                        if (changePassword.getData().getStatus().equalsIgnoreCase("Success")) {
                                            Util.showToast(mContext, changePassword.getData().getMessage());
                                            dismiss();
                                        } else
                                            Util.showToast(mContext, changePassword.getData().getMessage());

                                    }

                                    @Override
                                    public void failure(RetrofitError error) {

                                        Util.dismissProgressDailog();
                                        Log.e(TAG, "FAIL");
                                        Util.showToast(mContext, mContext.getString(R.string.lbl_retrofit_error));

                                    }
                                }
                        );
                    } else {
                        Util.showAlertDialog(mContext);
                    }
                }
                break;

            case R.id.tvCancel:
                dismiss();
                break;
        }
    }
}
