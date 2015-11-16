package com.heyjude.androidapp.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class CouponCampaign {

    @Expose
    private Integer totalViewed;
    @Expose
    private String issueFromDate;
    @Expose
    private String issueToDate;
    @Expose
    private String redeemFromDate;
    @Expose
    private String redeemToDate;
    @Expose
    private Integer minBasketValue;
    @Expose
    private Integer maxBasketValue;
    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private String termsAndConditions;
    @Expose
    private String imageUrl;
    @Expose
    private String createDate;
    @Expose
    private Boolean requireUserRef;
    @Expose
    private Boolean allowedUsersRestricted;
    @Expose
    private Integer maxNumberPerUser;
    @Expose
    private Integer maxLivePerUser;
    @Expose
    private String campaignType;
    @Expose
    private Integer minRank;
    @Expose
    private List<Category> categories = new ArrayList<Category>();
    @Expose
    private Float distance;
    @Expose
    private Integer maxAllowedToIssue;
    @Expose
    private Integer maxAllowedToIssueDaily;
    @Expose
    private Integer maxRedemptionRuleAmount;
    @Expose
    private Integer totalLive;
    @Expose
    private Integer totalRedeemed;
    @Expose
    private Integer totalExpired;
    @Expose
    private Integer totalIssued;
    @Expose
    private Integer totalIssuedToday;
    @Expose
    private List<CampaignInfo> campaignInfo = new ArrayList<CampaignInfo>();

    /**
     * @return The totalViewed
     */
    public Integer getTotalViewed() {
        return totalViewed;
    }

    /**
     * @param totalViewed The totalViewed
     */
    public void setTotalViewed(Integer totalViewed) {
        this.totalViewed = totalViewed;
    }

    /**
     * @return The issueFromDate
     */
    public String getIssueFromDate() {
        return issueFromDate;
    }

    /**
     * @param issueFromDate The issueFromDate
     */
    public void setIssueFromDate(String issueFromDate) {
        this.issueFromDate = issueFromDate;
    }

    /**
     * @return The issueToDate
     */
    public String getIssueToDate() {
        return issueToDate;
    }

    /**
     * @param issueToDate The issueToDate
     */
    public void setIssueToDate(String issueToDate) {
        this.issueToDate = issueToDate;
    }

    /**
     * @return The redeemFromDate
     */
    public String getRedeemFromDate() {
        return redeemFromDate;
    }

    /**
     * @param redeemFromDate The redeemFromDate
     */
    public void setRedeemFromDate(String redeemFromDate) {
        this.redeemFromDate = redeemFromDate;
    }

    /**
     * @return The redeemToDate
     */
    public String getRedeemToDate() {
        return redeemToDate;
    }

    /**
     * @param redeemToDate The redeemToDate
     */
    public void setRedeemToDate(String redeemToDate) {
        this.redeemToDate = redeemToDate;
    }

    /**
     * @return The minBasketValue
     */
    public Integer getMinBasketValue() {
        return minBasketValue;
    }

    /**
     * @param minBasketValue The minBasketValue
     */
    public void setMinBasketValue(Integer minBasketValue) {
        this.minBasketValue = minBasketValue;
    }

    /**
     * @return The maxBasketValue
     */
    public Integer getMaxBasketValue() {
        return maxBasketValue;
    }

    /**
     * @param maxBasketValue The maxBasketValue
     */
    public void setMaxBasketValue(Integer maxBasketValue) {
        this.maxBasketValue = maxBasketValue;
    }

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The termsAndConditions
     */
    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    /**
     * @param termsAndConditions The termsAndConditions
     */
    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    /**
     * @return The imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * @param imageUrl The imageUrl
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * @return The createDate
     */
    public String getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate The createDate
     */
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    /**
     * @return The requireUserRef
     */
    public Boolean getRequireUserRef() {
        return requireUserRef;
    }

    /**
     * @param requireUserRef The requireUserRef
     */
    public void setRequireUserRef(Boolean requireUserRef) {
        this.requireUserRef = requireUserRef;
    }

    /**
     * @return The allowedUsersRestricted
     */
    public Boolean getAllowedUsersRestricted() {
        return allowedUsersRestricted;
    }

    /**
     * @param allowedUsersRestricted The allowedUsersRestricted
     */
    public void setAllowedUsersRestricted(Boolean allowedUsersRestricted) {
        this.allowedUsersRestricted = allowedUsersRestricted;
    }

    /**
     * @return The maxNumberPerUser
     */
    public Integer getMaxNumberPerUser() {
        return maxNumberPerUser;
    }

    /**
     * @param maxNumberPerUser The maxNumberPerUser
     */
    public void setMaxNumberPerUser(Integer maxNumberPerUser) {
        this.maxNumberPerUser = maxNumberPerUser;
    }

    /**
     * @return The maxLivePerUser
     */
    public Integer getMaxLivePerUser() {
        return maxLivePerUser;
    }

    /**
     * @param maxLivePerUser The maxLivePerUser
     */
    public void setMaxLivePerUser(Integer maxLivePerUser) {
        this.maxLivePerUser = maxLivePerUser;
    }

    /**
     * @return The campaignType
     */
    public String getCampaignType() {
        return campaignType;
    }

    /**
     * @param campaignType The campaignType
     */
    public void setCampaignType(String campaignType) {
        this.campaignType = campaignType;
    }

    /**
     * @return The minRank
     */
    public Integer getMinRank() {
        return minRank;
    }

    /**
     * @param minRank The minRank
     */
    public void setMinRank(Integer minRank) {
        this.minRank = minRank;
    }

    /**
     * @return The categories
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * @param categories The categories
     */
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    /**
     * @return The distance
     */
    public Float getDistance() {
        return distance;
    }

    /**
     * @param distance The distance
     */
    public void setDistance(Float distance) {
        this.distance = distance;
    }

    /**
     * @return The maxAllowedToIssue
     */
    public Integer getMaxAllowedToIssue() {
        return maxAllowedToIssue;
    }

    /**
     * @param maxAllowedToIssue The maxAllowedToIssue
     */
    public void setMaxAllowedToIssue(Integer maxAllowedToIssue) {
        this.maxAllowedToIssue = maxAllowedToIssue;
    }

    /**
     * @return The maxAllowedToIssueDaily
     */
    public Integer getMaxAllowedToIssueDaily() {
        return maxAllowedToIssueDaily;
    }

    /**
     * @param maxAllowedToIssueDaily The maxAllowedToIssueDaily
     */
    public void setMaxAllowedToIssueDaily(Integer maxAllowedToIssueDaily) {
        this.maxAllowedToIssueDaily = maxAllowedToIssueDaily;
    }

    /**
     * @return The maxRedemptionRuleAmount
     */
    public Integer getMaxRedemptionRuleAmount() {
        return maxRedemptionRuleAmount;
    }

    /**
     * @param maxRedemptionRuleAmount The maxRedemptionRuleAmount
     */
    public void setMaxRedemptionRuleAmount(Integer maxRedemptionRuleAmount) {
        this.maxRedemptionRuleAmount = maxRedemptionRuleAmount;
    }

    /**
     * @return The totalLive
     */
    public Integer getTotalLive() {
        return totalLive;
    }

    /**
     * @param totalLive The totalLive
     */
    public void setTotalLive(Integer totalLive) {
        this.totalLive = totalLive;
    }

    /**
     * @return The totalRedeemed
     */
    public Integer getTotalRedeemed() {
        return totalRedeemed;
    }

    /**
     * @param totalRedeemed The totalRedeemed
     */
    public void setTotalRedeemed(Integer totalRedeemed) {
        this.totalRedeemed = totalRedeemed;
    }

    /**
     * @return The totalExpired
     */
    public Integer getTotalExpired() {
        return totalExpired;
    }

    /**
     * @param totalExpired The totalExpired
     */
    public void setTotalExpired(Integer totalExpired) {
        this.totalExpired = totalExpired;
    }

    /**
     * @return The totalIssued
     */
    public Integer getTotalIssued() {
        return totalIssued;
    }

    /**
     * @param totalIssued The totalIssued
     */
    public void setTotalIssued(Integer totalIssued) {
        this.totalIssued = totalIssued;
    }

    /**
     * @return The totalIssuedToday
     */
    public Integer getTotalIssuedToday() {
        return totalIssuedToday;
    }

    /**
     * @param totalIssuedToday The totalIssuedToday
     */
    public void setTotalIssuedToday(Integer totalIssuedToday) {
        this.totalIssuedToday = totalIssuedToday;
    }

    /**
     * @return The campaignInfo
     */
    public List<CampaignInfo> getCampaignInfo() {
        return campaignInfo;
    }

    /**
     * @param campaignInfo The campaignInfo
     */
    public void setCampaignInfo(List<CampaignInfo> campaignInfo) {
        this.campaignInfo = campaignInfo;
    }

}