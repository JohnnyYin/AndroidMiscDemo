package com.example.widgetlib.scalelayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.example.widgetlib.BuildConfig;

public class ScaleLayout extends FrameLayout {
	private static final String TAG = ScaleLayout.class.getSimpleName();

	private static final int GESTURE_DIRECTION_INIT = 0;// 初始状态
	private static final int GESTURE_DIRECTION_UP = 1;
	private static final int GESTURE_DIRECTION_DOWN = 2;
	private static final int GESTURE_DIRECTION_LEFT = 3;
	private static final int GESTURE_DIRECTION_RIGHT = 4;
	public static final int FOLW_VIEW_STATE_CLOSED = 1;
	public static final int FOLW_VIEW_STATE_CLOSING = 2;
	public static final int FOLW_VIEW_STATE_OPENED = 3;
	public static final int FOLW_VIEW_STATE_OPENING = 4;

	public static final int FOLW_VIEW_LOCATION_LEFT = 1;
	public static final int FOLW_VIEW_LOCATION_RIGHT = 2;

	private int mFoldViewLocation = FOLW_VIEW_LOCATION_LEFT;

	private View mInsideView;
	private View mOutsideView;

	// private Bitmap mFoldBitmapLeft;
	// private Bitmap mFoldBitmapRight;

	private Scroller mScroller;
	private GestureDetector mGestureDetector;

	private int mScreenWidth;
	private int mScreenHeight;
	
	private int mInsideScrollY;

	// private float mDownX;
	// private float mDownY;

	private Point mDownPoint = new Point();// 手指按下的位置
	private Point mLastPoint = new Point();// 最后一个点的位置

	private int mGestureDirection = GESTURE_DIRECTION_INIT;

	private int mMenuState = FOLW_VIEW_STATE_CLOSED;

	public static final int DEFAULT_ANIMATION_DURATION = 500;
	private int mScrollAnimationDuration = DEFAULT_ANIMATION_DURATION;

	private int mMenuWidth;

	float[] mMapPoints = new float[2];
	private Bitmap mInsideBitmap;
	private Camera camera = new Camera();
	private Matrix matrix = new Matrix();

	private float mCurDiatance;// 当前移动的距离

	private VelocityTracker mVelocityTracker;

	public ScaleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mInsideView = findViewWithTag("insideView");
		mOutsideView = findViewWithTag("outsideView");
		mOutsideView.setDrawingCacheEnabled(false);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		mScreenHeight = metrics.heightPixels;
		mScreenWidth = metrics.widthPixels;

