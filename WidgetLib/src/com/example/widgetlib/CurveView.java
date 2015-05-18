
package com.example.widgetlib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CurveView extends View {
    private class WipeAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime,
                Transformation t) {
            if (mWipeRect != null) {
                mWipeRect.set(
                        getLeft() + (int) (getWidth() * interpolatedTime),
                        getTop(), getRight(), getBottom());
            }
            postInvalidate();
            super.applyTransformation(interpolatedTime, t);
        }
    }

    private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG
            | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
            | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
            | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
    private boolean isInit = false;
    private WipeAnimation mAnim;
    private int mAxisHeight;
    private Paint mAxisPaint;
    private int mAxisWidth;
    private float[] mData;
    private int mHeight;
    private Point mOriginPoint;
    private float mPadding;
    private Paint mPaint;
    private Path mPath;
    private Paint mPointPaint;
    private int mPointRadius;
    private List<Point> mPoints;

    private float mTextSize;
    private int mWidth;

    private Paint mWipePaint;

    private Rect mWipeRect;

    public CurveView(Context context) {
        super(context);
        init();
    }

    public CurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CurveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private float getMax(float[] datas) {
        float max = 0.0f;
        if (datas != null && datas.length > 0) {
            max = datas[0];
            for (int i = 0; i < datas.length; i++) {
                float f = datas[i];
                max = Math.max(max, f);
            }
        }
        return max;
    }

    private void init() {
        mPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                getContext().getResources().getDisplayMetrics());
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10,
                getContext().getResources().getDisplayMetrics());

        mWipePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mWipePaint.setARGB(0, 0, 255, 0);
        mWipePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mWipePaint.setDither(true);
        mWipePaint.setStyle(Paint.Style.FILL);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Style.STROKE);

        mAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mAxisPaint.setColor(Color.WHITE);
        mAxisPaint.setStrokeWidth(2);

        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPointPaint.setColor(Color.YELLOW);
        mPointPaint.setTextAlign(Align.CENTER);
        mPointPaint.setTextSize(mTextSize);

        mPath = new Path();
        mPoints = new ArrayList<Point>();

        mWipeRect = new Rect();
        mAnim = new WipeAnimation();
        mAnim.setDuration(2000);

        mPointRadius = 4;
        mOriginPoint = new Point();
    }

    // 测试贝塞尔曲线的点，可以删除
    private float x;
    private float y;

    private void initData() {
        if (mData != null && mData.length > 0) {
            float max = getMax(mData);

            float rateY = mAxisHeight * .85f / max;
            float rateX = mData.length > 1 ? mAxisWidth * .9f
                    / (mData.length - 1) : 0;

            mPoints.clear();
            for (int i = 0; i < mData.length; i++) {
                float f = mData[i];
                Point point = new Point((int) (mOriginPoint.x + rateX * i),
                        (int) (mOriginPoint.y - f * rateY));
                mPoints.add(point);
                if (i == 0) {
                    mPath.moveTo(point.x, point.y);
                } else {
                    Point prePoint = mPoints.get(i - 1);
                    mPath.cubicTo((prePoint.x + point.x) / 2, prePoint.y,
                            (prePoint.x + point.x) / 2, point.y, point.x,
                            point.y);
                    // 测试贝塞尔曲线的点，可以删除
                    x = getX(prePoint.x, (prePoint.x + point.x) / 2, (prePoint.x + point.x) / 2,
                            point.x, 0.2f);
                    y = getX(prePoint.y, prePoint.y, point.y,
                            point.y, 0.2f);
                }
            }
            mWipeRect = new Rect();
            postInvalidate();
        }
        isInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLUE);

        canvas.drawLine(mOriginPoint.x, mOriginPoint.y - mAxisHeight,
                mOriginPoint.x, mOriginPoint.y, mAxisPaint);
        canvas.drawLine(mOriginPoint.x, mOriginPoint.y, mOriginPoint.x
                + mAxisWidth, mOriginPoint.y, mAxisPaint);
        int sc = canvas.saveLayer(0, 0, getRight(), getBottom(), null,
                LAYER_FLAGS);
        canvas.drawPath(mPath, mPaint);
        for (int i = 0; i < mPoints.size(); i++) {
            Point point = mPoints.get(i);
            canvas.drawCircle(point.x, point.y, mPointRadius, mPointPaint);
            canvas.drawText(String.valueOf(mData[i] + "MB"), point.x, point.y
                    - mTextSize, mPointPaint);
        }
        // 测试贝塞尔曲线的点，可以删除
        canvas.drawCircle(x, y, mPointRadius, mPointPaint);
        canvas.drawRect(mWipeRect, mWipePaint);
        canvas.restoreToCount(sc);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = getWidth();
        mHeight = getHeight();
        mWipeRect.set(this.getLeft(), this.getTop(), this.getRight(),
                this.getBottom());
        mAxisWidth = (int) (mWidth - 2.0f * mPadding);
        mAxisHeight = (int) (mAxisWidth * 0.618f);
        mOriginPoint.x = (int) mPadding;
        mOriginPoint.y = (int) ((mHeight + mAxisHeight) / 2.0f);
        if (!isInit && mWidth != 0) {
            initData();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setData(float[] datas) {
        mData = datas;
        isInit = false;
        if (mWidth != 0) {
            initData();
        }
    }

    public void startAnimation(int duration) {
        mAnim.setDuration(duration);
        startAnimation(mAnim);
    }

    /** 贝塞尔曲线方程 */
    public float getX(float p0, float p1, float p2, float p3, float t) {
        float x = 0;
        float t2 = 1 - t;
        x = p0 * t2 * t2 * t2 + 3 * p1 * t * t2 * t2 + 3 * p2 * t * t * t2 + p3
                * t * t * t;
        return x;
    }
}
