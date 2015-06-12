package com.johnnyyin.themedemo;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView mText;
    private ImageView mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mText = (TextView) findViewById(R.id.txt);
        mText.setText(getString(R.string.hello_world));

        mImg = (ImageView) findViewById(R.id.img);
        mImg.setImageResource(R.mipmap.about_share);

        findViewById(R.id.change_theme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeUtils.setTheme(new ThemeUtils.Theme(), getApplicationContext());
                Log.e("SS", "change");
                mText.setText(getString(R.string.hello_world));
                mImg.setImageResource(R.mipmap.about_share);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("SS", "locale:" + getResources().getConfiguration().locale);
    }
}
