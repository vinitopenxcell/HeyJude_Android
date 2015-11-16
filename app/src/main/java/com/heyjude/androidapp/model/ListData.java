package com.heyjude.androidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.heyjude.androidapp.db.DBHelper;

public class ListData {

    @Expose
    private Integer ID;
    @SerializedName("post_title")
    @Expose
    private String postTitle;
    @Expose
    private String image;
    @Expose
    private String distance;

    @Expose
    private String lat;
    @Expose
    private String lng;

    @Expose
    private String contactphone;

    @SerializedName("vendor_id")
    @Expose
    private String vendorId;

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("review_comment")
    @Expose
    private String reviewComment;

    @SerializedName("review_stars")
    @Expose
    private String reviewStars;

    @SerializedName("userProfileImage")
    @Expose
    private String userProfileImage;

    @SerializedName("userProfileName")
    @Expose
    private String userProfileName;

    @SerializedName("userCity")
    @Expose
    private String userCity;

    @SerializedName("task_id")
    @Expose
    private Integer taskId;

    @SerializedName("jude_id")
    @Expose
    private String judeId;

    @SerializedName("task_title")
    @Expose
    private String taskTitle = "";

    @SerializedName("task_stage")
    @Expose
    private String taskStage = "";

    @SerializedName("task_date")
    @Expose
    private String taskDate = "";

    private int readCount;


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

    /**
     * @return The distance
     */
    public String getDistance() {
        return distance;
    }

    /**
     * @param distance The distance
     */
    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getContactphone() {
        return contactphone;
    }

    public void setContactphone(String contactphone) {
        this.contactphone = contactphone;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public String getReviewStars() {
        return reviewStars;
    }

    public void setReviewStars(String reviewStars) {
        this.reviewStars = reviewStars;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getUserProfileName() {
        return userProfileName;
    }

    public void setUserProfileName(String userProfileName) {
        this.userProfileName = userProfileName;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskStage() {
        return taskStage;
    }

    public void setTaskStage(String taskStage) {
        this.taskStage = taskStage;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public String getJudeId() {
        return judeId;
    }

    public void setJudeId(String judeId) {
        this.judeId = judeId;
    }

    public void calculateReadCount(DBHelper dbHelper) {
        this.readCount = Integer.parseInt(dbHelper.getUnReadCount(String.valueOf(getTaskId()), "false"));
    }

}