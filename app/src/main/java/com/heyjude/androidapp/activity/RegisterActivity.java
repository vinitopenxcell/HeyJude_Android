package com.heyjude.androidapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.animation.AnimatorClass;
import com.heyjude.androidapp.apirequest.Request;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.customview.RoundedImageView;
import com.heyjude.androidapp.requestmodel.Key;
import com.heyjude.androidapp.requestmodel.LoginData;
import com.heyjude.androidapp.utility.Global;
import com.heyjude.androidapp.utility.HeyJudeApp;
import com.heyjude.androidapp.utility.Util;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * Created by aalap on 15/5/15.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "RegisterActivity";
    private Button btnRegister;
    private EditText etName, etEmail, etMobileno, etPass, etConfPass;
    private TextView txtPrivacyPolicy, txtTermsOfUse;
    private RoundedImageView imgSender;

    private File profile_pic;
    private MultipartTypedOutput multipartTypedOutput;

    //Shared Preferences
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean isImageset = false;

    private String email, password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        sharedPreferences = getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        init();

        //Get Current Longitude and Latitude
        Util.getCurrentLocation(RegisterActivity.this);

        btnRegister.setOnClickListener(this);
        txtPrivacyPolicy.setOnClickListener(this);
        txtTermsOfUse.setOnClickListener(this);
        imgSender.setOnClickListener(this);
    }

    public void init() {

        //Getting values form the User
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etMobileno = (EditText) findViewById(R.id.etMobileno);
        etPass = (EditText) findViewById(R.id.etPass);
        etConfPass = (EditText) findViewById(R.id.etConfPass);

        imgSender = (RoundedImageView) findViewById(R.id.imgSender);

        //Register Button
        btnRegister = (Button) findViewById(R.id.btnRegister);

        //Privacy Setting and Terms & Condition
        txtPrivacyPolicy = (TextView) findViewById(R.id.txtPrivacyPolicy);
        txtTermsOfUse = (TextView) findViewById(R.id.txtTermsOfUse);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.txtPrivacyPolicy:

                Util.HideKeyBoard(RegisterActivity.this);
                startActivity(new Intent(RegisterActivity.this, PrivacyPolicyActivity.class));
                AnimatorClass.appearLeftAnimation(RegisterActivity.this);

                break;

            case R.id.txtTermsOfUse:

                Util.HideKeyBoard(RegisterActivity.this);
                startActivity(new Intent(RegisterActivity.this, TermsOfUseActivity.class));
                AnimatorClass.appearLeftAnimation(RegisterActivity.this);

                break;

            case R.id.btnRegister:

                String name = etName.getText().toString();
                email = etEmail.getText().toString();
                String phonenumber = etMobileno.getText().toString();
                password = etPass.getText().toString();
                String conPass = etConfPass.getText().toString();

                if (isValidData(name, email, phonenumber, password, conPass)) {


                    Util.showProgressDailog(RegisterActivity.this);

                    //Make multiTypedOutput
                    multipartTypedOutput = new MultipartTypedOutput();
                    multipartTypedOutput.addPart(Request.FIELD_USERNAME, new TypedString(name));
                    multipartTypedOutput.addPart(Request.FIELD_EMAIL, new TypedString(String.valueOf(email)));
                    multipartTypedOutput.addPart(Request.FIELD_PWD, new TypedString(password));

                    if (isImageset)
                        multipartTypedOutput.addPart(Request.FIELD_IMAGE, new TypedFile("image/*", profile_pic));
                    else
                        multipartTypedOutput.addPart(Request.FIELD_IMAGE, new TypedString(""));

                    multipartTypedOutput.addPart(Request.FIELD_MOBILE, new TypedString(phonenumber));
                    multipartTypedOutput.addPart(Request.FIELD_PLATFORM, new TypedString("Android"));
                    multipartTypedOutput.addPart(Request.FIELD_DEVICE_TOKEN, new TypedString(sharedPreferences.getString(Global.PREF_DEVICE_TOKEN, "")));
                    multipartTypedOutput.addPart(Request.FIELD_LAT, new TypedString(Util.latitude));
                    multipartTypedOutput.addPart(Request.FIELD_LON, new TypedString(Util.longitude));


                    if (sharedPreferences.getString(Global.SESSION_KEY, null) != null) {
                        getRegistrationResponse();
                    } else {
                        getSessionKey();
                    }
                }
                break;

            case R.id.imgSender:
                selectImage();
                break;

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        AnimatorClass.disappearLeftAnimation(this);
    }

    private void selectImage() {


        final CharSequence[] options = {getString(R.string.lbl_take_image), getString(R.string.lbl_choose_from_gallery), getString(R.string.lbl_cancel)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

        builder.setTitle(getString(R.string.lbl_add_image));

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(getString(R.string.lbl_take_image))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);

                } else if (options[item].equals(getString(R.string.lbl_choose_from_gallery))) {

                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (options[item].equals(getString(R.string.lbl_cancel))) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {

                File file = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : file.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        file = temp;
                        break;
                    }
                }
                try {

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmapOptions.inSampleSize = 4;
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bitmapOptions);

                    isImageset = true;

                    FileOutputStream outFile = null;

                    profile_pic = new File(Environment.getExternalStorageDirectory(), String.valueOf(System.currentTimeMillis()) + ".jpg");

                    try {
                        outFile = new FileOutputStream(profile_pic);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outFile);
                        outFile.flush();
                        outFile.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Picasso.with(getApplicationContext()).load(profile_pic).into(imgSender);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("Canceled", "");
            }

        } else if (requestCode == 2) {

            if (resultCode == RESULT_OK) {

                Uri selectedImage = data.getData();

                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);

                profile_pic = new File(picturePath);
                c.close();
                Picasso.with(getApplicationContext()).load(profile_pic).into(imgSender);
                isImageset = true;
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("Canceled", "");
            }
        }

    }

    private boolean isValidData(String name, String email, String mobile, String pass, String conPass) {
        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            etName.setError(getString(R.string.alert_enter_name));
            isValid = false;
        } else if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.alert_enter_email));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.alert_enter_valid_email));
            isValid = false;
        } else if (mobile.length() != 10) {
            etMobileno.setError(getString(R.string.alert_enter_mobileNo
            ));
            isValid = false;
        } else if (TextUtils.isEmpty(pass)) {
            etPass.setError(getString(R.string.alert_enter_password));
            isValid = false;
        } else if (!TextUtils.equals(pass, conPass)) {
            etConfPass.setError(getString(R.string.alert_pass_match));
            isValid = false;
        }
        return isValid;
    }

    public void getSessionKey() {

        if (HeyJudeApp.getInstance().hasNetworkConnection()) {
            RestClient.getInstance().getApiService().getSessionKey(Request.KEY, new Callback<Key>() {

                @Override
                public void success(Key key, Response response) {

                    if (key.getStatus().equalsIgnoreCase(Request.SUCCESS)) {
                        HeyJudeApp.getInstance().setSessionKey(key.getToken());
                        getRegistrationResponse();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Util.showToast(RegisterActivity.this, getString(R.string.lbl_retrofit_error));
                    Log.e(TAG, error.getMessage().toString());
                    Util.dismissProgressDailog();
                }
            });
        }
    }

    public void getRegistrationResponse() {

        if (HeyJudeApp.getInstance().hasNetworkConnection()) {
            RestClient.getInstance().getApiService().registration(multipartTypedOutput, new Callback<LoginData>() {
                @Override
                public void success(LoginData loginData, Response response) {

                    Util.dismissProgressDailog();

                    if (loginData.getData().getStatus().equals(getString(R.string.response_success))) {

                        editor.putBoolean(Global.PREF_HAS_LOGGED_IN, true);
                        Gson gson = new Gson();
                        String json = gson.toJson(loginData);
                        editor.putString(Global.PREF_RESPONSE_OBJECT, json);


                        Util.HideKeyBoard(RegisterActivity.this);
                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        intent.putExtra(Global.PREF_LOGIN_DATA_OBJECT, loginData);
                        editor.putString(Global.PREF_LOGIN_WITH, getString(R.string.lbl_application));
                        editor.putInt(Global.PREF_LOGIN_COUNTER, 1);
                        editor.commit();
                        startActivity(intent);
                        AnimatorClass.appearLeftAnimation(RegisterActivity.this);
                        finish();
                    }

                    if (loginData.getData().getStatus().equals(getString(R.string.response_fail))) {

                        Util.showToast(RegisterActivity.this, getResources().getString(R.string.alert_email_already_in_use));
                        etEmail.setText("");
                        etEmail.requestFocus();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                    Util.dismissProgressDailog();
                    Util.showToast(RegisterActivity.this, getString(R.string.lbl_retrofit_error));
                    Log.e(TAG, error.getMessage().toString());
                }
            });
        } else {
            Util.showAlertDialog(RegisterActivity.this);
            Util.dismissProgressDailog();
        }
    }
}