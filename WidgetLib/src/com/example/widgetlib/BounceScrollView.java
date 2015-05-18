
package com.example.widgetlib;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * ScrollView反弹效果的实现
 */
public class BounceScrollView extends ScrollView {
    private static final int MOTION_DIRECTION_INIT = -1;
    private static final int MOTION_DIRECTION_UP = 1;
    private static final int MOTION_DIRECTION_DOWN = 2;

    private int mMotionDirection = MOTION_DIRECTION_INIT;
    private View mRootLayout;// 根View

    private Rect normal = new Rect();// 矩形(这里只是个形式，只是用于判断是否需要动画.)

    private SparseArray<Float> mLastPoint = new SparseArray<Float>();

    // private OverScroller mScroller;
    //
    // private float[] mLastVelocity = {
    // 0.0f, 0.0f, 0.0f, 0.0f
    // };

    // private boolean isCount = false;// 是否开始计算

    public BounceScrollView(Context context) {
        super(context);
        init();
    }

    public BounceScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BounceScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
    }

    /***
     * 回缩动画
     */
    public void animation() {
        // 开启移动动画
        TranslateAnimation ta = new TranslateAnimation(0, 0, mRootLayout.getTop(),
                normal.top);
        ta.setDuration(200);
        mRootLayout.clearAnimation();
        mRootLayout.startAnimation(ta);
        // 设置回到正常的布局位置
        mRootLayout.layout(normal.left, normal.top, normal.right, normal.bottom);
        normal.setEmpty();
    }

    // 是否需要开启动画
    public boolean isNeedAnimation() {
        return !normal.isEmpty();
    }

    public boolean isNeedMove() {
        int offset = mRootLayout.getMeasuredHeight() - getHeight();
        if (mRootLayout.getTop() != getTop()) {
            return true;
        }
        int scrollY = getScrollY();
        switch (mMotionDirection) {
            case MOTION_DIRECTION_UP:
                if (scrollY == offset)
                    return true;
                break;
            case MOTION_DIRECTION_DOWN:
                if (scrollY == 0)
                    return true;
                break;
        }
        return false;
    }

    @Override
    protected void onFinishInflate() {
        if (getChildCount() > 0) {
            mRootLayout = getChildAt(0);
        }
    }

    // @SuppressLint("NewApi")
    // protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    // super.onScrollChanged(l, t, oldl, oldt);
    // // if (t == 0) {
    // if (mScroller == null) {
    // try {
    // Field field = ScrollView.class.getDeclaredField("mScroller");
    // field.setAccessible(true);
    // Object obj = field.get(this);
    // if (obj instanceof OverScroller) {
    // mScroller = (OverScroller) obj;
    // }
    // } catch (NoSuchFieldException e) {
    // e.printStackTrace();
    // } catch (IllegalArgumentException e) {
    // e.printStackTrace();
    // } catch (IllegalAccessException e) {
    // e.printStackTrace();
    // }
    // } else {
    // float velocity = mScroller.getCurrVelocity();
    //
    // if (t == 0) {
    // velocity = (mLastVelocity[1] + mLastVelocity[2] + mLastVelocity[3] +
    // velocity) / 30;
    // Log.e("WidgetLib", "[BounceScrollView]-[onScrollChanged]velocity:" +
    // velocity);
    // TranslateAnimation ta = new TranslateAnimation(0, 0,
    // mRootLayout.getTop(),
    // normal.top + 100);
    // int time = (int) (100 / velocity);
    // ta.setDuration(time);
    // TranslateAnimation ta2 = new TranslateAnimation(0, 0, normal.top + 100,
    // normal.top);
    // ta2.setStartOffset(time);
    // ta2.setDuration(time);
    // mRootLayout.startAnimation(ta);
    // mRootLayout.startAnimation(ta2);
    // } else {
    // mLastVelocity[0] = mLastVelocity[1];
    // mLastVelocity[1] = mLastVelocity[2];
    // mLastVelocity[2] = mLastVelocity[3];
    // mLastVelocity[3] = velocity;
    // }
    // }
    // // }
    // }

    // @Override
    // public void computeScroll() {
    // super.computeScroll();
    // }
    // @Override
    // public void fling(int velocityY) {
    // Log.e("WidgetLib", "[BounceScrollView]-[fling]:" + velocityY);
    // super.fling(velocityY);
    // }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        if (mRootLayout != null) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mLastPoint.put(0, ev.getY(0));
                    break;
                case MotionEvent.ACTION_UP:
                    // 准确说这是最后一个点松开后要做回弹动画
                    mLastPoint.put(0, -1.0f);
                    if (isNeedAnimation()) {
                        animation();
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN: {
                    for (int i = 0; i < ev.getPointerCount(); i++) {
                        mLastPoint.put(i, -1.0f);
                    }
                    break;
                }
                case MotionEvent.ACTION_POINTER_UP:// 触摸点提起后将其上次的y值置为-1
                    mLastPoint.put(ev.getActionIndex(), -1.0f);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaY = 0;
                    for (int i = 0; i < ev.getPointerCount(); i++) {
                        Float lastY = mLastPoint.get(i);
                        float nowY = ev.getY(i);
                        mLastPoint.put(i, nowY);
                        if (lastY != null && lastY.floatValue() != -1.0f) {
                            float hy = nowY - lastY.floatValue();
                            deltaY = Math.abs(deltaY) > Math.abs(hy) ? deltaY : hy;
                        }
                    }
                    if (deltaY > 0) {
                        mMotionDirection = MOTION_DIRECTION_DOWN;
                    } else {
                        mMotionDirection = MOTION_DIRECTION_UP;
                    }
                    // 当滚动到最上或者最下时就不会再滚动，这时移动布局
                    if (isNeedMove()) {
                        // 初始化头部矩形
                        if (normal.isEmpty()) {
                            // 保存正常的布局位置
                            normal.set(mRootLayout.getLeft(), mRootLayout.getTop(),
                                    mRootLayout.getRight(), mRootLayout.getBottom());
                        }
                        // 移动布局
                        mRootLayout.layout(mRootLayout.getLeft(), mRootLayout.getTop()
                                + ((int) deltaY / 3),
                                mRootLayout.getRight(), mRootLayout.getBottom()
                                        + ((int) deltaY / 3));
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

}
