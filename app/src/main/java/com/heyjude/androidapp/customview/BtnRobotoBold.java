package com.heyjude.androidapp.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by aalap on 15/5/15.
 */
public class BtnRobotoBold extends Button {

    private Context mContext;
    private static Typeface dosisMedium;

    public BtnRobotoBold(Context context) {
        super(context);
        this.mContext = context;

        setFont();
    }

    public BtnRobotoBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        setFont();
    }


    private Typeface getDosisTypeface() {
        if (dosisMedium == null) {
            dosisMedium = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Bold.ttf");
        }

        return dosisMedium;
    }

    private void setFont() {
        if (isInEditMode())
            return;

        setTypeface(getDosisTypeface());
    }
}
