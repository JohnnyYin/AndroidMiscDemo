package com.toutiao.annotationdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Class<?> ITeacher = Class.forName("com.toutiao.annotationdemo.annotation.bean.ITeacher");
            Log.d("SS", "MainActivity.onCreate:" + ITeacher);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
