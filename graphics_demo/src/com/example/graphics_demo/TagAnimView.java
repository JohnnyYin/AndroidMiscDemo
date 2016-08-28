package com.example.graphics_demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/**
 * @author YinZhong
 * @since 16/8/27
 */
public class TagAnimView extends View {
    ///////////////////////////////////////////////////////////////////////////
    // View属性
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 黄色
     */
    private static final int DEFAULT_YELLOW_COLOR = 0xfffea03c;
    /**
     * 灰色
     */
    private static final int DEFAULT_GRAY_COLOR = 0xffb1b1b3;
    /**
     * 所有圆环直径占View宽度的最大比例
     */
    private static final float DIAMETER_MAX_RATIO = 0.98f;
    /**
     * view的默认尺寸
     */
    private static final int DEFAULT_VIEW_SIZE = 120;// 单位dp

    /**
     * View的尺寸
     */
    private int mSize;
    /**
     * View尺寸的1/2
     */
    private int mHalfSize;
    /**
     * Context
     */
    private Context mContext;

    ///////////////////////////////////////////////////////////////////////////
    // 边缘圆环
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 默认边缘圆环直径占view宽度的的比例
     */
    private static final float DEFAULT_EDGE_DIAMETER_RATIO = 0.76f;

    /**
     * 边缘圆环的画笔
     */
    private Paint mEdgePaint;
    /**
     * 边缘圆环的绘图半径, 不是真实半径。真实半径 = 绘图半径+画笔宽度的一半。
     */
    private float mEdgeRadius;
    /**
     * 边缘圆环线条当前的宽度
     */
    private float mEdgeStrokeWidth;
    /**
     * 边缘圆环线条最小的宽度
     */
    private float mEdgeMinStrokeWidth;
    /**
     * 边缘圆环线条最大的宽度
     */
    private float mEdgeMaxStrokeWidth;

    ///////////////////////////////////////////////////////////////////////////
    // 内部圆环
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 内部圆环直径最小值占view宽度的比例
     */
    private static final float RING_MIN_DIAMETER_RATIO = 0.0f;
    /**
     * 内部圆环内半径放大的结束时间点
     */
    private static final float RING_INNER_ENLARGE_END_TIME = 0.5f;

    /**
     * 内部圆环的画笔
     */
    private Paint mRingPaint;
    /**
     * 内部圆环的绘图半径, 不是真实半径。真实半径 = 绘图半径+画笔宽度的一半。
     */
    private float mRingRadius;

    ///////////////////////////////////////////////////////////////////////////
    // 中间文本
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 文本的画笔
     */
    private Paint mTextPaint;
    /**
     * 文本当前的size
     */
    private float mTextCurSize;
    /**
     * 文本最小的size
     */
    private float mTextMinSize;
    /**
     * 文本最大的size
     */
    private float mTextMaxSize;
    /**
     * 文本的绘制的起始x坐标
     */
    private float mTextStartX;
    /**
     * 文本的绘制的起始y坐标
     */
    private float mTextStartY;
    /**
     * 文本
     */
    private String mText;

    ///////////////////////////////////////////////////////////////////////////
    // 文本的下划线
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 下划线开始显示的起始时间点
     */
    private static final float UNDERLINE_START_SHOW_TIME = 0.2f;
    /**
     * 下划线的长度占view宽度的最小比例
     */
    private static final float UNDERLINE_MIN_RATIO = 0.68f;
    /**
     * 下划线的长度占view宽度的最大比例
     */
    private static final float UNDERLINE_MAX_RATIO = 0.9f;
    /**
     * 下划线的长度占view宽度的选中状态比例
     */
    private static final float UNDERLINE_SELECTED_RATIO = 0.74f;

    /**
     * 下划线的画笔
     */
    private Paint mUnderlinePaint;
    /**
     * 下划线的线宽
     */
    private float mUnderlineHeight;
    /**
     * 下划线的矩形
     */
    private RectF mUnderlineRectF;

