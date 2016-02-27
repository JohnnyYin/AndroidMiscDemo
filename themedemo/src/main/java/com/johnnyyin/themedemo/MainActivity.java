package com.johnnyyin.themedemo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private ListView mListView;
    private Button mChangeThemeBtn;
    private ColorStateList mTextColor;
    private TextView mSwitchPage;

    private int mThemeRes;

    @Override
    public void setTheme(int resid) {
        super.setTheme(resid);
        if (mThemeRes != resid) {
            mThemeRes = resid;
            Log.d("SS", "MainActivity.setTheme:" + mThemeRes);
        }
    }

    public int getThemeRes() {
        return mThemeRes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(ThemeUtils.THEME);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.list_view);
        List<String> list = new ArrayList<String>();
        int i = 0;
        while (i < 100) {
            list.add("item: " + i++);
        }
        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.height = 300;
                }
                if (mTextColor != null) {
                    view.setTextColor(mTextColor);
                }
                return view;
            }
        });
        mChangeThemeBtn = (Button) findViewById(R.id.change_theme);
        mChangeThemeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ThemeUtils.THEME == R.style.AppTheme) {
                    ThemeUtils.THEME = R.style.AppThemeNight;
                } else {
                    ThemeUtils.THEME = R.style.AppTheme;
                }
                applyTheme(ThemeUtils.THEME);
            }
        });
        mSwitchPage = (TextView) findViewById(R.id.switch_page);
        mSwitchPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ThemeUtils.THEME == R.style.AppTheme) {
                    ThemeUtils.THEME = R.style.AppThemeNight;
                } else {
                    ThemeUtils.THEME = R.style.AppTheme;
                }
//                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
    }

    private void applyTheme(int theme) {
        setTheme(theme);

        Drawable d = ThemeUtils.getListSelector(this);
        if (d != null) {
            mListView.setSelector(d);
        }
        mTextColor = ThemeUtils.getTextColor(this);
        ((ArrayAdapter) mListView.getAdapter()).notifyDataSetChanged();
        getWindow().setBackgroundDrawableResource(ThemeUtils.getWindowBackground(this));

        mChangeThemeBtn.setBackgroundDrawable(ThemeUtils.getButtonBackground(this));
        int oldOverScrollMode = mListView.getOverScrollMode();
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        mListView.setOverScrollMode(oldOverScrollMode);
        mSwitchPage.setBackgroundDrawable(ThemeUtils.getBaseClickableOnlyRippleEffectBackground(this));
//        mSwitchPage.setBackgroundResource(theme == R.style.AppTheme ? R.drawable.item_background : R.drawable.item_background_night);
        mSwitchPage.setTextColor(mTextColor);
        ThemeUtils.setStatusBarColor(this);
    }

}
