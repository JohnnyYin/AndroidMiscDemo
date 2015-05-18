package com.stackviewdemo.parabolademo.parabolademo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CopyOfGameView extends View implements Runnable {
	private class MovingObject {
		// 重力加速度
		public final double G = 9.8;

		// / <summary>
		// / 构造函数
		// / </summary>
		// / V0物体的初速度
		// / Sita物体初速度与水平方向的夹角
		// / Color颜色
		public MovingObject(double V0, double Sita, int red) {
			this.V0 = V0;
			this.Sita = Sita;
			this.Color = red;
		}

		// / <summary>
		// / 物体的初速度
		// / </summary>
		public double V0;

		// / <summary>
		// / 物体初速度与水平方向的夹角
		// / </summary>
		public double Sita;

		// / <summary>
		// / 物体的横坐标
		// / </summary>
		public double X;

		// / <summary>
		// / 物体的纵坐标
		// / </summary>
		public double Y;

		// 物体的颜色
		public int Color;

		// 要绘制的物体的矩形
		public Rect GetObjectRectangle() {
			return new Rect((int) X - 3, (int) Y - 3, 6, 6);
		}

		// / <summary>
		// / 最大射程
		// / </summary>
		public double Smax;

		// / <summary>
		// / 最大高度
		// / </summary>
		public double H;

		// / <summary>
		// / 运行时间
		// / </summary>
		public double T;

	}

	private Paint mPaint = null;

	double maxS = 0;
	double maxH = 0;
	double maxT = 0;
	double dx;
	double dy;
	double d;
	MovingObject obj;
	boolean isDraw = false;

	public CopyOfGameView(Context context) {
		super(context);
		init();
	}

	public CopyOfGameView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public CopyOfGameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		/* 构建对象 */
		mPaint = new Paint();

		// 计算物体的最大高度、运动时间及最大射程

		obj = new MovingObject(30, Math.PI / 4, Color.RED);
		// 运行时间
		obj.T = 2 * obj.V0 * Math.sin(obj.Sita) / 9.8;

		// 最大高度
		obj.H = obj.V0 * obj.V0 * Math.sin(obj.Sita) * Math.sin(obj.Sita) / (2 * 9.8);

		// 最大射程
		obj.Smax = 2 * obj.V0 * obj.V0 * Math.sin(obj.Sita) * Math.cos(obj.Sita) / 9.8;

		if (obj.Smax > maxS) {
			maxS = obj.Smax;
		}

		if (obj.H > maxH) {
			maxH = obj.H;
		}

		if (obj.T > maxT) {
			maxT = obj.T;
		}

		dx = (800 - 20) / maxS;
		dy = (480 - 20) / maxH;

		d = Math.min(dx, dy);
		// 最大运行时间
		Log.v("TAG", String.valueOf(maxT));
		// 最大高度
		Log.v("TAG", String.valueOf(maxH));
		// 最大射程
		Log.v("TAG", String.valueOf(maxS));
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		/* 设置画布的颜色 */
		canvas.drawColor(Color.BLACK);

		/* 设置取消锯齿效果 */
		mPaint.setAntiAlias(true);
		canvas.drawColor(Color.GREEN);
		if (isDraw) {
			canvas.drawCircle((float) obj.X, (float) obj.Y, 10, mPaint);
		}

	}

	// 触笔事件
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			new Thread(this).start();
			break;
		}
		return true;
	}

	public void run() {
		for (double t = 0; t < maxT; t += 0.01) {
			Log.v("TAG", String.valueOf(t));
			isDraw = true;
			// 水平坐标
			double x = obj.V0 * Math.cos(obj.Sita) * t;

			// 竖直坐标
			double y = obj.V0 * Math.sin(obj.Sita) * t - 9.8 * t * t / 2;

			if (y < 0) {
				continue;
			}

			// 坐标转换
			obj.X = 10 + d * x;
			obj.Y = 480 - 10 - d * y;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			// 使用postInvalidate可以直接在线程中更新界面
			postInvalidate();

		}

	}
}
