package com.cvte.demo.mesh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

import com.johnnyyin.meshdemo.R;

public class BitmapMesh {

    public static class SampleView extends View {

        private static final int WIDTH = 40;
        private static final int HEIGHT = 40;

        private Bitmap mBitmap;
        private boolean mIsDebug;
        private Paint mPaint;
        private float[] mInhalePoint;
        private InhaleMesh mInhaleMesh;

        public SampleView(Context context) {
            super(context);
            setFocusable(true);

            mBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.screenshot);

            mPaint = new Paint();
            mInhalePoint = new float[]{0, 0};
            mInhaleMesh = new InhaleMesh(WIDTH, HEIGHT);
            mInhaleMesh.setBitmapSize(mBitmap.getWidth(), mBitmap.getHeight());
        }

        public void setIsDebug(boolean isDebug) {
            mIsDebug = isDebug;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            float bitmapWidth = mBitmap.getWidth();
            float bitmapHeight = mBitmap.getHeight();

            buildPaths(bitmapWidth / 2, h - Utils.dp2px(getContext(), 10));
            buildMesh(bitmapWidth, bitmapHeight);
        }

        public boolean startAnimation(boolean reverse) {
            Animation anim = this.getAnimation();
            if (null != anim && !anim.hasEnded()) {
                return false;
            }

            PathAnimation animation = new PathAnimation(0, HEIGHT + 1, reverse,
                    new PathAnimation.IAnimationUpdateListener() {
                        @Override
                        public void onAnimUpdate(int index) {
                            mInhaleMesh.buildMeshes(index);
                            invalidate();
                        }
                    });

            animation.setDuration(800);
            startAnimation(animation);
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFCCCCCC);
            canvas.drawBitmapMesh(mBitmap,
                    mInhaleMesh.getWidth(),
                    mInhaleMesh.getHeight(),
                    mInhaleMesh.getVertices(),
                    0, null, 0, mPaint);
            // Draw the target point.
            mPaint.setColor(Color.RED);
            mPaint.setStrokeWidth(Utils.dp2px(getContext(), 1));
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mInhalePoint[0], mInhalePoint[1], Utils.dp2px(getContext(), 3), mPaint);

            if (mIsDebug) {
                // Draw the mesh vertices.
                canvas.drawPoints(mInhaleMesh.getVertices(), mPaint);
                // Draw the paths
                mPaint.setColor(Color.BLUE);
                mPaint.setStyle(Paint.Style.STROKE);
                Path[] paths = mInhaleMesh.getPaths();
                for (Path path : paths) {
                    canvas.drawPath(path, mPaint);
                }
            }
        }

        private void buildMesh(float w, float h) {
            mInhaleMesh.buildMeshes(w, h);
        }

        private void buildPaths(float endX, float endY) {
            mInhalePoint[0] = endX;
            mInhalePoint[1] = endY;
            mInhaleMesh.buildPaths(endX, endY);
        }

        private int mLastPointX = 0;
        private int mLastPointY = 0;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float[] pt = {event.getX(), event.getY()};

            if (event.getAction() == MotionEvent.ACTION_UP) {
                int x = (int) pt[0];
                int y = (int) pt[1];
                if (mLastPointX != x || mLastPointY != y) {
                    mLastPointX = x;
                    mLastPointY = y;
                    buildPaths(pt[0], pt[1]);
                    invalidate();
                }
            }
            return true;
        }
    }

    private static class PathAnimation extends Animation {

        public interface IAnimationUpdateListener {
            public void onAnimUpdate(int index);
        }

        private int mFromIndex;
        private int mEndIndex;
        private boolean mReverse;
        private IAnimationUpdateListener mListener;

        public PathAnimation(int fromIndex, int endIndex, boolean reverse,
                             IAnimationUpdateListener listener) {
            mFromIndex = fromIndex;
            mEndIndex = endIndex;
            mReverse = reverse;
            mListener = listener;
        }

        public boolean getTransformation(long currentTime, Transformation outTransformation) {
            return super.getTransformation(currentTime, outTransformation);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            Interpolator interpolator = this.getInterpolator();
            if (null != interpolator) {
                float value = interpolator.getInterpolation(interpolatedTime);
                interpolatedTime = value;
            }
            if (mReverse) {
                interpolatedTime = 1.0f - interpolatedTime;
            }
            int currentIndex = (int) (mFromIndex + (mEndIndex - mFromIndex) * interpolatedTime);

            if (null != mListener) {
                mListener.onAnimUpdate(currentIndex);
            }
        }
    }
}