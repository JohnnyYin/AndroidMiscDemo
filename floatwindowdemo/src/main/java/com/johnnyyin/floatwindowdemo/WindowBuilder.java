
package com.johnnyyin.floatwindowdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;

import java.lang.ref.WeakReference;

public abstract class WindowBuilder implements OnKeyListener, OnTouchListener {

    public static final String TAG = WindowBuilder.class.getSimpleName();

    private IBinder mToken;
    private WindowBase mWindowBase;
    public ViewGroup mRootView;
    private OnDialogDismissListener mOnDismissListener;
    public static final int CLOSE_NO_REASON = -1;
    public static final int CLOSE_BACK = -2;
    public static final int CLOSE_OUTTOUCH = -3;

    private WeakReference<View> mAttachView;
    private Runnable mPendingShowRunnable;

    /**
     * Build view on the window which the activity attach to
     */
    public WindowBuilder(Activity activity) {
        this(activity.getWindow().getDecorView());
    }

    /**
     * Build view on the window which the view attach to
     */
    public WindowBuilder(View view) {
        if (view != null) {
//            throw new NullPointerException("view is null");
            mToken = view.getWindowToken();
        }
        if (mToken == null) {
            mAttachView = new WeakReference<View>(view);
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    View view = mAttachView.get();
                    if (view == null) {
                        return;
                    }
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    mToken = view.getWindowToken();
                    if (mPendingShowRunnable == null) {
                        return;
                    }
                    if (!isShow()) {
                        mPendingShowRunnable.run();
                    }
                    mPendingShowRunnable = null;
                }
            });
        }
        initBuilder(null);
    }

    public void init(Context ctx) {
    }

    void initBuilder(Context ctx) {
        mWindowBase = getWindowBase();
        if (mWindowBase == null)
            throw new NullPointerException("getWindowBase() can't return null");
        mRootView = getRootView();
        if (mRootView == null)
            throw new NullPointerException("getRootView() can't return null");
        mRootView.setFocusableInTouchMode(true);
        mRootView.setOnKeyListener(this);
        mRootView.setOnTouchListener(this);
    }

    public View findId(int id) {
        return mRootView.findViewById(id);
    }

    public abstract ViewGroup getRootView();

    public abstract WindowBase getWindowBase();

    public interface OnDialogDismissListener {
        /**
         * return true for continue dispatch dismiss event, otherwise consume
         * the event.
         */
        public boolean onDismiss(int closeReason);
    }

    public void setOnDismissListener(OnDialogDismissListener listener) {
        mOnDismissListener = listener;
    }

    public void onDismissEvent(int closeReason) {
    }

    public void dispatchDismissEvent(int closeReason) {
        if (mOnDismissListener != null && mOnDismissListener.onDismiss(closeReason)) {
            return;
        }
        onDismissEvent(closeReason);
    }

    public boolean isShow() {
        return mWindowBase.isShow();
    }

    public void show() {
        if (mToken != null) {
            mWindowBase.show(mRootView, mToken);
        } else {
            mPendingShowRunnable = new Runnable() {
                @Override
                public void run() {
                    mWindowBase.show(mRootView, mToken);
                }
            };
        }
    }

    /**
     * show window view at position(x, y).
     */
    public void show(final int x, final int y) {
        if (mToken != null) {
            mWindowBase.show(mRootView, x, y, mToken);
        } else {
            mPendingShowRunnable = new Runnable() {
                @Override
                public void run() {
                    mWindowBase.show(mRootView, x, y, mToken);
                }
            };
        }
    }

    public void close() {
        close(CLOSE_NO_REASON);
    }

    private boolean mBlockDismiss;

    public void requestBlockDismiss() {
        mBlockDismiss = true;
    }

    /**
     * close window view by closeReason, maybe defined by user.
     */
    public void close(int closeReason) {
        dispatchDismissEvent(closeReason);
        if (!mBlockDismiss) {
            mWindowBase.remove();
        } else {
            mBlockDismiss = false;
        }
    }

    /**
     * update window view position by (x, y).
     */
    public void update(int x, int y) {
        mWindowBase.update(x, y);
    }

    /**
     * update window view position by layoutParams
     */
    public void update(WindowManager.LayoutParams lp) {
        mWindowBase.update(lp);
    }

    /**
     * Make sure the view is not the ViewGroup added by {@link WindowManager}
     * directly, otherwise {@link View#startAnimation(Animation)} will failed.
     */
    public void startAnimation(int id, Animation animation) {
        mRootView.findViewById(id).startAnimation(animation);
    }

    protected boolean mTouchDisable;

    /**
     * Disable Touch Detective.
     */
/*    public void setTouchDisable(boolean disable) {
        mTouchDisable = disable;
    }*/

    /**
     * Touch window outside will dismiss window.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mTouchDisable)
            return false;

        final int x = (int) event.getX();
        final int y = (int) event.getY();

        if ((event.getAction() == MotionEvent.ACTION_DOWN)
                && ((x < 0) || (x >= mRootView.getWidth()) || (y < 0) || (y >= mRootView
                .getHeight()))) {
            close(CLOSE_OUTTOUCH);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            close(CLOSE_OUTTOUCH);
            return true;
        } else {
            return false;
        }
    }

    private boolean mBackDisable;

    /**
     * Disable Back Detective.
     */
    public void setBackDisable(boolean disable) {
        mBackDisable = disable;
    }

    /**
     * Back pressed will dismiss window.
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (mBackDisable)
            return false;

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (!onBackPressed()) {
                close(CLOSE_BACK);
            }
            return true;
        }
        return false;
    }

    public boolean onBackPressed() {
        return false;
    }

    /**
     * only for the outside of the builder use.
     */
    public static void closeSelf(WindowBuilder builder) {
        if (builder != null && builder.isShow()) {
            // Log.d("show", "builder = " + builder);
            builder.close();
        }
    }

    private Intent mIntent;

    public void setIntent(Intent intent) {
        mIntent = intent;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public static interface FloatDialogListener {
        void onShow();

        void onDismiss(int result);
    }

    public static final int CLOSE_RESULT_GOHOME = 10;
}
