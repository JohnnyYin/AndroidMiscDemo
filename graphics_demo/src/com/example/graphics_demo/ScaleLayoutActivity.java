package com.example.graphics_demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ScaleLayoutActivity extends Activity {
	private ScaleLayout mScaleLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scale_layout);
		mScaleLayout = (ScaleLayout) findViewById(R.id.root);
		mScaleLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mScaleLayout.postInvalidate();
			}
		});
	}

}
