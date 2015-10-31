package com.johnnyyin.flabbybird.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.johnnyyin.flabbybird.R;

import java.util.ArrayList;
import java.util.List;

/**
 * http://blog.csdn.net/lmj623565791/article/details/42965779
 *
 * @author zhy
 */
public class GameFlabbyBird extends SurfaceView implements Callback, Runnable {

    private SurfaceHolder mHolder;
    /**
     * 与SurfaceHolder绑定的Canvas
     */
    private Canvas mCanvas;
    /**
     * 用于绘制的线程
     */
    private Thread t;
    /**
     * 线程的控制开关
     */
    private boolean isRunning;

    private Paint mPaint;

    /**
     * 当前View的尺寸
     */
    private int mWidth;
    private int mHeight;
    private RectF mGamePanelRect = new RectF();

    /**
     * 背景
     */
    private Bitmap mBg;

    /**
     * *********鸟相关**********************
     */
    private Bird mBird;
    private Bitmap mBirdBitmap;
    /**
     * 地板
     */
    private Floor mFloor;
    private Bitmap mFloorBg;

    /**
     * *********管道相关**********************
     */
    /**
     * 管道
     */
    private Bitmap mPipeTop;
    private Bitmap mPipeBottom;
    private RectF mPipeRect;
    private int mPipeWidth;
    /**
     * 管道的宽度 60dp
     */
    private static final int PIPE_WIDTH = 60;

    private List<Pipe> mPipes = new ArrayList<Pipe>();

    /**
     * 分数
     */
    private final int[] mNums = new int[]{R.drawable.n0, R.drawable.n1,
            R.drawable.n2, R.drawable.n3, R.drawable.n4, R.drawable.n5,
            R.drawable.n6, R.drawable.n7, R.drawable.n8, R.drawable.n9};
    private Bitmap[] mNumBitmap;
    private int mGrade = 100;
    private static final float RADIO_SINGLE_NUM_HEIGHT = 1 / 15f;
    private int mSingleGradeWidth;
    private int mSingleGradeHeight;
    private RectF mSingleNumRectF;

    private int mSpeed;

    public GameFlabbyBird(Context context) {
        this(context, null);
    }

    public GameFlabbyBird(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHolder = getHolder();
        mHolder.addCallback(this);

        setZOrderOnTop(true);// 设置画布 背景透明
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        // 设置可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        // 设置常亮
        this.setKeepScreenOn(true);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        initBitmaps();

        // 初始化速度
        mSpeed = Util.dp2px(getContext(), 2);
        mPipeWidth = Util.dp2px(getContext(), PIPE_WIDTH);

    }

    /**
     * 初始化图片
     */
    private void initBitmaps() {
        mBg = loadImageByResId(R.drawable.bg1);
        mBirdBitmap = loadImageByResId(R.drawable.b1);
        mFloorBg = loadImageByResId(R.drawable.floor_bg2);
        mPipeTop = loadImageByResId(R.drawable.g2);
        mPipeBottom = loadImageByResId(R.drawable.g1);

        mNumBitmap = new Bitmap[mNums.length];
        for (int i = 0; i < mNumBitmap.length; i++) {
            mNumBitmap[i] = loadImageByResId(mNums[i]);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("TAG", "surfaceCreated");

        // 开启线程
        isRunning = true;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.e("TAG", "surfaceChanged");
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("TAG", "surfaceDestroyed");
        // 通知关闭线程
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();

            try {
                if (end - start < 50) {
                    Thread.sleep(50 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    private void draw() {
        try {
            // 获得canvas
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                // drawSomething..

                drawBg();
                drawBird();

                drawPipes();

                drawFloor();

                drawGrades();

                // 更新我们地板绘制的x坐标
                mFloor.setX(mFloor.getX() - mSpeed);

            }
        } catch (Exception e) {
        } finally {
            if (mCanvas != null)
                mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void drawFloor() {
        mFloor.draw(mCanvas, mPaint);
    }

    /**
     * 绘制背景
     */
    private void drawBg() {
        mCanvas.drawBitmap(mBg, null, mGamePanelRect, null);
    }

    private void drawBird() {
        mBird.draw(mCanvas);
    }

    /**
     * 绘制管道
     */
    private void drawPipes() {
        for (Pipe pipe : mPipes) {
            pipe.setX(pipe.getX() - mSpeed);
            pipe.draw(mCanvas, mPipeRect);
        }
    }

    /**
     * 初始化尺寸相关
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;
        mGamePanelRect.set(0, 0, w, h);

        // 初始化mBird
        mBird = new Bird(getContext(), mWidth, mHeight, mBirdBitmap);
        // 初始化地板
        mFloor = new Floor(mWidth, mHeight, mFloorBg);
        // 初始化管道范围
        mPipeRect = new RectF(0, 0, mPipeWidth, mHeight);

        Pipe pipe = new Pipe(getContext(), w, h, mPipeTop, mPipeBottom);
        mPipes.add(pipe);

        //初始化分数
        mSingleGradeHeight = (int) (h * RADIO_SINGLE_NUM_HEIGHT);
        mSingleGradeWidth = (int) (mSingleGradeHeight * 1.0f / mNumBitmap[0].getHeight() * mNumBitmap[0].getWidth());
        mSingleNumRectF = new RectF(0, 0, mSingleGradeWidth, mSingleGradeHeight);

    }

    /**
     * 绘制分数
     */
    private void drawGrades() {
        String grade = mGrade + "";
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        mCanvas.translate(mWidth / 2 - grade.length() * mSingleGradeWidth / 2,
                1f / 8 * mHeight);
        //draw single num one by one
        for (int i = 0; i < grade.length(); i++) {
            String numStr = grade.substring(i, i + 1);
            int num = Integer.valueOf(numStr);
            mCanvas.drawBitmap(mNumBitmap[num], null, mSingleNumRectF, null);
            mCanvas.translate(mSingleGradeWidth, 0);
        }
        mCanvas.restore();

    }

    /**
     * 根据resId加载图片
     *
     * @param resId
     * @return
     */
    private Bitmap loadImageByResId(int resId) {
        return BitmapFactory.decodeResource(getResources(), resId);
    }
}
