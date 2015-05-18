
package com.example.graphics_demo;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ShaderActivity extends Activity {
	private RainBowView mRainBowView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shader);
        mRainBowView = (RainBowView) findViewById(R.id.rainbow);
        mRainBowView.startAnim(2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shader, menu);
        return true;
    }

}
