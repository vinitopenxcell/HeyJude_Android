package com.heyjude.androidapp.apirequest;

import com.heyjude.androidapp.model.ChangePassword;
import com.heyjude.androidapp.model.Data;
import com.heyjude.androidapp.model.GetCouponCampaign;
import com.heyjude.androidapp.model.IssueCouponWiGroup;
import com.heyjude.androidapp.model.Restaurant;
import com.heyjude.androidapp.model.RestaurantData;
import com.heyjude.androidapp.model.Task;
import com.heyjude.androidapp.model.User;
import com.heyjude.androidapp.model.VendorReviews;
import com.heyjude.androidapp.model.chatHistory.ChatHistory;
import com.heyjude.androidapp.requestmodel.CouponData;
import com.heyjude.androidapp.requestmodel.Key;
import com.heyjude.androidapp.requestmodel.LoginData;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.mime.MultipartTypedOutput;

/**
 * Created by aalap on 11/5/15.
 */
public interface ApiService {

    @FormUrlEncoded
    @POST(Request.GET_KEY)
    void getSessionKey(@Field(Request.STATIC_KEY) String key, Callback<Key> callback);

    @FormUrlEncoded
    @POST(Request.LOGIN_URL)
    void logIn(@FieldMap HashMap<String, String> loginFieldMap, Callback<LoginData> callback);

    @POST(Request.REGISTRATION_URL)
    void registration(@Body MultipartTypedOutput multipartTypedOutput, Callback<LoginData> callback);

    @FormUrlEncoded
    @POST(Request.FACEBOOK_URL)
    void facebooklLogin(@FieldMap HashMap<String, String> facebookFieldMap, Callback<LoginData> callback);

    @FormUrlEncoded
    @POST(Request.TWITTER_URL)
    void twitterlLogin(@FieldMap HashMap<String, String> twitterFieldMap, Callback<LoginData> callback);

    @FormUrlEncoded
    @POST(Request.LOGOUT_URL)
    void logout(@FieldMap HashMap<String, String> logoutFieldMap, Callback<LoginData> callback);

    @FormUrlEncoded
    @POST(Request.FORGOTPASS_URL)
    void forgotpassword(@Field(Request.FIELD_USER_EMAIL) String email, Callback<LoginData> callback);

    @Headers({
            "Content-Type: application/json"
    })
    @POST(Request.ISSUE_COUPON_URL)
    void wiGroupCouponRequest(@Body IssueCouponWiGroup issueCouponWiGroup,
                              Callback<CouponData> callback);


    @GET(Request.GET_COUPON_CAMPAIGN)
    void getCouponCampaign(@Query(Request.FIELD_LATITUDE) String latitude,
                           @Query(Request.FIELD_LONGITUDE) String longitude,
                           @Query(Request.FIELD_PAGE_OFFSET) String pageOffset,
                           @Query(Request.FIELD_PAGE_SIZE) String pageSize,
                           Callback<GetCouponCampaign> callback);


    @FormUrlEncoded
    @POST(Request.GET_RESTAURANTS)
    void getRestaurants(@Field(Request.FIELD_LAT) String lan,
                        @Field(Request.FIELD_LNG) String lng,
                        @Field(Request.FIELD_PAGED) int paged,
                        Callback<Restaurant> callback);

    @FormUrlEncoded
    @POST(Request.GET_RESTAURANTS_DATA)
    void getRestaurantsData(@Field(Request.FIELD_RESTAURANTID) int id,
                            Callback<RestaurantData> callback);

    @FormUrlEncoded
    @POST(Request.CHANGE_PASSWORD)
    void changePassword(@Field(Request.FIELD_USERID) String user_id,
                        @Field(Request.FIELD_CURRENT_PASSWORD) String currentpw,
                        @Field(Request.FIELD_NEW_PASSWORD) String newpw,
                        Callback<ChangePassword> userCallback);

    @POST(Request.UPDATE_PROFILE)
    void updateProfile(@Body MultipartTypedOutput multipartTypedOutput,
                       Callback<ChangePassword> userCallback);

    @FormUrlEncoded
    @POST(Request.CREATE_TASK)
    void createTask(@Field(Request.FIELD_TITLE) String title,
                    @Field(Request.FIELD_USERID) String user_id,
                    @Field(Request.FIELD_LOCATION) String location,
                    @Field(Request.FIELD_TELESAVE_ID) String telesave_id,
                    @Field(Request.usercountry) String usercountry,
                    @Field(Request.userstate) String userstate,
                    @Field(Request.usercity) String usercity,
                    Callback<ChangePassword> changePasswordCallback);

    @FormUrlEncoded
    @POST(Request.ADD_USER_REVIEW)
    void addUserReview(@Field(Request.taskid) String taskid,
                       @Field(Request.vendorId) String vendorId,
                       @Field(Request.userId) String userId,
                       @Field(Request.reviewComment) String reviewComment,
                       @Field(Request.reviewStars) String reviewStars,
                       Callback<User> userCallback);

    @FormUrlEncoded
    @POST(Request.GET_USER_REVIEW)
    void getUserReview(@Field(Request.vendorId) String vendorId,
                       @Field(Request.page) int page,
                       Callback<VendorReviews> callback);

    @FormUrlEncoded
    @POST(Request.STRIPE_PAYMENT)
    void stripePayment(@Field(Request.token) String token,
                       @Field(Request.current_user) String current_user,
                       @Field(Request.amount) int amount,
                       Callback<User> callback);

    @FormUrlEncoded
    @POST(Request.CURRENT_TASK)
    void getCurrentTask(@Field(Request.userId) String user_id,
                        Callback<Task> callback);

    @FormUrlEncoded
    @POST(Request.COMPLETED_TASK)
    void getCompletedTask(@Field(Request.userId) String user_id,
                          Callback<Task> callback);

    @FormUrlEncoded
    @POST(Request.CHAT_HISTORY_FROM_SERVER)
    void getChatHistory(@Field(Request.userId) String user_id,
                        @Field(Request.taskId) String taskId,
                        @Field(Request.judeId) String judeId,
                        Callback<ChatHistory> callback);

    @FormUrlEncoded
    @POST(Request.ACCEPT_QUOTE)
    void acceptQuote(@Field(Request.userid) String userid,
                     @Field(Request.vendor_id) String vendor_id,
                     @Field(Request.taskid) String taskid,
                     @Field(Request.flag) String flag,
                     Callback<Data> changePasswordCallback);

}