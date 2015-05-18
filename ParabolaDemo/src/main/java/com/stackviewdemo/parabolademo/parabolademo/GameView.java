package com.stackviewdemo.parabolademo.parabolademo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.Toast;

public class GameView extends View {
    private class MovingObject {
        public MovingObject(double v0, double angle) {
            this.v0 = v0;
            this.angle = angle;
        }

        public double v0;// / 物体的初速度
        public double angle; // / 物体初速度与水平方向的夹角
        public double x;// / 物体的横坐标
        public double y;// / 物体的纵坐标
        public double smax; // 水平面上最大射程
        public double H;// / 最大高度
    }

    private class MoveAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation tf) {
            float t = getDuration() * interpolatedTime / 1000;
            Log.v("TAG", String.valueOf(t));
            isDraw = true;
            // 水平坐标
            double x = obj.v0 * Math.cos(obj.angle) * t;
            // 竖直坐标
            double y = obj.v0 * Math.sin(obj.angle) * t - G * t * t / 2;
            double slope = Math.sin(obj.angle) / Math.cos(obj.angle) - G * x / (obj.v0 * obj.v0 * Math.cos(obj.angle) * Math.cos(obj.angle));// 斜率
            double angle = 180 * Math.atan(slope) / Math.PI;// 角度
            degress = -(obj.angle + angle);
            Log.e("XXX", "slope:" + String.valueOf(slope));
            Log.e("XXX", "angle:" + String.valueOf(angle));
            // 坐标转换
            obj.x = scale * x;
            obj.y = 500 - scale * y;
            postInvalidate();

            super.applyTransformation(interpolatedTime, tf);
        }
    }

    // 重力加速度
    private static final double G = 9.8;
    private int mWidth;
    private int mHeight;
    private Paint mPaint = null;

    //    private double maxS = 0;
//    private double maxH = 0;
    //    private double maxT = 0;
    private double dx;
    private double dy;
    //    private double d;
    private float scale = 1;// 像素和长度的比例
    private MovingObject obj;
    private boolean isDraw = false;
    private double degress = 0;

    private Bitmap mBmp;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private boolean initLine() {
        if (mWidth == 0 || mHeight == 0)
            return false;

        float v = 10;// m/s
        scale = mWidth / 10;// 10表示屏幕宽度代表的距离为10m

        // 计算物体的最大高度、运动时间及最大射程
        obj = new MovingObject(v, Math.PI / 2.6f);
        // 运行时间
        double ttt = 2 * obj.v0 * Math.sin(obj.angle) / G;// 水平面落地的时间
        // 最大高度
        obj.H = obj.v0 * obj.v0 * Math.sin(obj.angle) * Math.sin(obj.angle) / (2 * G);// (v * sin(Q))^2/(2g)
        // 最大射程
        obj.smax = 2 * obj.v0 * obj.v0 * Math.sin(obj.angle) * Math.cos(obj.angle) / G;// 2 * v ^ 2 * sin(Q) * cos (Q) / g
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getWidth();
        mHeight = getHeight();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBmp = BitmapFactory.decodeResource(getResources(), R.drawable.abnormal_percent);
    }

    private long time;
    private int count;

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.GREEN);
        if (isDraw) {
            long cur = System.currentTimeMillis();
            count++;
            if (cur - time > 1000) {
                Log.e("CMTV", String.valueOf(count));
                time = cur;
                count = 0;
            }

            Bitmap temp = getCircleBitmap(mBmp, (float) degress);
            canvas.drawBitmap(temp, (float) obj.x, (float) obj.y, mPaint);
            temp.recycle();
        }
    }

    private Bitmap getCircleBitmap(Bitmap bm, float degress) {
        Bitmap newb = Bitmap.createBitmap(mBmp.getWidth(), mBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newb);
        cv.rotate(degress, mBmp.getWidth() / 2, mBmp.getHeight() / 2);
        Bitmap mBmpTemp = Bitmap.createBitmap(bm, 0, 0, mBmp.getWidth(), mBmp.getHeight());
        cv.drawBitmap(mBmpTemp, 0, 0, mPaint);
        return newb;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                run();
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Toast.makeText(getContext(), "hello", Toast.LENGTH_SHORT).show();
        run();
        return super.onKeyDown(keyCode, event);
    }

    public void run() {
        if (initLine()) {
            clearAnimation();
            MoveAnimation animation = new MoveAnimation();
            animation.setDuration(50000);
            animation.setInterpolator(new LinearInterpolator());
            startAnimation(animation);
        }
    }
}