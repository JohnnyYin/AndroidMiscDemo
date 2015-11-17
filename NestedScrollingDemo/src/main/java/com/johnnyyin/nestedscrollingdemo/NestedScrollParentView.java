package com.johnnyyin.nestedscrollingdemo;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class NestedScrollParentView extends FrameLayout implements NestedScrollingParent {
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private TextView mTips;

    public NestedScrollParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTips = (TextView) findViewById(R.id.tips);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && mTips != null;
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        int targetY = -dyConsumed + mTips.getTop();
        if (targetY > getBottom() || targetY < getBottom() - mTips.getHeight()) {
            return;
        }
        mTips.offsetTopAndBottom(-dyConsumed);
        Log.e("SS", "dyConsumed = " + dyConsumed);
        Log.e("SSS", "" + Log.getStackTraceString(new Throwable()));
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // do nothing
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    // delegate
    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }
}
