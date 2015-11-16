package com.heyjude.androidapp.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by dipen on 29/9/15.
 */
public class EditRobotoRegular extends EditText {

    private Context mContext;
    private static Typeface dosisLight;

    public EditRobotoRegular(Context context) {
        super(context);
        this.mContext = context;
        setFont();
    }

    public EditRobotoRegular(Context context, AttributeSet attrs) {
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

