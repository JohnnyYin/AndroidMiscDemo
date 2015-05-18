package com.example.graphics_demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.renderscript.Type;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class RainBowView extends View {
	private Bitmap mRainBowBitmap;
	private Paint mTendencyFillPaint;
	private int mScreenWidth;
	private float mShaderWidth;

	private Animation mAnim = new Animation() {
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			RainBowView.this.applyTransformation(interpolatedTime);
			super.applyTransformation(interpolatedTime, t);
		}

	};

	private void applyTransformation(float interpolatedTime) {
		LinearGradient shader = new LinearGradient(
				(mScreenWidth + mShaderWidth) * interpolatedTime - mShaderWidth,
				0, (mScreenWidth + mShaderWidth) * interpolatedTime, 0,
				0xffffffff, 0x00ffffff, Shader.TileMode.CLAMP);
		mTendencyFillPaint.setShader(shader);
		postInvalidate();
	};

	public RainBowView(Context context) {
		super(context);
		init();
	}

	public RainBowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RainBowView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

//	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
//		final int reflectionGap = 4;
//		int width = bitmap.getWidth();
//		int height = bitmap.getHeight();
//
//		Matrix matrix = new Matrix();
//		matrix.preScale(1, -1);
//
//		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
//				width, height / 2, matrix, false);
//
//		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
//				(height + height / 2), Config.ARGB_8888);
//
//		Canvas canvas = new Canvas(bitmapWithReflection);
//		canvas.drawBitmap(bitmap, 0, 0, null);
//		Paint deafalutPaint = new Paint();
//		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);
//
//		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
//
//		Paint paint = new Paint();
//		LinearGradient shader = new LinearGradient(0, 0, 0, 0, 0x00ffffff,
//				0xffffffff, TileMode.CLAMP);
//		paint.setShader(shader);
//		// Set the Transfer mode to be porter duff and destination in
//		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
//		// Draw a rectangle using the paint with our linear gradient
//		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
//				+ reflectionGap, paint);
//
//		return bitmapWithReflection;
//	}

	private void init() {
		mRainBowBitmap = BitmapFactory.decodeResource(getContext()
				.getResources(), R.drawable.rainbow);

		mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
		LinearGradient shader = new LinearGradient(0, 0, mScreenWidth, 0,
				0xffffffff, 0x00ffffff, Shader.TileMode.CLAMP);
		mTendencyFillPaint = new Paint();
		mTendencyFillPaint.setDither(true);
		mTendencyFillPaint.setShader(shader);
		mTendencyFillPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

		mShaderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				150, getContext().getResources().getDisplayMetrics());
		mAnim.setDuration(2000);
	}

	public void startAnim(int duration) {
		if (mAnim != null) {
			mAnim.setDuration(duration);
			this.startAnimation(mAnim);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.saveLayer(0, 0, getRight(), getBottom(), null,
				Canvas.ALL_SAVE_FLAG);// 保存当前画布
		canvas.drawBitmap(mRainBowBitmap, 0, 0, null);
		canvas.drawRect(0, 0, mRainBowBitmap.getWidth(),
				mRainBowBitmap.getHeight(), mTendencyFillPaint);
	}
}