    ///////////////////////////////////////////////////////////////////////////
    // 动画相关
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 动画的时长
     */
    private static final long ANIM_DURATION = 500;
    /**
     * 动画放大过程的结束时间点
     */
    private static final float ANIM_ENLARGE_END_TIME = 0.3f;
    /**
     * 动画放大过程的时长
     */
    private static final float ANIM_ENLARGE_DURATION = ANIM_ENLARGE_END_TIME;
    /**
     * 动画缩小过程的结束时间点
     */
    private static final float ANIM_NARROW_END_TIME = 0.6f;
    /**
     * 动画缩小过程的时长
     */
    private static final float ANIM_NARROW_DURATION = ANIM_NARROW_END_TIME - ANIM_ENLARGE_END_TIME;
    /**
     * 动画缩小过程缩小的比例
     */
    private static final float ANIM_NARROW_RATIO = 3 / 4.0f;
    /**
     * 动画回弹过程的结束时间点
     */
    private static final float ANIM_SPRINGBACK_END_TIME = 1.0f;
    /**
     * 动画回弹过程的时长
     */
    private static final float ANIM_SPRINGBACK_DURATION = ANIM_SPRINGBACK_END_TIME - ANIM_NARROW_END_TIME;
    /**
     * 动画回弹过程变换后的起始时间
     */
    private static final float ANIM_SPRINGBACK_TRANSFORM_START_TIME = ANIM_SPRINGBACK_END_TIME - (ANIM_NARROW_END_TIME - ANIM_ENLARGE_END_TIME) * ANIM_NARROW_RATIO;

