
package com.example.graphics_demo;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

/**
 * 绘制简单的图表
 * 
 * @author i
 * @email admin@atiter.com
 * @date 2013-8-8 下午12:51:51
 */
public class ChartView extends View {
    private static final String TAG = "ChartView";

    private AxisPoint[] mTendencyPoints;
    private AxisPoint[] mXAxisPoints;
    private AxisPoint[] mYAxisPoints;

    private Path mAxisPath;
    private Path mTendencyPath;
    private Path mTendencyFillPath;// 与坐标轴的填充路径

    private Paint mGridPaint;
    private Paint mAxisPaint;
    private Paint mAxisTextPaint;
    private Paint mOuterDotPaint;
    private Paint mInnerDotPaint;
    private Paint mTendencyPaint;
    private Paint mTendencyFillPaint;

    private Point mOriginPoint;// 原点

    private int mLineWidth = 3;
    private int mAxisWidth = 2;
    private int mXAxisNum = 8;
    private int mYAxisNum = 4;
    private int mTendencyPointNum = 8;
    private int mXAxisSpace = 60;// 单元格间距
    private int mYAxisSpace = mXAxisSpace;
    private int mAxisTextSize = 14;
    private int mCurNum = mTendencyPointNum - 1;// 控制曲线显示点的个数

    private boolean mShowAnimation = true;

    public class AxisPoint extends Point {
        private String text;

        public AxisPoint(int x, int y) {
            super(x, y);
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG |
            Canvas.CLIP_SAVE_FLAG |
            Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
            Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
            Canvas.CLIP_TO_LAYER_SAVE_FLAG;

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initPoints();
        initPaths();
        initPaints();
    }

    public void startAnimation() {
        if (mShowAnimation) {
            mChartAnimation.setDuration(mTendencyPointNum * 80);
            mChartAnimation.setInterpolator(new DecelerateInterpolator());
            startAnimation(mChartAnimation);
        }
    }

    private void initPoints() {
        mOriginPoint = new Point(mXAxisSpace, mYAxisSpace * (mYAxisNum + 1));

        Random random = new Random();
        mTendencyPoints = new AxisPoint[mTendencyPointNum];
        for (int i = 0; i < mTendencyPoints.length; i++) {
            int value = 80 + random.nextInt(40);
            mTendencyPoints[i] = new AxisPoint(mOriginPoint.x + mXAxisSpace * i,
                    value);
            mTendencyPoints[i].setText(value + "");
        }

        mXAxisPoints = new AxisPoint[mXAxisNum];
        mYAxisPoints = new AxisPoint[mYAxisNum];

        for (int i = 0; i < mXAxisPoints.length; i++) {
            mXAxisPoints[i] = new AxisPoint(mOriginPoint.x + (mXAxisSpace * i), mOriginPoint.y);
            mXAxisPoints[i].setText(i + "");
        }

        for (int i = 0; i < mYAxisPoints.length; i++) {
            mYAxisPoints[i] = new AxisPoint(mOriginPoint.x, mOriginPoint.y - (mYAxisSpace * i));
            mYAxisPoints[i].setText(i + "");
        }
    }

    private void initPaths() {
        mAxisPath = new Path();

        mAxisPath.moveTo(mOriginPoint.x, mOriginPoint.y - (mYAxisSpace * mYAxisNum));
        mAxisPath.lineTo(mOriginPoint.x, mOriginPoint.y);
        mAxisPath.lineTo(mOriginPoint.x + (mXAxisSpace * mXAxisNum), mOriginPoint.y);

        initTendencyLine();
    }

    private void initTendencyLine() {
        if (mShowAnimation) {
            mTendencyPath = new Path();

            if (mCurNum != 0) {
                mTendencyPath.moveTo(mTendencyPoints[0].x, mTendencyPoints[0].y);

                for (int i = 1; i <= mCurNum; i++) {
                    mTendencyPath.lineTo(mTendencyPoints[i].x, mTendencyPoints[i].y);
                }

                mTendencyFillPath = new Path(mTendencyPath);
                mTendencyFillPath.lineTo(mTendencyPoints[mCurNum].x, mOriginPoint.y);
                mTendencyFillPath.lineTo(mOriginPoint.x, mOriginPoint.y);
                mTendencyFillPath.close();
            } else {
                mTendencyFillPath = new Path(mTendencyPath);
            }
        } else {
            mTendencyPath = new Path();
            mTendencyPath.moveTo(mTendencyPoints[0].x, mTendencyPoints[0].y);
            for (int i = 1; i < mTendencyPoints.length; i++) {
                mTendencyPath.lineTo(mTendencyPoints[i].x, mTendencyPoints[i].y);
            }

            mTendencyFillPath = new Path(mTendencyPath);
            mTendencyFillPath.lineTo(mTendencyPoints[mTendencyPoints.length - 1].x,
                    mOriginPoint.y);
            mTendencyFillPath.lineTo(mOriginPoint.x, mOriginPoint.y);
            mTendencyFillPath.close();
        }
    }

