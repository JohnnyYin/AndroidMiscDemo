package me.yinzhong.activitybackgrounddemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                startActivity(new Intent(this, NormalActivity.class));
                break;
            case R.id.btn2:
                startActivity(new Intent(this, TransparentActivity.class));
                break;
            case R.id.btn3:
                startActivity(new Intent(this, TransparentBackgroundActivity.class));
                break;
            case R.id.btn4:
                startActivity(new Intent(this, SwipeToDismissActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ALOG", "MainActivity.onResume:");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ALOG", "MainActivity.onPause:");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ALOG", "MainActivity.onStart:");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ALOG", "MainActivity.onStop:");
    }
}
