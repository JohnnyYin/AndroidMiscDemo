package com.example.graphics_demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.example.graphics_demo.PieChartView.PieItem;

public class PieChartActivity extends Activity {
	private PieChartView mPieChartView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pie_chart);

		mPieChartView = (PieChartView) findViewById(R.id.piechart);
		Random random = new Random();
		List<PieItem> data = new ArrayList<PieItem>();
		data.add(new PieItem(0.15f, Color.argb(255, random.nextInt(255),
				random.nextInt(255), random.nextInt(255)), "15%"));
		data.add(new PieItem(0.20f, Color.argb(255, random.nextInt(255),
				random.nextInt(255), random.nextInt(255)), "20%"));
		data.add(new PieItem(0.25f, Color.argb(255, random.nextInt(255),
				random.nextInt(255), random.nextInt(255)), "25%"));
		data.add(new PieItem(0.40f, Color.argb(255, random.nextInt(255),
				random.nextInt(255), random.nextInt(255)), "40%"));

		mPieChartView.setData(data);
	}
}
