package com.johnnyyin.pullanimationdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * @author YinZhong
 * @since 2017/1/21
 */
public class PullLoadingView2 extends ImageView {
    private Context mContext;
    private Path mCenterPath;
    private int mWidth;
    private int mHeight;
    private Paint mPaint = null;  // 贝塞尔曲线画笔
    private Paint mBgPaint = null;

    /**
     * 当前的下拉进度
     */
    private float mCurrPullProgress;

    public PullLoadingView2(Context context) {
        this(context, null);
    }

    public PullLoadingView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullLoadingView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mCenterPath = new Path();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(Color.WHITE);
//        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBgPaint.setColor(Color.RED);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w <= 0 || h <= 0 || w != h) {
            return;
        }
        mWidth = w;
        mHeight = h;
    }

    public float getPullProgress() {
        return mCurrPullProgress;
    }


    public void setPullProgress(float curPullDistance, float maxPullDistance) {
        if (maxPullDistance <= 0) {
            throw new IllegalArgumentException("maxPullDistance must more than 0.");
        }
        float progress = curPullDistance / maxPullDistance;
        setPullProgress(progress);
    }

    public void setPullProgress(float progress) {
        if (progress > 1.0f) {
            progress = 1.0f;
        } else if (progress < 0.0f) {
            progress = 0.0f;
        }
        if (mCurrPullProgress == progress) {
            return;
        }
        mCurrPullProgress = progress;
        if (mWidth <= 0 || mHeight <= 0) {
            return;
        }
        float ratio = mWidth * 0.9f;
        float deltaY = (1f - mCurrPullProgress) * 0.22f;
        float c1x = getC1x(deltaY);
        float deltaX = 0.7165f - c1x;
        mCenterPath.reset();
        float d = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        mCenterPath.moveTo(ratio * 0.5f, 0f);
        mCenterPath.quadTo(ratio * getC1x(deltaY), ratio * (0.375f - deltaY), ratio * 0.933f, ratio * 0.75f);
        mCenterPath.quadTo(ratio * 0.5f, ratio * (0.75f + d), ratio * 0.067f, ratio * 0.75f);
        mCenterPath.quadTo(ratio * getC3x(deltaY), ratio * (0.375f - deltaY), ratio * 0.5f, ratio * 0f);
        clearAnimation();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.scale(mCurrPullProgress, mCurrPullProgress, mWidth / 2, mHeight / 2);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2, mBgPaint);
        canvas.translate(mWidth / 2, mHeight / 2);
        canvas.rotate(-150 * mCurrPullProgress);
        canvas.translate(-mWidth / 2, -mHeight / 2);
        canvas.translate(-mWidth * 0.05f, -mHeight * 0.05f);
        canvas.drawPath(mCenterPath, mPaint);
        canvas.translate(mWidth * 0.05f, mHeight * 0.05f);
    }

    private float getC1x(float deltaY) {
        return ((0.5f - (0.375f - deltaY)) / 0.577367206f + 0.5f);
    }

    private float getC3x(float deltaY) {
        return (((0.375f - deltaY) - 0.5f) / 0.577367206f + 0.5f);
    }

    @Override
    public void startAnimation(Animation animation) {
//        super.startAnimation(animation);
    }
}
