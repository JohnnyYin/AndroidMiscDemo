package com.johnnyyin.ndkdemo;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Field;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("SS", "MainActivity.onClick:MediaPlayer = " + MediaPlayer.class.getClassLoader() + "");
                Log.d("SS", "MainActivity.onClick:MainActivity = " + MainActivity.class.getClassLoader() + "");
                AllocTest.monifyClassLoader(MediaPlayer.class, MainActivity.class);
                Log.d("SS", "MainActivity.onClick:MediaPlayer = " + MediaPlayer.class.getClassLoader() + "");
                Log.d("SS", "MainActivity.onClick:MainActivity = " + MainActivity.class.getClassLoader() + "");
//                AllocTest.test();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        dumpDexPathList();
    }

    private void dumpDexPathList() {
        try {
            ClassLoader loader = getApplication().getClassLoader();
            Field pathList = loader.getClass().getSuperclass().getDeclaredField("pathList");
            pathList.setAccessible(true);
            Object pathListObj = pathList.get(loader);
            Field dexElements = pathListObj.getClass().getDeclaredField("dexElements");
            dexElements.setAccessible(true);
            Object[] list = (Object[]) dexElements.get(pathListObj);
            Log.e("SS", Arrays.toString(list) + "");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SS", Log.getStackTraceString(e));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
