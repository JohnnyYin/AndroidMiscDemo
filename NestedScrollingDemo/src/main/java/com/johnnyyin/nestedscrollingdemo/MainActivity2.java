package com.johnnyyin.nestedscrollingdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends Activity {
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nested_scroll_view);
        mListView = (ListView) findViewById(R.id.list_view);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add("item = " + i);
        }
        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list));
    }
}
