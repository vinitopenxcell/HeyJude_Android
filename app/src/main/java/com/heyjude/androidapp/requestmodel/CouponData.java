package com.heyjude.androidapp.requestmodel;

import com.google.gson.annotations.SerializedName;
import com.heyjude.androidapp.model.Coupon;

import java.io.Serializable;

/**
 * Created by dipen on 13/7/15.
 */
public class CouponData implements Serializable {

    Coupon coupon;

    @SerializedName("responseCode")
    String responseCode;

    @SerializedName("responseDesc")
    String responseDesc;

    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseDesc() {
        return responseDesc;
    }

    public Coupon getVouchers() {
        return coupon;

    }
}
