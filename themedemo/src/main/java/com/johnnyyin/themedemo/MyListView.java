package com.johnnyyin.themedemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

public class MyListView extends ListView {
    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelector(int resID) {
        Log.e("SS", "setSelector: " + resID);
        super.setSelector(resID);
    }

    @Override
    public void setSelector(Drawable sel) {
        Log.e("SS", "setSelector: " + sel);
        super.setSelector(sel);
    }
}
