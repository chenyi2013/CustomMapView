package com.example.custommapview;

import java.util.ArrayList;

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
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class CustomView extends SurfaceView implements Callback {

	private static final Object touchLock = new Object();

	private SurfaceHolder mHolder;
	private DrawThread mDrawThread;
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
	
	private int iconBgWidth = convertDpToPx(200);
	private int iconBgHeight = convertDpToPx(250);
	
	Matrix matrix = new Matrix();
	float[] m = new float[9];
	
	Bitmap iconBg = null;
	Bitmap iconBgNew = null;
	Bitmap icon = null;
	Bitmap iconNew = null;
	int location = -1;

	private GestureDetectorCompat mDetector;
	private ScaleGestureDetector scaleGestureDetector;

	private ArrayList<GraphData> datas;

	private OnClickGraphListener onClickGraphListener;

	@SuppressLint("NewApi")
	private Property<CustomView, Float> mSacleFactorProperty = new Property<CustomView, Float>(
			Float.class, "scaleFactor") {

		@Override
		public Float get(CustomView object) {
			synchronized (touchLock) {
				return object.scaleFactor;
			}
		}

		@Override
		public void set(CustomView object, Float value) {
			synchronized (touchLock) {
				object.scaleFactor = value;
				scale();
			}
		}
	};

	private void scale() {
		moveX = moveX
				- ((scaleFactor - previousScaleFactor) * mMapBitmap.getWidth())
				/ 2;
		moveY = moveY
				- ((scaleFactor - previousScaleFactor) * mMapBitmap.getHeight())
				/ 2;
		refreshMap();
	}

	public void scaleUp() {
		if (scaleFactor < 2 && scaleFactor + 0.5f <= 2) {
			startAnimator(scaleFactor, scaleFactor + 0.5f);
		} else if (scaleFactor < 2 && scaleFactor + 0.5f > 2) {
			startAnimator(scaleFactor, 2);
		}
	}

	public void scaleDown() {
		if (scaleFactor > 0.5f && scaleFactor - 0.5f >= 0.5) {
			startAnimator(scaleFactor, scaleFactor - 0.5f);
		} else if (scaleFactor > 0.5f && scaleFactor - 0.5f < 0.5) {
			startAnimator(scaleFactor, 0.5f);
		}

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
		refreshMap();
	}

	public void setShowLocation(int location) {
		showLocation = location;
		refreshMap();
	}

	public Bitmap getMapBitmap() {
		return mMapBitmap;
	}

	public void setMapBitmap(Bitmap mMapBitmap) {
		this.mMapBitmap = mMapBitmap;
		refreshMap();
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
		
		iconBg = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_bg);
		iconBgNew = Bitmap.createScaledBitmap(iconBg, iconBgWidth,
				iconBgHeight, true);

	}

	private void initPaint() {
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setDither(true);
		mCirclePaint.setStyle(Style.STROKE);
		mCirclePaint.setStrokeWidth(2);
		mCirclePaint.setColor(0xff000000);
	}

	private void refreshMap() {
		if (mDrawThread != null) {
			mDrawThread.setDirtyFlag(true);
		}
	}
	
	
	private int convertDpToPx(float dp) {

		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());

	}

	private float convertSpToPx(float sp) {

		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
				getResources().getDisplayMetrics());

	}
	
	/**
	 * 得到字体高度
	 * 
	 * @param fontSize
	 * @return
	 */
	private int getFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		Rect rect = new Rect();
		paint.getTextBounds("0000", 0, "0000".length(), rect);
		return rect.height();

	}

	private void drawMap() {
		mCanvas = mHolder.lockCanvas();
		mCanvas.drawColor(Color.WHITE);
		GraphData data = null;

		if (mMapBitmap == null) {
			return;
		}

		if (isFirst) {

			moveX = (getWidth() - scaleFactor * mMapBitmap.getWidth()) / 2;
			moveY = (getHeight() - scaleFactor * mMapBitmap.getHeight()) / 2;

			isFirst = false;
		}
		
		matrix.getValues(m);
		m[Matrix.MTRANS_X] = moveX;
		m[Matrix.MTRANS_Y] = moveY;
		m[Matrix.MSCALE_X] = scaleFactor;
		m[Matrix.MSCALE_Y] = scaleFactor;
		matrix.setValues(m);
		
		mCanvas.drawBitmap(mMapBitmap, matrix, mCirclePaint);

		if (datas == null) {
			return;
		}

		for (int i = 0; i < datas.size(); i++) {
			data = datas.get(i);
			mCanvas.drawCircle(moveX + scaleFactor * data.getX(), moveY
					+ scaleFactor * data.getY(), 5, mCirclePaint);
		}

		data = datas.get(showLocation);

		mCanvas.drawLine(moveX + scaleFactor * data.getX(), moveY + scaleFactor
				* data.getY(), moveX + scaleFactor * data.getX(), moveY
				+ scaleFactor * data.getY() - lineHeight, mCirclePaint);

		mCanvas.drawBitmap(iconBgNew, moveX + scaleFactor * data.getX()
				- iconBgNew.getWidth() / 2, moveY + scaleFactor * data.getY()
				- iconBgNew.getHeight() - lineHeight, mCirclePaint);

		if (showLocation != location) {

			if (icon != null) {
				icon.recycle();
			}

			icon = BitmapFactory.decodeResource(getResources(), //
					R.drawable.aa);

			if (iconNew != null && !icon.equals(iconNew)) {
				iconNew.recycle();
			}
			iconNew = Bitmap.createScaledBitmap(icon, convertDpToPx(140),
					convertDpToPx(120), true);

		}

		mCanvas.drawBitmap(iconNew, //
				moveX + scaleFactor * data.getX() - iconNew.getWidth() / 2, //
				moveY + scaleFactor * data.getY() - iconBgNew.getHeight()
						+ convertDpToPx(30) //
						- lineHeight, mCirclePaint); //

		Paint paint = new Paint();
		paint.setTextSize(convertSpToPx(24));
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Align.CENTER);
		mCanvas.drawText("F2-102", moveX + scaleFactor * data.getX(),
				moveY + scaleFactor * data.getY() - iconBgNew.getHeight()
						+ iconNew.getHeight()
						+ getFontHeight(convertSpToPx(24)) + convertDpToPx(30)
						+ convertDpToPx(20), paint);
		location = showLocation;
		previousScaleFactor = scaleFactor;
		mHolder.unlockCanvasAndPost(mCanvas);
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
			iconBg.recycle();
			iconBgNew.recycle();
			icon.recycle();
			iconNew.recycle();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		if (mDrawThread == null) {
			mDrawThread = new DrawThread(mHolder);
			mDrawThread.start();
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		if (mDrawThread != null) {
			mDrawThread.setRunFlag(false);
			mDrawThread = null;
		}

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

			synchronized (touchLock) {
				currentX = e.getX();
				currentY = e.getY();

				if (datas != null) {

					data = datas.get(showLocation);
					Bitmap bitmap = BitmapFactory.decodeResource(
							getResources(), R.drawable.ic_launcher);
					if (moveX + scaleFactor * data.getX() - bitmap.getWidth()
							/ 2 <= currentX
							&& moveX + scaleFactor * data.getX()
									+ bitmap.getWidth() / 2 >= currentX
							&& moveY + scaleFactor * data.getY()
									- bitmap.getHeight() - lineHeight <= currentY
							&& moveY + scaleFactor * data.getY() - lineHeight >= currentY) {

						isChoice = true;

					}
					bitmap.recycle();

				}
			}

			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {

			synchronized (touchLock) {
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
				refreshMap();

			}
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			synchronized (touchLock) {
				dx = e.getX() - currentX;
				dy = e.getY() - currentY;

				if (isChoice && Math.sqrt(dx) < 5 && Math.sqrt(dy) < 5) {
					if (onClickGraphListener != null) {
						onClickGraphListener.onClick(showLocation);
					}
				}

				isChoice = false;
				refreshMap();
			}

			return true;
		}

	}

	class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {

			synchronized (touchLock) {

				scaleFactor *= detector.getScaleFactor();
				scaleFactor = (float) Math.max(0.5, Math.min(scaleFactor, 2f));
				scale();
			}
			return true;
		}
	}

	class DrawThread extends Thread {
		private boolean isRun = true;
		private boolean isDirty = true;

		public DrawThread(SurfaceHolder holder) {
			mHolder = holder;
		}

		public void setRunFlag(boolean bool) {
			isRun = bool;
		}

		public void setDirtyFlag(boolean bool) {
			isDirty = bool;
		}

		@Override
		public void run() {
			super.run();

			while (isRun) {
				if (!isDirty) {
					continue;
				}

				synchronized (mHolder) {

					synchronized (touchLock) {

						drawMap();
						isDirty = false;

					}

				}
			}
		}
	}

}
