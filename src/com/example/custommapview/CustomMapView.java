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
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CustomMapView extends View {

	private Paint mCirclePaint;

	private float moveX = 0;
	private float moveY = 0;

	private float distanceX = 0;
	private float distanceY = 0;

	private float currentX = 0;
	private float currentY = 0;

	private float scaleFactor = 1f;
	private float previousScaleFactor = 1f;
	
	private int showLocation = 0;

	private boolean isChoice = false;
	private float lineHeight = 20;
	private boolean isFirst = true;

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
		AnimatorSet set = new AnimatorSet();
		set.play(ObjectAnimator.ofFloat(this, mSacleFactorProperty, start, end));
		set.setDuration(500);
		set.start();
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		GraphData data = null;

		float dx = -1;
		float dy = -1;

		switch (event.getAction()) {
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
			break;
		case MotionEvent.ACTION_MOVE:

			moveX = distanceX + event.getX() - currentX;
			moveY = distanceY + event.getY() - currentY;
			invalidate();

			break;
		case MotionEvent.ACTION_UP:

			dx = event.getX() - currentX;
			dy = event.getY() - currentY;

			if (isChoice && Math.sqrt(dx) < 5 && Math.sqrt(dy) < 5) {
				if (onClickGraphListener != null) {
					onClickGraphListener.onClick(showLocation);
				}
			}

		case MotionEvent.ACTION_CANCEL:

			isChoice = false;
			moveX = event.getX() - currentX + distanceX;
			moveY = event.getY() - currentY + distanceY;

			distanceX = moveX;
			distanceY = moveY;

			invalidate();
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
