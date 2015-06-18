package com.johnnyyin.pullanimationdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class MainActivity extends Activity {
    private PullLoadingView mPullLoadingView;

    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            float percent = mPullLoadingView.getPullProgress();
            if (percent < 1) {
                percent += 0.01f;
                mPullLoadingView.setPullProgress(percent, 1.0f);
                mHandler.postDelayed(mRunnable, 20);
            } else {
                mPullLoadingView.startAnimation(null);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPullLoadingView = (PullLoadingView) findViewById(R.id.pull_loading_view);
        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPullLoadingView.setPullProgress(0, 1);
                mHandler.removeCallbacks(mRunnable);
                mHandler.post(mRunnable);
            }
        });
    }
}
