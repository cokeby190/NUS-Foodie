package com.android.dev.foodie;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapView;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class CustomPinpointNUS extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> pinpoints = new ArrayList<OverlayItem>();
	private Context c;

	public CustomPinpointNUS(Drawable m, Context context) {
		super(m, new DefaultResourceProxyImpl(context));
		c = context;

		// TODO Auto-generated constructor stub

	}

	protected OverlayItem createItem(int i) {
		return pinpoints.get(i);
	}

	@Override
	public int size() {
		return pinpoints.size();
	}

	public void insertPinpoint(OverlayItem x) {

		pinpoints.add(x);
		this.populate();
	}

	public boolean onSnapToItem(int arg0, int arg1, Point arg2, IMapView arg3) {
		// TODO Auto-generated method stub
		return false;
	}

}
