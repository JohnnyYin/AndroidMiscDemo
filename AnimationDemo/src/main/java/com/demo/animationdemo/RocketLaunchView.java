package com.demo.animationdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.ArrayList;
import java.util.Random;


/**
 * 火箭发射，并带烟雾浮动效果
 */
public class RocketLaunchView extends View {
    private class PointX extends Point {
        /**
         * 趋势<li>正：增</li><li>负：减</li>
         */
        public int trend;

        private PointX(int x, int y) {
            super(x, y);
        }

        private PointX(int x, int y, int trend) {
            super(x, y);
            this.trend = trend;
        }
    }

    private class WipeAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            if (mCurveData != null) {
                updateCurveData();
            } else {
                setData(getCurveData());
            }
            postInvalidate();
            super.applyTransformation(interpolatedTime, t);
        }
    }

    private boolean isInit = false;
    private WipeAnimation mAnim;
    private int mHeight;
    private int mWidth;

    private float mPadding;

    private ArrayList<PointX[]> mCurveData;
    private Path[] mFillPath;
    private Paint[] mFillPaint;

    private Path mLauchPath;// 火箭发射时烟雾路径

    private Path mClipPath;// 剪切出绘制的圆

    private Bitmap mBgBmp;
    private Bitmap mRocketBmp;

    private RectF mFogBoxRect;// 绘制烟雾的矩形

    public RocketLaunchView(Context context) {
        super(context);
    }

    public RocketLaunchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RocketLaunchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

