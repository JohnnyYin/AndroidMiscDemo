package com.stackviewdemo.rotateanimation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class RotateView extends View {

    private class RotateAnim extends Animation {
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            RotateView.this.applyTransformation(interpolatedTime);
            super.applyTransformation(interpolatedTime, t);
        }
    }

    private static final int DEFAULT_DURATION = 1080;
    private Animation mAnim = new RotateAnim();

    private Context mContext;
    private Bitmap mBmpLogo;
    private Bitmap mBmpCircle;
    private Bitmap mBmpTemp;

    private Rect mTopLogoSrc;
    private Rect mTopLogoDst;
    private Rect mBottomLogoSrc;
    private Rect mBottomLogoDst;
    private Rect mTopCircleSrc;
    private Rect mTopCircleDst;
    private Rect mBottomCircleSrc;
    private Rect mBottomCircleDst;

    private Paint mPaint;

    private int mWidth;
    private int mHeight;
    private int mBmpLogoHeight;
    private int mBmpLogoWidth;
    private int mBmpCircleHeight;
    private int mBmpCircleWidth;

    private float mCircleScale = 0.4f;// 圆的压缩比例
    private float mDegrees = 0.0f;// 帧之间的角度

    public RotateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public RotateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public RotateView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getWidth();
        mHeight = getHeight();
        if (mWidth != mHeight) {
            throw new RuntimeException("RotateView must be a quadrate.");
        }
        initRectIfNeed();
    }

    private void init() {
        mBmpLogo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        mBmpCircle = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        mBmpLogoHeight = mBmpLogo.getHeight();
        mBmpLogoWidth = mBmpLogo.getWidth();
        mBmpCircleHeight = mBmpCircle.getHeight();
        mBmpCircleWidth = mBmpCircle.getWidth();

        if (mBmpLogoHeight != mBmpLogoWidth || mBmpCircleHeight != mBmpCircleWidth) {
            throw new RuntimeException("RotateView image resource must be a quadrate.");
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mAnim.setRepeatCount(Animation.INFINITE);
        mAnim.setInterpolator(new LinearInterpolator());
    }

    private void initRectIfNeed() {
        if (mWidth == 0 || mHeight == 0)
            return;
        // logo部分
        mTopLogoSrc = new Rect(0, 0, mBmpLogoWidth, mBmpLogoHeight / 2);
        mBottomLogoSrc = new Rect(0, mBmpLogoHeight / 2, mBmpLogoWidth, mBmpLogoHeight);

        if (mBmpLogoWidth >= mWidth) {
            mTopLogoDst = new Rect(0, 0, mWidth, mHeight / 2);
            mBottomLogoDst = new Rect(0, mHeight / 2, mWidth, mHeight);
        } else {
            int space = (mWidth - mBmpLogoWidth) / 2;
            mTopLogoDst = new Rect(space, space, space + mBmpLogoWidth, mHeight / 2);
            mBottomLogoDst = new Rect(space, mHeight / 2, space + mBmpLogoWidth, mHeight - space);
        }

        // circle部分
        mTopCircleSrc = new Rect(0, 0, mBmpCircleWidth, mBmpCircleHeight / 2);
        mBottomCircleSrc = new Rect(0, mBmpCircleHeight / 2, mBmpCircleWidth, mBmpCircleHeight);

        if (mBmpCircleWidth >= mWidth) {
            int circleRealHeight = (int) (mBmpCircleHeight * mCircleScale);
            int ySpace = (mHeight - circleRealHeight) / 2;
            mTopCircleDst = new Rect(0, ySpace, mWidth, mHeight / 2);
            mBottomCircleDst = new Rect(0, mHeight / 2, mWidth, mHeight - ySpace);
        } else {
            int circleRealHeight = (int) (mBmpCircleHeight * mCircleScale);
            int ySpace = (mHeight - circleRealHeight) / 2;
            int xSpace = (mWidth - mBmpCircleWidth) / 2;
            mTopCircleDst = new Rect(xSpace, ySpace, xSpace + mBmpCircleWidth, mHeight / 2);
            mBottomCircleDst = new Rect(xSpace, mHeight / 2, xSpace + mBmpCircleWidth, mHeight - ySpace);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWidth == 0 || mHeight == 0 || mTopLogoSrc == null)
            return;
        Bitmap circleBitmap = getCircleBitmap(mBmpCircle);
        // 绘制上半部分
        canvas.drawBitmap(circleBitmap, mTopCircleSrc, mTopCircleDst, mPaint);
        canvas.drawBitmap(mBmpLogo, mTopLogoSrc, mTopLogoDst, mPaint);
        // 绘制下半部分
        canvas.drawBitmap(mBmpLogo, mBottomLogoSrc, mBottomLogoDst, mPaint);
        canvas.drawBitmap(circleBitmap, mBottomCircleSrc, mBottomCircleDst, mPaint);
        circleBitmap.recycle();
    }

    private Bitmap getCircleBitmap(Bitmap bm) {
        Bitmap newb = Bitmap.createBitmap(mBmpCircleWidth, mBmpCircleHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newb);
        cv.rotate(mDegrees, mBmpCircleWidth / 2, mBmpCircleWidth / 2);
        mBmpTemp = Bitmap.createBitmap(bm, 0, 0, mBmpCircleWidth, mBmpCircleHeight);
        cv.drawBitmap(mBmpTemp, 0, 0, mPaint);
        return newb;
    }

    private void applyTransformation(float interpolatedTime) {
        mDegrees = interpolatedTime * 360;
        postInvalidate();
    }

    public void startAnim() {
        mAnim.setDuration(DEFAULT_DURATION);
    }

    public void startAnim(int duration) {
        mAnim.setDuration(duration);
        clearAnimation();
        startAnimation(mAnim);
    }

    public void recycle() {
        clearAnimation();
        if (mBmpTemp != null && !mBmpTemp.isRecycled()) {
            mBmpTemp.recycle();
        }
        if (mBmpLogo != null && !mBmpLogo.isRecycled()) {
            mBmpLogo.recycle();
            if (mBmpCircle != null && !mBmpCircle.isRecycled()) {
                mBmpCircle.recycle();
            }
        }
    }

}
