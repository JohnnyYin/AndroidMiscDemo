
package com.example.graphics_demo;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/**
 * 绘制简单的图表
 * 
 * @author i
 * @email admin@atiter.com
 * @date 2013-8-9 上午10:52:44
 */
public class ChartActivity extends Activity {
    private ChartView mChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        mChartView = (ChartView) findViewById(R.id.chart_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChartView.startAnimation();
    }
    
    @Override
    protected void onDestroy() {
        mChartView.destory();
        super.onDestroy();
    }

}
