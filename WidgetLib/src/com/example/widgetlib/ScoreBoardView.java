
package com.example.widgetlib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class ScoreBoardView extends LinearLayout {
    private int mItemH;
    private int mDuration = 1500;
    private int mNum;

    private Context mContext;

    private ViewGroup mRoot;

    private ScoreAnimatiom mAnim = new ScoreAnimatiom();

    private class ScoreAnimatiom extends Animation {
        private int mNum;

        public void setNum(int num) {
            this.mNum = num;
        }

        protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (mRoot != null)
                mRoot.scrollTo(0, -(int) (interpolatedTime * mItemH * this.mNum));
            super.applyTransformation(interpolatedTime, t);
        };

    }

    public ScoreBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public ScoreBoardView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    @Override
    protected void onFinishInflate() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mRoot = (ViewGroup) inflater.inflate(R.layout.score_board_view, null);
        if (mRoot != null) {
            this.addView(mRoot);
        }
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mItemH = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init() {
        mAnim.setDuration(mDuration);
        mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    public void startAnim(int duration) {
        mAnim.setDuration(duration);
        clearAnimation();
        startAnimation(mAnim);
    }

    public void startAnim() {
        startAnim(mDuration);
    }

    public void setNum(int num) {
        this.mNum = num;
        mAnim.setNum(num);
    }

}
