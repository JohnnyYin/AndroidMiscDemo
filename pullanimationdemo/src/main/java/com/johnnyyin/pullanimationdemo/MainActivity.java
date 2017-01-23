package com.johnnyyin.pullanimationdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class MainActivity extends Activity {
    private PullLoadingView mPullLoadingView;
    private PullLoadingView2 mPullLoadingView2;

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
                    mHandler.postDelayed(mRunnable, 20);
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
            if (percent < 1) {
                percent += 0.01f;
                mPullLoadingView2.setPullProgress(percent, 1.0f);
                if (mPullLoadingView2.getVisibility() == View.VISIBLE) {
                    mHandler.postDelayed(mRunnable2, 20);
                }
            } else {
                mPullLoadingView2.startAnimation(null);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPullLoadingView = (PullLoadingView) findViewById(R.id.pull_loading_view);
        mPullLoadingView2 = (PullLoadingView2) findViewById(R.id.pull_loading_view2);
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
                mPullLoadingView.clearAnimation();
                mPullLoadingView.setVisibility(View.GONE);
                mPullLoadingView2.setVisibility(View.VISIBLE);
                mPullLoadingView2.setPullProgress(0, 1);
                mHandler.removeCallbacks(mRunnable);
                mHandler.removeCallbacks(mRunnable2);
                mHandler.post(mRunnable2);
            }
        });
    }
}