    /**
     * 动画
     */
    private class TagAnimation extends Animation {

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            TagAnimView.this.applyTransformation(interpolatedTime);
        }
    }

    public TagAnimView(Context context) {
        super(context);
        init();
    }

    public TagAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TagAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        // View相关
        mContext = getContext();
        mSize = (int) dp2px(mContext, DEFAULT_VIEW_SIZE);
        mHalfSize = mSize / 2;

        // 边缘圆环相关
        mEdgeRadius = mSize * DEFAULT_EDGE_DIAMETER_RATIO / 2;

        mEdgeMinStrokeWidth = dp2px(mContext, 1);
        mEdgeMaxStrokeWidth = dp2px(mContext, 3);
        mEdgeStrokeWidth = mEdgeMinStrokeWidth;

        mEdgePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mEdgePaint.setStyle(Paint.Style.STROKE);
        mEdgePaint.setStrokeWidth(mEdgeStrokeWidth);
        mEdgePaint.setColor(DEFAULT_GRAY_COLOR);

        // 内部圆环相关
        mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setColor(DEFAULT_YELLOW_COLOR);

        // 文本相关
        mTextMinSize = sp2px(mContext, 14);
        mTextMaxSize = sp2px(mContext, 17);
        mTextCurSize = mTextMinSize;

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setColor(DEFAULT_GRAY_COLOR);
        mTextPaint.setTextSize(mTextCurSize);

        // 文本下划线相关
        mUnderlineHeight = dp2px(mContext, 2);
        mUnderlineRectF = new RectF();

        mUnderlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mUnderlinePaint.setColor(DEFAULT_YELLOW_COLOR);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 固定宽高
        setMeasuredDimension(mSize, mSize);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // 绘制边缘圆环
        canvas.drawCircle(mHalfSize, mHalfSize, mEdgeRadius, mEdgePaint);

        // 绘制内部圆环
        if (mRingRadius > 0) {
            canvas.drawCircle(mHalfSize, mHalfSize, mRingRadius, mRingPaint);
        }

        // 绘制文字
        if (!TextUtils.isEmpty(mText)) {
            if (mTextStartX <= 0) {
                measureText();
            }
            canvas.drawText(mText, mTextStartX, mTextStartY, mTextPaint);
        }

        // 绘制文字的下划线
        if (!mUnderlineRectF.isEmpty()) {
            canvas.drawRoundRect(mUnderlineRectF, mUnderlineHeight / 2, mUnderlineHeight / 2, mUnderlinePaint);
        }
    }

    /**
     * 测量文字
     */
    private void measureText() {
        mTextStartX = (mSize - mTextPaint.measureText(mText)) / 2;
        // 文本绘制在指定y坐标的上方。这里的y坐标是中心线往下偏移 1/3 文本size的距离。
        mTextStartY = mHalfSize + mTextCurSize / 3;
    }

    /**
     * 应用动画的变换
     */
    protected void applyTransformation(float interpolatedTime) {
        // 取消选择, 动画反过来执行
        interpolatedTime = isSelected() ? interpolatedTime : 1 - interpolatedTime;

        // 变换过后的插值
        float transformInterpolatedTime = getTransformInterpolation(interpolatedTime);

        // 边缘圆环
        // 边缘圆环的真实半径
        final float edgeRealRadius;
        {
            // 计算圆环宽度的变化
            mEdgeStrokeWidth = mEdgeMinStrokeWidth + (mEdgeMaxStrokeWidth - mEdgeMinStrokeWidth) * transformInterpolatedTime;
            mEdgePaint.setStrokeWidth(mEdgeStrokeWidth);

            // 计算颜色变化: 由灰色变为黄色
            if (interpolatedTime <= ANIM_ENLARGE_END_TIME) {
                final int red = (int) (Color.red(DEFAULT_GRAY_COLOR) + (Color.red(DEFAULT_YELLOW_COLOR) - Color.red(DEFAULT_GRAY_COLOR)) * transformInterpolatedTime);
                final int green = (int) (Color.green(DEFAULT_GRAY_COLOR) + (Color.green(DEFAULT_YELLOW_COLOR) - Color.green(DEFAULT_GRAY_COLOR)) * transformInterpolatedTime);
                final int blue = (int) (Color.blue(DEFAULT_GRAY_COLOR) + (Color.blue(DEFAULT_YELLOW_COLOR) - Color.blue(DEFAULT_GRAY_COLOR)) * transformInterpolatedTime);
                mEdgePaint.setColor(Color.argb(0xff, red, green, blue));
            } else {
                mEdgePaint.setColor(DEFAULT_YELLOW_COLOR);
            }

            // 计算半径变化
            edgeRealRadius = (mSize * (DEFAULT_EDGE_DIAMETER_RATIO + (DIAMETER_MAX_RATIO - DEFAULT_EDGE_DIAMETER_RATIO) * transformInterpolatedTime)) / 2;
            mEdgeRadius = edgeRealRadius - mEdgeStrokeWidth / 2;
        }

        // 内部圆环
        {
            // 回弹过程内部圆环不显示
            if (interpolatedTime <= ANIM_NARROW_END_TIME) {
                // 计算圆环外半径, 只有放大过程和外部半径不一致
                final float ringRealOuterRadius = (interpolatedTime >= ANIM_ENLARGE_END_TIME) ? edgeRealRadius
                    : (mSize * (RING_MIN_DIAMETER_RATIO + (DIAMETER_MAX_RATIO - RING_MIN_DIAMETER_RATIO) * transformInterpolatedTime)) / 2;
                // 计算圆环内半径的比例
                float innerRatio = interpolatedTime >= RING_INNER_ENLARGE_END_TIME ? 1.0f : interpolatedTime / RING_INNER_ENLARGE_END_TIME;
                innerRatio = innerRatio * innerRatio;// 开始速度慢, 后面加速
                // 计算圆环内半径
                final float ringRealInnerRadius = (mSize * (RING_MIN_DIAMETER_RATIO + (DIAMETER_MAX_RATIO - RING_MIN_DIAMETER_RATIO) * innerRatio)) / 2;
                // 计算画笔的宽度
                final float ringStrokeWidth = Math.max(ringRealOuterRadius - ringRealInnerRadius, 0.0f);
                mRingPaint.setStrokeWidth(ringStrokeWidth);

                // 计算内部圆环半径变化
                mRingRadius = ringRealOuterRadius - ringStrokeWidth / 2;

                // 计算Alpha值变化
                final int alpha;
                if (interpolatedTime <= ANIM_ENLARGE_END_TIME) {// 放大过程
                    alpha = 0xff;
                } else if (interpolatedTime <= ANIM_NARROW_END_TIME) {// 缩小过程
                    alpha = (int) (0xff - (0xff * (interpolatedTime - ANIM_ENLARGE_END_TIME) / ANIM_NARROW_DURATION));
                } else {// 回弹过程
                    alpha = 0x00;
                }
                mRingPaint.setColor(Color.argb(alpha, Color.red(DEFAULT_YELLOW_COLOR), Color.green(DEFAULT_YELLOW_COLOR), Color.blue(DEFAULT_YELLOW_COLOR)));
            } else {
                mRingRadius = 0;
            }
        }

        // 文本
        {
            // 计算文本size变化
            mTextCurSize = mTextMinSize + (mTextMaxSize - mTextMinSize) * transformInterpolatedTime;
            mTextPaint.setTextSize(mTextCurSize);
            // 根据变化后的size重新测量文本
            measureText();
        }

        // 下划线
        {
            if (interpolatedTime <= UNDERLINE_START_SHOW_TIME) {
                mUnderlineRectF.setEmpty();
            } else {
                // 计算下划线的起始y坐标。中心线往下偏移 3/4 个文本size的距离。这个值和 {@link #mTextStartY} 有关系
                final float underlineStartY = mHalfSize + mTextCurSize * 3 / 4;

                // 计算下划线的宽度占view宽度的比例
                final float underlineWidthRatio;
                if (interpolatedTime <= ANIM_ENLARGE_END_TIME) {// 放大过程
                    underlineWidthRatio = UNDERLINE_MIN_RATIO + (UNDERLINE_MAX_RATIO - UNDERLINE_MIN_RATIO) * interpolatedTime / ANIM_ENLARGE_DURATION;
                } else if (interpolatedTime <= ANIM_NARROW_END_TIME) {// 缩小过程
                    underlineWidthRatio = UNDERLINE_MAX_RATIO - (UNDERLINE_MAX_RATIO - UNDERLINE_MIN_RATIO) * (interpolatedTime - ANIM_ENLARGE_END_TIME) / ANIM_NARROW_DURATION;
                } else {// 回弹过程
                    underlineWidthRatio = UNDERLINE_MIN_RATIO + (UNDERLINE_SELECTED_RATIO - UNDERLINE_MIN_RATIO) * (interpolatedTime - ANIM_NARROW_END_TIME) / ANIM_SPRINGBACK_DURATION;
                }
                // 计算下划线的起始x坐标
                final float underlineStartX = (mSize - (mSize - mTextStartX * 2) * underlineWidthRatio) / 2;
                // 设置下划线矩形
                mUnderlineRectF.set(underlineStartX, underlineStartY, mSize - underlineStartX, underlineStartY + mUnderlineHeight);
            }
        }

        invalidate();
    }

    /**
     * 获得变换后的插值
     */
    private float getTransformInterpolation(float input) {
        if (input <= ANIM_ENLARGE_END_TIME) {// 放大过程: [0.0f ~ 0.3f] -> [0.0f ~ 1.0f];
            return (input / ANIM_ENLARGE_DURATION);
        } else if (input <= ANIM_NARROW_END_TIME) {// 缩小过程: (0.3f ~ 0.6f] -> (1.0f ~ 0.775f]; // TODO: 根据动画帧貌似0.78f是最佳
            return (ANIM_SPRINGBACK_END_TIME - (input - ANIM_ENLARGE_END_TIME) * ANIM_NARROW_RATIO);
        } else {
            return ANIM_SPRINGBACK_TRANSFORM_START_TIME + (input - ANIM_NARROW_END_TIME) / 5;// 回弹过程: (0.6f ~ 1.0f] -> (0.775f ~ 0.855f]; // TODO: 根据动画帧貌似0.86f是最佳
        }
    }

    /**
     * 开始动画
     */
    public void startAnim() {
        setSelected(!isSelected());
        clearAnimation();
        Animation animation = new TagAnimation();
        animation.setDuration(ANIM_DURATION);
        animation.setInterpolator(new LinearInterpolator());
        startAnimation(animation);
    }

    /**
     * 设置文本
     *
     * @param text 文本
     */
    public void setText(String text) {
        mText = text;
    }

    /**
     * dp转px
     */
    private static float dp2px(Context context, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     */
    private static float sp2px(Context context, int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

}
