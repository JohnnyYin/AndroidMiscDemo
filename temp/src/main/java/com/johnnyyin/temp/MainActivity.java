package com.johnnyyin.temp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private ListView mListView;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag) {
                    Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
                    animation.setDuration(300);
                    animation.setFillAfter(true);
                    animation.setFillEnabled(true);
                    v.startAnimation(animation);
                    flag = true;
                } else {
                    Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    animation.setDuration(300);
                    animation.setFillAfter(true);
                    animation.setFillEnabled(true);
                    v.startAnimation(animation);
                    flag = false;
                }
            }
        });
        Log.e("SS", getResources().getResourceEntryName(R.layout.activity_main) + ":" + getResources().getIdentifier("activity_main", "layout", getPackageName()));

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 300;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                }
                ((TextView) convertView.findViewById(R.id.text)).setText("item: " + position);
                return convertView;
            }
        });

        Log.e("SS", "Activity.getApplication = " + getApplication());
        Log.e("SS", "Activity.getApplicationContext = " + getApplicationContext());
        Log.e("SS", "Application.this = " + MyApplication.sInstance);
        Log.e("SS", "Application.getApplicationContext = " + MyApplication.sInstance.getApplicationContext());

        Log.e("SS", "Activity.getBaseContext = " + getBaseContext());
        Log.e("SS", "Application.getBaseContext = " + MyApplication.sInstance.getBaseContext());

        Log.e("SS", "Activity = " + this);
        Log.e("SS", "mListView.getContext = " + mListView.getContext());
        Log.e("SS", "Window.getContext = " + getWindow().getContext());
        Log.e("SS", "DecorView.getContext = " + getWindow().getDecorView().getContext());

        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_main, null, false);
        Log.e("SS", "view.getContext = " + view.getContext());
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
