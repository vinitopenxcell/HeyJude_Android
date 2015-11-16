package com.heyjude.androidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @Expose
    private Integer ID;
    @SerializedName("post_title")
    @Expose
    private String postTitle;
    @SerializedName("post_content")
    @Expose
    private String postContent;
    @SerializedName("trending_items")
    @Expose
    private String trendingItems;
    @SerializedName("wpcf_exclusions")
    @Expose
    private String wpcfExclusions;
    @Expose
    private String contactaddress;
    @Expose
    private String contactphone;
    @Expose
    private String contactemail;
    @SerializedName("offer_valid")
    @Expose
    private String offerValid;
    @Expose
    private String lat;
    @Expose
    private String lng;
    @Expose
    private String image;


    @Expose
    private String name;

    @Expose
    private String status;
    @Expose
    private String message;

    @SerializedName("request_id")
    @Expose
    private String requestId;

    /**
     * @return The ID
     */
    public Integer getID() {
        return ID;
    }

    /**
     * @param ID The ID
     */
    public void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     * @return The postTitle
     */
    public String getPostTitle() {
        return postTitle;
    }

    /**
     * @param postTitle The post_title
     */
    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    /**
     * @return The postContent
     */
    public String getPostContent() {
        return postContent;
    }

    /**
     * @param postContent The post_content
     */
    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    /**
     * @return The trendingItems
     */
    public String getTrendingItems() {
        return trendingItems;
    }

    /**
     * @param trendingItems The trending_items
     */
    public void setTrendingItems(String trendingItems) {
        this.trendingItems = trendingItems;
    }

    /**
     * @return The wpcfExclusions
     */
    public String getWpcfExclusions() {
        return wpcfExclusions;
    }

    /**
     * @param wpcfExclusions The wpcf_exclusions
     */
    public void setWpcfExclusions(String wpcfExclusions) {
        this.wpcfExclusions = wpcfExclusions;
    }

    /**
     * @return The contactaddress
     */
    public String getContactaddress() {
        return contactaddress;
    }

    /**
     * @param contactaddress The contactaddress
     */
    public void setContactaddress(String contactaddress) {
        this.contactaddress = contactaddress;
    }

    /**
     * @return The contactphone
     */
    public String getContactphone() {
        return contactphone;
    }

    /**
     * @param contactphone The contactphone
     */
    public void setContactphone(String contactphone) {
        this.contactphone = contactphone;
    }

    /**
     * @return The contactemail
     */
    public String getContactemail() {
        return contactemail;
    }

    /**
     * @param contactemail The contactemail
     */
    public void setContactemail(String contactemail) {
        this.contactemail = contactemail;
    }

    /**
     * @return The offerValid
     */
    public String getOfferValid() {
        return offerValid;
    }

    /**
     * @param offerValid The offer_valid
     */
    public void setOfferValid(String offerValid) {
        this.offerValid = offerValid;
    }

    /**
     * @return The lat
     */
    public String getLat() {
        return lat;
    }

    /**
     * @param lat The lat
     */
    public void setLat(String lat) {
        this.lat = lat;
    }

    /**
     * @return The lng
     */
    public String getLng() {
        return lng;
    }

    /**
     * @param lng The lng
     */
    public void setLng(String lng) {
        this.lng = lng;
    }

    /**
     * @return The image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image The image
     */
    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}