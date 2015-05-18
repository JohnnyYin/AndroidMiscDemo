package com.demo.animationdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener {
    private Button mRocketLauch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRocketLauch = (Button) findViewById(R.id.rocket_lauch);
        mRocketLauch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rocket_lauch:
                startActivity(new Intent(getApplicationContext(), RocketLauchActivity.class));
                break;
        }
    }
}
