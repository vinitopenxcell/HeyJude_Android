package com.heyjude.androidapp.model;

/**
 * Created by dipen on 17/7/15.
 */
public class IssueCouponWiGroup {

    final String campaignId;
    final String userRef;

    //Just fro checking that Whether Retrofit with WiGroup API is Wrking or not?
    // And After running that yea its working properly

    public IssueCouponWiGroup(String campaignId, String userRef) {

        this.campaignId = campaignId;
        this.userRef = userRef;
    }
}
