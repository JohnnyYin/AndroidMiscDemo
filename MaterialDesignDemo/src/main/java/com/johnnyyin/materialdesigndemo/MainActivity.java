package com.johnnyyin.materialdesigndemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private ListView mListView;
    private List<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.change_theme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApplicationContext().setTheme(R.style.AppTheme_night);
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });

        mListView = (ListView) findViewById(R.id.listview);
        mData = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            mData.add("item:" + i);
        }
        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mData.size();
            }

            @Override
            public String getItem(int position) {
                return mData.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item, parent, false);
                }
                ((TextView) convertView.findViewById(R.id.title)).setText(getItem(position));
                return convertView;
            }

        });
//        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mData));
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