//    private float getMax(float[] datas) {
//        float max = 0.0f;
//        if (datas != null && datas.length > 0) {
//            max = datas[0];
//            for (int i = 0; i < datas.length; i++) {
//                float f = datas[i];
//                max = Math.max(max, f);
//            }
//        }
//        return max;
//    }

    private float px2dp(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                getResources().getDisplayMetrics());
    }

    private void init() {
        if (!isInit && mWidth != 0) {
            mPadding = px2dp(0);
            mFogBoxRect = new RectF(mPadding, mPadding, mWidth - mPadding, mHeight - mPadding);

            mBgBmp = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
            mRocketBmp = BitmapFactory.decodeResource(getResources(), R.drawable.rocket);

            mClipPath = new Path();
            mClipPath.addCircle(mWidth / 2, mHeight / 2, mWidth / 2 - px2dp(16), Path.Direction.CW);

            float rocketWidth = px2dp(20);
            float baseLine = mHeight * 5 / 6.0f;
            mLauchPath = new Path();
            mLauchPath.moveTo(mFogBoxRect.left, baseLine - mFogBoxRect.width() / 4.0f);
            RectF rectF = new RectF(mFogBoxRect.left, baseLine - mFogBoxRect.width() / 2.0f, mFogBoxRect.left + mFogBoxRect.width() / 2.0f - rocketWidth / 2, baseLine);
            mLauchPath.addArc(rectF, 180, -180);

            RectF rectF2 = new RectF(rectF.right + rocketWidth, rectF.top, mFogBoxRect.left + mFogBoxRect.width(), rectF.bottom);

            mLauchPath.moveTo(rectF.right, baseLine - mFogBoxRect.width() / 4.0f);
            mLauchPath.lineTo(rectF2.left, baseLine - mFogBoxRect.width() / 4.0f);

            mLauchPath.addArc(rectF2, 180, -180);

            mLauchPath.lineTo(mWidth, mHeight);
            mLauchPath.lineTo(0, mHeight);
            mLauchPath.lineTo(0, baseLine - mFogBoxRect.width() / 4.0f);

            mAnim = new WipeAnimation();
            mAnim.setDuration(2000);
        }
        isInit = true;
    }

    private void initData() {
        mFillPath = new Path[mCurveData.size()];
        mFillPaint = new Paint[mCurveData.size()];


        if (mCurveData != null && mCurveData.size() > 0) {
            for (int j = 0; j < mCurveData.size(); j++) {
                Point[] data = mCurveData.get(j);

                mFillPath[j] = new Path();

                Point prePoint = null;
                for (int i = 0; i < data.length; i++) {
                    Point point = data[i];
                    if (i == 0) {
                        mFillPath[j].moveTo(point.x, point.y);
                    } else {
                        mFillPath[j].cubicTo((prePoint.x + point.x) / 2, prePoint.y,
                                (prePoint.x + point.x) / 2, point.y, point.x,
                                point.y);
                    }
                    prePoint = point;
                }

                mFillPath[j].lineTo(prePoint.x, mFogBoxRect.bottom);
                mFillPath[j].lineTo(mFogBoxRect.left, mFogBoxRect.bottom);
                mFillPath[j].close();

                LinearGradient lg = new LinearGradient(0, mHeight, 0,
                        0,
                        Color.argb(0 + j * 20, 0, j * 30, 204),
                        Color.argb(64 + j * 20, 0, j * 30 + 10, 204),
                        Shader.TileMode.MIRROR);
                mFillPaint[j] = new Paint(Paint.ANTI_ALIAS_FLAG);
                mFillPaint[j].setARGB(255, 0, 80, 204);
                mFillPaint[j].setDither(true);
                mFillPaint[j].setShader(lg);
            }
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        // 首先绘制背景
        if (mBgBmp != null) {
            Rect src = new Rect(0, 0, mBgBmp.getWidth(), mBgBmp.getHeight());
            Rect dst = new Rect(0, 0, mWidth, mHeight);
            canvas.drawBitmap(mBgBmp, src, dst, null);
        }

        if (mClipPath != null) {
            canvas.clipPath(mClipPath, Region.Op.INTERSECT);
        }

        if (mFillPath != null && mLauchPath != null) {
            // 漂浮烟雾
            for (int i = 0; i < mFillPath.length; i++) {
                Path fillPath = mFillPath[i];
                canvas.drawPath(fillPath, mFillPaint[i]);
            }
            canvas.drawPath(mLauchPath, mFillPaint[mFillPath.length - 1]);
        }

        // 绘制火箭
        if (mRocketBmp != null) {
            float baseLine = mHeight * 5 / 6.0f;
            canvas.drawBitmap(mRocketBmp, (mWidth - mRocketBmp.getWidth()) / 2, baseLine - mWidth / 4.0f - mRocketBmp.getHeight(), null);
        }

        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = getWidth();
        mHeight = getHeight();

        if (!isInit && mWidth != 0) {
            init();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setData(ArrayList<PointX[]> datas) {
        mCurveData = datas;
        if (mWidth != 0) {
            initData();
        }
    }

    public void startAnimation(int duration) {
        mAnim.setDuration(duration);
        mAnim.setRepeatCount(-1);
        startAnimation(mAnim);
    }

    /**
     * 贝塞尔曲线方程
     */
//    public float getX(float p0, float p1, float p2, float p3, float t) {
//        float x = 0;
//        float t2 = 1 - t;
//        x = p0 * t2 * t2 * t2 + 3 * p1 * t * t2 * t2 + 3 * p2 * t * t * t2 + p3
//                * t * t * t;
//        return x;
//    }

    private static int sCurveRate = 100;
    private static int sCurveMax = 35;
    private static int sCurveMin = 20;
    private static int sCurvePointNum = 5;

    public ArrayList<PointX[]> getCurveData() {
        Random random = new Random();
        ArrayList<PointX[]> datas = new ArrayList<PointX[]>();
        int offset = sCurveMax - sCurveMin;
        PointX[] data1 = new PointX[sCurvePointNum];
        float rate = mFogBoxRect.height() / 100;
        sCurveMax = (int) (mFogBoxRect.top + rate * 85);
        sCurveMin = (int) (mFogBoxRect.top + rate * 65);

        data1[0] = new PointX((int) mFogBoxRect.left, (int) (mFogBoxRect.top + rate * 62), 1);
        data1[1] = new PointX((int) (mFogBoxRect.left + mFogBoxRect.width() * 0.3), (int) (mFogBoxRect.top + rate * 75), -1);
        data1[2] = new PointX((int) (mFogBoxRect.left + mFogBoxRect.width() * 0.5), (int) (mFogBoxRect.top + rate * 85), 1);
        data1[3] = new PointX((int) (mFogBoxRect.left + mFogBoxRect.width() * 0.7), (int) (mFogBoxRect.top + rate * 72), -1);
        data1[4] = new PointX((int) (mFogBoxRect.left + mFogBoxRect.width() * 1.0), (int) (mFogBoxRect.top + rate * 64), 1);

        PointX[] data2 = new PointX[sCurvePointNum];
        data2[0] = new PointX((int) mFogBoxRect.left, (int) (mFogBoxRect.top + rate * 67), -1);
        data2[1] = new PointX((int) (mFogBoxRect.left + mFogBoxRect.width() * 0.3), (int) (mFogBoxRect.top + rate * 72), 1);
        data2[2] = new PointX((int) (mFogBoxRect.left + mFogBoxRect.width() * 0.5), (int) (mFogBoxRect.top + rate * 80), -1);
        data2[3] = new PointX((int) (mFogBoxRect.left + mFogBoxRect.width() * 0.7), (int) (mFogBoxRect.top + rate * 70), 1);
        data2[4] = new PointX((int) (mFogBoxRect.left + mFogBoxRect.width() * 1.0), (int) (mFogBoxRect.top + rate * 60), -1);

        PointX[] data3 = new PointX[sCurvePointNum];
        data3[0] = new PointX((int) mFogBoxRect.left, (int) (mFogBoxRect.top + rate * 70), 1);
        data3[1] = new PointX((int) (mFogBoxRect.left + mFogBoxRect.width() * 0.3), (int) (mFogBoxRect.top + rate * 78), -1);
        data3[2] = new PointX((int) (mFogBoxRect.left + mFogBoxRect.width() * 0.5), (int) (mFogBoxRect.top + rate * 82), 1);
        data3[3] = new PointX((int) (mFogBoxRect.left + mFogBoxRect.width() * 0.7), (int) (mFogBoxRect.top + rate * 70), -1);
        data3[4] = new PointX((int) (mFogBoxRect.left + mFogBoxRect.width() * 1.0), (int) (mFogBoxRect.top + rate * 78), 1);

        datas.add(data1);
        datas.add(data2);
        datas.add(data3);
        return datas;
    }

    private void updateCurveData() {
        if (mCurveData != null) {
            Random random = new Random();
            for (int i = 0; i < mCurveData.size(); i++) {
                boolean odd = (i % 2 != 0);
                PointX[] data = mCurveData.get(i);
                // 奇数从左往右移动

//                int offset = sCurveMax - sCurveMin;
//                if (odd) {
//                    for (int j = data.length - 1; j >= 0; j--) {
//                        if (j == 0) {
//                            data[j] = sCurveMin + random.nextInt(offset);
//                        } else {
//                            data[j] = data[j - 1];
//                        }
//                        Log.i("XXX", "RocketLaunchView.updateCurveData()" + data[j]);
//                    }
//                } else {// 偶数相反
//                    for (int j = 0; j < data.length; j++) {
//                        if (j == data.length - 1) {
//                            data[j] = sCurveMin + random.nextInt(offset);
//                        } else {
//                            data[j] = data[j + 1];
//                        }
//                        Log.i("XXX", "RocketLaunchView.updateCurveData()" + data[j]);
//                    }
//                }
                for (int j = 0; j < data.length; j++) {
                    data[j].x = data[j].x - 1 + random.nextInt(3);
                    int temp = data[j].y + data[j].trend;
                    if (temp > sCurveMin && temp < sCurveMax) {
                        data[j].y = temp;
                    } else {
                        data[j].trend = -data[j].trend;
                        data[j].y += data[j].trend;
                    }
                    Log.i("XXX", "RocketLaunchView.updateCurveData()" + data[j]);
                }
            }
            setData(mCurveData);
        }
    }

    public void recycle() {
        if (mBgBmp != null && !mBgBmp.isRecycled()) {
            mBgBmp.recycle();
        }
        if (mRocketBmp != null && !mRocketBmp.isRecycled()) {
            mRocketBmp.recycle();
        }
    }
}