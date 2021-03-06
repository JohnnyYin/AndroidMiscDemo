package com.johnnyyin.detectoom;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private List<byte[]> mBytes = new ArrayList<byte[]>();
    private SoftReference<Object> mDetectObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Object o = new Object();
        mDetectObject = new SoftReference<Object>(o);
        findViewById(R.id.alloc_mem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBytes.add(new byte[1024 * 1024 * 2]);
                Log.e("SS", "size = " + (mBytes.size() * 2) + "M" + ", obj = " + mDetectObject.get());
            }
        });
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
