package com.johnnyyin.webviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

/**
 * Created by Johnny on 15/8/5.
 */
public class MyWebView extends WebView {
    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Log.e("SS", "draw:" + getContext().getClass().getName());
    }
}