    private void initPaints() {
        mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridPaint.setARGB(160, 6, 96, 206);
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setDither(true);
        mGridPaint.setStrokeWidth(1);

        mAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAxisPaint.setARGB(255, 255, 255, 255);// 纯白
        mAxisPaint.setStyle(Paint.Style.STROKE);
        mAxisPaint.setDither(true);
        mAxisPaint.setStrokeWidth(mAxisWidth);

        mAxisTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAxisTextPaint.setARGB(255, 255, 255, 255);// 纯白
        mAxisTextPaint.setTextAlign(Paint.Align.CENTER);
        mAxisTextPaint.setTextSize(mAxisTextSize);

        mTendencyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTendencyPaint.setARGB(255, 255, 255, 255);
        mTendencyPaint.setStyle(Paint.Style.STROKE);
        mTendencyPaint.setDither(true);
        mTendencyPaint.setStrokeWidth(mLineWidth);
        mTendencyPaint.setPathEffect(new CornerPathEffect(mLineWidth));

        LinearGradient lg = new LinearGradient(mOriginPoint.x, mOriginPoint.y, mOriginPoint.x,
                mYAxisPoints[mYAxisNum - 1].y,
                Color.argb(0, 255, 255, 255),
                Color.argb(64, 255, 255, 255),
                Shader.TileMode.MIRROR);
        mTendencyFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTendencyFillPaint.setARGB(255, 255, 255, 255);
        mTendencyFillPaint.setDither(true);
        mTendencyFillPaint.setShader(lg);

        mOuterDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterDotPaint.setARGB(255, 255, 255, 255);
        mOuterDotPaint.setDither(true);
        mOuterDotPaint.setStyle(Paint.Style.FILL);

        mInnerDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerDotPaint.setARGB(0, 0, 255, 0);
        mInnerDotPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));// 相交时清除相交部分
        mInnerDotPaint.setDither(true);
        mInnerDotPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBg(canvas);
        drawAxis(canvas);
        drawTendencyPath(canvas);
    }

    private void drawBg(Canvas canvas) {
        // 绘制背景
        canvas.drawColor(Color.argb(255, 6, 109, 206));

        int width = getWidth();
        int height = getHeight();

        int xNum = width / mXAxisSpace;
        int yNum = height / mYAxisSpace;

        float[] pts = new float[(xNum + yNum) * 4];
        for (int i = 0; i < xNum; i++) {
            pts[i * 4] = 0.0f + mXAxisSpace * (i + 1);
            pts[i * 4 + 1] = 0;
            pts[i * 4 + 2] = 0.0f + mXAxisSpace * (i + 1);
            pts[i * 4 + 3] = height;
        }
        for (int i = 0; i < yNum; i++) {
            int start = xNum * 4;
            pts[start + i * 4] = 0;
            pts[start + i * 4 + 1] = 0.0f + mYAxisSpace * (i + 1);
            pts[start + i * 4 + 2] = width;
            pts[start + i * 4 + 3] = 0.0f + mYAxisSpace * (i + 1);
        }

        canvas.drawLines(pts, mGridPaint);
    }

    private void drawAxis(Canvas canvas) {
        // 绘制坐标轴
        canvas.drawPath(mAxisPath, mAxisPaint);

        // 画坐标轴上的点
        for (int i = 0; i < mXAxisPoints.length; i++) {
            AxisPoint point = mXAxisPoints[i];
            canvas.drawLine(point.x, point.y, point.x, point.y - mAxisWidth - 2, mAxisPaint);
            canvas.drawText(point.getText(), point.x, point.y + mAxisTextSize + mAxisTextSize / 2,
                    mAxisTextPaint);
        }
        for (int i = 0; i < mYAxisPoints.length; i++) {
            AxisPoint point = mYAxisPoints[i];
            canvas.drawLine(point.x, point.y, point.x + mAxisWidth + 2, point.y, mAxisPaint);
            if (i != 0)
                canvas.drawText(point.getText(), point.x - mAxisTextSize, point.y + mAxisTextSize
                        / 2, mAxisTextPaint);
        }
    }

    private void drawTendencyPath(Canvas canvas) {
        canvas.saveLayer(0, 0, getRight(), getBottom(), null, Canvas.ALL_SAVE_FLAG);// 保存当前画布
        // 绘制曲线图
        canvas.drawPath(mTendencyPath, mTendencyPaint);
        canvas.drawPath(mTendencyFillPath, mTendencyFillPaint);

        // 绘制折点
        int drawNum = mCurNum;
        if (!mShowAnimation)
            drawNum = mTendencyPoints.length - 1;
        for (int i = 0; i <= drawNum; i++) {
            AxisPoint point = mTendencyPoints[i];
            canvas.drawCircle(point.x, point.y, mLineWidth + 2, mOuterDotPaint);
            canvas.drawCircle(point.x, point.y, mLineWidth, mInnerDotPaint);
            canvas.drawText(point.getText(), point.x, point.y - mAxisTextSize / 2 - 2,
                    mAxisTextPaint);
        }
    }

    private Animation mChartAnimation = new Animation() {
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            setmOffset(interpolatedTime);
        };
    };

    public boolean isShowAnimation() {
        return mShowAnimation;
    }

    public void setShowAnimation(boolean mShowAnimation) {
        this.mShowAnimation = mShowAnimation;
    }

    private void setmOffset(float offset) {
        int temp = (int) ((mTendencyPointNum - 1) * offset);
        if (temp != mCurNum) {
            mCurNum = temp;
            initTendencyLine();
            invalidate();
        }
    }

    public void destory() {
        if (mShowAnimation) {
            mChartAnimation.cancel();
        }
    }

}
