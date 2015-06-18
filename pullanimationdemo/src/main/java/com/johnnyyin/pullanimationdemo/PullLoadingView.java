package com.johnnyyin.pullanimationdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;

public class PullLoadingView extends ImageView {

    private static final long DEFAULT_ANIMATION_DURATION = 2000;
    private static final int LINE_COUNT = 3;
    /**
     * 左右padding
     */
    private static final float HORIZONTAL_PADDING_PERCENT = 0.15f;
    /**
     * 上下padding
     */
    private static final float VERTICAL_PADDING_PERCENT = 0.19f;
    /**
     * 每个drawable的最大高度
     */
    private static final float DRAWABLE_MAX_HEIGHT = 0.272f;

    private static final float CENTER_PADDING = 0.05f;

    private class PullLoadingAnimation extends Animation {

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            dispatchApplyTransformation(interpolatedTime, t);
        }

    }

    public interface AnimationDrawable {
        void updatePullProgress(float percent);

        void draw(Canvas canvas);

        void applyTransformation(float interpolatedTime, Transformation t);

        void onSizeChanged(int w, int h, int oldw, int oldh);

        void clearAnimation();

        void onThemeChanged(boolean isNightTheme);
    }

    private class EdgeAnimationDrawable implements AnimationDrawable {
        /**
         * 圆角的半径，相对于view尺寸的百分比
         */
        private static final float EDGE_CORNER_RADIUS_PERCENT = 0.2f;
        private final Path path;
        private final RectF mArcRectF;

        /**
         * 边框圆角的半径
         */
        private float cornerRadius;
        /**
         * 边框圆周直径
         */
        private float cornerDiameter;
        /**
         * 边框每个角的弧长
         */
        private float arcLength;
        /**
         * 圆角周长
         */
        private float circumference;
        /**
         * 边框直线区域边长
         */
        private float lineLength;
        /**
         * 边框的周长
         */
        private float perimeter;
        private float curProgress;

        public EdgeAnimationDrawable() {
            path = new Path();
            mArcRectF = new RectF();
        }

        @Override
        public void updatePullProgress(float progress) {
            if (curProgress == progress) {
                return;
            }
            curProgress = progress;

            path.reset();

            float remainLength = perimeter * progress;

            float padding = mBaseLineWidth / 2;

            do {
                // 右上角
                float splitLength = remainLength > arcLength ? arcLength : remainLength;
                remainLength -= splitLength;
                if (splitLength <= 0) {
                    break;
                } else {
                    float sweepAngle = (360 * splitLength) / circumference;
                    mArcRectF.set(mWidth - cornerDiameter - padding, padding, mWidth - padding, cornerDiameter + padding);
                    path.addArc(mArcRectF, 0, -sweepAngle);
                }

                // 上
                splitLength = remainLength > lineLength ? lineLength : remainLength;
                remainLength -= splitLength;
                if (splitLength <= 0) {
                    break;
                } else {
                    path.moveTo(mWidth - cornerRadius - padding, padding);
                    path.lineTo(mWidth - cornerRadius - padding - splitLength, padding);
                }

                // 左上角
                splitLength = remainLength > arcLength ? arcLength : remainLength;
                remainLength -= splitLength;
                if (splitLength <= 0) {
                    break;
                } else {
                    float sweepAngle = (360 * splitLength) / circumference;
                    mArcRectF.set(padding, padding, cornerDiameter + padding, cornerDiameter + padding);
                    path.addArc(mArcRectF, -90, -sweepAngle);
                }

                // 左
                splitLength = remainLength > lineLength ? lineLength : remainLength;
                remainLength -= splitLength;
                if (splitLength <= 0) {
                    break;
                } else {
                    path.moveTo(padding, cornerRadius + padding);
                    path.lineTo(padding, splitLength + cornerRadius + padding);
                }

                // 左下角
                splitLength = remainLength > arcLength ? arcLength : remainLength;
                remainLength -= splitLength;
                if (splitLength <= 0) {
                    break;
                } else {
                    float sweepAngle = (360 * splitLength) / circumference;
                    mArcRectF.set(padding, mHeight - cornerDiameter - padding, cornerDiameter + padding, mHeight - padding);
                    path.addArc(mArcRectF, -180, -sweepAngle);
                }

                // 下
                splitLength = remainLength > lineLength ? lineLength : remainLength;
                remainLength -= splitLength;
                if (splitLength <= 0) {
                    break;
                } else {
                    path.moveTo(cornerRadius + padding, mHeight - padding);
                    path.lineTo(splitLength + cornerRadius + padding, mHeight - padding);
                }

                // 右下角
                splitLength = remainLength > arcLength ? arcLength : remainLength;
                remainLength -= splitLength;
                if (splitLength <= 0) {
                    break;
                } else {
                    float sweepAngle = (360 * splitLength) / circumference;
                    mArcRectF.set(mWidth - cornerDiameter - padding, mHeight - cornerDiameter - padding, mWidth - padding, mHeight - padding);
                    path.addArc(mArcRectF, -270, -sweepAngle);
                }

                // 右
                splitLength = remainLength > lineLength ? lineLength : remainLength;
                if (splitLength <= 0) {
                    break;
                } else {
                    path.moveTo(mWidth - padding, mHeight - cornerRadius - padding);
                    path.lineTo(mWidth - padding, mHeight - cornerRadius - padding - splitLength);
                }
            } while (false);
        }

        @Override
        public void draw(Canvas canvas) {
            if (path.isEmpty()) {
                return;
            }
            canvas.drawPath(path, mBaseLinePaint);
        }

        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            updatePullProgress(1.0f);
        }

        @Override
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            cornerRadius = w * EDGE_CORNER_RADIUS_PERCENT;
            cornerDiameter = cornerRadius * 2;
            lineLength = w - cornerDiameter - mBaseLineWidth;
            circumference = (float) (Math.PI * cornerDiameter);
            arcLength = circumference / 4;
            perimeter = lineLength * 4 + circumference;
        }

        @Override
        public void clearAnimation() {

        }

        @Override
        public void onThemeChanged(boolean isNightTheme) {

        }
    }

    private class ShortLineAnimationDrawable implements AnimationDrawable {
        private static final float SHORT_LINE_LENGTH_PERCENT = 0.5f - HORIZONTAL_PADDING_PERCENT - CENTER_PADDING;
        private final Path path;
        /**
         * 每条短横线的长度
         */
        private float length;
        private float startY;
        private float startX;
        private float deltaY;
        private float curProgress;

        public ShortLineAnimationDrawable() {
            path = new Path();
        }

        @Override
        public void updatePullProgress(float progress) {
            if (curProgress == progress) {
                return;
            }
            curProgress = progress;

            path.reset();
            if (progress <= 0.25f) {
                return;
            } else if (progress > 0.5f) {
                progress = 0.5f;
            }

            float localPercent = (progress - 0.25f) / 0.25f;
            float remainLength = localPercent * length * LINE_COUNT;

            float splitLength;
            int line = 0;
            float y;
            do {
                splitLength = remainLength > length ? length : remainLength;
                remainLength -= splitLength;
                if (splitLength <= 0) {
                    break;
                } else {
                    y = startY + deltaY * line;
                    path.moveTo(startX, y);
                    path.lineTo(startX + splitLength, y);
                }
            } while (splitLength > 0 && ++line < LINE_COUNT);
        }

        @Override
        public void draw(Canvas canvas) {
            if (path.isEmpty()) {
                return;
            }
            canvas.drawPath(path, mWideLinePaint);
        }

        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            updatePullProgress(1.0f);
            float localStartX = startX;
            float localEndX = startX + length;
            float localStartY = startY;

            if (interpolatedTime < 0.075f) {
            } else if (interpolatedTime < 0.195f) {// longing offset == +0.02f
                float localPercent = (interpolatedTime - 0.075f) / 0.12f;
                localStartX = startX - (startX - mHorizontalPadding) * localPercent;
                localStartY = startY + (mLongLineDrawable.startY - startY) * localPercent;
            } else if (interpolatedTime < 0.325f) {
                localStartX = mHorizontalPadding;
                localStartY = mLongLineDrawable.startY;
            } else if (interpolatedTime < 0.395f) {// shorting offset == -0.03f
                float localPercent = (interpolatedTime - 0.325f) / 0.07f;
                float targetEndX = mWidth / 2 - mCenterPadding;
                localStartX = mHorizontalPadding;
                localEndX -= (localEndX - targetEndX) * localPercent;
                localStartY = mLongLineDrawable.startY;
            } else if (interpolatedTime < 0.575f) {
                localStartX = mHorizontalPadding;
                localEndX = mWidth / 2 - mCenterPadding;
                localStartY = mLongLineDrawable.startY;
            } else if (interpolatedTime < 0.695f) {// longing offset == +0.02f
                float localPercent = (interpolatedTime - 0.575f) / 0.12f;
                float lastEndX = mWidth / 2 - mCenterPadding;
                localStartX = mHorizontalPadding;
                localEndX = lastEndX + (localEndX - lastEndX) * localPercent;
                localStartY = mLongLineDrawable.startY - (mLongLineDrawable.startY - startY) * localPercent;
            } else if (interpolatedTime < 0.825f) {
                localStartX = mHorizontalPadding;
            } else if (interpolatedTime < 0.895f) {// shorting offset == -0.03f
                float localPercent = (interpolatedTime - 0.825f) / 0.07f;
                localStartX = mHorizontalPadding + (startX - mHorizontalPadding) * localPercent;
            }

            path.reset();
            int line = 0;
            float y;
            do {
                y = localStartY + deltaY * line;
                path.moveTo(localStartX, y);
                path.lineTo(localEndX, y);
            } while (++line < LINE_COUNT);

        }

        @Override
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            length = w * SHORT_LINE_LENGTH_PERCENT;
            startX = mWidth / 2 + mCenterPadding;
            deltaY = mLineVerticalSpacing;
            startY = mVerticalPadding + mWideLineWidth / 2;
        }

        @Override
        public void clearAnimation() {

        }

        @Override
        public void onThemeChanged(boolean isNightTheme) {

        }

    }

    private class LongLineAnimationDrawable implements AnimationDrawable {
        /**
         * 长线条的长度
         */
        private static final float LONG_LINE_LENGTH_PERCENT = (1 - HORIZONTAL_PADDING_PERCENT * 2);

        private final Path path;

        /**
         * 每条短横线的长度
         */
        private float length;
        private float startY;
        private float startX;
        private float deltaY;
        private float curProgress;

        public LongLineAnimationDrawable() {
            path = new Path();
        }

        @Override
        public void updatePullProgress(float progress) {
            if (curProgress == progress) {
                return;
            }
            curProgress = progress;

            path.reset();
            if (progress <= 0.5f) {
                return;
            }

            float localPercent = (progress - 0.5f) / 0.5f;
            float remainLength = localPercent * length * LINE_COUNT;


            float splitLength;
            int line = 0;
            float y;
            do {
                splitLength = remainLength > length ? length : remainLength;
                remainLength -= splitLength;
                if (splitLength <= 0) {
                    break;
                } else {
                    y = startY + deltaY * line;
                    path.moveTo(startX, y);
                    path.lineTo(startX + splitLength, y);
                }
            } while (splitLength > 0 && ++line < LINE_COUNT);
        }

        @Override
        public void draw(Canvas canvas) {
            if (path.isEmpty()) {
                return;
            }
            canvas.drawPath(path, mWideLinePaint);
        }

        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            updatePullProgress(1.0f);
            float localStartX = startX;
            float localEndX = startX + length;
            float localStartY = startY;
            if (interpolatedTime < 0.075f) {
            } else if (interpolatedTime < 0.145f) {// shorting duration offset == -0.03f
                float localPercent = (interpolatedTime - 0.075f) / 0.07f;
                float targetEndX = mWidth / 2 - mCenterPadding;
                localEndX -= (localEndX - targetEndX) * localPercent;
                localStartY -= (startY - mShortLineDrawable.startY) * localPercent;
            } else if (interpolatedTime < 0.325f) {
                localEndX = mWidth / 2 - mCenterPadding;
                localStartY = mShortLineDrawable.startY;
            } else if (interpolatedTime < 0.445f) {// longing duration offset == +0.02f
                float localPercent = (interpolatedTime - 0.325f) / 0.12f;
                float targetEndX = startX + length;
                float lastEndX = mWidth / 2 - mCenterPadding;
                localEndX = lastEndX + (targetEndX - lastEndX) * localPercent;
                localStartY = mShortLineDrawable.startY;
            } else if (interpolatedTime < 0.575f) {
                localStartY = mShortLineDrawable.startY;
            } else if (interpolatedTime < 0.645f) {// shorting duration offset == -0.03f
                float localPercent = (interpolatedTime - 0.575f) / 0.07f;
                localStartX += (mShortLineDrawable.startX - localStartX) * localPercent;
                localStartY = mShortLineDrawable.startY + (startY - mShortLineDrawable.startY) * localPercent;
            } else if (interpolatedTime < 0.825f) {
                localStartX = mShortLineDrawable.startX;
            } else if (interpolatedTime < 0.945f) {// longing duration offset == +0.02f
                float localPercent = (interpolatedTime - 0.825f) / 0.12f;
                localStartX = mShortLineDrawable.startX - (mShortLineDrawable.startX - localStartX) * localPercent;
            }

            path.reset();
            int line = 0;
            float y;
            do {
                y = localStartY + deltaY * line;
                path.moveTo(localStartX, y);
                path.lineTo(localEndX, y);
            } while (++line < LINE_COUNT);
        }

        @Override
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            length = w * LONG_LINE_LENGTH_PERCENT;
            startX = mHorizontalPadding;
            deltaY = mLineVerticalSpacing;
            startY = mHeight - mVerticalPadding - w * DRAWABLE_MAX_HEIGHT + mWideLineWidth / 2;
        }

        @Override
        public void clearAnimation() {

        }

        @Override
        public void onThemeChanged(boolean isNightTheme) {

        }

    }

    private class PaneAnimationDrawable implements AnimationDrawable {
        /**
         * 图片的宽
         */
        private static final float PANE_WIDTH_PERCENT = 0.354f;
        /**
         * 图片的高
         */
        private static final float PANE_HEIGHT_PERCENT = DRAWABLE_MAX_HEIGHT;

        private final Path borderPath;
        private final Path contentPath;
        private final Paint contentPaint;
        private final RectF rect;
        private int contentColor;
        private float perimeter;
        private float translateX;
        private float translateY;
        private float curProgress;

        public PaneAnimationDrawable() {
            contentColor = mContext.getResources().getColor(R.color.ssxinmian1);
//            contentColor = ThemeR.getColor(mContext, R.color.ssxinmian1, mIsNightTheme);

            borderPath = new Path();
            contentPath = new Path();
            contentPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            contentPaint.setColor(contentColor);

            rect = new RectF();
        }

        @Override
        public void updatePullProgress(float progress) {
            if (curProgress == progress) {
                return;
            }
            curProgress = progress;

            borderPath.reset();
            contentPath.reset();
            if (progress > 0.25f) {
                progress = 0.25f;
            }
            float halfEdgeLineWidth = mBaseLineWidth / 2;
            float localPercent = progress / (0.25f);
            float remainLength = localPercent * perimeter;
            float splitLength;
            do {
                splitLength = remainLength > rect.width() ? rect.width() : remainLength;
                remainLength -= splitLength;
                if (splitLength <= 0) {
                    break;
                } else {
                    borderPath.moveTo(rect.right, rect.top + halfEdgeLineWidth);
                    borderPath.lineTo(rect.right - splitLength, rect.top + halfEdgeLineWidth);

                    contentPath.moveTo(rect.right, rect.top);
                    contentPath.lineTo(rect.right - splitLength, rect.top);
                }

                splitLength = remainLength > rect.height() ? rect.height() : remainLength;
                remainLength -= splitLength;
                if (splitLength <= 0) {
                    break;
                } else {
                    borderPath.moveTo(rect.left + halfEdgeLineWidth, rect.top);
                    borderPath.lineTo(rect.left + halfEdgeLineWidth, rect.top + splitLength);

                    contentPath.lineTo(rect.left, rect.top + splitLength);
                }

                splitLength = remainLength > rect.width() ? rect.width() : remainLength;
                remainLength -= splitLength;
                if (splitLength <= 0) {
                    break;
                } else {
                    borderPath.moveTo(rect.left, rect.bottom - halfEdgeLineWidth);
                    borderPath.lineTo(rect.left + splitLength, rect.bottom - halfEdgeLineWidth);

                    contentPath.lineTo(rect.left + splitLength, rect.bottom);
                }

                splitLength = remainLength > rect.height() ? rect.height() : remainLength;
                if (splitLength <= 0) {
                    break;
                } else {
                    borderPath.moveTo(rect.right - halfEdgeLineWidth, rect.bottom);
                    borderPath.lineTo(rect.right - halfEdgeLineWidth, rect.bottom - splitLength);

                    contentPath.lineTo(rect.right, rect.bottom - splitLength);
                }
            } while (false);
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.translate(translateX, translateY);
            if (!contentPath.isEmpty()) {
                canvas.drawPath(contentPath, contentPaint);
            }
            if (!borderPath.isEmpty()) {
                canvas.drawPath(borderPath, mBaseLinePaint);
            }
            canvas.translate(-translateX, -translateY);
        }

        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            updatePullProgress(1.0f);
            /// animation start offset == -0.01f
            if (interpolatedTime < 0.065f) {
                translateX = 0;
                translateY = 0;
            } else if (interpolatedTime < 0.115f) {// animation duration offset = -0.05f
                float localPercent = (interpolatedTime - 0.065f) / 0.05f;
                translateX = (mWidth - rect.centerX() * 2) * localPercent;
                translateY = 0;
            } else if (interpolatedTime < 0.315f) {
                translateX = (mWidth - rect.centerX() * 2);
                translateY = 0;
            } else if (interpolatedTime < 0.365f) {// animation duration offset = -0.05f
                float localPercent = (interpolatedTime - 0.315f) / 0.05f;
                translateX = (mWidth - rect.centerX() * 2);
                translateY = (mHeight - rect.centerY() * 2) * localPercent;
            } else if (interpolatedTime < 0.565f) {
                translateX = (mWidth - rect.centerX() * 2);
                translateY = (mHeight - rect.centerY() * 2);
            } else if (interpolatedTime < 0.615f) {// animation duration offset = -0.05f
                float localPercent = (interpolatedTime - 0.565f) / 0.05f;
                translateX = (mWidth - rect.centerX() * 2) * (1 - localPercent);
                translateY = (mHeight - rect.centerY() * 2);
            } else if (interpolatedTime < 0.815f) {
                translateX = 0;
                translateY = (mHeight - rect.centerY() * 2);
            } else if (interpolatedTime < 0.865f) {// animation duration offset = -0.05f
                float localPercent = (interpolatedTime - 0.815f) / 0.05f;
                translateX = 0;
                translateY = (mHeight - rect.centerY() * 2) * (1 - localPercent);
            } else {
                translateX = 0;
                translateY = 0;
            }
        }

        @Override
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            rect.set(mHorizontalPadding, mVerticalPadding, mHorizontalPadding + w * PANE_WIDTH_PERCENT, mVerticalPadding + h * PANE_HEIGHT_PERCENT);
            perimeter = (rect.width() + rect.height()) * 2;
        }

        @Override
        public void clearAnimation() {
            translateX = 0;
            translateY = 0;
        }

        @Override
        public void onThemeChanged(boolean isNightTheme) {
            contentColor = mContext.getResources().getColor(R.color.ssxinmian1);
//            contentColor = ThemeR.getColor(mContext, R.color.ssxinmian1, isNightTheme);
            contentPaint.setColor(contentColor);
        }
    }

    private Context mContext;

    private int mWidth;
    private int mHeight;
    /**
     * 线条颜色
     */
    private int mLineColor;
    private float mLineVerticalSpacing;
    private float mHorizontalPadding;
    private float mCenterPadding;
    private float mVerticalPadding;
    private float mBaseLineWidth;
    private Paint mBaseLinePaint;
    private float mWideLineWidth;
    private Paint mWideLinePaint;
    private float mMinLineWidth;

    private EdgeAnimationDrawable mEdgeDrawable;
    private ShortLineAnimationDrawable mShortLineDrawable;
    private LongLineAnimationDrawable mLongLineDrawable;
    private PaneAnimationDrawable mPaneDrawable;
    private boolean mIsNightTheme;

    /**
     * 当前的下拉进度
     */
    private float mCurrPullProgress;

    public PullLoadingView(Context context) {
        super(context);
        init(context);
    }

    public PullLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;

        mLineColor = mContext.getResources().getColor(R.color.ssxinxian1_disable);
