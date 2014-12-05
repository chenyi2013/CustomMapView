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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
	private ArrayList<ShopsData> mData;
	private RelativeLayout mRelativeLayout;

	LinearLayout la;

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

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeStream(getResources()
				.openRawResource(R.raw.aaa), new Rect(), options);
		mCustomMapView = (CustomMapView) findViewById(R.id.map_view);
		mCustomMapView.setPublicFacility(getPublicFacilities());
		mCustomMapView.setMapBitmap(bitmap);
		mCustomMapView.bindData(mData = getData());
		mCustomMapView.setShowLocation(0);
		final Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon);
		mCustomMapView.setIcon(bitmap1);
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

		final Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon1);
		final Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon2);
		final Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon3);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mCustomMapView.setShowLocation(position);
				switch (position % 4) {
				case 0:
					mCustomMapView.setIcon(bitmap1);
					break;
				case 1:
					mCustomMapView.setIcon(bitmap2);
					break;
				case 2:
					mCustomMapView.setIcon(bitmap3);
					break;
				case 3:
					mCustomMapView.setIcon(bitmap4);
					break;
				}

			}
		});

	}

	private ArrayList<PublicFacilityData> getPublicFacilities() {
		ArrayList<PublicFacilityData> datas = new ArrayList<PublicFacilityData>();

		// lift
		datas.add(new PublicFacilityData(153, 108, PublicFacilityData.ESCALATOR));
		datas.add(new PublicFacilityData(290, 108, PublicFacilityData.ESCALATOR));
		datas.add(new PublicFacilityData(344, 108, PublicFacilityData.ESCALATOR));
		datas.add(new PublicFacilityData(525, 108, PublicFacilityData.ESCALATOR));
		datas.add(new PublicFacilityData(812, 96, PublicFacilityData.ESCALATOR));
		datas.add(new PublicFacilityData(1083, 173,
				PublicFacilityData.ESCALATOR));

		// elevator
		datas.add(new PublicFacilityData(225, 74, PublicFacilityData.ELEVATOR));
		datas.add(new PublicFacilityData(411, 74, PublicFacilityData.ELEVATOR));
		datas.add(new PublicFacilityData(604, 78, PublicFacilityData.ELEVATOR));
		datas.add(new PublicFacilityData(780, 68, PublicFacilityData.ELEVATOR));

		// stair
		datas.add(new PublicFacilityData(253, 73, PublicFacilityData.STAIRWAY));
		datas.add(new PublicFacilityData(448, 74, PublicFacilityData.STAIRWAY));
		datas.add(new PublicFacilityData(942, 32, PublicFacilityData.STAIRWAY));

		// wc
		datas.add(new PublicFacilityData(416, 56, PublicFacilityData.TOILET));
		datas.add(new PublicFacilityData(779, 49, PublicFacilityData.TOILET));
		return datas;

	}

	private ArrayList<ShopsData> getData() {
		ArrayList<ShopsData> datas = new ArrayList<ShopsData>();
		datas.add(new ShopsData(75, 113, "F2-124"));
		datas.add(new ShopsData(140, 63, "F2-152"));
		datas.add(new ShopsData(197, 147, "F2-155"));
		datas.add(new ShopsData(204, 109, "F2-35"));
		datas.add(new ShopsData(239, 109, "F2-1522"));
		datas.add(new ShopsData(240, 147, "F2-456"));
		datas.add(new ShopsData(268, 147, "F2-455"));
		datas.add(new ShopsData(299, 147, "F2-185"));
		datas.add(new ShopsData(328, 147, "F2-855"));
		datas.add(new ShopsData(358, 147, "F2-115"));

		datas.add(new ShopsData(387, 147, "F2-109"));
		datas.add(new ShopsData(418, 147, "F2-1095"));
		datas.add(new ShopsData(448, 147, "F2-0155"));
		datas.add(new ShopsData(218, 89, "F2-7155"));
		datas.add(new ShopsData(249, 89, "F2-1955"));
		datas.add(new ShopsData(409, 89, "F2-1525"));
		datas.add(new ShopsData(447, 89, "F2-15155"));
		datas.add(new ShopsData(334, 63, "F2-4345"));
		datas.add(new ShopsData(490, 54, "F2-1775"));
		datas.add(new ShopsData(400, 109, "F2-14564"));
		datas.add(new ShopsData(482, 140, "F2-1673"));
		datas.add(new ShopsData(482, 166, "F2-1565"));

		datas.add(new ShopsData(550, 54, "F2-156545"));
		datas.add(new ShopsData(598, 54, "F2-15455"));
		datas.add(new ShopsData(627, 54, "F2-15656"));
		datas.add(new ShopsData(656, 54, "F2-15645"));
		datas.add(new ShopsData(683, 54, "F2-156545"));
		datas.add(new ShopsData(708, 54, "F2-156564"));
		datas.add(new ShopsData(817, 53, "F2-15656"));
		datas.add(new ShopsData(863, 53, "F2-133"));
		datas.add(new ShopsData(909, 53, "F2-1225"));

		datas.add(new ShopsData(734, 68, "F2-5555"));
		datas.add(new ShopsData(756, 68, "F2-6755"));
		datas.add(new ShopsData(684, 95, "F2-7355"));
		datas.add(new ShopsData(976, 93, "F2-73555"));
		datas.add(new ShopsData(1136, 85, "F2-3555"));
		datas.add(new ShopsData(663, 140, "F2-3555"));
		datas.add(new ShopsData(693, 140, "F2-3555"));
		datas.add(new ShopsData(731, 140, "F2-6655"));
		datas.add(new ShopsData(790, 140, "F2-35655"));
		datas.add(new ShopsData(838, 140, "F2-3555"));
		datas.add(new ShopsData(890, 140, "F2-35655"));
		datas.add(new ShopsData(939, 140, "F2-7755"));
		datas.add(new ShopsData(1123, 128, "F2-6555"));
		datas.add(new ShopsData(1149, 163, "F2-6555"));

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
			mCustomMapView.setShowType(PublicFacilityData.ESCALATOR);

			break;
		case R.id.elevator:
			mCustomMapView.setShowType(PublicFacilityData.ELEVATOR);

			break;
		case R.id.stair:
			mCustomMapView.setShowType(PublicFacilityData.STAIRWAY);

			break;
		case R.id.wc:
			mCustomMapView.setShowType(PublicFacilityData.TOILET);

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
