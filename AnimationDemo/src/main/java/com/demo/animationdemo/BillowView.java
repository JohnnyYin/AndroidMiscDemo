
package com.demo.animationdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 水波纹组件
 *
 * @author Administrator
 */
public class BillowView extends ImageView {
    /**
     * 保持原先放大倍数不变
     */
    public final static float SCALE_KEEP = -1;
    /**
     * 平滑放大时每次变化的最小步进值
     */
    private final static long THREAD_SLEEP = 30;
    private int mCount = 2;


    /** 背景色 */
    //不再需要了
    /**
     * 前景色
     */
    private volatile int[] mFGColors = new int[]{0x15ffffff, 0x18ffffff};
    /**
     * 一个完整二次贝赛尔曲线的宽
     */
    private int[] mPitchs = new int[]{400, 1000};
    /**
     * 一个完整二次贝赛尔曲线控制点距离平衡位置的高
     */
    private int[] mExtents = new int[]{12, 28};
    /**
     * 整个波纹每次平移的速度
     */
    private int[] mSpeeds = new int[]{10, 20};
    /**
     * 每条波纹的路径
     */
    private Path[] mPaths;
    /**
     * 每条波纹的平移距离
     */
    private int[] mOffsets;

    /**
     * 摆动的幅度
     */
    private float mPresentX, mPresentY;
    private volatile float mScaleX, mScaleY;
    private volatile float mStepX, mStepY;

    private Paint mPaint;

//    private volatile boolean isRunning = true;
//    private Thread mThread;

    private int disWidth;
    private int disHeight;

    private Timer mTimer = null;
    private boolean mIsLowFlag = false;

    /**
     * 百分比
     */
    private float mPercentage;

    private boolean mIsBillowStopped;

    /**
     * 判断水波纹当前是否处于动画状态中
     */
    public boolean isBillowStopped() {
        return this.mIsBillowStopped;
    }

    public BillowView(Context context) {
        this(context, null);
    }

    public BillowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BillowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(false);
        setClickable(false);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaths = new Path[mCount];
        mOffsets = new int[mCount];
        for (int i = 0; i < mCount; i++) {
            mPaths[i] = new Path();
            mOffsets[i] = 0;
        }

        mScaleX = 2;
        mPresentX = 1;
        mStepX = 0.01f;

    }

    /**
     * 设置当前是否是低性能手机
     *
     * @param isLowFlag
     */
    public void setLowFlag(boolean isLowFlag) {
        this.mIsLowFlag = isLowFlag;
        stop();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (disWidth == 0 || disHeight == 0) {
            disWidth = getWidth();
            disHeight = getHeight();
            mScaleY = disHeight * 0.99f;///某些情况，水波纹的计算速度较慢，会导致水波高度先达到峰值后又开始上涨，所以应该先设置最低值
//        	mFloatStayPositionX = disWidth * mFloatStayPositionPercent;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
//        recycleFloatBitmap();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        start();
        super.onAttachedToWindow();
    }

    /**
     * 标记当前是否正在上涨
     */
    private boolean isIncreasing = false;

    /**
     * 设置正在上涨的状态
     *
     * @param increase
     */
    public void setIncreasingState(boolean increase) {
        if (increase == isIncreasing) {
            return;
        }
        this.isIncreasing = increase;
        if (isIncreasing) {
            mCount = 2;
            mExtents = new int[]{32, 58};
            mSpeeds = new int[]{30, 40};
        } else {
            mCount = 2;
            mExtents = new int[]{12, 28};
            mSpeeds = new int[]{10, 20};
        }
    }

    /**
     * 设置颜色
     *
     * @param colors
     */
    public void setColors(int[] colors) {
        mFGColors = colors;
    }

    /**
     * 设置百分比
     *
     * @param percentage
     */
    public void setPercentage(float percentage) {
        if (percentage < 0 || percentage > 1) {
            return;
        }
        mPercentage = percentage;
        int _scaleY = (int) (getHeight() * percentage);
        mScaleY = getHeight() - _scaleY;
        invalidate();
    }

    /**
     * 计算比例
     */
    private void calcScale() {
        if (mPresentX != mScaleX) {
            mPresentX += mStepX;
        }
        if ((mStepX < 0 && mPresentX < mScaleX) || (mStepX > 0 && mPresentX > mScaleX)) {
            mPresentX = mScaleX;
        }

        if (mPresentY != mScaleY) {
            mPresentY += mStepY;
        }
        if ((mStepY < 0 && mPresentY < mScaleY) || (mStepY > 0 && mPresentY > mScaleY)) {
            mPresentY = mScaleY;
        }
    }

    /**
     * 开始水波纹绘制动画
     */
    public synchronized void start() {
//        mCurAnimTime = 0;
//        Log.d("test", "---------BillowView start-------------mIsBillowStopped:"+mIsBillowStopped);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mIsBillowStopped = false;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mIsLowFlag) {
                    stop();
                }
//				if(mCurAnimTime <= mFloatStartOffset && mCurAnimTime + THREAD_SLEEP > mFloatStartOffset && mPercentage >= 0.3 && mPercentage <= 0.9) {
//			        recycleFloatBitmap();
//				}
//				if(mCurAnimTime <= mFloatStartOffset) {
//				    mCurAnimTime += THREAD_SLEEP;
//				}
                postInvalidate();
//				Log.d("test", "---------BillowView run-------------");
            }
        }, 0, THREAD_SLEEP);
    }

    /**
     * 停止水波纹绘制动画
     */
    public synchronized void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
