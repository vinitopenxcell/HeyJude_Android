package com.heyjude.androidapp.animation;

import android.app.Activity;

import com.heyjude.androidapp.R;

/**
 * Created by akash on 22/4/15.
 */
public class AnimatorClass {

    public static void appearLeftAnimation(Activity a) {
        a.overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
    }

    public static void disappearLeftAnimation(Activity a) {
        a.overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

}