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
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;

/**
 * 
 * @author Kevin
 * 
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CustomMapView extends View {

	/**
	 * 当touchMode等于该值时表示当前的手势模式处于缩放手势模式
	 */
	private final int SCALE_MODE = 1;
	/**
	 * 当touchMode等于该值时表示当前的手势模式处于移动手势模式
	 */
	private final int MOVE_MODE = 2;

	private Paint mCirclePaint;

	/**
	 * 在缩放手势模拟时两手指所产生的缩放因子
	 */
	private float scale;

	/**
	 * 地图在X轴方向的起点座标
	 */
	private float moveX = 0;
	/**
	 * 地图在Y轴方向的起点座标
	 */
	private float moveY = 0;

	/**
	 * 手指在X轴方向移动的距离
	 */
	private float distanceX = 0;
	/**
	 * 手指在Y轴方向移动的距离
	 */
	private float distanceY = 0;

	/**
	 * 手指在屏幕上按下的点在X轴方向的座标
	 */
	private float currentX = 0;
	/**
	 * 手指在屏幕上按下的点在Y轴方向的座标
	 */
	private float currentY = 0;

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
	/**
	 * 手势模式，如果是常量SCALE_MODE代表当前处于缩放手势模式如果是MOVE_MODE代表处于移动手势模式
	 */
	private int touchMode = -1;

	private AnimatorSet mAnimatorSet;

	/**
	 * 在缩放手势模式下两手指之间按在屏幕按下上时的初始距离
	 */
	private float oldDistance;
	/**
	 * 在缩放手势模式下两手指之间按在屏幕上移动或抬起时的距离
	 */
	private float newDistance;

	/**
	 * 判断当前在地图上标出的标签在手指按下时是否被点击
	 */
	private boolean isChoice = false;

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
			moveX = moveX
					- ((scaleFactor - previousScaleFactor) * mMapBitmap
							.getWidth()) / 2;
			moveY = moveY
					- ((scaleFactor - previousScaleFactor) * mMapBitmap
							.getHeight()) / 2;
			distanceX = moveX;
			distanceY = moveY;
			invalidate();
		};
	};

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

	private void init(Context context) {
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setDither(true);
		mCirclePaint.setStyle(Style.STROKE);
		mCirclePaint.setStrokeWidth(2);
		mCirclePaint.setColor(0xff000000);

	}

	public void setShowLocation(int location) {
		showLocation = location;
		invalidate();
	}

	public void scaleUp() {
		if (scaleFactor < 2) {
			startAnimator(scaleFactor, scaleFactor + 0.5f);
		}
	}

	public void scaleDown() {
		if (scaleFactor > 0.5f) {
			startAnimator(scaleFactor, scaleFactor - 0.5f);
		}

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

			distanceX = moveX;
			distanceY = moveY;

			isFirst = false;
		}

		Matrix matrix = new Matrix();
		matrix.postScale(scaleFactor, scaleFactor);
		Bitmap resizeBmp = Bitmap.createBitmap(mMapBitmap, 0, 0,
				mMapBitmap.getWidth(), mMapBitmap.getHeight(), matrix, true);
		canvas.drawBitmap(resizeBmp, moveX, moveY, mCirclePaint);

		if (!mMapBitmap.equals(resizeBmp)) {
			resizeBmp.recycle();
		}

		if (datas == null) {
			return;
		}

		for (int i = 0; i < datas.size(); i++) {
			data = datas.get(i);
			canvas.drawCircle(moveX + scaleFactor * data.getX(), moveY
					+ scaleFactor * data.getY(), 5, mCirclePaint);
		}

		data = datas.get(showLocation);
		Bitmap bitmap = null;
		canvas.drawLine(moveX + scaleFactor * data.getX(), moveY + scaleFactor
				* data.getY(), moveX + scaleFactor * data.getX(), moveY
				+ scaleFactor * data.getY() - lineHeight, mCirclePaint);
		bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);

		canvas.drawBitmap(bitmap,
				moveX + scaleFactor * data.getX() - bitmap.getWidth() / 2,
				moveY + scaleFactor * data.getY() - bitmap.getHeight()
						- lineHeight, mCirclePaint);
		bitmap.recycle();

		previousScaleFactor = scaleFactor;

		// Bitmap bitmap1 = null;
		// for (int i = 0; i < datas.size(); i++) {
		// data = datas.get(i);
		// canvas.drawLine(moveX + scaleFactor * data.getX(), moveY
		// + scaleFactor * data.getY(),
		// moveX + scaleFactor * data.getX(), moveY + scaleFactor
		// * data.getY() - 20, mCirclePaint);
		// bitmap1 = BitmapFactory.decodeResource(getResources(),
		// R.drawable.ic_launcher);
		//
		// canvas.drawBitmap(bitmap1, moveX + scaleFactor * data.getX()
		// - bitmap1.getWidth() / 2, moveY + scaleFactor * data.getY()
		// - bitmap1.getHeight() - 20, mCirclePaint);
		// bitmap1.recycle();
		// }
	}

	// 求距离
	private float getSpacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		GraphData data = null;

		float dx = -1;
		float dy = -1;

		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:

			currentX = event.getX();
			currentY = event.getY();

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
			touchMode = MOVE_MODE;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:

			touchMode = SCALE_MODE;
			oldDistance = getSpacing(event);
			break;

		case MotionEvent.ACTION_MOVE:

			if (touchMode == MOVE_MODE) {

				float width = scaleFactor * mMapBitmap.getWidth();
				float height = scaleFactor * mMapBitmap.getHeight();

				if (width <= getWidth()
						&& distanceX + event.getX() - currentX >= 0
						&& distanceX + event.getX() - currentX <= getWidth()
								- width) {
					moveX = distanceX + event.getX() - currentX;

				} else if (width <= getWidth()
						&& distanceX + event.getX() - currentX < 0) {
					moveX = 0;
				} else if (width <= getWidth()
						&& distanceX + event.getX() - currentX > getWidth()
								- width) {
					moveX = getWidth() - width;
				}

				if (height <= getHeight()
						&& distanceY + event.getY() - currentY >= 0
						&& distanceY + event.getY() - currentY <= getHeight()
								- height) {
					moveY = distanceY + event.getY() - currentY;

				} else if (height <= getHeight()
						&& distanceY + event.getY() - currentY < 0) {
					moveY = 0;
				} else if (height <= getHeight()
						&& distanceY + event.getY() - currentY > getHeight()
								- height) {
					moveY = getHeight() - height;
				}

				// ----------------------------------
				if (width > getWidth() && event.getX() - currentX > 0) {
					if (distanceX + event.getX() - currentX <= 0) {
						moveX = distanceX + event.getX() - currentX;
					} else {
						moveX = 0;
					}

				} else if (width > getWidth() && event.getX() - currentX < 0) {
					if (distanceX + event.getX() - currentX >= getWidth()
							- width) {
						moveX = distanceX + event.getX() - currentX;
					} else {
						moveX = getWidth() - width;
					}

				}

				if (height > getHeight() && event.getY() - currentY > 0) {
					if (distanceY + event.getY() - currentY <= 0) {
						moveY = distanceY + event.getY() - currentY;
					} else {
						moveY = 0;
					}

				} else if (height > getHeight() && event.getY() - currentY < 0) {
					if (distanceY + event.getY() - currentY >= getHeight()
							- height) {
						moveY = distanceY + event.getY() - currentY;
					} else {
						moveY = getHeight() - height;
					}

				}
				// ----------------------------------------

				invalidate();

			} else if (touchMode == SCALE_MODE && event.getPointerCount() > 1) {

				newDistance = getSpacing(event);
				if (newDistance > 20) {
					scale = newDistance / oldDistance;
				}
			}

			break;

		case MotionEvent.ACTION_POINTER_UP:

			break;
		case MotionEvent.ACTION_UP:

			if (touchMode == MOVE_MODE) {
				dx = event.getX() - currentX;
				dy = event.getY() - currentY;

				if (isChoice && Math.sqrt(dx) < 5 && Math.sqrt(dy) < 5) {
					if (onClickGraphListener != null) {
						onClickGraphListener.onClick(showLocation);
					}
				}
			} else if (touchMode == SCALE_MODE) {
				if (scale > 1.2) {
					scaleUp();
				} else if (scale < 0.8) {
					scaleDown();
				}
			}

		case MotionEvent.ACTION_CANCEL:

			if (touchMode == MOVE_MODE) {
				isChoice = false;
				float width = scaleFactor * mMapBitmap.getWidth();
				float height = scaleFactor * mMapBitmap.getHeight();

				if (width <= getWidth()
						&& distanceX + event.getX() - currentX >= 0
						&& distanceX + event.getX() - currentX <= getWidth()
								- width) {
					moveX = distanceX + event.getX() - currentX;

				} else if (width <= getWidth()
						&& distanceX + event.getX() - currentX < 0) {
					moveX = 0;
				} else if (width <= getWidth()
						&& distanceX + event.getX() - currentX > getWidth()
								- width) {
					moveX = getWidth() - width;
				}

				if (height <= getHeight()
						&& distanceY + event.getY() - currentY >= 0
						&& distanceY + event.getY() - currentY <= getHeight()
								- height) {
					moveY = distanceY + event.getY() - currentY;

				} else if (height <= getHeight()
						&& distanceY + event.getY() - currentY < 0) {
					moveY = 0;
				} else if (height <= getHeight()
						&& distanceY + event.getY() - currentY > getHeight()
								- height) {
					moveY = getHeight() - height;
				}

				// ----------------------------------
				if (width > getWidth() && event.getX() - currentX > 0) {
					if (distanceX + event.getX() - currentX <= 0) {
						moveX = distanceX + event.getX() - currentX;
					} else {
						moveX = 0;
					}

				} else if (width > getWidth() && event.getX() - currentX < 0) {
					if (distanceX + event.getX() - currentX >= getWidth()
							- width) {
						moveX = distanceX + event.getX() - currentX;
					} else {
						moveX = getWidth() - width;
					}

				}

				if (height > getHeight() && event.getY() - currentY > 0) {
					if (distanceY + event.getY() - currentY <= 0) {
						moveY = distanceY + event.getY() - currentY;
					} else {
						moveY = 0;
					}

				} else if (height > getHeight() && event.getY() - currentY < 0) {
					if (distanceY + event.getY() - currentY >= getHeight()
							- height) {
						moveY = distanceY + event.getY() - currentY;
					} else {
						moveY = getHeight() - height;
					}

				}
				// ----------------------------------------

				distanceX = moveX;
				distanceY = moveY;
				invalidate();

			}
			scale = -1;
			break;

		}
		return true;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mMapBitmap != null) {
			mMapBitmap.recycle();
		}
	}

}
