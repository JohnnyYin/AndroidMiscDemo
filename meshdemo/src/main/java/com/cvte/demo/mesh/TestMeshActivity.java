package com.cvte.demo.mesh;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class TestMeshActivity extends Activity {

    private BitmapMesh.SampleView mSampleView;
    private boolean mDebugMesh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSampleView = new BitmapMesh.SampleView(this);
        mSampleView.setIsDebug(mDebugMesh);
        mSampleView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        Button btn = new Button(this);
        btn.setText("Run");
        btn.setTextSize(18.0f);
        btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btn.setOnClickListener(new View.OnClickListener() {
            boolean mReverse = false;

            @Override
            public void onClick(View v) {
                if (mSampleView.startAnimation(mReverse)) {
                    mReverse = !mReverse;
                }
            }
        });

        Button debug = new Button(this);
        debug.setText("Debug");
        debug.setTextSize(18.0f);
        debug.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        debug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDebugMesh = !mDebugMesh;
                mSampleView.setIsDebug(mDebugMesh);
                mSampleView.invalidate();
            }
        });

        LinearLayout btns = new LinearLayout(this);
        btns.setOrientation(LinearLayout.HORIZONTAL);
        btns.setGravity(Gravity.CENTER_VERTICAL);
        btns.addView(btn);
        btns.addView(debug);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.addView(btns);
        linearLayout.addView(mSampleView);

        setContentView(linearLayout);
    }
}
