package com.stackviewdemo.parabolademo.parabolademo;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;


public class MainActivity extends Activity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameView = (GameView) findViewById(R.id.game_view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        gameView.run();
        return super.onKeyDown(keyCode, event);
    }
}
