package com.heyjude.androidapp.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by aalap on 15/5/15.
 */
public class BtnRobotoLight extends Button {

    private Context mContext;
    private static Typeface dosisLight;

    public BtnRobotoLight(Context context) {
        super(context);
        this.mContext = context;
        setFont();
    }

    public BtnRobotoLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setFont();
    }

    private Typeface getDosisTypeface() {
        if (dosisLight == null) {
            dosisLight = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Regular.ttf");
        }

        return dosisLight;
    }

    private void setFont() {
        if (isInEditMode())
            return;

        setTypeface(getDosisTypeface());
    }
}