		mMenuWidth = mScreenWidth * 2 / 3;
	}

	protected void onDetachedFromWindow() {
		if ((mInsideBitmap != null) && (!mInsideBitmap.isRecycled()))
			mInsideBitmap.recycle();
		mInsideBitmap = null;
		super.onDetachedFromWindow();
	}

	void setScrollDuration(int duration) {
		mScrollAnimationDuration = duration;
	}

	void setMenuWidth(int width) {
		if (width > 0 && width < mScreenWidth)
			this.mMenuWidth = width;
	}

	public int getMenuState() {
		return mMenuState;
	}

	public void setFoldViewLocation(int location) {
		if (location != FOLW_VIEW_LOCATION_LEFT
				|| location != FOLW_VIEW_LOCATION_RIGHT)
			throw new IllegalArgumentException("折叠视图位置有误！");
		this.mFoldViewLocation = location;
	}

	private void init() {
		// setClickable(true);
		// setLongClickable(true);
		mScroller = new Scroller(getContext());
		mGestureDetector = new GestureDetector(getContext(),
				new CustomGestureListener());
	}

	// 调用此方法滚动到目标位置
	public void smoothScrollTo(int fx, int fy) {
		smoothScrollTo(fx, fy, mScrollAnimationDuration);
	}

	// 调用此方法滚动到目标位置
	public void smoothScrollTo(int fx, int fy, int duration) {
		int dx = fx - mScroller.getFinalX();
		int dy = fy - mScroller.getFinalY();
		mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx,
				dy, duration);
		invalidate();// 这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
	}

	// 调用此方法设置滚动的相对偏移
	public void smoothScrollBy(int dx, int dy) {
		smoothScrollBy(dx, dy, mScrollAnimationDuration);
	}

	// 调用此方法设置滚动的相对偏移
	public void smoothScrollBy(int dx, int dy, int duration) {
		if (mGestureDirection == GESTURE_DIRECTION_RIGHT) {
			if (Math.abs(mScroller.getFinalX() + dx) >= mMenuWidth) {
				showFoldView();
				return;
			}
		} else {
			// 左滑限制
			if ((mScroller.getFinalX() + dx) > mScreenWidth) {
				return;
			}
			// if ((mScroller.getFinalX() + dx) > 0) {// 左滑减速
			// dx = Math.abs((int) ((dx - 0.5) / 2));
			// }
			// bringToFront();
		}
		// 设置mScroller的滚动偏移量
		mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx,
				dy, duration);
		invalidate();// 这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		switch (mFoldViewLocation) {
		case FOLW_VIEW_LOCATION_LEFT:
			mOutsideView.layout(left - mOutsideView.getMeasuredWidth(), 0,
					left, bottom - top);
			break;
		case FOLW_VIEW_LOCATION_RIGHT:
			mOutsideView.layout(right, 0,
					right + mOutsideView.getMeasuredWidth(), bottom - top);
			break;
		default:
			break;
		}
		// if ((paramBoolean) && (this.bool2 == null))
		// {
		// if (this.bool)
		// scrollTo(this.rightView.getMeasuredWidth(), 0);
		// }
		// else
		// return;
		// scrollTo(0, 0);
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		int scrollX = getScrollX();
		if ((child == this.mInsideView) && (scrollX != 0)
				&& (scrollX != this.mInsideView.getMeasuredWidth())) {
			// switch (mFoldViewLocation) {
			// case FOLW_VIEW_LOCATION_LEFT:
			// if (BuildConfig.DEBUG) {
			// Log.e("WidgetLib", "[ScaleLayout]-[drawChild]:" + scrollX);
			// }
			// canvas.translate(scrollX, 0.0f);
			// break;
			// case FOLW_VIEW_LOCATION_RIGHT:
			// canvas.translate(getMeasuredWidth(), 0.0f);// 画布平移
			// break;
			// default:
			// return super.drawChild(canvas, child, drawingTime);
			// }

			// float angle = (float) (Math.acos((Math.abs(scrollX) / (float)
			// child
			// .getMeasuredWidth())) * 180 / Math.PI);
			// System.out.println(scrollX + ":" + child.getMeasuredWidth() + ":"
			// + angle + ":" + getMeasuredWidth());
			// angle = 90.0f * (1.0f - Math.abs(scrollX
			// / (float) child.getMeasuredWidth()));
			float scale = 1.0f - (Math.abs(scrollX * 0.25f) / (float) mScreenWidth);
			Paint localPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			localPaint.setAlpha((int) (255 * scale * 4));

			mMapPoints[0] = (mScreenWidth / 2);
			mMapPoints[1] = (mScreenHeight / 2);
			camera.save();
			camera.getMatrix(matrix);
			matrix.mapPoints(mMapPoints);
			matrix.setScale(scale, scale, mMapPoints[0], mMapPoints[1]);
			canvas.translate(scrollX, 0.0f);
			canvas.drawBitmap(mInsideBitmap, matrix, localPaint);
			canvas.translate(-scrollX, 0.0f);
			camera.restore();
			camera.save();
			return true;
		}
		return super.drawChild(canvas, child, drawingTime);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		
		if(mInsideView.getScrollY() != mInsideScrollY) {
			mInsideScrollY = mInsideView.getScrollY();
//			if (((oldl == 0) || (oldl == mInsideView.getMeasuredWidth()))
//					&& ((l != 0) || (l != mInsideView.getMeasuredWidth()))) {
//				if (mInsideBitmap == null) {
					mInsideBitmap = Bitmap.createBitmap(mInsideView.getWidth(),
							mInsideView.getHeight(), Bitmap.Config.ARGB_8888);
					Canvas localCanvas = new Canvas(mInsideBitmap);
					mInsideView.draw(localCanvas);
//				}
//			}
		}
	}

	@Override
	public void computeScroll() {
		// 先判断mScroller滚动是否完成
		if (mScroller.computeScrollOffset()) {
			// 这里调用View的scrollTo()完成实际的滚动
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

			// 必须调用该方法，否则不一定能看到滚动效果
			postInvalidate();

		} else {
			if (mMenuState == FOLW_VIEW_STATE_OPENING) {
				mMenuState = FOLW_VIEW_STATE_OPENED;
			}
		}
	}

	// public boolean onInterceptTouchEvent(MotionEvent ev) {
	// int action = ev.getAction();
	// if (getScrollX() == mOutsideView.getMeasuredWidth()) {
	// return false;
	// }
	// switch (action) {
	// case MotionEvent.ACTION_DOWN:
	// break;
	// case MotionEvent.ACTION_MOVE:
	// default:
	// break;
	// }
	// return true;
	// // int i1 = ev.getAction();
	// // float f1 = ev.getX();
	// // float f2 = ev.getY();
	// // switch (i1)
	// // {
	// // default:
	// // case 2:
	// // case 0:
	// // case 1:
	// // case 3:
	// // }
	// // while (this.o == 1)
	// // {
	// // // return true;
	// // int i3 = (int) Math.abs(this.m - f1);
	// // int i4 = (int) Math.abs(this.n - f2);
	// // if (this.o == 0)
	// // if (i3 > i4)
	// // {
	// // if (i3 > this.p)
	// // {
	// // this.o = 1;
	// // this.r = false;
	// // }
	// // }
	// // else if (i4 > this.p)
	// // {
	// // this.o = 2;
	// // this.r = false;
	// // // continue;
	// // this.m = f1;
	// // this.n = f2;
	// // if ((isShown()) && (f1 < getMeasuredWidth() -
	// // this.rightView.getMeasuredWidth())
	// // && (f2 > this.view.getMeasuredHeight()))
	// // {
	// // this.r = true;
	// // return true;
	// // }
	// // if (this.scroller.isFinished())
	// // ;
	// // for (int i2 = 0;; i2 = 1)
	// // {
	// // this.o = i2;
	// // break;
	// // }
	// // this.o = 0;
	// // }
	// // }
	// }

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		int curX = (int) event.getX();
		int curY = (int) event.getY();

		if (mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();
		mVelocityTracker.addMovement(event);
		if (BuildConfig.DEBUG) {
			Log.e("WidgetLib", "[ScaleLayout]-[onInterceptTouchEvent]:" + curX
					+ ":" + curY + ":" + event.getAction());
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownPoint.set(curX, curY);
			mLastPoint.set(curX, curY);
			mGestureDirection = GESTURE_DIRECTION_INIT;
			return false;
		case MotionEvent.ACTION_MOVE:
			if (mGestureDirection == GESTURE_DIRECTION_INIT) {
				int xDis = curX - mDownPoint.x;
				int yDis = curY - mDownPoint.y;
				if (Math.abs(xDis) * 0.8 < Math.abs(yDis)) {// 上下
					if (yDis < 0)
						mGestureDirection = GESTURE_DIRECTION_UP;// 上
					else
						mGestureDirection = GESTURE_DIRECTION_DOWN;// 下
				} else {// 左右
					if (xDis < 0)
						mGestureDirection = GESTURE_DIRECTION_LEFT;// 左
					else
						mGestureDirection = GESTURE_DIRECTION_RIGHT;// 右
				}
			}
			if (BuildConfig.DEBUG) {
				Log.e("WidgetLib",
						"[ScaleLayout]-[onInterceptTouchEvent]11111:"
								+ FOLW_VIEW_LOCATION_LEFT + ":" + getScrollX()
								+ ":");
			}
			// 非左右滑动的不处理
			if (mGestureDirection <= 2)
				return false;
			float distanceX = curX - mLastPoint.x;

			mLastPoint.set(curX, curY);
			// 向左滑动大于0，向右滑动下于0
			if (BuildConfig.DEBUG) {
				Log.e("WidgetLib",
						"[ScaleLayout]-[onInterceptTouchEvent]11111:"
								+ FOLW_VIEW_LOCATION_LEFT + ":" + getScrollX()
								+ ":" + distanceX);
			}
			switch (mFoldViewLocation) {
			case FOLW_VIEW_LOCATION_LEFT:
				if ((getScrollX() - distanceX) > 0
						|| getScrollX() - distanceX < -mOutsideView
								.getMeasuredWidth())
					return false;
				break;
			case FOLW_VIEW_LOCATION_RIGHT:
				if ((getScrollX() - distanceX) < 0
						|| getScrollX() - distanceX > mOutsideView
								.getMeasuredWidth())
					return false;
				break;
			default:
				return false;
			}
			mCurDiatance = -distanceX;
			scrollBy((int) -distanceX, 0);
			return true;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (mGestureDirection <= 2)
				return false;

			mVelocityTracker.computeCurrentVelocity(1000);// 单位：像素/秒
			float xVelocity = mVelocityTracker.getXVelocity();
			xVelocity /= getContext().getResources().getDisplayMetrics().densityDpi;
			// System.out.println(mVelocityTracker.getXVelocity() +
			// "vmVelocityTracker");
			// System.out.println(xVelocity + "vmVelocityTracker");
			// System.out.println(xVelocity * getContext()
			// .getResources().getDisplayMetrics().widthPixels +
			// "vmVelocityTracker");

			// 向右为正，向左为负
			if (xVelocity > 2.0f) {
				switch (mFoldViewLocation) {
				case FOLW_VIEW_LOCATION_LEFT:
					showFoldView();
					break;
				case FOLW_VIEW_LOCATION_RIGHT:
					showContentView();
					break;
				default:
					return false;
				}
			} else if (xVelocity < -2.0f) {
				switch (mFoldViewLocation) {
				case FOLW_VIEW_LOCATION_LEFT:
					showContentView();
					break;
				case FOLW_VIEW_LOCATION_RIGHT:
					showFoldView();
					break;
				default:
					return false;
				}
			} else {
				// 判断是否已经完成闭合，以一半为界限
				if (Math.abs(getScrollX()) > mOutsideView.getMeasuredWidth() / 2)
					showFoldView();
				else
					showContentView();
			}
			return true;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int curX = (int) event.getX();
		int curY = (int) event.getY();

		if (BuildConfig.DEBUG) {
			Log.e("WidgetLib", "[ScaleLayout]-[onTouchEvent]:" + curX + ":"
					+ curY + ":" + event.getAction());
		}
		if (mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();
		mVelocityTracker.addMovement(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownPoint.set(curX, curY);
			mLastPoint.set(curX, curY);
			mGestureDirection = GESTURE_DIRECTION_INIT;
			return false;
		case MotionEvent.ACTION_MOVE:
			if (mGestureDirection == GESTURE_DIRECTION_INIT) {
				int xDis = curX - mDownPoint.x;
				int yDis = curY - mDownPoint.y;
				if (Math.abs(xDis) * 0.8 < Math.abs(yDis)) {// 上下
					if (yDis < 0)
						mGestureDirection = GESTURE_DIRECTION_UP;// 上
					else
						mGestureDirection = GESTURE_DIRECTION_DOWN;// 下
				} else {// 左右
					if (xDis < 0)
						mGestureDirection = GESTURE_DIRECTION_LEFT;// 左
					else
						mGestureDirection = GESTURE_DIRECTION_RIGHT;// 右
				}
			}
			if (BuildConfig.DEBUG) {
				Log.e("WidgetLib", "[ScaleLayout]-[onTouchEvent]11111:"
						+ FOLW_VIEW_LOCATION_LEFT + ":" + getScrollX() + ":");
			}
			// 非左右滑动的不处理
			if (mGestureDirection <= 2)
				return false;
			float distanceX = curX - mLastPoint.x;

			mLastPoint.set(curX, curY);
			// 向左滑动大于0，向右滑动下于0

			if (BuildConfig.DEBUG) {
				Log.e("WidgetLib", "[ScaleLayout]-[onTouchEvent]11111:"
						+ FOLW_VIEW_LOCATION_LEFT + ":" + getScrollX() + ":"
						+ distanceX);
			}
			switch (mFoldViewLocation) {
			case FOLW_VIEW_LOCATION_LEFT:
				if ((getScrollX() - distanceX) > 0
						|| getScrollX() - distanceX < -mOutsideView
								.getMeasuredWidth())
					return false;
				break;
			case FOLW_VIEW_LOCATION_RIGHT:
				if ((getScrollX() - distanceX) < 0
						|| getScrollX() - distanceX > mOutsideView
								.getMeasuredWidth())
					return false;
				break;
			default:
				return false;
			}
			mCurDiatance = -distanceX;
			if (BuildConfig.DEBUG) {
				Log.e("WidgetLib", "[ScaleLayout]-[onTouchEvent]scrollBy:"
						+ distanceX);
			}
			scrollBy((int) -distanceX, 0);
			return true;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (mGestureDirection <= 2)
				return false;

			mVelocityTracker.computeCurrentVelocity(1000);// 单位：像素/秒
			float xVelocity = mVelocityTracker.getXVelocity();
			xVelocity /= getContext().getResources().getDisplayMetrics().densityDpi;
			// System.out.println(mVelocityTracker.getXVelocity() +
			// "vmVelocityTracker");
			// System.out.println(xVelocity + "vmVelocityTracker");
			// System.out.println(xVelocity * getContext()
			// .getResources().getDisplayMetrics().widthPixels +
			// "vmVelocityTracker");

			// 向右为正，向左为负
			if (xVelocity > 2.0f) {
				switch (mFoldViewLocation) {
				case FOLW_VIEW_LOCATION_LEFT:
					showFoldView();
					break;
				case FOLW_VIEW_LOCATION_RIGHT:
					showContentView();
					break;
				default:
					return false;
				}
			} else if (xVelocity < -2.0f) {
				switch (mFoldViewLocation) {
				case FOLW_VIEW_LOCATION_LEFT:
					showContentView();
					break;
				case FOLW_VIEW_LOCATION_RIGHT:
					showFoldView();
					break;
				default:
					return false;
				}
			} else {
				// 判断是否已经完成闭合，以一半为界限
				if (Math.abs(getScrollX()) > mOutsideView.getMeasuredWidth() / 2)
					showFoldView();
				else
					showContentView();
			}
			return true;
		}

		// if (!mGestureDetector.onTouchEvent(event)) {
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_UP:
		// switch (mMenuState) {
		// case MENU_STATE_CLOSED:
		// break;
		// case MENU_STATE_CLOSING:
		// break;
		// case MENU_STATE_OPENED:
		// break;
		// case MENU_STATE_OPENING:
		// return true;
		// default:
		// break;
		// }
		// int offset = mScroller.getCurrX();
		// if (-offset > mMenuWidth * 2 / 3
		// && -offset < mMenuWidth) {
		// showMenu();
		// } else {
		// // closeMenu();
		// }
		// break;
		// }
		// }
		return true;
	}

	private void showFoldView() {
		if (!mScroller.isFinished())
			mScroller.abortAnimation();

		int duration = Math.abs(mOutsideView.getMeasuredWidth()
				- Math.abs(getScrollX()));

		switch (mFoldViewLocation) {
		case FOLW_VIEW_LOCATION_LEFT:
			mScroller.startScroll(getScrollX(), 0,
					-mOutsideView.getMeasuredWidth() - getScrollX(), 0,
					duration);
			break;
		case FOLW_VIEW_LOCATION_RIGHT:
			mScroller
					.startScroll(getScrollX(), 0,
							mOutsideView.getMeasuredWidth() - getScrollX(), 0,
							duration);
			break;
		}
		postInvalidate();
		mMenuState = FOLW_VIEW_STATE_OPENING;
	}

	private void showContentView() {
		if (!mScroller.isFinished())
			mScroller.abortAnimation();
		int duration = Math.abs(getScrollX());
		mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, duration);
		postInvalidate();
		mMenuState = FOLW_VIEW_STATE_CLOSED;
	}

	public void toggle() {
		switch (mMenuState) {
		case FOLW_VIEW_STATE_CLOSED:
		case FOLW_VIEW_STATE_CLOSING:
			showFoldView();
			break;
		case FOLW_VIEW_STATE_OPENED:
		case FOLW_VIEW_STATE_OPENING:
			showContentView();
			break;
		default:
			break;
		}
	}

	class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (e1.getX() > e2.getX()) {// 左
				mGestureDirection = GESTURE_DIRECTION_LEFT;
			} else {
				mGestureDirection = GESTURE_DIRECTION_RIGHT;
			}

			// // TODO 菜单隐藏状态下右拉不处理
			if (mMenuState == FOLW_VIEW_STATE_CLOSED
					&& mGestureDirection == GESTURE_DIRECTION_RIGHT) {
				return false;
			}
			int dis = (int) distanceX;
			dis = dis % 2 == 1 ? dis - 1 : dis;
			scrollBy(dis, 0);
			// smoothScrollBy(dis, 0);
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e2.getX() - e1.getX() > 100 && Math.abs(velocityX) > 200) {
				showFoldView();
			} else if (e2.getX() - e1.getX() < -100
					&& Math.abs(velocityX) > 200) {
				showContentView();
			} else {
				return false;
			}
			return true;
		}

	}
}