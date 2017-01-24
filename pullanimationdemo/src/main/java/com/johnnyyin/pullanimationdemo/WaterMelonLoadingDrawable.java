package com.johnnyyin.pullanimationdemo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 西瓜LoadingDrawable
 *
 * @author YinZhong
 * @since 2017/1/29
 */
public class WaterMelonLoadingDrawable extends Drawable implements Animatable {
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
            movePoint(centrePoint, centrePoint.x, centrePoint.y, this.angle, centreCircleRadius - radius);
            this.startX = this.centrePoint.x;
            this.startY = this.centrePoint.y;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 各种控制常量begin
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 默认的动画时长
     */
    private static final long DEFAULT_ANIMATION_DURATION = 1700;// 默认1.7秒
    /**
     * 默认Loading变换过程的动画时长
     */
    private static final long DEFAULT_TRANSFORM_ANIMATION_DURATION = 200;// 默认0.20秒
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
     * Loading的颜色
     */
    private static final int LOADING_COLOR = 0xfff0f0f0;
    /**
     * 中心圆的size比例
     */
    private static final float CENTRE_CIRCLE_SIZE_RATIO = 0.5f;
    /**
     * 中心圆放大的最大比例
     */
    private static final float CENTRE_CIRCLE_SCALE_MAX_RATIO = 0.06f;
    /**
     * Loading与边界的padding比例
     */
    private static final float LOADING_PADDING_RATIO = 0.32f;
    /**
     * 内部圆的半径与控件size的比例
     */
    private static final float INNER_CIRCLE_RADIUS_RATIO = 0.14f;
    /**
     * Loading的size与控件size的比例
     */
    private static final float LOADING_SIZE_RATIO = 0.02f;
    /**
     * Loading变换过程动画的因子
     */
    private static final float LOADING_TRANSFORM_FACTOR = 2f;
    /**
     * 小球贝塞尔曲线控制变量
     */
    private static final float BALL_CURVE_ANGLE = 0.3f;
    /**
     * 小球贝塞尔曲线控制变量
     */
    private static final float BALL_HANDLE_RATE = 1f;
    /**
     * 小球与中心圆分离的最大距离于控件size的比例
     */
    private static final float BALL_SEPARATE_MAX_DISTANCE = 0.4f;
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
     * 控制flag-禁止Loading变换动画
     */
    public static final int FLAG_DISABLE_LOADING_TRANSFORM_ANIM = 0x1;
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
     * 临时用的点1
     */
    private final PointF mTmpPointF1 = new PointF();
    /**
     * 临时用的点2
     */
    private final PointF mTmpPointF2 = new PointF();
    /**
     * 整个控件展示的矩阵
     */
    private final RectF mDisplayRect = new RectF();
    /**
     * 临时用的RectF
     */
    private final RectF mTmpRectF = new RectF();
    /**
     * 当前的下拉进度
     */
    private float mCurrPullProgress;
    /**
     * 当前状态
     */
    private int mState = STATE_PULL;
    /**
     * 当前的控制flag
     */
    private int mFlags;
    ///////////////////////////////////////////////////////////////////////////
    // 通用end
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // 绘制相关begin
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 主色调
     */
    private int mPrimaryColor;
    /**
     * 中心圆的半径
     */
    private float mCentreCircleRadius;
    /**
     * 中心圆的画笔
     */
    private Paint mCentreCirclePaint;
    /**
     * 中心圆缩放的比例
     */
    private float mCentreCircleScale = 1;
    /**
     * 中间Loading的Matrix
     */
    private Matrix mLoadingMatrix;
    /**
     * 中间Loading的路径
     */
    private Path mLoadingPath;
    /**
     * 中间Loading的画笔
     */
    private Paint mLoadingPaint;
    /**
     * 内部圆圈的半径
     */
    private float mInnerCircleRadius;
    /**
     * 中间Loading与边界的padding大小
     */
    private float mLoadingPadding;
    /**
     * 中间Loading当前的size
     */
    private float mCurLoadingSize;
    /**
     * 渐变效果的遮罩画笔
     */
    private Paint mCoverPaint;
    /**
     * 外围的球
     */
    private final List<Ball> mBalls = new ArrayList<>();
    /**
     * 小球移动的速度
     */
    private float mBallTranslateVelocity;
    ///////////////////////////////////////////////////////////////////////////
    // 绘制相关end
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // 动画相关begin
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 当前运行动画的插值
     */
    private float mAnimationInterpolatedTime = -1f;
    /**
     * 动画重复的次数
     */
    private int mAnimationRepeatCount;
    /**
     * 当前正在运行的动画
     */
    private AnimatorSet mAnimator;
    /**
     * 正在等待执行动画
     */
    private boolean mPendingStartAnim;
    /**
     * 循环动画监听器
     */
    private Animator.AnimatorListener mLoopAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
            mCentreCircleScale = 1f;
            mAnimationInterpolatedTime = 0.0f;
            mAnimationRepeatCount = 0;
            mBalls.clear();
            addBall();
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mCentreCircleScale = 1f;
            mAnimationInterpolatedTime = 1.0f;
            mAnimationRepeatCount = 0;
            mBalls.clear();
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            onAnimationEnd(animator);
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
            final Callback callback = getCallback();
            if (callback == null) {// 避免View已经销毁了，动画还在执行
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        stop();
                    }
                });
                return;
            }
            mAnimationRepeatCount++;
            addBall();
        }
    };
    /**
     * 循环动画更新监听器
     */
    private ValueAnimator.AnimatorUpdateListener mLoopAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mAnimationInterpolatedTime = (float) animation.getAnimatedValue();
            updateBalls();
            invalidateSelf();
        }
    };
    /**
     * 变换动画更新监听器
     */
    private ValueAnimator.AnimatorUpdateListener mTransformAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float animatorValue = (float) animation.getAnimatedValue();
            transformLoadingPath(mLoadingPath, animatorValue);

            if (animatorValue <= 0.5f) {
                animatorValue = animatorValue / 0.5f;
            } else {
                animatorValue = (1.0f - animatorValue) / 0.5f;
            }
            mCentreCircleScale = 1 + CENTRE_CIRCLE_SCALE_MAX_RATIO * animatorValue;

            invalidateSelf();
        }
    };
    ///////////////////////////////////////////////////////////////////////////
    // 动画相关end
    ///////////////////////////////////////////////////////////////////////////

    public WaterMelonLoadingDrawable(Context context, int primaryColor, int size, int flags) {
        mFlags = flags;
        init();
        if (primaryColor == 0) {
            primaryColor = context.getResources().getColor(R.color.material_red);
        }
        setPrimaryColor(primaryColor);
        if (size > 0) {
            setBounds(0, 0, size, size);
        }
    }

    /**
     * 初始化
     */
    private void init() {
        mLoadingPath = new Path();

        mLoadingPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mLoadingPaint.setColor(LOADING_COLOR);

        mCentreCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        mLoadingMatrix = new Matrix();
        mCoverPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        if (bounds == null) {
            return;
        }
        final int w = bounds.width();
        final int h = bounds.height();
        if (w <= 0 || h <= 0) {
            return;
        }
        mSize = Math.min(w, h);
        mHalfSize = mSize / 2;

        mCentreCircleRadius = mHalfSize * CENTRE_CIRCLE_SIZE_RATIO;

        mLoadingPadding = mSize * LOADING_PADDING_RATIO;
        mInnerCircleRadius = mSize * INNER_CIRCLE_RADIUS_RATIO;

        mCoverPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mDisplayRect.set(0, 0, mSize, mSize);

        mBallTranslateVelocity = mHalfSize * 1.0f / BALL_TRANSLATE_DURATION;

        setPrimaryColor(mPrimaryColor);

        if (mPendingStartAnim) {
            if (isRunning()) {
                mPendingStartAnim = false;
            } else {
                start();
            }
        }
    }

    /**
     * 设置主色调
     */
    public void setPrimaryColor(int color) {
        mPrimaryColor = color;

        if (!isValidState()) {
            return;
        }

        mCentreCirclePaint.setColor(mPrimaryColor);

        // 设置遮罩的渐变区间和渐变颜色
        if (mHalfSize > 0) {
            mCoverPaint.setShader(new RadialGradient(mHalfSize, mHalfSize, (float) (mHalfSize * Math.sqrt(2)),
                new int[]{mPrimaryColor, mPrimaryColor, 0x00ffffff, 0x00ffffff},
                new float[]{0.0f, 0.36f, 0.74f, 1.0f}, Shader.TileMode.CLAMP));
        }
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
        setPullProgress(curPullDistance / maxPullDistance, isRebound);
    }

    /**
     * 设置当前的下拉进度
     *
     * @param progress  当前进度
     * @param isRebound 是否是回弹
     */
    public void setPullProgress(float progress, boolean isRebound) {
        mState = isRebound ? STATE_REBOUND : STATE_PULL;
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
        if (!isValidState()) {
            return;
        }

        transformTriangle2Circle(progress);

        if (isRunning()) {
            stop();
        }
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isValidState()) {
            return;
        }
        final boolean isAnimating = mState == STATE_ANIMATING;
        final boolean isPulling = mState == STATE_PULL;
        if (isAnimating && mAnimationInterpolatedTime >= 0f) {
            canvas.saveLayer(mDisplayRect, null, Canvas.ALL_SAVE_FLAG);
            for (Ball ball : mBalls) {
                if (ball.enabled) {
                    drawBall(canvas, ball);
                }
            }
            canvas.drawRect(mDisplayRect, mCoverPaint);
        }
        if (isPulling) {
            canvas.scale(mCurrPullProgress, mCurrPullProgress, mHalfSize, mHalfSize);
        }
        canvas.drawCircle(mHalfSize, mHalfSize, mCentreCircleRadius * mCentreCircleScale, mCentreCirclePaint);
        float translateX = 0f;
        float translateY = 0f;
        if (isAnimating) {
            if (mAnimationInterpolatedTime >= 0f) {
                canvas.translate(mHalfSize, mHalfSize);
                canvas.rotate(LOADING_ROTATE_ANGLE_ON_ANIMATION * mAnimationInterpolatedTime);
                canvas.translate(-mHalfSize, -mHalfSize);
            }
        } else if (!isPulling) {
            translateX = (mSize - mCurLoadingSize) / 2;
            translateY = ((mSize - (mHalfSize - mLoadingPadding) * 2 * (1 - mCurrPullProgress)) - mCurLoadingSize) / 2;
            canvas.translate(translateX, translateY);
        } else {
            translateX = (mSize - mCurLoadingSize) / 2;
            translateY = (mSize - mCurLoadingSize) / 2;
            canvas.translate(translateX, translateY);
        }
        if (!isAnimating) {
            mLoadingMatrix.reset();
            mLoadingMatrix.setRotate(-CIRCLE_TRIANGLE_TRANSFORM_ROTATE_ANGLE * mCurrPullProgress, mCurLoadingSize / 2, mCurLoadingSize / 2);
            mLoadingPath.transform(mLoadingMatrix);
        }
        canvas.drawPath(mLoadingPath, mLoadingPaint);
        if (translateX != 0f || translateY != 0f) {
            canvas.translate(-translateX, -translateY);
        }
    }

    /**
     * 画小球
     */
    private void drawBall(Canvas canvas, Ball ball) {
        // 绘制小球
        canvas.drawCircle(ball.centrePoint.x, ball.centrePoint.y, ball.radius, mCentreCirclePaint);

        PointF center1 = ball.centrePoint;
        mTmpPointF2.x = mTmpPointF2.y = mHalfSize;
        PointF center2 = mTmpPointF2;
        float dis = distanceBetweenTwoPoint(center2.x, center2.y, center1.x, center1.y);
        float u1 = 0.0f;
        float u2 = 0.0f;
        float radius1 = ball.radius;
        float radius2 = mCentreCircleRadius;
        if (dis == 0 || dis > mSize * BALL_SEPARATE_MAX_DISTANCE || dis <= Math.abs(radius1 - radius2)) {
            return;
        } else if (dis < radius1 + radius2) {
            float f = 2 * radius1 * dis;
            if (f == 0) {
                return;
            }
            u1 = (float) Math.acos((radius1 * radius1 + dis * dis - radius2 * radius2) / f);
            f = 2 * radius2 * dis;
            if (f == 0) {
                return;
            }
            u2 = (float) Math.acos((radius2 * radius2 + dis * dis - radius1 * radius1) / f);
        }
        float angle1 = angleBetweenTwoPoint(center1.x, center1.y, center2.x, center2.y);
        float angle2 = (float) Math.acos((radius1 - radius2) / dis);
        float angle1a = angle1 + u1 + (angle2 - u1) * BALL_CURVE_ANGLE;
        float angle1b = angle1 - u1 - (angle2 - u1) * BALL_CURVE_ANGLE;
        float angle2a = (float) (angle1 + Math.PI - u2 - (Math.PI - u2 - angle2) * BALL_CURVE_ANGLE);
        float angle2b = (float) (angle1 - Math.PI + u2 + (Math.PI - u2 - angle2) * BALL_CURVE_ANGLE);

        PointF p1a = movePoint(mTmpPointF1, center1.x, center1.y, angle1a, radius1);
        float p1ax = p1a.x;
        float p1ay = p1a.y;
        PointF p1b = movePoint(mTmpPointF1, center1.x, center1.y, angle1b, radius1);
        float p1bx = p1b.x;
        float p1by = p1b.y;
        PointF p2a = movePoint(mTmpPointF1, center2.x, center2.y, angle2a, radius2);
        float p2ax = p2a.x;
        float p2ay = p2a.y;
        PointF p2b = movePoint(mTmpPointF1, center2.x, center2.y, angle2b, radius2);
        float p2bx = p2b.x;
        float p2by = p2b.y;

        final float totalRadius = radius1 + radius2;
        if (totalRadius == 0) {
            return;
        }
        float dis2 = Math.min(BALL_CURVE_ANGLE * BALL_HANDLE_RATE, lengthWithPoint(minusPointA(mTmpPointF1, p1ax, p1ay, p2ax, p2ay)) / totalRadius);
        dis2 *= Math.min(1, dis * 2 / totalRadius);

        PointF cp1a = movePoint(mTmpPointF1, p1ax, p1ay, (float) (angle1a - Math.PI / 2), radius1 * dis2);
        float cp1ax = cp1a.x;
        float cp1ay = cp1a.y;
        PointF cp2a = movePoint(mTmpPointF1, p2ax, p2ay, (float) (angle2a + Math.PI / 2), radius2 * dis2);
        float cp2ax = cp2a.x;
        float cp2ay = cp2a.y;
        PointF cp2b = movePoint(mTmpPointF1, p2bx, p2by, (float) (angle2b - Math.PI / 2), radius2 * dis2);
        float cp2bx = cp2b.x;
        float cp2by = cp2b.y;
        PointF cp1b = movePoint(mTmpPointF1, p1bx, p1by, (float) (angle1b + Math.PI / 2), radius1 * dis2);
        float cp1bx = cp1b.x;
        float cp1by = cp1b.y;

        ball.path.reset();
        ball.path.moveTo(p1ax, p1ay);
        ball.path.cubicTo(cp1ax, cp1ay, cp2ax, cp2ay, p2ax, p2ay);
        ball.path.lineTo(p2bx, p2by);
        ball.path.cubicTo(cp2bx, cp2by, cp1bx, cp1by, p1bx, p1by);
        // 绘制小球与中心圆的曲线
        canvas.drawPath(ball.path, mCentreCirclePaint);
    }

    /**
     * 三角形与圆形变换
     */
    private void transformTriangle2Circle(float progress) {
        final float ratio = mInnerCircleRadius * 2 * progress;
        mCurLoadingSize = ratio;

        mLoadingPath.reset();
        mLoadingPath.moveTo(ratio * 0.5f, 0f);
        final float c1x = getTriangle2CircleC1x(progress);
        final float c2x = getTriangle2CircleC2x(progress);
        mLoadingPath.cubicTo(ratio * c1x, ratio * getTriangle2CircleC1y(c1x), ratio * c2x, ratio * getTriangle2CircleC2y(c2x), ratio * 0.933f, ratio * 0.75f);
        final float c3x = getTriangle2CircleC3x(progress);
        final float c4x = getTriangle2CircleC4x(progress);
        mLoadingPath.cubicTo(ratio * c3x, ratio * getTriangle2CircleC3y(c3x), ratio * c4x, ratio * getTriangle2CircleC4y(c4x), ratio * 0.067f, ratio * 0.75f);
        final float c5x = getTriangle2CircleC5x(progress);
        final float c6x = getTriangle2CircleC6x(progress);
        mLoadingPath.cubicTo(ratio * c5x, ratio * getTriangle2CircleC5y(c5x), ratio * c6x, ratio * getTriangle2CircleC6y(c6x), ratio * 0.5f, ratio * 0f);
        mLoadingPath.close();
    }

    /**
     * 变换Loading的path
     */
    private void transformLoadingPath(Path path, float progress) {
        float r = mSize * LOADING_SIZE_RATIO;
        float angle = (float) (Math.PI * 3 / 2 + progress * Math.PI / 2);
        float angleL = (float) (angle / Math.PI * 180);
        float padding = mLoadingPadding;
        float x0 = mHalfSize;
        float y0 = padding;
        float x1r = mHalfSize - padding - r * 2;
        float x2r = mHalfSize - padding;

        movePoint(mTmpPointF1, mHalfSize, mHalfSize, angle, x1r);

        float x1 = mTmpPointF1.x;
        float y1 = mTmpPointF1.y;

        movePoint(mTmpPointF1, mHalfSize, mHalfSize, angle, x2r);

        float x2 = mTmpPointF1.x;
        float y2 = mTmpPointF1.y;
        float r1x = x1 - x0, r2x = x2 - x0;
        float r1y = y1 - y0, r2y = y2 - y0;
        path.reset();
        path.moveTo(x0, y0);
        path.cubicTo(x0 + r1x / LOADING_TRANSFORM_FACTOR, y0, x1, y1 - r1y / LOADING_TRANSFORM_FACTOR, x1, y1);
        mTmpRectF.set((x2 + x1) / 2 - r, (y1 + y2) / 2 - r, (x2 + x1) / 2 + r, (y1 + y2) / 2 + r);
        path.addArc(mTmpRectF, angleL - 180, -180);
        path.cubicTo(x2, y2 - r2y / LOADING_TRANSFORM_FACTOR, x0 + r2x / LOADING_TRANSFORM_FACTOR, y0, x0, y0);
        path.close();
    }

    /**
     * 更新球
     */
    private void updateBalls() {
        final Iterator<Ball> iterator = mBalls.iterator();
        while (iterator.hasNext()) {
            final Ball b = iterator.next();
            if (b.startTime <= mAnimationRepeatCount + mAnimationInterpolatedTime) {
                movePoint(mTmpPointF1, b.startX, b.startY, b.angle, mBallTranslateVelocity * DEFAULT_ANIMATION_DURATION * (mAnimationRepeatCount + mAnimationInterpolatedTime - b.startTime));
                b.centrePoint.x = mTmpPointF1.x;
                b.centrePoint.y = mTmpPointF1.y;
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
        mBalls.add(new Ball(mSize * 0.072f, 315, mSize, mCentreCircleRadius, mAnimationRepeatCount + 0.0f));
        mBalls.add(new Ball(mSize * 0.072f, 30, mSize, mCentreCircleRadius, mAnimationRepeatCount + 0.0f));
        mBalls.add(new Ball(mSize * 0.043f, 100, mSize, mCentreCircleRadius, mAnimationRepeatCount + 0.0f));

        mBalls.add(new Ball(mSize * 0.072f, 180, mSize, mCentreCircleRadius, mAnimationRepeatCount + 0.1f));

        mBalls.add(new Ball(mSize * 0.05f, 260, mSize, mCentreCircleRadius, mAnimationRepeatCount + 0.3f));

        mBalls.add(new Ball(mSize * 0.1f, 130, mSize, mCentreCircleRadius, mAnimationRepeatCount + 0.4f));

        mBalls.add(new Ball(mSize * 0.072f, 50, mSize, mCentreCircleRadius, mAnimationRepeatCount + 0.55f));

        mBalls.add(new Ball(mSize * 0.086f, 230, mSize, mCentreCircleRadius, mAnimationRepeatCount + 0.7f));
        mBalls.add(new Ball(mSize * 0.043f, 100, mSize, mCentreCircleRadius, mAnimationRepeatCount + 0.7f));

        mBalls.add(new Ball(mSize * 0.057f, 0, mSize, mCentreCircleRadius, mAnimationRepeatCount + 0.75f));

        mBalls.add(new Ball(mSize * 0.072f, 190, mSize, mCentreCircleRadius, mAnimationRepeatCount + 0.80f));

        mBalls.add(new Ball(mSize * 0.05f, 270, mSize, mCentreCircleRadius, mAnimationRepeatCount + 0.9f));

        mBalls.add(new Ball(mSize * 0.072f, 130, mSize, mCentreCircleRadius, mAnimationRepeatCount + 0.92f));
    }

    @Override
    public void setAlpha(int alpha) {
        // don't support
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // don't support
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    @Override
    public void start() {
        if (!isValidState()) {
            mPendingStartAnim = true;
            return;
        }
        mPendingStartAnim = false;
        mCurrPullProgress = -1;
        setPullProgress(0.0f, true);
        mState = STATE_ANIMATING;
        mAnimationInterpolatedTime = -1f;
        mAnimationRepeatCount = 0;
        stop();

        final ValueAnimator loadingTransformAnimator;
        if ((mFlags & FLAG_DISABLE_LOADING_TRANSFORM_ANIM) != FLAG_DISABLE_LOADING_TRANSFORM_ANIM) {
            loadingTransformAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            loadingTransformAnimator.setDuration(DEFAULT_TRANSFORM_ANIMATION_DURATION);
        } else {
            loadingTransformAnimator = ValueAnimator.ofFloat(1.0f, 1.0f);
            loadingTransformAnimator.setDuration(1);
        }
        loadingTransformAnimator.addUpdateListener(mTransformAnimatorUpdateListener);
        loadingTransformAnimator.setInterpolator(new LinearInterpolator());

        final ValueAnimator loopAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        loopAnimator.addUpdateListener(mLoopAnimatorUpdateListener);
        loopAnimator.addListener(mLoopAnimatorListener);
        loopAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
        loopAnimator.setRepeatCount(ValueAnimator.INFINITE);
        loopAnimator.setRepeatMode(ValueAnimator.RESTART);
        loopAnimator.setInterpolator(new LinearInterpolator());

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(loopAnimator).after(loadingTransformAnimator);
        animatorSet.start();
        mAnimator = animatorSet;
    }

    @Override
    public void stop() {
        mPendingStartAnim = false;
        if (!isRunning()) {
            return;
        }
        mAnimator.cancel();
        ArrayList<Animator> animators = mAnimator.getChildAnimations();
        if (animators != null) {
            for (Animator animator : animators) {
                animator.removeListener(mLoopAnimatorListener);
                animator.removeAllListeners();
                if (animator instanceof ValueAnimator) {
                    ((ValueAnimator) animator).removeAllUpdateListeners();
                }
            }
        }
        mAnimator = null;
    }

    @Override
    public boolean isRunning() {
        return mAnimator != null && mAnimator.isRunning();
    }

    /**
     * 当前是否是有效的状态
     */
    private boolean isValidState() {
        return mSize > 0;
    }

    private static PointF minusPointA(PointF out, float p1x, float p1y, float p2x, float p2y) {
        out.x = p1x - p2x;
        out.y = p1y - p2y;
        return out;
    }

    private static float distanceBetweenTwoPoint(float p1x, float p1y, float p2x, float p2y) {
        final float dx = p1x - p2x;
        final float dy = p1y - p2y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private static float angleBetweenTwoPoint(float p1x, float p1y, float p2x, float p2y) {
        return (float) Math.atan2(p2y - p1y, p2x - p1x);
    }

    private static PointF movePoint(PointF out, float x, float y, float radians, float length) {
        out.x = x;
        out.y = y;
        if (length == 0f) {
            return out;
        }
        out.x = (float) (out.x + length * Math.cos(radians));
        out.y = (float) (out.y + length * Math.sin(radians));
        return out;
    }

    private static float lengthWithPoint(PointF p) {
        return (float) Math.sqrt(p.x * p.x + p.y * p.y);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 圆与三角形变换的贝塞尔曲线控制点方程begin
    ///////////////////////////////////////////////////////////////////////////
    private static float getTriangle2CircleC1x(float progress) {
        return 0.8815f - (0.8815f - 0.5f) * progress;
    }

    private static float getTriangle2CircleC1y(float x) {
        return 0f;
    }

    private static float getTriangle2CircleC2x(float progress) {
        return 1.1266f - (1.1266f - 0.933f) * progress;
    }

    private static float getTriangle2CircleC2y(float x) {
        return (1.366f - x) / 0.577333333f;
    }

    private static float getTriangle2CircleC3x(float progress) {
        return 0.7409f + (0.933f - 0.7409f) * progress;
    }

    private static float getTriangle2CircleC3y(float x) {
        return (1.366f - x) / 0.577333333f;
    }

    private static float getTriangle2CircleC4x(float progress) {
        return 0.2591f - (0.2591f - 0.067f) * progress;
    }

    private static float getTriangle2CircleC4y(float x) {
        return (x - 0.067f) / 0.577333333f + 0.75f;
    }

    private static float getTriangle2CircleC5x(float progress) {
        return -0.1266f + (0.067f + 0.1266f) * progress;
    }

    private static float getTriangle2CircleC5y(float x) {
        return (x - 0.067f) / 0.577333333f + 0.75f;
    }

    private static float getTriangle2CircleC6x(float progress) {
        return 0.1185f + (0.5f - 0.1185f) * progress;
    }

    private static float getTriangle2CircleC6y(float x) {
        return 0f;
    }
    ///////////////////////////////////////////////////////////////////////////
    // 圆与三角形变换的贝塞尔曲线控制点方程end
    ///////////////////////////////////////////////////////////////////////////

}
