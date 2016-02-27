package com.johnnyyin.webviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.OverScroller;

import java.lang.reflect.Field;

/**
 * Created by Johnny on 15/8/5.
 */
public class MyWebView extends WebView {
    public MyWebView(Context context) {
        super(context);
        init();
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private OverScroller mOverScroller;

    private void init() {
//        try {
//            Field mProvider = WebView.class.getDeclaredField("mProvider");
//            mProvider.setAccessible(true);
//            Object mProvideObj = mProvider.get(this);
//            Field mAwContents = mProvideObj.getClass().getDeclaredField("mAwContents");
//            mAwContents.setAccessible(true);
//            Object mAwContentsObj = mAwContents.get(mProvideObj);
//            Field mScrollOffsetManager = mAwContentsObj.getClass().getDeclaredField("mScrollOffsetManager");
//            mScrollOffsetManager.setAccessible(true);
//            Object mScrollOffsetManagerObj = mScrollOffsetManager.get(mAwContentsObj);
//            Field mScroller = mScrollOffsetManagerObj.getClass().getDeclaredField("mScroller");
//            mScroller.setAccessible(true);
//            Object mScrollerObj = mScroller.get(mScrollOffsetManagerObj);
//            if (mScrollerObj instanceof OverScroller) {
//                mOverScroller = (OverScroller) mScrollerObj;
//            }
//        } catch (Throwable e) {
//            Log.e("SS", "exception :" + Log.getStackTraceString(e));
//        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Log.e("SS", "draw:" + getContext().getClass().getName());
    }

    @Override
    public void onChildViewAdded(View parent, View child) {
        super.onChildViewAdded(parent, child);
        Log.d("SS", "MyWebView.onChildViewAdded:" + child);
    }

    @Override
    public void onChildViewRemoved(View p, View child) {
        super.onChildViewRemoved(p, child);
        Log.d("SS", "MyWebView.onChildViewRemoved:" + child);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        Log.d("SS", "MyWebView.onSizeChanged:");
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        Log.d("SS", "MyWebView.onScrollChanged:");
    }

}
