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

	Matrix matrix = new Matrix();
	float[] m = new float[9];

	private int iconBgWidth = convertDpToPx(60);
	private int iconBgHeight = convertDpToPx(80);

	Bitmap iconBg = null;
	Bitmap iconBgNew = null;
	Bitmap icon = null;
	Bitmap iconNew = null;
	Bitmap wc = null;
	Bitmap stair = null;
	Bitmap elevator = null;
	Bitmap lift = null;

	int location = -1;
	int showType = -1;

	private GestureDetectorCompat mDetector;
	private ScaleGestureDetector scaleGestureDetector;

	private ArrayList<ShopsData> datas;
	private ArrayList<PublicFacilityData> publicFacilities;

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

	public void setShowType(int type) {
		showType = type;
		invalidate();
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

	public void bindData(ArrayList<ShopsData> datas) {

		this.datas = datas;
		invalidate();
	}

	public void setPublicFacility(ArrayList<PublicFacilityData> publicFacilities) {

		this.publicFacilities = publicFacilities;

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

		wc = BitmapFactory.decodeResource(getResources(), R.drawable.wc);
		elevator = BitmapFactory.decodeResource(getResources(),
				R.drawable.elevator);
		lift = BitmapFactory.decodeResource(getResources(), R.drawable.lift);
		stair = BitmapFactory.decodeResource(getResources(), R.drawable.stair);

	}

	public void scaleUp() {
		if (scaleFactor < 2 && scaleFactor + 0.5f <= 2) {
			startAnimator(scaleFactor, scaleFactor + 0.5f);
		} else if (scaleFactor < 2 && scaleFactor + 0.5f > 2) {
			startAnimator(scaleFactor, 2);
		}
	}

	public void scaleDown() {
		if (scaleFactor > 1f && scaleFactor - 0.5f >= 1f) {
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

		ShopsData data = null;

		if (mMapBitmap == null) {
			return;
		}

		if (isFirst) {

			// float scaleHeight = getHeight() / ((float)
			// mMapBitmap.getHeight());
			// float scaleWidth = getWidth() / ((float) mMapBitmap.getWidth());
			// scaleFactor = scaleHeight > scaleWidth ? scaleWidth :
			// scaleHeight;

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

		canvas.drawBitmap(mMapBitmap, matrix, mCirclePaint);

		if (datas == null) {
			return;
		}

		PublicFacilityData facilityData;
		if (publicFacilities != null) {
			for (int i = 0; i < publicFacilities.size(); i++) {
				facilityData = publicFacilities.get(i);
				// canvas.drawCircle(moveX + scaleFactor * data.getX(), moveY
				// + scaleFactor * data.getY(), 5, mCirclePaint);

				switch (facilityData.getType()) {
				case PublicFacilityData.ELEVATOR:
					if (showType == PublicFacilityData.ELEVATOR) {
						canvas.drawBitmap(elevator,
								moveX + scaleFactor * facilityData.getX()
										- elevator.getWidth() / 2, moveY
										+ scaleFactor * facilityData.getY()
										- elevator.getHeight() / 2,
								mCirclePaint);
					}

					break;
				case PublicFacilityData.ESCALATOR:
					if (showType == PublicFacilityData.ESCALATOR) {
						canvas.drawBitmap(lift, moveX + scaleFactor
								* facilityData.getX() - lift.getWidth() / 2,
								moveY + scaleFactor * facilityData.getY()
										- lift.getHeight() / 2, mCirclePaint);
					}

					break;
				case PublicFacilityData.STAIRWAY:
					if (showType == PublicFacilityData.STAIRWAY) {
						canvas.drawBitmap(stair, moveX + scaleFactor
								* facilityData.getX() - stair.getWidth() / 2,
								moveY + scaleFactor * facilityData.getY()
										- stair.getHeight() / 2, mCirclePaint);
					}

					break;
				case PublicFacilityData.TOILET:
					if (showType == PublicFacilityData.TOILET) {
						canvas.drawBitmap(
								wc,
								moveX + scaleFactor * facilityData.getX()
										- wc.getWidth() / 2,
								moveY + scaleFactor * facilityData.getY()
										- wc.getHeight() / 2, mCirclePaint);
					}

					break;
				}
			}
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
					R.drawable.icon);

			if (iconNew != null && !icon.equals(iconNew)) {
				iconNew.recycle();
			}
			iconNew = Bitmap.createScaledBitmap(icon, convertDpToPx(50),
					convertDpToPx(40), true);

		}

		canvas.drawBitmap(iconNew, //
				moveX + scaleFactor * data.getX() - iconNew.getWidth() / 2, //
				moveY + scaleFactor * data.getY() - iconBgNew.getHeight()
						+ convertDpToPx(5) //
						- lineHeight, mCirclePaint); //

		Paint paint = new Paint();
		paint.setTextSize(convertSpToPx(12));
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Align.CENTER);
		canvas.drawText("F2-10" + showLocation,
				moveX + scaleFactor * data.getX(),
				moveY + scaleFactor * data.getY() - iconBgNew.getHeight()
						+ iconNew.getHeight()
						+ getFontHeight(convertSpToPx(12)) + convertDpToPx(10),
				paint);
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
			iconBg.recycle();
			iconBgNew.recycle();
			icon.recycle();
			iconNew.recycle();
			elevator.recycle();
			lift.recycle();
			stair.recycle();
			wc.recycle();
		}
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		private float x;
		private float y;

		private float dx;
		private float dy;

		private float width;
		private float height;

		private ShopsData data = null;
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

			float ih = -1;

			if (data != null) {
				ih = data.getY() * scaleFactor;
			} else {
				ih = height / 2;
			}

			if (width < getWidth()) {

				if (x >= -iconBgNew.getWidth() / 2
						&& x <= getWidth() - width + iconBgNew.getWidth() / 2) {

					moveX = x;
				} else if (x < -iconBgNew.getWidth() / 2) {
					moveX = -iconBgNew.getWidth() / 2;
				} else if (x > getWidth() - width + iconBgNew.getWidth() / 2) {
					moveX = getWidth() - width + iconBgNew.getWidth() / 2;
				}

			} else {
				if (distanceX < 0) {
					// #FIXED
					if (x <= iconBgNew.getWidth() / 2) {

						moveX = x;

					} else {
						moveX = iconBgNew.getWidth() / 2;
					}

				} else if (distanceX > 0) {

					if (x >= getWidth() - width - iconBgNew.getWidth() / 2) {
						moveX = x;
					} else {
						moveX = getWidth() - width - iconBgNew.getWidth() / 2;
					}

				}

			}

			if (height <= getHeight()) {

				if (y >= -iconBgNew.getHeight() && y <= getHeight() - height) {

					moveY = y;
				} else if (y < -iconBgNew.getHeight()) {
					moveY = -iconBgNew.getHeight();
				} else if (x > getHeight() - height) {
					moveY = getHeight() - height;
				}

			} else {
				if (distanceY < 0) {

					if (ih - iconBgNew.getHeight() < 0) {

						if (y <= iconBgNew.getHeight() - ih) {
							moveY = y;
						} else {
							moveY = iconBgNew.getHeight() - ih;
						}
					} else {
						if (y <= 0) {
							moveY = y;
						} else {
							moveY = 0;
						}

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
