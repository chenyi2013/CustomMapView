package com.example.custommapview;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomMapView extends View {

	private Context mContext;
	private Paint mCirclePaint;

	private float moveX = 0;
	private float moveY = 0;

	private float distanceX = 0;
	private float distanceY = 0;

	private float currentX = 0;
	private float currentY = 0;

	private float scaleFactor = 1f;
	private int showLocation = 0;

	private boolean isChoice = false;
	private float lineHeight = 20;

	private ArrayList<GraphData> datas;
	private OnClickGraphListener onClickGraphListener;

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

	public CustomMapView(Context context) {
		super(context);
		mContext = context;
		init(context);

	}

	public CustomMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init(context);

	}

	public CustomMapView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
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

		scaleFactor = scaleFactor + 0.5f;
		invalidate();

	}

	public void scaleDown() {
		if (scaleFactor > 0.5f) {
			scaleFactor = scaleFactor - 0.5f;
			invalidate();
		}

	}

	@SuppressLint({ "DrawAllocation", "NewApi" })
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		GraphData data = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeStream(mContext.getResources()
				.openRawResource(R.raw.ic_test), new Rect(), options);
		Matrix matrix = new Matrix();
		matrix.postScale(scaleFactor, scaleFactor);
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		canvas.drawBitmap(resizeBmp, moveX, moveY, mCirclePaint);
		bitmap.recycle();

		if (datas == null) {
			return;
		}

		for (int i = 0; i < datas.size(); i++) {
			data = datas.get(i);
			canvas.drawCircle(moveX + scaleFactor * data.getX(), moveY
					+ scaleFactor * data.getY(), 5, mCirclePaint);
		}

		data = datas.get(showLocation);
		Bitmap bitmap1 = null;
		canvas.drawLine(moveX + scaleFactor * data.getX(), moveY + scaleFactor
				* data.getY(), moveX + scaleFactor * data.getX(), moveY
				+ scaleFactor * data.getY() - lineHeight, mCirclePaint);
		bitmap1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);

		canvas.drawBitmap(bitmap1,
				moveX + scaleFactor * data.getX() - bitmap1.getWidth() / 2,
				moveY + scaleFactor * data.getY() - bitmap1.getHeight()
						- lineHeight, mCirclePaint);
		bitmap1.recycle();

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

}
