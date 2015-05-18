package com.stackviewdemo.stackviewdemo;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class ColorAdapter extends BaseAdapter {

    private Context mContext;

    private int[] mColors;

    public ColorAdapter(Context context, int[] colors) {
        mContext = context;
        mColors = colors;
    }

    public int getCount() {
        return mColors == null ? 0 : mColors.length;
    }

    public Object getItem(int position) {
        return mColors == null ? null : mColors[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View cacheView, ViewGroup parent) {
        float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, mContext.getResources().getDisplayMetrics());
        LinearLayout.LayoutParams colorLayoutParams = new LinearLayout.LayoutParams((int) height, (int) height);

        LinearLayout colorLayout = new LinearLayout(mContext);
        colorLayout.setBackgroundColor(mColors[position]);
        colorLayout.setLayoutParams(colorLayoutParams);

        return colorLayout;
    }

}

