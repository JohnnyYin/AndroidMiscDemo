
package com.example.graphics_demo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;

public class PieChartView extends View implements OnTouchListener {
    public static class PieItem {
        public float precent;// 0~1
        public int color;
        public String description;
        float angle;
        boolean check;
        float interpolatedTime;
        Rect descRect;

        public PieItem(float precent, int color, String description) {
            this.color = color;
            this.description = description;
            this.precent = precent;
            this.check = true;
        }
    }

    private static final int DESC_DIRECTION_RIGHT = 1;
    private static final int DESC_DIRECTION_BOTTOM = 2;
    private static final int DESC_DIRECTION_LEFT = 3;
    private static final int DESC_DIRECTION_TOP = 4;

    private Path mPiePath;

    private Paint mPiePaint;
    private Paint mPieTextPaint;
    private Paint mPieDescPaint;
    private Paint mShadowPaint;

    private RectF mPieRectF;
    private RectF mPieDescRectF;

    private int mWidth;
    private int mHeight;
    private int mPieWidth;
    private int mPieHeight;
    private int mPieDescWidth;
    private int mPieDescHeight;
    private int mAnimDuration = 200;
    private int mDescDirection;
    private int mDescRowNum;// 列数

    private float mCheckOffset;
    private float mPadding;
    private float mRadius;// 半径
    private float mFontSize = 14;// 单位：sp
    private float mDescBlockWidth = 25;// 单位：dp
    private float mDescBlockHeight = 15;// 单位：dp
    private float mDescBlockMargin = 15;// 单位：dp
    private float mDescItemWidth;// 单位：dp
    private float mInterpolatedTime;// 当前动画的运行时间
    private float mInterpolatedTimeOffset;// 增量

    private float[] mPieCenterPoint = new float[2];

    private List<PieItem> mData = new ArrayList<PieItem>();

    private Animation mAnim = new Animation() {
        protected void applyTransformation(float interpolatedTime,
                Transformation t) {
            mInterpolatedTimeOffset = interpolatedTime > mInterpolatedTime ? interpolatedTime
                    - mInterpolatedTime
                    : 0.0f;
            mInterpolatedTime = interpolatedTime;
            postInvalidate();
            super.applyTransformation(interpolatedTime, t);
        };
    };

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private float[] getCenterPoint(float centerAngle) {
        float[] point = new float[2];
        point[0] = mPieCenterPoint[0] + (mRadius / 2.0f)
                * (float) Math.cos(centerAngle / 180 * Math.PI);
        point[1] = mPieCenterPoint[1] + (mRadius / 2.0f)
                * (float) Math.sin(centerAngle / 180 * Math.PI);
        return point;
    }

    /**
     * 在指定角度上的偏移量
     * 
     * @param base
     * @param centerAngle
     * @return
     */
    public float[] getOffset(float centerAngle) {
        return getOffset(mInterpolatedTime, centerAngle);
    }

    public float[] getOffset(float interpolatedTime, float centerAngle) {
        float[] point = new float[2];
        point[0] = interpolatedTime * mCheckOffset
                * (float) Math.cos(centerAngle / 180 * Math.PI);
        point[1] = interpolatedTime * mCheckOffset
                * (float) Math.sin(centerAngle / 180 * Math.PI);
        return point;
    }

