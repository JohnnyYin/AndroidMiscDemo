package com.johnnyyin.themedemo;

import android.os.Bundle;
import android.widget.Button;

public class SecondActivity extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((Button) findViewById(R.id.change_theme)).setText("SecondActivity");
    }
}
