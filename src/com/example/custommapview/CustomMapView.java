package com.example.custommapview;

import java.util.ArrayList;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * 
 * @author Kevin
 * 
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CustomMapView extends View {

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
	private float previousScaleFactor = -1f;

	/**
	 * 当前在地图上标出的标签
	 */
	private int showLocation = 0;

	private AnimatorSet mAnimatorSet;

	/**
	 * 当前在地图上标出的标签底部竖线的高度
	 */
	private float lineHeight = 0;

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

	Bitmap resizeBmp = null;
	Bitmap iconBg = null;
	Bitmap iconBgNew = null;
	Bitmap icon = null;
	Bitmap iconNew = null;
	int location = -1;

	private GestureDetectorCompat mDetector;
	private ScaleGestureDetector scaleGestureDetector;

	private ArrayList<GraphData> datas;

	private OnClickGraphListener onClickGraphListener;

	private Property<CustomMapView, Float> mSacleFactorProperty = new Property<CustomMapView, Float>(
			Float.class, "scaleFactor") {

		@Override
		public Float get(CustomMapView object) {
			return object.scaleFactor;
		}

		@Override
		public void set(CustomMapView object, Float value) {
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

	public Bitmap getMapBitmap() {
		return mMapBitmap;
	}

	public void setMapBitmap(Bitmap mMapBitmap) {
		this.mMapBitmap = mMapBitmap;
		invalidate();
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

	public CustomMapView(Context context) {
		super(context);
		init(context);

	}

	public CustomMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	public CustomMapView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);

	}

	private void initPaint() {
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setDither(true);
		mCirclePaint.setStyle(Style.STROKE);
		mCirclePaint.setStrokeWidth(2);
		mCirclePaint.setColor(0xff000000);
	}

	private void init(Context context) {
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

	private int convertDpToPx(float dp) {

		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());

	}

	private float convertSpToPx(float sp) {

		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
				getResources().getDisplayMetrics());

	}

	@SuppressLint({ "DrawAllocation", "NewApi" })
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		GraphData data = null;

		if (mMapBitmap == null) {
			return;
		}

		if (isFirst) {

			moveX = (getWidth() - scaleFactor * mMapBitmap.getWidth()) / 2;
			moveY = (getHeight() - scaleFactor * mMapBitmap.getHeight()) / 2;

			isFirst = false;
		}

		if (scaleFactor != previousScaleFactor) {

			if (resizeBmp != null && !mMapBitmap.equals(resizeBmp)) {
				resizeBmp.recycle();
			}

			Matrix matrix = new Matrix();
			matrix.postScale(scaleFactor, scaleFactor);
			resizeBmp = Bitmap
					.createBitmap(mMapBitmap, 0, 0, mMapBitmap.getWidth(),
							mMapBitmap.getHeight(), matrix, true);
		}

		canvas.drawBitmap(resizeBmp, moveX, moveY, mCirclePaint);

		if (datas == null) {
			return;
		}

		for (int i = 0; i < datas.size(); i++) {
			data = datas.get(i);
			canvas.drawCircle(moveX + scaleFactor * data.getX(), moveY
					+ scaleFactor * data.getY(), 5, mCirclePaint);
		}

		data = datas.get(showLocation);

		canvas.drawLine(moveX + scaleFactor * data.getX(), moveY + scaleFactor
				* data.getY(), moveX + scaleFactor * data.getX(), moveY
				+ scaleFactor * data.getY() - lineHeight, mCirclePaint);

		canvas.drawBitmap(iconBgNew, moveX + scaleFactor * data.getX()
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

		canvas.drawBitmap(iconNew, //
				moveX + scaleFactor * data.getX() - iconNew.getWidth() / 2, //
				moveY + scaleFactor * data.getY() - iconBgNew.getHeight()
						+ convertDpToPx(30) //
						- lineHeight, mCirclePaint); //

		Paint paint = new Paint();
		paint.setTextSize(convertSpToPx(24));
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Align.CENTER);
		canvas.drawText("F2-102", moveX + scaleFactor * data.getX(),
				moveY + scaleFactor * data.getY() - iconBgNew.getHeight()
						+ iconNew.getHeight()
						+ getFontHeight(convertSpToPx(24)) + convertDpToPx(30)
						+ convertDpToPx(20), paint);
		location = showLocation;
		previousScaleFactor = scaleFactor;
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
			resizeBmp.recycle();
			iconBg.recycle();
			iconBgNew.recycle();
			icon.recycle();
			iconNew.recycle();
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

			currentX = e.getX();
			currentY = e.getY();

			if (datas != null) {

				data = datas.get(showLocation);
				if (moveX + scaleFactor * data.getX() - iconBgNew.getWidth()
						/ 2 <= currentX
						&& moveX + scaleFactor * data.getX()
								+ iconBgNew.getWidth() / 2 >= currentX
						&& moveY + scaleFactor * data.getY()
								- iconBgNew.getHeight() - lineHeight <= currentY
						&& moveY + scaleFactor * data.getY() - lineHeight >= currentY) {

					isChoice = true;

				}
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

			invalidate();
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			dx = e.getX() - currentX;
			dy = e.getY() - currentY;

			if (isChoice && Math.sqrt(dx) < convertDpToPx(5)
					&& Math.sqrt(dy) < convertDpToPx(5)) {
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
			scaleFactor *= detector.getScaleFactor();
			scaleFactor = (float) Math.max(0.5, Math.min(scaleFactor, 2f));
			scale();
			invalidate();
			return true;
		}
	}

}
