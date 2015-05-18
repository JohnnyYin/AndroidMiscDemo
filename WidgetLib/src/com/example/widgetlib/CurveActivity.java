package com.example.widgetlib;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class CurveActivity extends Activity {
	private CurveView mCurveView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCurveView = new CurveView(getBaseContext());
		setContentView(mCurveView);

		Random random = new Random();
		float[] datas = new float[7];
		for (int i = 0; i < datas.length; i++) {
			datas[i] = random.nextInt(400);
		}
		mCurveView.setData(datas);
		mCurveView.startAnimation(5000);
		mCurveView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mCurveView.startAnimation(5000);
			}
		});
	}

}
