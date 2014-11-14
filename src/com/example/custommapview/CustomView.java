package com.example.custommapview;

import java.util.ArrayList;

import com.example.custommapview.CustomMapView.MyGestureListener;
import com.example.custommapview.CustomMapView.ScaleListener;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class CustomView extends SurfaceView implements Callback {

	private static final int DRAW_MAP = 1;
	private static final int SCALE_MAP = 2;

	private SurfaceHolder mHolder;
	private HandlerThread mDrawThread;

	private volatile Looper mLooper;
	private volatile CustomHandler mHandler;

	private Canvas mCanvas = null;
	private Paint mCirclePaint;

	/**
	 * 地图在X轴方向的起点座标
	 */
	private float moveX = 0;
	/**
	 * 地图在Y轴方向的起点座标
	 */
	private float moveY = 0;

	/**
	 * 地图的当前缩放因子
	 */
	private float scaleFactor = 1f;

	/**
	 * 地图的前一次缩放因子
	 */
	private float previousScaleFactor = 1f;

	/**
	 * 当前在地图上标出的标签
	 */
	private int showLocation = 0;
	private AnimatorSet mAnimatorSet;

	/**
	 * 当前在地图上标出的标签底部竖线的高度
	 */
	private float lineHeight = 20;

	/**
	 * 用于判断是否是初次设置地图图片
	 */
	private boolean isFirst = true;

	/**
	 * 设置的地图图片
	 */
	private Bitmap mMapBitmap;

	/**
	 * 手指在屏幕上按下的点在X轴方向的座标
	 */
	private float currentX = 0;
	/**
	 * 手指在屏幕上按下的点在Y轴方向的座标
	 */
	private float currentY = 0;

	private GestureDetectorCompat mDetector;
	private ScaleGestureDetector scaleGestureDetector;

	private ArrayList<GraphData> datas;

	private OnClickGraphListener onClickGraphListener;

	@SuppressLint("NewApi")
	private Property<CustomView, Float> mSacleFactorProperty = new Property<CustomView, Float>(
			Float.class, "scaleFactor") {

		@Override
		public Float get(CustomView object) {
			return object.scaleFactor;
		}

		@Override
		public void set(CustomView object, Float value) {
			object.scaleFactor = value;
			scale();
			invalidate();
		};
	};

	private void scale() {
		moveX = moveX
				- ((scaleFactor - previousScaleFactor) * mMapBitmap.getWidth())
				/ 2;
		moveY = moveY
				- ((scaleFactor - previousScaleFactor) * mMapBitmap.getHeight())
				/ 2;
	}

	@SuppressLint("NewApi")
	private void startAnimator(float start, float end) {

		if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
			return;
		}
		mAnimatorSet = new AnimatorSet();
		mAnimatorSet.play(ObjectAnimator.ofFloat(this, mSacleFactorProperty,
				start, end));
		mAnimatorSet.setDuration(500);
		mAnimatorSet.start();

	}

	public OnClickGraphListener getOnClickGraphListener() {
		return onClickGraphListener;
	}

	public void setOnClickGraphListener(
			OnClickGraphListener onClickGraphListener) {
		this.onClickGraphListener = onClickGraphListener;
	}

	public interface OnClickGraphListener {
		public void onClick(int position);
	}

	public void bindData(ArrayList<GraphData> datas) {

		this.datas = datas;
		invalidate();
	}

	public void setShowLocation(int location) {
		showLocation = location;
		invalidate();
	}

	private final class CustomHandler extends Handler {
		public CustomHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case DRAW_MAP:
				drawMap();
				break;
			case SCALE_MAP:
				scaleFactor *= (float) msg.obj;
				scaleFactor = (float) Math.max(0.5, Math.min(scaleFactor, 2f));
				scale();
				drawMap();
				break;
			default:
				break;
			}
		}
	}

	public Bitmap getMapBitmap() {
		return mMapBitmap;
	}

	public void setMapBitmap(Bitmap mMapBitmap) {
		this.mMapBitmap = mMapBitmap;
		refreshView(DRAW_MAP);
	}

	public CustomView(Context context) {
		super(context);
		init(context);
	}

	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CustomView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {

		mHolder = getHolder();
		mHolder.addCallback(this);
		initPaint();
		mDetector = new GestureDetectorCompat(getContext(),
				new MyGestureListener());
		scaleGestureDetector = new ScaleGestureDetector(getContext(),
				new ScaleListener());

	}

	private void initPaint() {
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setDither(true);
		mCirclePaint.setStyle(Style.STROKE);
		mCirclePaint.setStrokeWidth(2);
		mCirclePaint.setColor(0xff000000);
	}

	private void drawMap() {
		mCanvas = mHolder.lockCanvas();
		mCanvas.drawColor(Color.BLACK);
		GraphData data = null;

		if (mMapBitmap == null) {
			return;
		}

		if (isFirst) {

			moveX = (getWidth() - scaleFactor * mMapBitmap.getWidth()) / 2;
			moveY = (getHeight() - scaleFactor * mMapBitmap.getHeight()) / 2;

			isFirst = false;
		}

		Matrix matrix = new Matrix();
		matrix.postScale(scaleFactor, scaleFactor);
		Bitmap resizeBmp = Bitmap.createBitmap(mMapBitmap, 0, 0,
				mMapBitmap.getWidth(), mMapBitmap.getHeight(), matrix, true);
		mCanvas.drawBitmap(resizeBmp, moveX, moveY, mCirclePaint);

		if (!mMapBitmap.equals(resizeBmp)) {
			resizeBmp.recycle();
		}

		if (datas == null) {
			return;
		}

		for (int i = 0; i < datas.size(); i++) {
			data = datas.get(i);
			mCanvas.drawCircle(moveX + scaleFactor * data.getX(), moveY
					+ scaleFactor * data.getY(), 5, mCirclePaint);
		}

		data = datas.get(showLocation);
		Bitmap bitmap = null;
		mCanvas.drawLine(moveX + scaleFactor * data.getX(), moveY + scaleFactor
				* data.getY(), moveX + scaleFactor * data.getX(), moveY
				+ scaleFactor * data.getY() - lineHeight, mCirclePaint);
		bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);

		mCanvas.drawBitmap(bitmap,
				moveX + scaleFactor * data.getX() - bitmap.getWidth() / 2,
				moveY + scaleFactor * data.getY() - bitmap.getHeight()
						- lineHeight, mCirclePaint);
		bitmap.recycle();

		previousScaleFactor = scaleFactor;
		mHolder.unlockCanvasAndPost(mCanvas);
	}

	public void refreshView(int what) {
		if (mHandler != null) {
			mHandler.sendEmptyMessage(what);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean retVal = scaleGestureDetector.onTouchEvent(event);
		retVal = mDetector.onTouchEvent(event) || retVal;
		return retVal || super.onTouchEvent(event);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mMapBitmap != null) {
			mMapBitmap.recycle();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		if (mDrawThread == null) {
			mDrawThread = new HandlerThread("CustomViewThread");
			mDrawThread.start();
			mLooper = mDrawThread.getLooper();
			mHandler = new CustomHandler(mLooper);
		}

		refreshView(DRAW_MAP);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		private float x;
		private float y;

		private float dx;
		private float dy;

		private float width;
		private float height;

		private GraphData data = null;
		private boolean isChoice = false;

		@Override
		public boolean onDown(MotionEvent e) {

			currentX = e.getX();
			currentY = e.getY();

			if (datas != null) {

				data = datas.get(showLocation);
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.ic_launcher);
				if (moveX + scaleFactor * data.getX() - bitmap.getWidth() / 2 <= currentX
						&& moveX + scaleFactor * data.getX()
								+ bitmap.getWidth() / 2 >= currentX
						&& moveY + scaleFactor * data.getY()
								- bitmap.getHeight() - lineHeight <= currentY
						&& moveY + scaleFactor * data.getY() - lineHeight >= currentY) {

					isChoice = true;

				}
				bitmap.recycle();

			}
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {

			width = scaleFactor * mMapBitmap.getWidth();
			height = scaleFactor * mMapBitmap.getHeight();

			x = moveX - distanceX;
			y = moveY - distanceY;

			if (width <= getWidth()) {

				if (x >= 0 && x <= getWidth() - width) {
					moveX = x;
				} else if (x < 0) {
					moveX = 0;
				} else if (x > getWidth() - width) {
					moveX = getWidth() - width;
				}

			} else {
				if (distanceX < 0) {

					if (x <= 0) {
						moveX = x;
					} else {
						moveX = 0;
					}

				} else if (distanceX > 0) {

					if (x >= getWidth() - width) {
						moveX = x;
					} else {
						moveX = getWidth() - width;
					}

				}

			}

			if (height <= getHeight()) {

				if (y >= 0 && y <= getHeight() - height) {
					moveY = y;
				} else if (y < 0) {
					moveY = 0;
				} else if (y > getHeight() - height) {
					moveY = getHeight() - height;
				}

			} else {
				if (distanceY < 0) {

					if (y <= 0) {
						moveY = y;
					} else {
						moveY = 0;
					}

				} else if (distanceY > 0) {

					if (y >= getHeight() - height) {
						moveY = y;
					} else {
						moveY = getHeight() - height;
					}

				}

			}

			mHandler.removeMessages(DRAW_MAP);
			refreshView(DRAW_MAP);
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			dx = e.getX() - currentX;
			dy = e.getY() - currentY;

			if (isChoice && Math.sqrt(dx) < 5 && Math.sqrt(dy) < 5) {
				if (onClickGraphListener != null) {
					onClickGraphListener.onClick(showLocation);
				}
			}

			isChoice = false;
			return true;
		}

	}

	class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {

			mHandler.removeMessages(SCALE_MAP);
			Message msg = mHandler.obtainMessage();
			msg.obj = detector.getScaleFactor();
			msg.what = SCALE_MAP;
			msg.sendToTarget();
			// refreshView(SCALE_MAP);

			return true;
		}
	}

}
