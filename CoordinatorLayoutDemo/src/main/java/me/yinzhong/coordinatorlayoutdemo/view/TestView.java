package me.yinzhong.coordinatorlayoutdemo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * @author YinZhong
 * @since 2016/12/11
 */
public class TestView extends View {
    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int mTouchSlop = -1;
    private float mDownX;
    private float mDownY;
    private float mLastX;
    private float mLastY;
    private long mCustomedEventTime;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mTouchSlop == -1) {
            mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        }
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = mDownX = ev.getRawX();
                mLastY = mDownY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getRawX();
                final float curY = ev.getRawY();

                boolean custom = false;
                if (mCustomedEventTime != ev.getDownTime()) {
                    if (Math.abs(curY - mDownY) > mTouchSlop || Math.abs(curX - mDownX) > mTouchSlop) {
                        custom = true;
                    }
                } else {
                    custom = true;
                }

                if (custom) {
                    final float deltaX = curX - mLastX;
                    final float deltaY = curY - mLastY;
                    offsetLeftAndRight((int) deltaX);
                    offsetTopAndBottom((int) deltaY);
                }

                mLastX = curX;
                mLastY = curY;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mCustomedEventTime = 0;
                break;
        }
        return true;
    }

}
