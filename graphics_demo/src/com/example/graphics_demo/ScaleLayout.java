package com.example.graphics_demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class ScaleLayout extends LinearLayout {
	private int mScalePercent;
	private Bitmap mNormalBmp;
	private Paint mPaint;
	private Matrix mMatrix;
	private Rect mSrcRect;
	private Rect mDstRect;

	public ScaleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ScaleLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		mMatrix = new Matrix();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mNormalBmp == null) {
			mNormalBmp = Bitmap.createBitmap(getWidth(), getHeight(),
					Bitmap.Config.ARGB_8888);
			mSrcRect = new Rect(getLeft(), getTop(), getRight(), getBottom());
			mDstRect = new Rect(getLeft() + 200, getTop() + 200,
					getRight() / 2, getBottom() / 2);
			Canvas localCanvas = new Canvas(mNormalBmp);
			this.draw(localCanvas);
			super.onDraw(canvas);
		} else {
			canvas.drawBitmap(mNormalBmp, mSrcRect, mDstRect, mPaint);
			super.onDraw(canvas);
		}
	}

//	@Override
//	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
//		if (mNormalBmp != null) {
//			Log.e("graphics_demo", "[ScaleLayout]-[drawChild]:" + "222");
//			mMatrix.postScale(0.5f, 0.5f);
//			canvas.drawBitmap(mNormalBmp, mSrcRect, mDstRect, mPaint);
//			return true;
//		} else {
//			Log.e("graphics_demo", "[ScaleLayout]-[drawChild]:" + "super");
//			return super.drawChild(canvas, child, drawingTime);
//		}
//	}
}
