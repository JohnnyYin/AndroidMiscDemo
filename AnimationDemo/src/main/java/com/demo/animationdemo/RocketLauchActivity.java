package com.demo.animationdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class RocketLauchActivity extends Activity {
    private RocketLaunchView mRocketLaunchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocket_lauch);
        mRocketLaunchView = (RocketLaunchView) findViewById(R.id.rocket_lauch_view);

        mRocketLaunchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRocketLaunchView.startAnimation(1000);
            }
        });
    }

}
