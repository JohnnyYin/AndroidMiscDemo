
package com.example.widgetlib;

import com.example.widgetlib.scalelayout.ScaleLayoutActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
    private Button mBounceScrollView;
    private Button mScoreBoardView;
    private Button mScaleLayout;
    private Button mGridView;
    private Button mCurveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBounceScrollView = (Button) findViewById(R.id.BounceScrollView);
        mBounceScrollView.setOnClickListener(this);
        mScoreBoardView = (Button) findViewById(R.id.ScoreBoardView);
        mScoreBoardView.setOnClickListener(this);
        mScaleLayout = (Button) findViewById(R.id.scale_layout);
        mScaleLayout.setOnClickListener(this);
        mGridView = (Button) findViewById(R.id.my_gridview);
        mGridView.setOnClickListener(this);
        mCurveView = (Button) findViewById(R.id.curve_view);
        mCurveView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scale_layout: {
                startActivity(new Intent(this, ScaleLayoutActivity.class));
                break;
            }
            case R.id.ScoreBoardView: {
                Intent intent = new Intent(MainActivity.this, ScoreBoardViewActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.BounceScrollView: {
                Intent intent = new Intent(MainActivity.this, BounceScrollViewActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.my_gridview: {
                Intent intent = new Intent(MainActivity.this, MyGridViewActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.curve_view: {
                Intent intent = new Intent(MainActivity.this, CurveActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

}
