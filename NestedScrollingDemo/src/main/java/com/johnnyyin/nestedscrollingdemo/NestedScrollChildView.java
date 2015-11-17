package com.johnnyyin.nestedscrollingdemo;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ListView;

public class NestedScrollChildView extends ListView implements NestedScrollingChild {
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private boolean mNeedNestedScroll;
    private float mLastMotionY;
    private float mLast;
    private float mTouchSlop;
    private boolean mStartDispatch;
    private int[] mOffsetInWindow = new int[2];

    public NestedScrollChildView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        super.setNestedScrollingEnabled(false);
        setNestedScrollingEnabled(true);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() != MotionEvent.ACTION_DOWN && !mNeedNestedScroll) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mNeedNestedScroll = startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                mLast = mLastMotionY = ev.getY();
                mStartDispatch = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(mLastMotionY - ev.getY()) <= mTouchSlop) {
                    break;
                }
                int deltaY = (int) (ev.getY() - mLast);
                if (!mStartDispatch) {
                    deltaY = (Math.abs(deltaY) - mTouchSlop) * deltaY > 0 ? 1 : -1;
                    mStartDispatch = true;
                }
                dispatchNestedScrollDelegate(0, deltaY, 0, 0, mOffsetInWindow);
                Log.e("SS", "deltaY = " + deltaY);
                mLast = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                stopNestedScroll();
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return false;
    }

    public boolean dispatchNestedScrollDelegate(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
