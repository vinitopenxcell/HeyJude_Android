package com.heyjude.androidapp.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by dipen on 7/10/15.
 */
public class ButtonOcra extends Button {


    private Context mContext;
    private static Typeface dosisMedium;

    public ButtonOcra(Context context) {
        super(context);
        this.mContext = context;

        setFont();
    }

    public ButtonOcra(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        setFont();
    }


    private Typeface getDosisTypeface() {
        if (dosisMedium == null) {
            dosisMedium = Typeface.createFromAsset(mContext.getAssets(), "OCRATEXT.TTF");
        }

        return dosisMedium;
    }

    private void setFont() {
        if (isInEditMode())
            return;

        setTypeface(getDosisTypeface());
    }
}
