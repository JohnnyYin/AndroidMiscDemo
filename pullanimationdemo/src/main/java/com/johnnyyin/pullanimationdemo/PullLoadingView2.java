package com.johnnyyin.pullanimationdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 下拉刷新动画View
 *
 * @author YinZhong
 * @since 2017/1/21
 */
public class PullLoadingView2 extends ImageView implements Animation.AnimationListener {
    /**
     * 圆球
     */
    private static class Ball {
        /**
         * 起点x坐标
         */
        final float startX;
        /**
         * 起点y坐标
         */
        final float startY;
        /**
         * 半径
         */
        final float radius;
        /**
         * 运行的角度
         */
        final float angle;
        /**
         * 圆心
         */
        final PointF centrePoint = new PointF();
        /**
         * 路径
         */
        final Path path = new Path();
        /**
         * 开始时间
         */
        final float startTime;
        /**
         * 是否可用
         */
        boolean enabled;

        public Ball(float radius, int angle, float containerSize, float centreCircleRadius, float startTime) {
            this.radius = radius;
            this.angle = (float) (angle / 180f * Math.PI);
            this.centrePoint.x = this.centrePoint.y = containerSize / 2;
            this.startTime = startTime;
            movePoint2(centrePoint, this.angle, centreCircleRadius - radius);
            this.startX = this.centrePoint.x;
            this.startY = this.centrePoint.y;
        }
    }

