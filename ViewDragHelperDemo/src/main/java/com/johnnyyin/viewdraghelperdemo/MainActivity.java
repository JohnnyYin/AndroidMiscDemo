package com.johnnyyin.viewdraghelperdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((DragFrameLayout) findViewById(R.id.drag_layout)).addDragView(findViewById(R.id.content_text_view));
    }
}
