package com.johnnyyin.floatwindowdemo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public abstract class WindowBase {
    public Context mContext;
    private WindowManager mWindowManager;
    private LayoutParams mLayoutParams;
    public View mView;
    private boolean mShow;
    private long mLastShowTime;
    private final int SHOW_INTERVAL = 20;

    public WindowBase() {
        mContext = BaseApplication.getInst();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = initLayoutParams();
        if (mLayoutParams == null)
            throw new NullPointerException("initLayoutParams() can't return null");
        mLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    public LayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    /**
     * Return a {@link LayoutParams} indicate the way for
     * {@link WindowManager} to layout the special view
     */
    public abstract LayoutParams initLayoutParams();

    public boolean isShow() {
        return mShow;
    }

    private boolean checkInterval() {
        long current = System.currentTimeMillis();
        if (current - mLastShowTime < SHOW_INTERVAL) {
            return true;
        } else {
            mLastShowTime = current;
            return false;
        }
    }

    /**
     * Show a view added by {@link WindowManager}. If {@link LayoutParams#type}
     * is not equal {@link LayoutParams#TYPE_SYSTEM_ALERT}, then
     * the windowToken shouldn't be null.
     */
    public void show(View view, IBinder token) {
        show(view, 0, 0, token);
    }

    public void show(View view, int x, int y, IBinder token) {
        if (!mShow) {
            if (checkInterval())
                return;
            mView = view;
            if (mWindowManager != null && mView != null) {
                try {
                    if (token != null)
                        mLayoutParams.token = token;
                    mLayoutParams.x = x;
                    mLayoutParams.y = y;
                    mWindowManager.addView(mView, mLayoutParams);
                    mShow = true;
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Remove the view added by calling {@link WindowBase#show(View, IBinder)}
     */
    public void remove() {
        if (mShow) {
            if (checkInterval())
                return;
            if (mWindowManager != null && mView != null) {
                try {
                    mWindowManager.removeView(mView);
                    mShow = false;
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Update the view added by calling {@link WindowBase#show(View, IBinder)}
     * according to the position(x, y)
     */
    public void update(int x, int y) {
        if (mShow) {
            if (mWindowManager != null && mView != null) {
                try {
                    mLayoutParams.x = x;
                    mLayoutParams.y = y;
                    mWindowManager.updateViewLayout(mView, mLayoutParams);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Update the view added by calling {@link WindowBase#show(View, IBinder)}
     * according to the layoutParams
     */
    public void update(LayoutParams layoutParams) {
        if (mShow) {
            if (mWindowManager != null && mView != null) {
                try {
                    mLayoutParams = layoutParams;
                    mWindowManager.updateViewLayout(mView, mLayoutParams);
                } catch (Exception e) {
                }
            }
        }
    }

}
