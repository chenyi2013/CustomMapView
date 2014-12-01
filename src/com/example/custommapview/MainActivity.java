package com.example.custommapview;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.custommapview.CustomMapView.OnClickGraphListener;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	private Button mScaleUp;
	private Button mScaleDown;
	private CustomMapView mCustomMapView;
	private GridView mGridView;
	private TextView mLift;
	private TextView mElevator;
	private TextView mStair;
	private TextView mWc;
	private ArrayList<GraphData> mData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLift = (TextView) findViewById(R.id.lift);
		mLift.setOnClickListener(this);
		mElevator = (TextView) findViewById(R.id.elevator);
		mElevator.setOnClickListener(this);
		mStair = (TextView) findViewById(R.id.stair);
		mStair.setOnClickListener(this);
		mWc = (TextView) findViewById(R.id.wc);
		mWc.setOnClickListener(this);
		mScaleUp = (Button) findViewById(R.id.scale_up);
		mScaleUp.setOnClickListener(this);
		mScaleDown = (Button) findViewById(R.id.scale_down);
		mScaleDown.setOnClickListener(this);
		mCustomMapView = (CustomMapView) findViewById(R.id.map_view);
		mCustomMapView.bindData(mData = getData());
		mCustomMapView.setPublicFacility(getPublicFacilities());

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeStream(getResources()
				.openRawResource(R.raw.aaa), new Rect(), options);
		mCustomMapView.setMapBitmap(bitmap);
		mCustomMapView.setOnClickGraphListener(new OnClickGraphListener() {

			@Override
			public void onClick(int position) {

				Toast.makeText(getApplicationContext(),
						"current click Item" + position, Toast.LENGTH_LONG)
						.show();

			}
		});
		mGridView = (GridView) findViewById(R.id.grid_view);
		mGridView.setAdapter(new MyAdapter());
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mCustomMapView.setShowLocation(position);

			}
		});

	}

	private ArrayList<GraphData> getPublicFacilities() {
		ArrayList<GraphData> datas = new ArrayList<GraphData>();

		// lift
		datas.add(new GraphData(153, 108, GraphData.LIFT_POINT));
		datas.add(new GraphData(290, 108, GraphData.LIFT_POINT));
		datas.add(new GraphData(344, 108, GraphData.LIFT_POINT));
		datas.add(new GraphData(525, 108, GraphData.LIFT_POINT));
		datas.add(new GraphData(812, 96, GraphData.LIFT_POINT));
		datas.add(new GraphData(1083, 173, GraphData.LIFT_POINT));

		// elevator
		datas.add(new GraphData(225, 74, GraphData.ELEVATOR_POINT));
		datas.add(new GraphData(411, 74, GraphData.ELEVATOR_POINT));
		datas.add(new GraphData(604, 78, GraphData.ELEVATOR_POINT));
		datas.add(new GraphData(780, 68, GraphData.ELEVATOR_POINT));

		// stair
		datas.add(new GraphData(253, 73, GraphData.STAIR_POINT));
		datas.add(new GraphData(448, 74, GraphData.STAIR_POINT));
		datas.add(new GraphData(942, 32, GraphData.STAIR_POINT));

		// wc
		datas.add(new GraphData(416, 56, GraphData.WC_POINT));
		datas.add(new GraphData(779, 49, GraphData.WC_POINT));
		return datas;

	}

	private ArrayList<GraphData> getData() {
		ArrayList<GraphData> datas = new ArrayList<GraphData>();
		datas.add(new GraphData(75, 113, GraphData.SHOPS_POINT));
		datas.add(new GraphData(140, 63, GraphData.SHOPS_POINT));
		datas.add(new GraphData(197, 147, GraphData.SHOPS_POINT));
		datas.add(new GraphData(204, 109, GraphData.SHOPS_POINT));
		datas.add(new GraphData(239, 109, GraphData.SHOPS_POINT));
		datas.add(new GraphData(240, 147, GraphData.SHOPS_POINT));
		datas.add(new GraphData(268, 147, GraphData.SHOPS_POINT));
		datas.add(new GraphData(299, 147, GraphData.SHOPS_POINT));
		datas.add(new GraphData(328, 147, GraphData.SHOPS_POINT));
		datas.add(new GraphData(358, 147, GraphData.SHOPS_POINT));

		datas.add(new GraphData(387, 147, GraphData.SHOPS_POINT));
		datas.add(new GraphData(418, 147, GraphData.SHOPS_POINT));
		datas.add(new GraphData(448, 147, GraphData.SHOPS_POINT));
		datas.add(new GraphData(218, 89, GraphData.SHOPS_POINT));
		datas.add(new GraphData(249, 89, GraphData.SHOPS_POINT));
		datas.add(new GraphData(409, 89, GraphData.SHOPS_POINT));
		datas.add(new GraphData(447, 89, GraphData.SHOPS_POINT));
		datas.add(new GraphData(334, 63, GraphData.SHOPS_POINT));
		datas.add(new GraphData(490, 54, GraphData.SHOPS_POINT));
		datas.add(new GraphData(400, 109, GraphData.SHOPS_POINT));
		datas.add(new GraphData(482, 140, GraphData.SHOPS_POINT));
		datas.add(new GraphData(482, 166, GraphData.SHOPS_POINT));

		datas.add(new GraphData(550, 54, GraphData.SHOPS_POINT));
		datas.add(new GraphData(598, 54, GraphData.SHOPS_POINT));
		datas.add(new GraphData(627, 54, GraphData.SHOPS_POINT));
		datas.add(new GraphData(656, 54, GraphData.SHOPS_POINT));
		datas.add(new GraphData(683, 54, GraphData.SHOPS_POINT));
		datas.add(new GraphData(708, 54, GraphData.SHOPS_POINT));
		datas.add(new GraphData(817, 53, GraphData.SHOPS_POINT));
		datas.add(new GraphData(863, 53, GraphData.SHOPS_POINT));
		datas.add(new GraphData(909, 53, GraphData.SHOPS_POINT));

		datas.add(new GraphData(734, 68, GraphData.SHOPS_POINT));
		datas.add(new GraphData(756, 68, GraphData.SHOPS_POINT));
		datas.add(new GraphData(684, 95, GraphData.SHOPS_POINT));
		datas.add(new GraphData(976, 93, GraphData.SHOPS_POINT));
		datas.add(new GraphData(1136, 85, GraphData.SHOPS_POINT));
		datas.add(new GraphData(663, 140, GraphData.SHOPS_POINT));
		datas.add(new GraphData(693, 140, GraphData.SHOPS_POINT));
		datas.add(new GraphData(731, 140, GraphData.SHOPS_POINT));
		datas.add(new GraphData(790, 140, GraphData.SHOPS_POINT));
		datas.add(new GraphData(838, 140, GraphData.SHOPS_POINT));
		datas.add(new GraphData(890, 140, GraphData.SHOPS_POINT));
		datas.add(new GraphData(939, 140, GraphData.SHOPS_POINT));
		datas.add(new GraphData(1123, 128, GraphData.SHOPS_POINT));
		datas.add(new GraphData(1149, 163, GraphData.SHOPS_POINT));

		return datas;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.scale_up:
			mCustomMapView.scaleUp();

			break;
		case R.id.scale_down:
			mCustomMapView.scaleDown();

			break;
		case R.id.lift:
			mCustomMapView.setShowType(GraphData.LIFT_POINT);

			break;
		case R.id.elevator:
			mCustomMapView.setShowType(GraphData.ELEVATOR_POINT);

			break;
		case R.id.stair:
			mCustomMapView.setShowType(GraphData.STAIR_POINT);

			break;
		case R.id.wc:
			mCustomMapView.setShowType(GraphData.WC_POINT);

			break;
		}

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.item, parent, false);
			}

			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.img);
			switch (position % 4) {
			case 0:
				imageView.setImageResource(R.drawable.icon);
				break;
			case 1:
				imageView.setImageResource(R.drawable.icon1);
				break;
			case 2:
				imageView.setImageResource(R.drawable.icon2);
			case 3:
				imageView.setImageResource(R.drawable.icon3);
				break;

			default:
				break;
			}
			return convertView;
		}
	}
}
