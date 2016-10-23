package com.johnnyyin.vectordrawabledemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView) findViewById(R.id.animated_vector_drawable);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = ((ImageView) v).getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
            }
        });
        ImageView rightImage = (ImageView) findViewById(R.id.right_animated_vector_drawable);
        rightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = ((ImageView) v).getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
            }
        });

        final ImageView pausePlayButton = (ImageView) findViewById(R.id.pause_to_play);
        pausePlayButton.setImageResource(R.drawable.ic_pause_to_play);
        pausePlayButton.setTag(R.drawable.ic_pause_to_play);
        pausePlayButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                int redId = (int) pausePlayButton.getTag();
                if (redId == R.drawable.ic_pause_to_play) {
                    redId = R.drawable.ic_play_to_pause;
                } else {
                    redId = R.drawable.ic_pause_to_play;
                }
                pausePlayButton.setImageResource(redId);
                pausePlayButton.setTag(redId);
                Drawable drawable = ((ImageView) v).getDrawable();
                if (drawable instanceof Animatable2) {
                    ((Animatable2) drawable).start();
                }
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
