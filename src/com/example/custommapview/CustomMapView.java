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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author Kevin
 * 
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CustomMapView extends ViewGroup {

	private Paint mCirclePaint;

	/**
	 * ��ͼ��X�᷽����������
	 */
	private float moveX = 0;
	/**
	 * ��ͼ��Y�᷽����������
	 */
	private float moveY = 0;

	/**
	 * ��ͼ�ĵ�ǰ��������
	 */
	private float scaleFactor = 1f;

	/**
	 * ��ͼ��ǰһ����������
	 */
	private float previousScaleFactor = -1f;

	private float initScaleFactor = 1f;

	/**
	 * ��ǰ�ڵ�ͼ�ϱ���ı�ǩ
	 */
	private int showLocation = 0;

	private AnimatorSet mAnimatorSet;

	/**
	 * �����ж��Ƿ��ǳ������õ�ͼͼƬ
	 */
	private boolean isFirst = true;

	/**
	 * ���õĵ�ͼͼƬ
	 */
	private Bitmap mMapBitmap;
	Matrix matrix = new Matrix();
	float[] m = new float[9];

	private int iconBgWidth = 0;
	private int iconBgHeight = 0;

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
			requestLayout();
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
		requestLayout();
	}

	public Bitmap getMapBitmap() {
		return mMapBitmap;
	}

	public void setMapBitmap(Bitmap mMapBitmap) {
		this.mMapBitmap = mMapBitmap;
		invalidate();
		requestLayout();
	}

	public interface OnClickGraphListener {
		public void onClick(int position);
	}

	public void bindData(ArrayList<ShopsData> datas) {

		this.datas = datas;
		invalidate();
		requestLayout();
	}

	public void setPublicFacility(ArrayList<PublicFacilityData> publicFacilities) {

		this.publicFacilities = publicFacilities;

	}

	public void setShowLocation(int location) {
		showLocation = location;
		invalidate();
		requestLayout();
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
		if (scaleFactor >= initScaleFactor
				&& scaleFactor - 0.5f >= initScaleFactor) {
			startAnimator(scaleFactor, scaleFactor - 0.5f);
		} else if (scaleFactor > 0.5f && scaleFactor - 0.5f < 0.5) {
			startAnimator(scaleFactor, 0.5f);
		}

	}

	@SuppressLint({ "DrawAllocation", "NewApi" })
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mMapBitmap == null) {
			return;
		}

		if (isFirst) {

			float scaleHeight = getHeight() / ((float) mMapBitmap.getHeight());
			float scaleWidth = getWidth() / ((float) mMapBitmap.getWidth());
			scaleFactor = scaleHeight > scaleWidth ? scaleWidth : scaleHeight;
			initScaleFactor = scaleFactor;
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
			elevator.recycle();
			lift.recycle();
			stair.recycle();
			wc.recycle();
		}
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		private float x;
		private float y;

		private float width;
		private float height;

		private ShopsData data = null;

		@Override
		public boolean onDown(MotionEvent e) {
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

				if (x >= -iconBgWidth / 2
						&& x <= getWidth() - width + iconBgWidth / 2) {

					moveX = x;
				} else if (x < -iconBgWidth / 2) {
					moveX = -iconBgWidth / 2;
				} else if (x > getWidth() - width + iconBgWidth / 2) {
					moveX = getWidth() - width + iconBgWidth / 2;
				}

			} else {
				if (distanceX < 0) {
					if (x <= iconBgWidth / 2) {

						moveX = x;

					} else {
						moveX = iconBgWidth / 2;
					}

				} else if (distanceX > 0) {

					if (x >= getWidth() - width - iconBgWidth / 2) {
						moveX = x;
					} else {
						moveX = getWidth() - width - iconBgWidth / 2;
					}

				}

			}

			if (height <= getHeight()) {

				if (y >= -iconBgHeight && y <= getHeight() - height) {

					moveY = y;
				} else if (y < -iconBgHeight) {
					moveY = -iconBgHeight;
				} else if (x > getHeight() - height) {
					moveY = getHeight() - height;
				}

			} else {
				if (distanceY < 0) {

					if (ih - iconBgHeight < 0) {

						if (y <= iconBgHeight - ih) {
							moveY = y;
						} else {
							moveY = iconBgHeight - ih;
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
			requestLayout();
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
			requestLayout();

			return true;
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		for (int i = 0, size = getChildCount(); i < size; i++) {
			View view = getChildAt(i);
			int w = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);

			view.measure(w, h);
			iconBgHeight = view.getMeasuredHeight();
			iconBgWidth = view.getMeasuredWidth();

			ShopsData data = null;

			if (datas != null) {
				data = datas.get(showLocation);
				int x = (int) (moveX + scaleFactor * data.getX() - iconBgWidth / 2);
				int y = (int) (moveY + scaleFactor * data.getY() - iconBgHeight);
				view.layout(x, y, x + iconBgWidth, y + iconBgHeight);
			}

		}

	}
}
