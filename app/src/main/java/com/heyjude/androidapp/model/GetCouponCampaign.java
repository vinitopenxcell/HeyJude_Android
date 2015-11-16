package com.heyjude.androidapp.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class GetCouponCampaign {

    @Expose
    private List<CouponCampaign> couponCampaigns = new ArrayList<CouponCampaign>();
    @Expose
    private Paging paging;
    @Expose
    private String responseCode;
    @Expose
    private String responseDesc;

    /**
     *
     * @return
     * The couponCampaigns
     */
    public List<CouponCampaign> getCouponCampaigns() {
        return couponCampaigns;
    }

    /**
     *
     * @param couponCampaigns
     * The couponCampaigns
     */
    public void setCouponCampaigns(List<CouponCampaign> couponCampaigns) {
        this.couponCampaigns = couponCampaigns;
    }

    /**
     *
     * @return
     * The paging
     */
    public Paging getPaging() {
        return paging;
    }

    /**
     *
     * @param paging
     * The paging
     */
    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    /**
     *
     * @return
     * The responseCode
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     *
     * @param responseCode
     * The responseCode
     */
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    /**
     *
     * @return
     * The responseDesc
     */
    public String getResponseDesc() {
        return responseDesc;
    }

    /**
     *
     * @param responseDesc
     * The responseDesc
     */
    public void setResponseDesc(String responseDesc) {
        this.responseDesc = responseDesc;
    }

}