//        Log.d("test", "---------BillowView stop-------------mIsBillowStopped:"+mIsBillowStopped);
        mIsBillowStopped = true;
//        mCurAnimTime = 0;
//        recycleFloatBitmap();
    }

    /**
     * 画水波纹
     *
     * @param canvas
     */
    private void drawWave(Canvas canvas) {
//        int _width = getWidth();
//        int _height = getHeight();
//    	Log.d(TAG, disWidth+"___drawWave___"+disHeight+"_____________mScaleY:"+mScaleY);
        int xPos, loop, saveCount;
        float extent = 0;
        for (int i = 0; i < mCount; i++) {
            mOffsets[i] = (mOffsets[i] - mSpeeds[i]) % mPitchs[i];
            mPaths[i].reset();
            mPaths[i].moveTo(0, disHeight);
            mPaths[i].lineTo(mOffsets[i], mScaleY);
            xPos = mOffsets[i];
            loop = 0;

//            Log.i(TAG, mOffsets[i]+"___drawWave___mExtents[i]:"+mExtents[i]+"_____________mScaleY:"+mScaleY);

//            extent = mExtents[i] * mPresentX;
            //Percentage 不再改变
            extent = mExtents[i] * 1;

            do {
                xPos = mOffsets[i] + mPitchs[i] * loop;
                mPaths[i].cubicTo(xPos + mPitchs[i] / 2, mScaleY - extent,
                        xPos + mPitchs[i] / 2, mScaleY + extent,
                        xPos + mPitchs[i], mScaleY);
                loop++;
                // float start
//                if (isFloatShow() && i == mFollowNum && mFloatViewX < xPos + mPitchs[i] && mFloatViewX > xPos) {
//                    float t = (float) (mFloatViewX - xPos) / mPitchs[i];
//                    mFloatViewY = getBezierValue(mScaleY, mScaleY - extent, mScaleY + extent,
//                            mScaleY, t);
//                    mAngle = getAngle(new float[]{
//                            xPos, mScaleY
//                    }, new float[]{
//                            xPos + mPitchs[i] / 2, mScaleY - extent
//                    }, new float[]{
//                            xPos + mPitchs[i] / 2, mScaleY + extent
//                    }, new float[]{
//                            xPos + mPitchs[i], mScaleY
//                    }, t);
//                }
                // float end
//                Log.i(TAG, mPaths[i]+"___drawWave___loop:"+loop+"_____________mScaleY:"+mScaleY);
            } while (xPos < disWidth);
            mPaths[i].lineTo(disWidth, disHeight);
            mPaths[i].close();

            saveCount = canvas.save();
//            if (isFloatShow() && i == mFollowNum) {
//                drawFloatView(canvas);
//            }
            canvas.clipPath(mPaths[i]);
            mPaint.setColor(mFGColors[i]);
            canvas.drawPath(mPaths[i], mPaint);
            canvas.restoreToCount(saveCount);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // float start
//        if (mCurAnimTime > mFloatStartOffset && mFloatViewX < mFloatStayPositionX) {
//            mFloatViewX += getFloatXOffset();
//            if (mFloatViewX > mFloatStayPositionX) {
//                mFloatViewX = mFloatStayPositionX;
//            }
//        }
        // float end
//    	drawOther(canvas, mPaint);
        calcScale();
        canvas.setDrawFilter(mPainFilter);
        drawWave(canvas);
    }

    PaintFlagsDrawFilter mPainFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG);

//    @Override
//    public void run() {
//        while (isRunning) {
//        	postInvalidate();
//            try {
//                Thread.sleep(THREAD_SLEEP);
//            } catch (InterruptedException ex) {
//                Thread.interrupted();// 只有这个地方也interrupted线程才会真正停止
//            }
//        }
//    }

    /** ---------水波中的漂浮物相关 start--------- **/
