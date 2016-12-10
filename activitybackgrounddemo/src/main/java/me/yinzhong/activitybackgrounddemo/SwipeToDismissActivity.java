package me.yinzhong.activitybackgrounddemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * windowSwipeToDismiss=true
 *
 * @author YinZhong
 * @since 2016/11/13
 */
public class SwipeToDismissActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_to_dismiss);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long startTime = System.currentTimeMillis();
        getWindow().findViewById(android.R.id.content).buildDrawingCache();
        Bitmap bitmap = getWindow().findViewById(android.R.id.content).getDrawingCache();
        Log.d("ALOG", "SwipeToDismissActivity.onResume:" + (System.currentTimeMillis() - startTime));
    }
}
