package com.johnnyyin.dispatchmotionevent;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends Activity {
    private ListView mListView;
    private int mLastMotionY;
    private int mMoveCount;

    private Runnable mFlingRunning = new Runnable() {
        @Override
        public void run() {
            mMoveCount++;
            mLastMotionY -= dp2px(40) + mMoveCount;
            if (mLastMotionY > 0) {
                mListView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 0, mLastMotionY, 0));
                mListView.postDelayed(mFlingRunning, 8);
            } else {
                mMoveCount = 0;
                mListView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, mLastMotionY, 0));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listview);
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            list.add("item:" + i);
        }
        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list));

        findViewById(R.id.fling).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mListView.fling(9000);

                mMoveCount = 0;
                mLastMotionY = dp2px(420);
                MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, mLastMotionY, 0);
                mListView.dispatchTouchEvent(event);

                mListView.postDelayed(mFlingRunning, 8);
            }
        });


        findViewById(R.id.down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLastMotionY = dp2px(420);
                MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, mLastMotionY, 0);
                mListView.dispatchTouchEvent(event);
            }
        });


        findViewById(R.id.move).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int deltaY = dp2px(-16);
                mLastMotionY += deltaY;
                MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 0, mLastMotionY, 0);
                mListView.dispatchTouchEvent(event);
            }
        });


        findViewById(R.id.up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, mLastMotionY, 0);
                mListView.dispatchTouchEvent(event);
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
