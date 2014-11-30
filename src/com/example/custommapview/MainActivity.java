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
import android.widget.Toast;

import com.example.custommapview.CustomMapView.OnClickGraphListener;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	private Button mScaleUp;
	private Button mScaleDown;
	private CustomMapView mCustomMapView;
	private GridView mGridView;
	private ArrayList<GraphData> mData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mScaleUp = (Button) findViewById(R.id.scale_up);
		mScaleUp.setOnClickListener(this);
		mScaleDown = (Button) findViewById(R.id.scale_down);
		mScaleDown.setOnClickListener(this);
		mCustomMapView = (CustomMapView) findViewById(R.id.map_view);
		mCustomMapView.bindData(mData = getData());

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeStream(getResources()
				.openRawResource(R.raw.ic_test), new Rect(), options);
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

	private ArrayList<GraphData> getData() {
		ArrayList<GraphData> datas = new ArrayList<GraphData>();
//		datas.add(new GraphData(205, 223, GraphData.BLUE_POINT));
		datas.add(new GraphData(1, 1, GraphData.BLUE_POINT));
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
			return convertView;
		}
	}
}
