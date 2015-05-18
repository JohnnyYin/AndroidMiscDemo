package com.example.graphics_demo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	private Button mBtnChart;
	private Button mBtnShader;
	private Button mBtnXfermode;
	private Button mBtnPieChart;
	private Button mBtnScaleLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();
	}

	private void initViews() {
		mBtnChart = (Button) findViewById(R.id.chart);
		mBtnChart.setOnClickListener(this);

		mBtnPieChart = (Button) findViewById(R.id.piechart);
		mBtnPieChart.setOnClickListener(this);

		mBtnShader = (Button) findViewById(R.id.shader);
		mBtnShader.setOnClickListener(this);

		mBtnXfermode = (Button) findViewById(R.id.xfermode);
		mBtnXfermode.setOnClickListener(this);

		mBtnScaleLayout = (Button) findViewById(R.id.scale_layout);
		mBtnScaleLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chart: {
			Intent intent = new Intent(getApplicationContext(),
					ChartActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.piechart: {
			Intent intent = new Intent(getApplicationContext(),
					PieChartActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.shader: {
			Intent intent = new Intent(getApplicationContext(),
					ShaderActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.xfermode: {
			Intent intent = new Intent(getApplicationContext(),
					XfermodeActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.scale_layout: {
			Intent intent = new Intent(getApplicationContext(),
					ScaleLayoutActivity.class);
			startActivity(intent);
			break;
		}
		default:
			break;
		}
	}
}
