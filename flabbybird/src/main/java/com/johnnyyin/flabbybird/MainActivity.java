package com.johnnyyin.flabbybird;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.johnnyyin.flabbybird.view.GameFlabbyBird;

public class MainActivity extends Activity {
    private GameFlabbyBird mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mGame = new GameFlabbyBird(this);
        setContentView(mGame);

    }

}
