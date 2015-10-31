package com.johnnyyin.touchdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

/**
 * Created by Johnny on 15/10/29.
 */
public class MyLinearLayout extends LinearLayout {
    private Field mFirstTouchTarget;

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            mFirstTouchTarget = ViewGroup.class.getDeclaredField("mFirstTouchTarget");
            mFirstTouchTarget.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    int i = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            Log.e("SS", "MyLinearLayout.dispatchTouchEvent:" + ev.getAction() + ":" + mFirstTouchTarget.get(this));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e("SS", "MyLinearLayout.onInterceptTouchEvent:" + ev.getAction());
//        if (i++ > 5) {
//            Log.e("SS", "MyLinearLayout.onInterceptTouchEvent:==========================================");
//            return true;
//        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("SS", "MyLinearLayout.onTouchEvent:" + event.getAction());
        return super.onTouchEvent(event);
    }

}
