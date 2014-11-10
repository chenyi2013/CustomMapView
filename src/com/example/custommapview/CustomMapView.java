package com.example.custommapview;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class CustomMapView extends View {

	private int mWidth;
	private int mHeight;
	private Context mContext;
	private Paint mCirclePaint;

	private ArrayList<GraphData> datas;

	private void initData() {
		datas = new ArrayList<GraphData>();
		datas.add(new GraphData(205, 223, GraphData.BLUE_POINT));
		datas.add(new GraphData(321, 157, GraphData.BLUE_POINT));
		datas.add(new GraphData(520, 58, GraphData.BLUE_POINT));
		datas.add(new GraphData(543, 250, GraphData.BLUE_POINT));
		datas.add(new GraphData(205, 425, GraphData.BLUE_POINT));
		datas.add(new GraphData(368, 285, GraphData.BLUE_POINT));
		datas.add(new GraphData(343, 396, GraphData.BLUE_POINT));
		datas.add(new GraphData(497, 445, GraphData.BLUE_POINT));
		datas.add(new GraphData(223, 576, GraphData.BLUE_POINT));
		datas.add(new GraphData(511, 605, GraphData.BLUE_POINT));

		datas.add(new GraphData(424, 160, GraphData.RED_POINT));
		datas.add(new GraphData(626, 174, GraphData.RED_POINT));
		datas.add(new GraphData(231, 285, GraphData.RED_POINT));
		datas.add(new GraphData(654, 312, GraphData.RED_POINT));
		datas.add(new GraphData(444, 364, GraphData.RED_POINT));
		datas.add(new GraphData(637, 494, GraphData.RED_POINT));
		datas.add(new GraphData(335, 542, GraphData.RED_POINT));

	}

	public CustomMapView(Context context) {
		super(context);
		mContext = context;
		init(context);
		initData();
	}

	public CustomMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init(context);
		initData();

	}

	public CustomMapView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		init(context);
		initData();

	}

	private void init(Context context) {
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setDither(true);
		mCirclePaint.setStyle(Style.STROKE);
		mCirclePaint.setStrokeWidth(2);
		mCirclePaint.setColor(0xff000000);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		BitmapFactory.decodeStream(
				context.getResources().openRawResource(R.raw.ic_test),
				new Rect(), options);
		mWidth = options.outWidth;
		mHeight = options.outHeight;

		Bitmap bitmap = BitmapFactory.decodeStream(mContext.getResources()
				.openRawResource(R.raw.ic_test), new Rect(), options);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),
				bitmap);
		setBackground(bitmapDrawable);
		System.out.println("start");

	}

	@SuppressLint({ "DrawAllocation", "NewApi" })
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		GraphData data = null;

		for (int i = 0; i < datas.size(); i++) {
			data = datas.get(i);
			canvas.drawCircle(data.getX(), data.getY(), 5, mCirclePaint);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(mWidth, mHeight);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		GraphData data = null;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			for (int i = 0; i < datas.size(); i++) {
				data = datas.get(i);

				if (event.getX() >= data.getX() - 5
						&& event.getX() <= data.getX() + 5
						&& event.getY() >= data.getY() - 5
						&& event.getY() <= data.getY() + 5) {
					
					System.out.println("choice:"+i);

				}
			}

			break;
		case MotionEvent.ACTION_MOVE:

			break;
		case MotionEvent.ACTION_UP:

			break;
		case MotionEvent.ACTION_CANCEL:

			break;

		}
		return super.onTouchEvent(event);
	}

}