    /**
     * 下拉刷新动画类
     */
    private class PullLoadingAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            dispatchApplyTransformation(interpolatedTime, t);
        }
    }

    /**
     * Log TAG
     */
    private static final String TAG = "PullLoadingView2";

    ///////////////////////////////////////////////////////////////////////////
    // 各种控制常量begin
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 默认的动画时长
     */
    private static final long DEFAULT_ANIMATION_DURATION = 1700;// 默认1.7秒
    /**
     * 每个小球移动的时长
     */
    private static final long BALL_TRANSLATE_DURATION = DEFAULT_ANIMATION_DURATION / 4;
    /**
     * 圆圈和三角变换过程中旋转的角度
     */
    private static final float CIRCLE_TRIANGLE_TRANSFORM_ROTATE_ANGLE = 270;
    /**
     * Loading在动画周期内旋转的角度
     */
    private static final int LOADING_ROTATE_ANGLE_ON_ANIMATION = 4 * 360;
    /**
     * 圆圈颜色-红色
     */
    private static final int CIRCLE_COLOR_RED = 0xffff334c;
    /**
     * 圆圈颜色-灰色
     */
    private static final int CIRCLE_COLOR_GREY = 0xffc4c4c4;
    /**
     * Loading的颜色
     */
    private static final int LOADING_COLOR = 0xfff0f0f0;
    /**
     * 中心圆的size比例
     */
    private static final float CENTRE_CIRCLE_SIZE_RATIO = 0.5f;
    /**
     * Loading与边界的padding比例
     */
    private static final float LOADING_PADDING_RATIO = 0.31f;
    /**
     * Loading的size与控件size的比例
     */
    private static final float LOADING_SIZE_RATIO = 0.02f;
    /**
     * 下拉状态
     */
    private static final int STATE_PULL = 0;
    /**
     * 回弹状态
     */
    private static final int STATE_REBOUND = 1;
    /**
     * 动画状态
     */
    private static final int STATE_ANIMATING = 2;
    /**
     * Loading开始由点变为弧形在回弹过程中的开始进度
     */
    private static final float LOADING_TRANSFORM_START_PROGRESS_IN_REBOUND = 0.2f;
    ///////////////////////////////////////////////////////////////////////////
    // 各种控制常量end
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // 通用begin
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 控件的size
     */
    private int mSize;
    /**
     * 控件的size的一半
     */
    private int mHalfSize;
    /**
     * 临时用的点
     */
    private PointF mTmpPointF = new PointF();
    /**
     * 整个控件展示的矩阵
     */
    private RectF mDisplayRect = new RectF();
    /**
     * 临时用的RectF
     */
    private RectF mTmpRectF = new RectF();
    /**
     * 当前的下拉进度
     */
    private float mCurrPullProgress;
    /**
     * 当前状态
     */
    private int mState = STATE_PULL;
    /**
     * 动画的插入时间
     */
    private float mAnimationInterpolatedTime = -1f;
    /**
     * 渐变效果的遮罩画笔
     */
    private Paint mCoverPaint;
    ///////////////////////////////////////////////////////////////////////////
    // 通用end
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // 中心圆相关begin
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 中心圆的半径
     */
    private float mCentreCircleRadius;
    /**
     * 中心圆的画笔
     */
    private Paint mCentreCirclePaint;
    /**
     * 中心圆的Matrix
     */
    private Matrix mCentreCircleMatrix;
    /**
     * 中心圆的路径
     */
    private Path mCentreCirclePath;
    ///////////////////////////////////////////////////////////////////////////
    // 中心圆相关end
    ///////////////////////////////////////////////////////////////////////////
    private static final float kFactor = 2f;

    /**
     * 中间Loading的画笔
     */
    private Paint mLoadingPaint;
    /**
     * 中间Loading的半径
     */
    private float mLoadingRadius;
    /**
     * 中间Loading与边界的padding大小
     */
    private float mLoadingPadding;
    /**
     * 当前Loading的size
     */
    private float mCurLoadingSize;

    private static final float kCurveAngle = 0.3f;
    private static final float kHandleRate = 1f;

    /**
     * 外围的球
     */
    private List<Ball> mBalls = new ArrayList<>();
    /**
     * 小球移动的速度
     */
    private float mBallTranslateVelocity;

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

    /**
     * 初始化
     */
    private void init(Context context) {
        mCentreCirclePath = new Path();

        mLoadingPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mLoadingPaint.setColor(LOADING_COLOR);

        mCentreCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mCentreCirclePaint.setColor(CIRCLE_COLOR_RED);

        mCentreCircleMatrix = new Matrix();
        mCoverPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w <= 0 || h <= 0 || w != h) {
            return;
        }
        mSize = w;
        mHalfSize = mSize / 2;

        mCentreCircleRadius = mHalfSize * CENTRE_CIRCLE_SIZE_RATIO;

        mLoadingPadding = mSize * LOADING_PADDING_RATIO;
        mLoadingRadius = mHalfSize - mLoadingPadding;

        // 设置遮罩的渐变区间和渐变颜色
        mCoverPaint.setShader(new RadialGradient(mHalfSize, mHalfSize, mHalfSize,
            new int[]{Color.argb(0, 255, 255, 255), Color.argb(0, 255, 255, 255), Color.argb(128, 255, 255, 255), Color.WHITE},
            new float[]{0.0f, 0.75f, 0.875f, 1.0f}, Shader.TileMode.CLAMP));
        mDisplayRect.set(0, 0, w, h);

        mBallTranslateVelocity = mHalfSize * 1.0f / BALL_TRANSLATE_DURATION;
    }

    /**
     * 获得当前的下拉进度
     */
    public float getPullProgress() {
        return mCurrPullProgress;
    }

    /**
     * 设置当前的下拉进度
     *
     * @param curPullDistance 当前的下拉距离
     * @param maxPullDistance 最大的下拉距离
     * @param isRebound       是否是回弹
     */
    public void setPullProgress(float curPullDistance, float maxPullDistance, boolean isRebound) {
        if (maxPullDistance <= 0) {
            throw new IllegalArgumentException("maxPullDistance must more than 0.");
        }
        mState = isRebound ? STATE_REBOUND : STATE_PULL;
        float progress = curPullDistance / maxPullDistance;
        setPullProgress(progress);
    }

    private void setPullProgress(float progress) {
        if (progress > 1.0f) {
            progress = 1.0f;
        } else if (progress < 0.0f) {
            progress = 0.0f;
        }
        if (mCurrPullProgress == progress) {
            return;
        }
        mAnimationInterpolatedTime = -1f;
        if (!mBalls.isEmpty()) {
            mBalls.clear();
        }
        mCurrPullProgress = progress;
        if (mSize <= 0) {
            return;
        }
        final boolean isRebound = mState == STATE_REBOUND;
        if (isRebound && mCurrPullProgress <= LOADING_TRANSFORM_START_PROGRESS_IN_REBOUND) {// Loading变换过程
            float newProgress = 1 - (mCurrPullProgress / LOADING_TRANSFORM_START_PROGRESS_IN_REBOUND);
            getLoadingTransformPath(mCentreCirclePath, newProgress);
        } else {
            if (isRebound) {
                progress = (progress - LOADING_TRANSFORM_START_PROGRESS_IN_REBOUND) / (1f - LOADING_TRANSFORM_START_PROGRESS_IN_REBOUND);
            }
            float ratio = mLoadingRadius * 2 * progress;
            mCurLoadingSize = ratio;
            mCentreCirclePath.reset();
            mCentreCirclePath.moveTo(ratio * 0.5f, 0f);
            mCentreCirclePath.cubicTo(ratio * getc1x(progress), ratio * 0f, ratio * getc2x(progress), ratio * getc2y(getc2x(progress)), ratio * 0.933f, ratio * 0.75f);
            mCentreCirclePath.cubicTo(ratio * getc3x(progress), ratio * getc3y(getc3x(progress)), ratio * getc4x(progress), ratio * getc4y(getc4x(progress)), ratio * 0.067f, ratio * 0.75f);
            mCentreCirclePath.cubicTo(ratio * getc5x(progress), ratio * getc5y(getc5x(progress)), ratio * getc6x(progress), ratio * 0f, ratio * 0.5f, ratio * 0f);
        }
        if (getAnimation() != null) {
            clearAnimation();
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mAnimationInterpolatedTime != -1) {
            for (Ball ball : mBalls) {
                if (ball.enabled) {
                    drawBall(canvas, ball);
                }
            }
            canvas.drawRect(0, 0, mSize, mSize, mCoverPaint);
        }
        if (mState != STATE_REBOUND) {
            canvas.scale(mCurrPullProgress, mCurrPullProgress, mHalfSize, mHalfSize);
        }
        canvas.drawCircle(mHalfSize, mHalfSize, mCentreCircleRadius, mCentreCirclePaint);
        float translateX = 0f;
        float translateY = 0f;
        if (mState == STATE_REBOUND) {
            if (mCurrPullProgress > LOADING_TRANSFORM_START_PROGRESS_IN_REBOUND) {
                translateX = (mSize - mCurLoadingSize) / 2;
                float newProgress = (mCurrPullProgress - LOADING_TRANSFORM_START_PROGRESS_IN_REBOUND) / (1 - LOADING_TRANSFORM_START_PROGRESS_IN_REBOUND);
                translateY = ((mSize - mLoadingRadius * 2 * (1 - newProgress)) - mCurLoadingSize) / 2;
                canvas.translate(translateX, translateY);
            }
        } else {
            translateX = (mSize - mCurLoadingSize) / 2;
            translateY = (mSize - mCurLoadingSize) / 2;
            canvas.translate(translateX, translateY);
        }
        if (mAnimationInterpolatedTime != -1) {
            canvas.translate(mHalfSize, mHalfSize);
            canvas.rotate(LOADING_ROTATE_ANGLE_ON_ANIMATION * mAnimationInterpolatedTime);
            canvas.translate(-mHalfSize, -mHalfSize);
        }
        if (mState != STATE_REBOUND || mCurrPullProgress > LOADING_TRANSFORM_START_PROGRESS_IN_REBOUND) {
            mCentreCircleMatrix.reset();
            mCentreCircleMatrix.setRotate(-CIRCLE_TRIANGLE_TRANSFORM_ROTATE_ANGLE * mCurrPullProgress, mCurLoadingSize / 2, mCurLoadingSize / 2);
            mCentreCirclePath.transform(mCentreCircleMatrix);
        }
        canvas.drawPath(mCentreCirclePath, mLoadingPaint);
        if (translateX != 0f || translateY != 0f) {
            canvas.translate(-translateX, -translateY);
        }
    }

    private float getc1x(float progress) {
        return 0.8815f - (0.8815f - 0.5f) * progress;
    }

    private float getc1y(float x) {
        return 0;
    }

    private float getc2x(float progress) {
        return 1.1266f - (1.1266f - 0.933f) * progress;
    }

    private float getc2y(float x) {
        return (1.366f - x) / 0.577333333f;
    }

    private float getc3x(float progress) {
        return 0.7409f + (0.933f - 0.7409f) * progress;
    }

    private float getc3y(float x) {
        return (1.366f - x) / 0.577333333f;
    }

    private float getc4x(float progress) {
        return 0.2591f - (0.2591f - 0.067f) * progress;
    }

    private float getc4y(float x) {
        return (x - 0.067f) / 0.577333333f + 0.75f;
    }

    private float getc5x(float progress) {
        return -0.1266f + (0.067f + 0.1266f) * progress;
    }

    private float getc5y(float x) {
        return (x - 0.067f) / 0.577333333f + 0.75f;
    }

    private float getc6x(float progress) {
        return 0.1185f + (0.5f - 0.1185f) * progress;
    }

    @Override
    public void startAnimation(Animation animation) {
        if (getAnimation() != null) {
            clearAnimation();
        }
        if (!(animation instanceof PullLoadingAnimation)) {
            animation = new PullLoadingAnimation();
            animation.setDuration(DEFAULT_ANIMATION_DURATION);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.RESTART);
        }
        animation.setAnimationListener(this);
        animation.setInterpolator(new LinearInterpolator());
        super.startAnimation(animation);
    }

    private void dispatchApplyTransformation(float interpolatedTime, Transformation t) {
        mAnimationInterpolatedTime = interpolatedTime;
        updateBalls();
        invalidate();
    }

    /**
     * 画小球
     */
    private void drawBall(Canvas canvas, Ball ball) {
        canvas.drawCircle(ball.centrePoint.x, ball.centrePoint.y, ball.radius, mCentreCirclePaint);

        float kMaxDis = mHalfSize;

        PointF center1 = new PointF(ball.centrePoint.x, ball.centrePoint.y);
        PointF center2 = new PointF(mHalfSize, mHalfSize);
        float dis = distanceBetweenPointA(center2, center1);
        float u1 = 0.0f;
        float u2 = 0.0f;
        float radius1 = ball.radius;
        float radius2 = mCentreCircleRadius;
        if (dis > kMaxDis || dis <= Math.abs(radius1 - radius2)) {
            return;
        } else if (dis < radius1 + radius2) {
            u1 = (float) Math.acos((radius1 * radius1 + dis * dis - radius2 * radius2) / (2 * radius1 * dis));
            u2 = (float) Math.acos((radius2 * radius2 + dis * dis - radius1 * radius1) / (2 * radius2 * dis));
        }
        float angle1 = angleBetweenPointA(center1, center2);
        float angle2 = (float) Math.acos((radius1 - radius2) / dis);
        float angle1a = angle1 + u1 + (angle2 - u1) * kCurveAngle;
        float angle1b = angle1 - u1 - (angle2 - u1) * kCurveAngle;
        float angle2a = (float) (angle1 + Math.PI - u2 - (Math.PI - u2 - angle2) * kCurveAngle);
        float angle2b = (float) (angle1 - Math.PI + u2 + (Math.PI - u2 - angle2) * kCurveAngle);

        PointF p1a = movePoint(center1, angle1a, radius1);
        PointF p1b = movePoint(center1, angle1b, radius1);
        PointF p2a = movePoint(center2, angle2a, radius2);
        PointF p2b = movePoint(center2, angle2b, radius2);

        float totalRadius = radius1 + radius2;
        float dis2 = Math.min(kCurveAngle * kHandleRate, lengthWithPoint(minusPointA(p1a, p2a)) / totalRadius);
        dis2 *= Math.min(1, dis * 2 / totalRadius);

        PointF cp1a = movePoint(p1a, (float) (angle1a - Math.PI / 2), radius1 * dis2);
        PointF cp2a = movePoint(p2a, (float) (angle2a + Math.PI / 2), radius2 * dis2);
        PointF cp2b = movePoint(p2b, (float) (angle2b - Math.PI / 2), radius2 * dis2);
        PointF cp1b = movePoint(p1b, (float) (angle1b + Math.PI / 2), radius1 * dis2);

        ball.path.reset();
        ball.path.moveTo(p1a.x, p1a.y);
        ball.path.cubicTo(cp1a.x, cp1a.y, cp2a.x, cp2a.y, p2a.x, p2a.y);
        ball.path.lineTo(p2b.x, p2b.y);
        ball.path.cubicTo(cp2b.x, cp2b.y, cp1b.x, cp1b.y, p1b.x, p1b.y);
        canvas.drawPath(ball.path, mCentreCirclePaint);
    }

    private double distance(float p1x, float p1y, float p2x, float p2y) {
        return Math.sqrt(Math.pow(p1x - p2x, 2) + Math.pow(p1y - p2y, 2));
    }

    private double angleBetween(float p1x, float p1y, float p2x, float p2y) {
        return Math.atan2(p2y - p1y, p2x - p1x);
    }

    private PointF minusPointA(PointF pa, PointF pb) {
        return new PointF(pa.x - pb.x, pa.y - pb.y);
    }

    private PointF middlePointBetweenPointA(PointF pa, PointF pb) {
        return new PointF((pa.x + pb.x) / 2, (pa.y + pb.y) / 2);
    }

    private float distanceBetweenPointA(PointF pa, PointF pb) {
        float dx = pa.x - pb.x;
        float dy = pa.y - pb.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private float angleBetweenPointA(PointF pa, PointF pb) {
        return (float) Math.atan2(pb.y - pa.y, pb.x - pa.x);
    }

    private PointF movePoint(PointF p, float radians, float length) {
        return new PointF((float) (p.x + length * Math.cos(radians)), (float) (p.y + length * Math.sin(radians)));
    }

    private static void movePoint2(PointF p, float radians, float length) {
        if (length == 0f) {
            return;
        }
        p.x = (float) (p.x + length * Math.cos(radians));
        p.y = (float) (p.y + length * Math.sin(radians));
    }

    /**
     * 获得Loading变换过程的path
     */
    private Path getLoadingTransformPath(Path path, float progress) {
        float r = mSize * LOADING_SIZE_RATIO;
        float angle = (float) (Math.PI * 3 / 2 + progress * Math.PI / 2);
        float anlgeL = (float) (angle / Math.PI * 180);
        float padding = mLoadingPadding;
        float x0 = mHalfSize;
        float y0 = padding;
        float x1r = mHalfSize - padding - r * 2;
        float x2r = mHalfSize - padding;

        mTmpPointF.x = mTmpPointF.y = mHalfSize;
        movePoint2(mTmpPointF, angle, x1r);

        float x1 = mTmpPointF.x;
        float y1 = mTmpPointF.y;

        mTmpPointF.x = mTmpPointF.y = mHalfSize;
        movePoint2(mTmpPointF, angle, x2r);

        float x2 = mTmpPointF.x;
        float y2 = mTmpPointF.y;
        float r1x = x1 - x0, r2x = x2 - x0;
        float r1y = y1 - y0, r2y = y2 - y0;
        path.reset();
        path.moveTo(x0, y0);
        path.cubicTo(x0 + r1x / kFactor, y0, x1, y1 - r1y / kFactor, x1, y1);
        mTmpRectF.set((x2 + x1) / 2 - r, (y1 + y2) / 2 - r, (x2 + x1) / 2 + r, (y1 + y2) / 2 + r);
        path.addArc(mTmpRectF, anlgeL - 180, -180);
        path.cubicTo(x2, y2 - r2y / kFactor, x0 + r2x / kFactor, y0, x0, y0);
        path.close();
        return path;
    }

    /**
     * 获得圆上一点的y坐标
     *
     * @return
     */
    private float getCircleY(float a, float b, float r, float x) {
        // y = b + √(r²-(x-a)²);// a,b为圆心坐标
        return (float) (b + Math.sqrt((r * r - (x - a) * (x - a))));
    }

    private float lengthWithPoint(PointF p) {
        return (float) Math.sqrt(p.x * p.x + p.y * p.y);
    }

    /**
     * 更新球
     */
    private void updateBalls() {
        final Iterator<Ball> iterator = mBalls.iterator();
        while (iterator.hasNext()) {
            final Ball b = iterator.next();
            if (b.startTime <= mAnimationInterpolatedTime) {
                mTmpPointF.x = b.startX;
                mTmpPointF.y = b.startY;
                movePoint2(mTmpPointF, b.angle, mBallTranslateVelocity * DEFAULT_ANIMATION_DURATION * (mAnimationInterpolatedTime - b.startTime));
                b.centrePoint.x = mTmpPointF.x;
                b.centrePoint.y = mTmpPointF.y;
                b.enabled = mDisplayRect.intersects(b.centrePoint.x - b.radius, b.centrePoint.y - b.radius, b.centrePoint.x + b.radius, b.centrePoint.y + b.radius);
                if (!b.enabled) {
                    iterator.remove();
                }
            } else {
                b.enabled = false;
            }
        }
    }

    /**
     * 添加小球
     */
    private void addBall() {
        mBalls.clear();
        mBalls.add(new Ball(mSize * 0.072f, 315, mSize, mCentreCircleRadius, 0.0f));
        mBalls.add(new Ball(mSize * 0.072f, 30, mSize, mCentreCircleRadius, 0.0f));
        mBalls.add(new Ball(mSize * 0.043f, 100, mSize, mCentreCircleRadius, 0.0f));

        mBalls.add(new Ball(mSize * 0.072f, 180, mSize, mCentreCircleRadius, 0.1f));

        mBalls.add(new Ball(mSize * 0.05f, 260, mSize, mCentreCircleRadius, 0.3f));

        mBalls.add(new Ball(mSize * 0.1f, 130, mSize, mCentreCircleRadius, 0.45f));

        mBalls.add(new Ball(mSize * 0.072f, 50, mSize, mCentreCircleRadius, 0.55f));

        mBalls.add(new Ball(mSize * 0.086f, 230, mSize, mCentreCircleRadius, 0.7f));
        mBalls.add(new Ball(mSize * 0.043f, 100, mSize, mCentreCircleRadius, 0.7f));

        mBalls.add(new Ball(mSize * 0.057f, 210, mSize, mCentreCircleRadius, 0.75f));

        mBalls.add(new Ball(mSize * 0.072f, 30, mSize, mCentreCircleRadius, 0.85f));

        mBalls.add(new Ball(mSize * 0.05f, 270, mSize, mCentreCircleRadius, 0.9f));

        mBalls.add(new Ball(mSize * 0.072f, 180, mSize, mCentreCircleRadius, 0.92f));
    }

    @Override
    public void onAnimationStart(Animation animation) {
        mAnimationInterpolatedTime = 0.0f;
        addBall();
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mAnimationInterpolatedTime = 1.0f;
        mBalls.clear();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        mAnimationInterpolatedTime = 0.0f;
        addBall();
    }

    /**
     * 限制float的最大和最小边界
     */
    private static float limit(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }

}
