package com.myo.buddy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class CounterFrameLayout extends ImageView {
    private static final String TAG = CounterFrameLayout.class.getSimpleName();
    private float mProgress;

    public CounterFrameLayout(Context context) {
        super(context);
    }

    public CounterFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CounterFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setProgress(float progress) {
        Log.e(TAG, "" + progress);
        mProgress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float offset = (canvas.getHeight()/2) + (canvas.getHeight() * (mProgress - 0.5f));
        canvas.clipRect(0, 0, canvas.getWidth(), offset, Region.Op.DIFFERENCE);
        super.onDraw(canvas);
    }

}
