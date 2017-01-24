package com.johnnyyin.pullanimationdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * @author YinZhong
 * @since 2017/1/28
 */
public class PullLoadingView3 extends ImageView {
    private WaterMelonLoadingDrawable mWaterMelonLoadingDrawable;

    public PullLoadingView3(Context context) {
        this(context, null);
    }

    public PullLoadingView3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullLoadingView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mWaterMelonLoadingDrawable = new WaterMelonLoadingDrawable(context, 0, 0, 0);
        setImageDrawable(mWaterMelonLoadingDrawable);
    }

    /**
     * 获得当前的下拉进度
     */
    public float getPullProgress() {
        return mWaterMelonLoadingDrawable.getPullProgress();
    }

    /**
     * 设置当前的下拉进度
     *
     * @param curPullDistance 当前的下拉距离
     * @param maxPullDistance 最大的下拉距离
     * @param isRebound       是否是回弹
     */
    public void setPullProgress(float curPullDistance, float maxPullDistance, boolean isRebound) {
        mWaterMelonLoadingDrawable.setPullProgress(curPullDistance, maxPullDistance, isRebound);
    }

    @Override
    public void startAnimation(Animation animation) {
        mWaterMelonLoadingDrawable.start();
    }
}
