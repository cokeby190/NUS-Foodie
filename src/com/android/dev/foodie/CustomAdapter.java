package com.android.dev.foodie;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {

	// XML node keys
	static final String DIST = "dist";
	static final String FOOD_STALL = "food_stall"; // parent node
	static final String CANTEEN_NAME = "canteen_name";
	static final String STORE_NAME = "store_name";
	static final String LOCATION = "location";
	static final String ROOM_CODE = "room_code";
	static final String STORE_TYPE = "store_type";
	static final String CUISINE = "cuisine";
	static final String HALAL = "halal";
	static final String MENU = "menu";
	static final String AIRCON = "aircon";
	static final String AVAILABILITY_WEEKDAY = "availability_weekday";
	static final String AVAILABILITY_WEEKEND = "availability_weekend";
	static final String AVAILABILITY_VAC_WEEKDAY = "availability_vac_weekday";
	static final String AVAILABILITY_VAC_WEEKEND = "availability_vac_weekend";
	static final String AVAILABILITY_PUBHOL = "availability_pubhol";
	static final String IMG_PATH = "img_path";

	private Context context;
	private ArrayList<HashMap<String, String>> menuItems;
	int layoutResourceId;
	ViewHolder holder;
	int position_temp;
	public ImageLoader imageLoader;

	public CustomAdapter(Context context, int layoutResourceId,
			ArrayList<HashMap<String, String>> data) {
		this.context = context;
		this.menuItems = data;
		this.layoutResourceId = layoutResourceId;
		imageLoader=new ImageLoader(context);
	}

	public int getCount() {
		return menuItems.size();
	}

	public Object getItem(int position) {
		return menuItems.get(position);

	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		holder = null;
		position_temp = position;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new ViewHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.imageView1);
			holder.store_name = (TextView) row.findViewById(R.id.textView1);
			holder.location = (TextView) row.findViewById(R.id.textView2);
			holder.canteen_name = (TextView) row.findViewById(R.id.textView3);

			row.setTag(holder);

		} else
			holder = (ViewHolder) row.getTag();

		holder.store_name.setText(menuItems.get(position).get(STORE_NAME));
		holder.location.setText(menuItems.get(position).get(LOCATION));
		holder.canteen_name.setText(menuItems.get(position).get(CANTEEN_NAME));

		imageLoader.DisplayImage(menuItems.get(position).get(IMG_PATH), holder.imgIcon);

		return row;
	}

	static class ViewHolder {
		ImageView imgIcon;
		TextView store_name;
		TextView location;
		TextView canteen_name;
	}
}