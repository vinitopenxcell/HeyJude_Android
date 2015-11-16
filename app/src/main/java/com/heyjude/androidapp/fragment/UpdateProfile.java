package com.heyjude.androidapp.fragment;

import android.app.ProgressDialog;
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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heyjude.androidapp.R;
import com.heyjude.androidapp.apirequest.Request;
import com.heyjude.androidapp.apirequest.RestClient;
import com.heyjude.androidapp.customview.RoundedImageView;
import com.heyjude.androidapp.dialog.ChangePasswordDialog;
import com.heyjude.androidapp.model.ChangePassword;
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
 * Created by dipen on 3/9/15.
 */
public class UpdateProfile extends Fragment implements View.OnClickListener {

    private EditText etName;
    private Button btnChangePassword, btnUpadteProfile;
    private TextView tvEmail, tvPhone;
    private RoundedImageView ivProfile;
    private CharSequence[] options;
    private File profile_pic;
    private boolean isImageset = false;
    private MultipartTypedOutput multipartTypedOutput;
    private ProgressBar progressBar;

    private SharedPreferences sharedPreferences;
    private LoginData loginData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_update_profile, container, false);

        Gson gson = new Gson();
        sharedPreferences = getActivity().getSharedPreferences(Global.PREF_NAME, Context.MODE_PRIVATE);
        loginData = gson.fromJson(sharedPreferences.getString(Global.PREF_RESPONSE_OBJECT, ""), LoginData.class);

        etName = (EditText) v.findViewById(R.id.etName);
        ivProfile = (RoundedImageView) v.findViewById(R.id.imgSender);
        tvEmail = (TextView) v.findViewById(R.id.tvEmail);
        tvPhone = (TextView) v.findViewById(R.id.tvMobileno);
        btnChangePassword = (Button) v.findViewById(R.id.btnChangePassword);
        btnUpadteProfile = (Button) v.findViewById(R.id.btnUpadteProfile);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        ivProfile.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
        btnUpadteProfile.setOnClickListener(this);

        bindValues();


        return v;
    }

    private void bindValues() {
        etName.setText(loginData.getData().getUserName());
        tvEmail.setText(loginData.getData().getEmail());
        tvPhone.setText(loginData.getData().getPhonenumber());

        if (!loginData.getData().getImageProfile().isEmpty())
            Picasso.with(getActivity()).load(loginData.getData().getImageProfile()).fit().centerCrop().placeholder(R.drawable.ic_no_profile_pic).into(ivProfile);
        else
            Picasso.with(getActivity()).load(R.drawable.ic_no_profile_pic).placeholder(R.drawable.ic_no_profile_pic).error(R.drawable.ic_no_profile_pic).into(ivProfile);

        progressBar.setVisibility(View.GONE);

    }


    private void selectImage() {


        options = new CharSequence[]{getString(R.string.lbl_take_image), getString(R.string.lbl_choose_from_gallery), getString(R.string.lbl_cancel)};


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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
                }/* else if (options[item].equals(getString(R.string.lbl_remove_image))) {
                    user.setImageProfile("");
                    Picasso.with(getActivity()).load(R.drawable.ic_no_profile_pic).placeholder(R.drawable.ic_no_profile_pic).into(ivProfile);
                }*/

            }

        });

        builder.show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.imgSender:
                selectImage();
                break;

            case R.id.btnChangePassword:

                ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(getActivity());
                changePasswordDialog.show();

                /*Window window = changePasswordDialog.getWindow();
                window.setLayout(900, 700);*/

                break;

            case R.id.btnUpadteProfile:

                if (TextUtils.isEmpty(etName.getText().toString())) {
                    Util.showToast(getActivity(), "Please Enter the Name");
                } else {
                    Updateprofile();
                }


                break;

        }

    }

    private void Updateprofile() {

        if (HeyJudeApp.hasNetworkConnection()) {

            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.lbl_loading), false, false);

            multipartTypedOutput = new MultipartTypedOutput();
            multipartTypedOutput.addPart(Request.FIELD_USERID, new TypedString(loginData.getData().getId()));
            multipartTypedOutput.addPart(Request.FIELD_USERNAME, new TypedString(etName.getText().toString()));

            if (isImageset)
                multipartTypedOutput.addPart(Request.FIELD_IMAGE, new TypedFile("image/*", profile_pic));
            else
                multipartTypedOutput.addPart(Request.FIELD_IMAGE, new TypedString(""));

            RestClient.getInstance().getApiService().updateProfile(
                    multipartTypedOutput, new Callback<ChangePassword>() {
                        @Override
                        public void success(ChangePassword changePassword, Response response) {
                            Log.e("Update Profile Success", "");

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if (changePassword.getData().getStatus().equals("Success")) {

                                if (changePassword.getData().getName() != null) {
                                    loginData.getData().setUserName(changePassword.getData().getName());
                                }

                                if (changePassword.getData().getImage() != null) {
                                    loginData.getData().setImageProfile(changePassword.getData().getImage());
                                }

                                Util.showToast(getActivity(), changePassword.getData().getMessage());
                            } else {
                                Util.showToast(getActivity(), changePassword.getData().getMessage());
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("Failure Profile Failure", "");

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Util.showToast(getActivity(), getString(R.string.lbl_retrofit_error));
                        }
                    }
            );

        } else {
            Util.showToast(getActivity(), getString(R.string.alert_connection));
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if (resultCode == getActivity().RESULT_OK) {

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
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),
                            bitmapOptions);

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

                    Picasso.with(getActivity()).load(profile_pic).into(ivProfile);
                    loginData.getData().setImageProfile(profile_pic.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Log.e("Canceled", "");
            }

        } else if (requestCode == 2) {

            if (resultCode == getActivity().RESULT_OK) {

                Uri selectedImage = data.getData();

                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);

                profile_pic = new File(picturePath);
                c.close();
                Picasso.with(getActivity()).load(profile_pic).into(ivProfile);
                loginData.getData().setImageProfile(profile_pic.toString());
                isImageset = true;
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Log.e("Canceled", "");
            }
        }
    }
}