    private void init() {
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                mFontSize, getResources().getDisplayMetrics());
        mDescBlockWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                25.0f, getResources().getDisplayMetrics());
        mDescBlockHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                14.0f, getResources().getDisplayMetrics());
        mDescBlockMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                8.0f, getResources().getDisplayMetrics());
        mCheckOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                10.0f, getResources().getDisplayMetrics());// 10dp
        mPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15.0f,
                getResources().getDisplayMetrics());// 15dp

        mPiePath = new Path();

        mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPiePaint.setColor(Color.WHITE);
        // mPiePaint.setShadowLayer(textSize / 2, textSize / 4, textSize / 4,
        // 0xCC000000);

        mPieTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPieTextPaint.setColor(Color.WHITE);
        mPieTextPaint.setTextAlign(Align.CENTER);
        mPieTextPaint.setTextSize(textSize);
        mPieTextPaint.setShadowLayer(textSize / 2, textSize / 4, textSize / 4,
                0xCC000000);

        mPieDescPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPieDescPaint.setColor(Color.BLACK);
        mPieDescPaint.setTextAlign(Align.LEFT);
        mPieDescPaint.setTextSize(mDescBlockHeight);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setColor(Color.BLACK);
        mShadowPaint.setShadowLayer(textSize / 2, textSize / 4, textSize / 4,
                0xCC000000);

        mPieRectF = new RectF();
        mPieDescRectF = new RectF();

        mAnim.setDuration(mAnimDuration);
        mAnim.setInterpolator(new OvershootInterpolator(6.0f));
        this.setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // canvas.translate((mWidth - mPieWidth) / 2, (mHeight - mPieHeight) /
        // 2);
        float currAngle = 0.0f;
        for (int i = 0; i < mData.size(); i++) {
            PieItem item = mData.get(i);
            float centerAngle = currAngle + item.angle / 2;

            mPiePath.reset();
            mPiePath.moveTo(mPieCenterPoint[0], mPieCenterPoint[1]);
            mPiePath.arcTo(mPieRectF, currAngle, item.angle);// 与X轴重合的位置为0°，第三个参数为扫过的角度，正为顺时针旋转，负为逆时针
            mPiePath.close();
            mPiePaint.setColor(item.color);

            float[] offset = null;
            if (item.check) {
                offset = getOffset(centerAngle);
                item.interpolatedTime = mInterpolatedTime;
                canvas.translate(offset[0], offset[1]);
            } else if (item.interpolatedTime > 0.0f) {
                float interpolatedTime = item.interpolatedTime
                        - mInterpolatedTimeOffset;// 每次减去增量
                if (interpolatedTime > 0) {
                    offset = getOffset(interpolatedTime, centerAngle);
                    item.interpolatedTime = interpolatedTime;
                    canvas.translate(offset[0], offset[1]);
                } else {
                    item.interpolatedTime = 0;
                }
            }
            canvas.drawPath(mPiePath, mPiePaint);
            float[] textPoint = getCenterPoint(centerAngle);
            canvas.drawText(item.description, textPoint[0], textPoint[1],
                    mPieTextPaint);
            if (item.check) {
                canvas.translate(-offset[0], -offset[1]);
            } else if (item.interpolatedTime > 0.0f) {
                if (offset != null)
                    canvas.translate(-offset[0], -offset[1]);
            }
            currAngle += item.angle;

            // 绘制描述
            mPieDescPaint.setColor(item.color);
            canvas.drawRect(item.descRect, mPieDescPaint);
            canvas.drawText(item.description, item.descRect.right + mDescBlockWidth / 3,
                    item.descRect.bottom, mPieDescPaint);
        }
        // canvas.translate(-(mWidth - mPieWidth) / 2, -(mHeight - mPieHeight) /
        // 2);
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        if (mWidth != 0 && mHeight != 0) {
            mDescDirection = mWidth > mHeight ? DESC_DIRECTION_RIGHT : DESC_DIRECTION_BOTTOM;
            mPieWidth = Math.min(mWidth, mHeight);
            mPieHeight = mPieWidth;
            mRadius = mPieWidth / 2;
            // canvas.translate((mWidth - mPieWidth) / 2, (mHeight - mPieHeight)
            // /
            // 2);
            switch (mDescDirection) {
                case DESC_DIRECTION_RIGHT: {
                    mPieRectF.set(mPadding + (mWidth - mPieWidth) / 2, mPadding
                            + (mHeight - mPieHeight)
                            / 2,
                            (mWidth - mPieWidth) / 2 + mPieWidth - mPadding, (mHeight - mPieHeight)
                                    / 2
                                    + mPieHeight
                                    - mPadding);
                    mPieCenterPoint[0] = mPieRectF.centerX();
                    mPieCenterPoint[1] = mPieRectF.centerY();

                    mPieDescRectF.set(mPieRectF.left, mPieRectF.bottom, mPieRectF.right,
                            mPieRectF.bottom
                                    + mHeight - mPieHeight);
                }
                    break;
                case DESC_DIRECTION_BOTTOM: {
                    mPieRectF.set(mPadding, mPadding, mPieWidth - mPadding, mPieHeight
                            - mPadding);
                    mPieCenterPoint[0] = mPieRectF.centerX();
                    mPieCenterPoint[1] = mPieRectF.centerY();

                    mPieDescRectF.set(mPieRectF.left, mPieRectF.bottom, mPieRectF.right,
                            mPieRectF.bottom
                                    + mHeight - mPieHeight);

                    mDescRowNum = 3;
                    mDescItemWidth = mPieDescRectF.width() / mDescRowNum;
                }
                    break;
            }
            initDescRect();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float x = event.getX() - mPieCenterPoint[0];
                float y = event.getY() - mPieCenterPoint[1];
                // Log.e("graphics_demo", "[PieChartView]-[onTouch]:" + x + ":"
                // +
                // y);
                if (x * x + y * y < mRadius * mRadius) {
                    float totalAngle = 0.0f;
                    double angle = Math.atan(y / x) * 180.0f / Math.PI;
                    if (x > 0) {// 第一，四象限
                        if (y < 0) {// 第四象限
                            angle = 360 + angle;
                        }
                    } else {// 第二,三象限
                        angle += 180;
                    }
                    for (int i = 0; i < mData.size(); i++) {
                        PieItem data = mData.get(i);
                        if (angle >= totalAngle && angle < totalAngle + data.angle) {
                            data.check = !data.check;
                            for (int j = 0; j < mData.size(); j++) {
                                if (i == j)
                                    continue;
                                mData.get(j).check = false;
                            }
                            startAnimation(mAnim);
                            break;
                        }
                        totalAngle += data.angle;
                    }
                }
                break;
        }
        return true;
    }

    public void setData(List<PieItem> data) {
        mData.clear();
        if (data != null && data.size() > 0) {
            for (PieItem pieItem : data)
                pieItem.angle = pieItem.precent * 360.0f;
            mData.addAll(data);
        }
    }

    private void initDescRect() {
        if (mData != null) {
            for (int i = 0; i < mData.size(); i++) {
                PieItem item = mData.get(i);
                int line = i / mDescRowNum;
                int num = i % mDescRowNum;
                int left = (int) (mPieDescRectF.left + mDescBlockMargin + num * mDescItemWidth);
                int top = (int) (mPieDescRectF.top + mDescBlockMargin + mDescBlockMargin + line
                        * (mDescBlockHeight * 2));
                Rect rect = new Rect(left,
                        top,
                        (int) (left + mDescBlockWidth),
                        (int) (top + mDescBlockHeight));
                item.descRect = rect;
            }
        }
    }

    public void setFontSize(float fontSize) {
        this.mFontSize = fontSize;
    }

    public void setAnimDuration(int duration) {
        this.mAnimDuration = duration;
    }
}