//        mLineColor = ThemeR.getColor(mContext, R.color.ssxinxian1_disable, mIsNightTheme);

        mMinLineWidth = 1;// 1px

        mBaseLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBaseLinePaint.setStyle(Paint.Style.STROKE);
        mBaseLinePaint.setColor(mLineColor);

        mWideLinePaint = new Paint(mBaseLinePaint);

        mEdgeDrawable = new EdgeAnimationDrawable();
        mShortLineDrawable = new ShortLineAnimationDrawable();
        mLongLineDrawable = new LongLineAnimationDrawable();
        mPaneDrawable = new PaneAnimationDrawable();
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
        clearAnimation();
        mEdgeDrawable.updatePullProgress(mCurrPullProgress);
        mPaneDrawable.updatePullProgress(mCurrPullProgress);
        mShortLineDrawable.updatePullProgress(mCurrPullProgress);
        mLongLineDrawable.updatePullProgress(mCurrPullProgress);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mEdgeDrawable.draw(canvas);
        mShortLineDrawable.draw(canvas);
        mLongLineDrawable.draw(canvas);
        mPaneDrawable.draw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w <= 0 || h <= 0 || w != h) {
            return;
        }
        mWidth = w;
        mHeight = h;

        float size = Math.max(mWidth / 48.0f, mMinLineWidth);

        mBaseLineWidth = size;
        mWideLineWidth = size * 2;

        mLineVerticalSpacing = (w * DRAWABLE_MAX_HEIGHT - mWideLineWidth) / 2;
        mHorizontalPadding = w * HORIZONTAL_PADDING_PERCENT;
        mCenterPadding = w * CENTER_PADDING;
        mVerticalPadding = w * VERTICAL_PADDING_PERCENT;

        mBaseLinePaint.setStrokeWidth(mBaseLineWidth);
        mWideLinePaint.setStrokeWidth(mWideLineWidth);

        mEdgeDrawable.onSizeChanged(w, h, oldw, oldh);
        mPaneDrawable.onSizeChanged(w, h, oldw, oldh);
        mShortLineDrawable.onSizeChanged(w, h, oldw, oldh);
        mLongLineDrawable.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void startAnimation(Animation animation) {
        clearAnimation();
        if (!(animation instanceof PullLoadingAnimation)) {
            animation = new PullLoadingAnimation();
            animation.setDuration(DEFAULT_ANIMATION_DURATION);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.RESTART);
            animation.setInterpolator(new LinearInterpolator());
        }
        super.startAnimation(animation);
    }

    @Override
    public void clearAnimation() {
        if (getAnimation() == null) {
            return;
        }
        super.clearAnimation();
        dispatchClearAnimation();
    }

    private void dispatchClearAnimation() {
        mEdgeDrawable.clearAnimation();
        mPaneDrawable.clearAnimation();
        mShortLineDrawable.clearAnimation();
        mLongLineDrawable.clearAnimation();
    }

    private void dispatchApplyTransformation(float interpolatedTime, Transformation t) {
        mEdgeDrawable.applyTransformation(interpolatedTime, t);
        mPaneDrawable.applyTransformation(interpolatedTime, t);
        mShortLineDrawable.applyTransformation(interpolatedTime, t);
        mLongLineDrawable.applyTransformation(interpolatedTime, t);
        invalidate();
    }

    public void setTheme(boolean isNightTheme) {
        if (mIsNightTheme == isNightTheme) {
            return;
        }
        mIsNightTheme = isNightTheme;

        mLineColor = mContext.getResources().getColor(R.color.ssxinxian1_disable);
//        mLineColor = ThemeR.getColor(mContext, R.color.ssxinxian1_disable, mIsNightTheme);
        mBaseLinePaint.setColor(mLineColor);
        mWideLinePaint.setColor(mLineColor);

        mEdgeDrawable.onThemeChanged(mIsNightTheme);
        mPaneDrawable.onThemeChanged(mIsNightTheme);
        mShortLineDrawable.onThemeChanged(mIsNightTheme);
        mLongLineDrawable.onThemeChanged(mIsNightTheme);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
    }

    @Override
    public void setImageResource(int resId) {
    }

    @Override
    public void setImageURI(Uri uri) {
    }
}
