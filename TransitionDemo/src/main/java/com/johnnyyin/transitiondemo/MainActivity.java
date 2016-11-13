package com.johnnyyin.transitiondemo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                // create the transition animation - the images in the layouts
                // of both activities are defined with android:transitionName="robot"
                ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(MainActivity.this, view, "test");
                // start the new activity
                startActivity(intent, options.toBundle());
            }
        });
    }
}
