package com.johnnyyin.transitiondemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author YinZhong
 * @since 2016/11/6
 */
public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // inside your activity (if you did not enable transitions in your theme)
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

//    @Override
//    public void onBackPressed() {
//        finishAfterTransition();
//    }
}