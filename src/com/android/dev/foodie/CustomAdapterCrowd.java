package com.android.dev.foodie;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CustomAdapterCrowd extends BaseAdapter {

	// XML node keys
	static final String LOCATION = "location"; // parent node
	static final String CROWD_METRIC = "crowd_metric";
	
	private Context context;
	private ArrayList<HashMap<String, String>> menuItems;
	int layoutResourceId;
	ViewHolder holder;
	int position_temp;
	
	TextView canteen_name;
	ProgressBar metric;
	Button snapshot;

	public CustomAdapterCrowd(Context context, int layoutResourceId,
			ArrayList<HashMap<String, String>> data) {
		this.context = context;
		this.menuItems = data;
		this.layoutResourceId = layoutResourceId;
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
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutResourceId, parent, false);

			canteen_name = (TextView) row.findViewById(R.id.tv_crowd_canteen);
			metric = (ProgressBar) row.findViewById(R.id.pb_crowd);
			snapshot = (Button) row.findViewById(R.id.b_crowd_snap);
			
			holder = new ViewHolder();
			holder.canteen_name = (TextView) row.findViewById(R.id.tv_crowd_canteen);
			holder.metric = (ProgressBar) row.findViewById(R.id.pb_crowd);
			holder.snapshot = (Button) row.findViewById(R.id.b_crowd_snap);
			row.setTag(holder);

		} else
			holder = (ViewHolder) row.getTag();

		float metric_db = Float.valueOf(menuItems.get(position).get(CROWD_METRIC));
		int metric_int = (int) (metric_db*100);
		Log.v("METRIC", String.valueOf(metric_int));
		
		holder.canteen_name.setText(menuItems.get(position).get(LOCATION));
		holder.metric.setProgress(metric_int);
				
		return row;
	}

	static class ViewHolder {
		TextView canteen_name;
		ProgressBar metric;
		Button snapshot;
	}
}