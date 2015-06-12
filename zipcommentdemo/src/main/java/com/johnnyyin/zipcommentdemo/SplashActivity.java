package com.johnnyyin.zipcommentdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SplashActivity extends Activity {
    private Handler mHandler = new Handler();
    private boolean mGoSecond;
    private static final String SP_KEY = "hasjump";

    private Runnable mGoMainRunnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(SplashActivity.this, !mGoSecond ? MainActivity.class : SecondActivity.class));
            saveJumpStatus();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler.postDelayed(mGoMainRunnable, 1000);
        getZipComment();
    }

    private void getZipComment() {
        if (getSharedPreferences("jump", MODE_PRIVATE).getBoolean(SP_KEY, false)) {
            return;
        }
        try {
            ZipFile zipFile = new ZipFile(getPackageCodePath());
            ZipEntry entry = zipFile.getEntry("assets/jump");
            if (entry != null) {
                String comment = entry.getComment();
                if ("second".equals(comment)) {
                    mGoSecond = true;
                }
            }
        } catch (IOException e) {
            saveJumpStatus();
            e.printStackTrace();
        }
    }

    private void saveJumpStatus() {
        getSharedPreferences("jump", MODE_PRIVATE).edit().putBoolean(SP_KEY, true).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