//    private Activity mActivity;
//    private Bitmap mFloatBitmap;
//    private float mFloatViewX;
//    private float mFloatViewY = disHeight;
//    private int mFloatWidth;
//    private int mFloatHeight;
//    private float mFloatStayPositionX;
//    private float mFloatStayPositionPercent = 0.25f;
//    private int mFloatXOffset = Commons.dip2px(getContext(), 1);
//    private long mCurAnimTime = 0;
//    private int mFloatStartOffset = 2500;
//    private int mFollowNum = 0;
//    private Rect mFloatRect;
//    private CloudResourceStatus mCRMemmoryFloatStatus = new CloudResourceStatus(11);
//    private Paint mFloatPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
//    private Matrix mFloatMatrix = new Matrix();
//    private float mAngle = 0;
//
//    public void setActivity(Activity activity) {
//        mActivity = activity;
//    }
//
//    private float getFloatXOffset() {
//        return mFloatViewX > 0 ? mFloatXOffset * (1.0f - mFloatViewX / mFloatStayPositionX)
//                : mFloatXOffset;
//    }
//
//    public void setFloatBitmap(Bitmap bm) {
//        if (bm != null) {
//            mFloatBitmap = bm;
//            mFloatWidth = bm.getWidth();
//            mFloatHeight = bm.getHeight();
//            mFloatViewX = -mFloatWidth;
//            setFloatListener();
//            mCRMemmoryFloatStatus.reset();
//        }
//    }
//
//    public void recycleFloatBitmap() {
//        if (mFloatBitmap != null) {
//            mFloatBitmap.recycle();
//            mFloatBitmap = null;
//            removeFloatListener();
//            report();
//        }
//    }
//
//    private boolean isFloatShow() {
//        return mFloatBitmap != null && mCurAnimTime > mFloatStartOffset;
//    }
//
//    private void setFloatListener() {
//        this.setOnTouchListener(new OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        if(mActivity != null && mFloatRect != null && mFloatRect.contains((int)event.getX(),(int) event.getY())) {
//                            CloudResourceUtil.performResourceClick(getRootView(), mActivity, CloudResourceManager.KEY_MEMORYNEWS, mCRMemmoryFloatStatus);
//                        }
//                        break;
//                }
//                return false;
//            }
//        });
//    }
//
//    private void removeFloatListener() {
//        this.setOnTouchListener(null);
//    }
//
//    /** 绘制漂浮物 */
//    private void drawFloatView(Canvas canvas) {
//        if(mFloatBitmap != null && !mFloatBitmap.isRecycled() && mFloatViewX <= mFloatStayPositionX) {
//            mFloatRect = new Rect((int)(mFloatViewX - mFloatWidth / 2), (int)(mFloatViewY
//                    - mFloatHeight / 2), (int)(mFloatViewX + mFloatWidth / 2), (int)(mFloatViewY
//                    + mFloatHeight / 2));
//            mFloatMatrix.preTranslate(mFloatWidth / 2, mFloatHeight / 2);
//            mFloatMatrix.postTranslate(-mFloatWidth / 2, -mFloatHeight / 2);
//            mFloatMatrix.setRotate(mAngle, mFloatWidth / 2, mFloatHeight / 2);
//            canvas.translate(mFloatViewX - mFloatWidth / 2, mFloatViewY - mFloatHeight / 2);
//            canvas.drawBitmap(mFloatBitmap, mFloatMatrix, mFloatPaint);
//            canvas.translate(-(mFloatViewX - mFloatWidth / 2), -(mFloatViewY - mFloatHeight / 2));
//            mCRMemmoryFloatStatus.isShow = true;
//        }
//    }
//
//    private void report() {
//        if (mCRMemmoryFloatStatus.isShow) {
//            KInfocClientAssist.getInstance().reportData("cm_amusement", mCRMemmoryFloatStatus.getClickDes());
//            KInfocClientAssist.getInstance().reportData("cm_festivaldisplay", mCRMemmoryFloatStatus.getShowDes());
//            mCRMemmoryFloatStatus.reset();
//        }
//    }

//    /** 贝塞尔曲线方程 */
//    private float getBezierValue(float p0, float p1, float p2, float p3, float t) {
//        float value = 0;
//        float t2 = 1 - t;
//        value = p0 * t2 * t2 * t2 + 3 * p1 * t * t2 * t2 + 3 * p2 * t * t * t2 + p3
//                * t * t * t;
//        return value;
//    }
//
//    /** 获得当前点的倾斜角度 */
//    private float getAngle(float[] p1, float[] p2, float[] p3, float[] p4, float t) {
//        float[] p5 = new float[2];
//        float[] p6 = new float[2];
//        float[] p7 = new float[2];
//
//        p5[0] = p1[0] + (p2[0] - p1[0]) * t;
//        p5[1] = p1[1] + (p2[1] - p1[1]) * t;
//        p6[0] = p2[0] + (p3[0] - p2[0]) * t;
//        p6[1] = p2[1] + (p3[1] - p2[1]) * t;
//        p7[0] = p3[0] + (p4[0] - p3[0]) * t;
//        p7[1] = p3[1] + (p4[1] - p3[1]) * t;
//
//        float[] p8 = new float[2];
//        float[] p9 = new float[2];
//
//        p8[0] = p5[0] + (p6[0] - p5[0]) * t;
//        p8[1] = p5[1] + (p6[1] - p5[1]) * t;
//        p9[0] = p6[0] + (p7[0] - p6[0]) * t;
//        p9[1] = p6[1] + (p7[1] - p6[1]) * t;
//
//        float slope = (p9[1] - p8[1]) / (p9[0] - p8[0]);
//
//        Double angle = Double.valueOf(Math.atan(slope) * 180 / Math.PI);
//        return angle.floatValue();
//    }
    /** ---------水波中的漂浮物相关 end--------- **/

}
