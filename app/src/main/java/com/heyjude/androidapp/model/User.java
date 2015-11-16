package com.heyjude.androidapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by dipen on 7/7/15.
 */
public class User implements Serializable {

    @SerializedName("ID")
    String id;

    @SerializedName("user_email")
    String email;

    @SerializedName("user_name")
    String userName;

    @SerializedName("profile_image")
    String imageProfile = "";

    @SerializedName("message")
    String message;

    @SerializedName("status")
    String status;

    @SerializedName("phonenumber")
    String phonenumber;


    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getId() {
        return id;

    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
