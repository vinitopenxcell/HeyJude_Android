package com.heyjude.androidapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by dipen on 13/7/15.
 */
public class Coupon implements Serializable {

    @SerializedName("id")
    int id;

    @SerializedName("userRef")
    String userRef;

    @SerializedName("campaignId")
    int campaignId;

    @SerializedName("campaignName")
    String campaignName;

    @SerializedName("campaignType")
    String campaignType;


    @SerializedName("termsAndConditions")
    String termsAndConditions;

    @SerializedName("createDate")
    String createDate;

    @SerializedName("description")
    String description;

    @SerializedName("imageURL")
    String imageURL;

    @SerializedName("redeemFromDate")
    String redeemFromDate;

    @SerializedName("redeemToDate")
    String redeemToDate;

    @SerializedName("wiCode")
    String wiCode;

    @SerializedName("voucherAmount")
    int voucherAmount;

    @SerializedName("wiQr")
    String wiQR;



    public int getId() {
        return id;
    }

    public String getUserRef() {
        return userRef;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public String getCampaignType() {
        return campaignType;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getRedeemFromDate() {
        return redeemFromDate;
    }

    public String getRedeemToDate() {
        return redeemToDate;
    }

    public String getWiCode() {
        return wiCode;
    }

    public int getVoucherAmount() {
        return voucherAmount;
    }

    public String getWiQR() {
        return wiQR;
    }
}
