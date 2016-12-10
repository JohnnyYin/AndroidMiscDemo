package me.yinzhong.coordinatorlayoutdemo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author YinZhong
 * @since 2016/12/11
 */
@CoordinatorLayout.DefaultBehavior(TestBehaviorView.Behavior.class)
public class TestBehaviorView extends View {
    public TestBehaviorView(Context context) {
        this(context, null);
    }

    public TestBehaviorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestBehaviorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    public static final class Behavior extends CoordinatorLayout.Behavior<TestBehaviorView> {
        public Behavior() {
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, TestBehaviorView child, View dependency) {
            return dependency instanceof TestView;
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, TestBehaviorView child, View dependency) {
            child.setY(dependency.getY());
            child.setX(child.getContext().getResources().getDisplayMetrics().widthPixels - dependency.getX() - child.getWidth());
            return true;
        }

    }
}
