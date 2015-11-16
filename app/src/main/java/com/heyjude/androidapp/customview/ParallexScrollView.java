package com.heyjude.androidapp.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by akash on 13/8/15.
 */
public class ParallexScrollView extends ScrollView {


    public interface OnScrollChangedListener {
        public void onScrollChanged(int deltaX, int deltaY);
    }

    private OnScrollChangedListener mOnScrollChangedListener;

    public ParallexScrollView(Context context) {
        super(context);
    }

    public ParallexScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParallexScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(l - oldl, t - oldt);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }
}
