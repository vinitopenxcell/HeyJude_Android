package com.heyjude.androidapp.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by aalap on 29/5/15.
 */
public class EditOcra extends EditText {

    private Context mContext;
    private static Typeface dosisMedium;

    public EditOcra(Context context) {
        super(context);
        this.mContext = context;

        setFont();
    }

    public EditOcra(Context context, AttributeSet attrs) {
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