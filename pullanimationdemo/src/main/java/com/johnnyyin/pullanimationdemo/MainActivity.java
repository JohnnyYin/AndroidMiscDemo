package com.johnnyyin.pullanimationdemo;

import android.app.Activity;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class MainActivity extends Activity {
    private PullLoadingView mPullLoadingView;
    private PullLoadingView3 mPullLoadingView2;

    private boolean mIsRebound;
    private boolean mIsAniming;

    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            mPullLoadingView.clearAnimation();
            float percent = mPullLoadingView.getPullProgress();
            if (percent < 1) {
                percent += 0.01f;
                mPullLoadingView.setPullProgress(percent, 1.0f);
                if (mPullLoadingView.getVisibility() == View.VISIBLE) {
                    mHandler.postDelayed(mRunnable, 16);
                }
            } else {
                mPullLoadingView.startAnimation(null);
            }
        }
    };

    private Runnable mRunnable2 = new Runnable() {

        @Override
        public void run() {
            mPullLoadingView2.clearAnimation();
            float percent = mPullLoadingView2.getPullProgress();
            if (!mIsRebound && !mIsAniming) {
                if (percent < 1) {
                    percent += 0.005f;
                    mPullLoadingView2.setPullProgress(percent, 1.0f, mIsRebound);
                } else {
                    mIsRebound = true;
                }
                if (mPullLoadingView2.getVisibility() == View.VISIBLE) {
                    mHandler.postDelayed(mRunnable2, 16);
                }
            } else if (mIsRebound && !mIsAniming) {
                if (percent > 0) {
                    percent -= 0.005f;
                    mIsRebound = true;
                    mPullLoadingView2.setPullProgress(percent, 1.0f, mIsRebound);
                } else {
                    mIsRebound = false;
                    mIsAniming = true;
                }
                if (mPullLoadingView2.getVisibility() == View.VISIBLE) {
                    mHandler.postDelayed(mRunnable2, 16);
                }
            } else {
                mIsRebound = false;
                mIsAniming = true;
                mPullLoadingView2.startAnimation(null);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPullLoadingView = (PullLoadingView) findViewById(R.id.pull_loading_view);
        mPullLoadingView2 = (PullLoadingView3) findViewById(R.id.pull_loading_view2);
        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPullLoadingView2.clearAnimation();
                mPullLoadingView2.setVisibility(View.GONE);
                mPullLoadingView.setVisibility(View.VISIBLE);
                mPullLoadingView.setPullProgress(0, 1);
                mHandler.removeCallbacks(mRunnable2);
                mHandler.removeCallbacks(mRunnable);
                mHandler.post(mRunnable);
            }
        });

        findViewById(R.id.start_button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mIsRebound = false;
                mIsAniming = false;
                mPullLoadingView.clearAnimation();
                mPullLoadingView.setVisibility(View.GONE);
                mPullLoadingView2.setVisibility(View.VISIBLE);
                mPullLoadingView2.setPullProgress(0, 1, mIsRebound);
                mHandler.removeCallbacks(mRunnable);
                mHandler.removeCallbacks(mRunnable2);
                mHandler.post(mRunnable2);

//                mPullLoadingView.setVisibility(View.GONE);
//                mPullLoadingView2.setVisibility(View.VISIBLE);
//                mPullLoadingView2.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                    @Override
//                    public boolean onPreDraw() {
//                        mPullLoadingView2.getViewTreeObserver().removeOnPreDrawListener(this);
//                        mPullLoadingView2.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                stopAnimatable(mPullLoadingView2);
//                                startAnimatable(mPullLoadingView2);
//                            }
//                        });
//                        return true;
//                    }
//                });
            }
        });
    }

    private static Animatable getAnimatable(View view) {
        if (view == null) {
            return null;
        }

        Animatable animatable = null;

        Drawable drawable = view.getBackground();
        if (Animatable.class.isInstance(drawable)) {
            animatable = (Animatable) drawable;
        } else if (ImageView.class.isInstance(view)) {
            drawable = ((ImageView) view).getDrawable();
            if (Animatable.class.isInstance(drawable)) {
                animatable = (Animatable) drawable;
            }
        }
        return animatable;
    }

    /**
     * 执行Animatable
     */
    public static void startAnimatable(View view) {
        final Animatable animatable = getAnimatable(view);
        if (animatable != null && !animatable.isRunning()) {
            animatable.start();
        }
    }

    /**
     * 停止Animatable
     */
    public static void stopAnimatable(View view) {
        final Animatable animatable = getAnimatable(view);
        if (animatable != null && animatable.isRunning()) {
            animatable.stop();
        }
    }
}
