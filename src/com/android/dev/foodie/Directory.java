package com.android.dev.foodie;

import java.util.List;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import sg.edu.nus.ami.wifilocation.APLocation;

//import com.geoserver.nus.GetBuilding.receive_service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Directory extends Activity implements LocationListener, android.view.View.OnClickListener {
	/** Called when the activity is first created. */

	MapView map;
	long start, stop;
	MyLocationOverlay compass;
	MapController controller;
	int x,y;
	GeoPoint touchPoint;
	Drawable d;
	List<Overlay> overlayList;
	LocationManager lm;
	String towers;
	int lat = 0, longi = 0;
	Button floorPlan, latlong, building;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.osmdroidmap);
		map = (MapView) findViewById(R.id.mapview);
		map.setBuiltInZoomControls(true);
		map.setMultiTouchControls(true);

		
		overlayList = map.getOverlays();
		
		
		//start service
		 int counter = 1;
		 Intent intent1 = new Intent(this, ServiceLocation.class);
		 intent1.putExtra("counter", counter++);
		 startService(intent1);
		
		 receive_service msg_receive = new receive_service();
		
		 IntentFilter filter = new IntentFilter();
		 filter.addAction(ServiceLocation.BROADCAST_ACTION);
		 registerReceiver(msg_receive, filter);
		
		
		compass = new MyLocationOverlay(Directory.this, map);
		overlayList.add(compass);
		
		controller = map.getController();
		controller.setZoom(16);
		GeoPoint point = new GeoPoint(1.2921659,103.776053957);
		
		controller.setCenter(point);
		
		map.invalidate();
		d = getResources().getDrawable(R.drawable.rice_rating);
		
		//placing pinpoint at location
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria crit = new Criteria();
		
		towers = lm.getBestProvider(crit, false);
		Location location = lm.getLastKnownLocation(towers);
		if(location != null){
			lat = (int) (location.getLatitude()* 1E6);
			longi = (int) (location.getLongitude()* 1E6);
			GeoPoint ourLocation = new GeoPoint(lat, longi);
			OverlayItem  overlayItem = new OverlayItem("2nd string", "what's up", ourLocation);

			CustomPinpointNUS custom = new CustomPinpointNUS(d, Directory.this);
			custom.insertPinpoint(overlayItem);
			overlayList.add(custom);
		}else{
			Toast.makeText(this, "Couldn't get provider", Toast.LENGTH_SHORT).show();
		}
		

		declarations();
	}

	private void declarations() {
		// TODO Auto-generated method stub
		floorPlan = (Button) findViewById(R.id.bFloor);
		floorPlan.setOnClickListener(this);
//		latlong = (Button) findViewById(R.id.bLatLong);
//		latlong.setOnClickListener(this);
		building = (Button) findViewById(R.id.bRoofPlan);
		building.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		compass.disableCompass();
		lm.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		compass.enableCompass();
		lm.requestLocationUpdates(towers, 5000, 1, this);
		 int counter = 1;
		 Intent intent1 = new Intent(this, ServiceLocation.class);
		 intent1.putExtra("counter", counter++);
		 startService(intent1);
	
	}


	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	

	public void onLocationChanged(Location l) {
		// TODO Auto-generated method stub
		lat = (int) (l.getLatitude()* 1E6);
		longi = (int) (l.getLongitude()* 1E6);
		GeoPoint ourLocation = new GeoPoint(lat, longi);
		OverlayItem  overlayItem = new OverlayItem("2nd string", "what's up", ourLocation);
		
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		
		
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		/*--------------------------------------------Shows Floor Plan When Clicked -------------------------------------*/
		case R.id.bFloor:
		//Intent  a = new Intent(Directory.this, MapofNUS.class);
		//startActivity(a);
		break;
		
//		case R.id.bLatLong:
//			Intent b = new Intent(NUSMapsActivity.this, GetLatLong.class);
//			startActivity(b);
		
		case R.id.bRoofPlan:
		//	Intent c = new Intent(NUSMapsActivity.this, GetBuilding.class);
		//	startActivity(c);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuInflater blowUp = getMenuInflater();
		blowUp.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
//		case R.id.aboutUs:
//			Intent i = new Intent("com.thenewboston.travis.ABOUT");
//			startActivity(i);

//			break;
//		case R.id.preferences:
//			Intent p = new Intent("com.thenewboston.travis.PREFS");
//			startActivity(p);
//			break;
		case R.id.exit:
			
			stopService();
			finish();
			break;
		}
		return false;
	}



	public class receive_service extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			APLocation return_location = new APLocation();

			// if(action.equals("LOCATION")) {
			Bundle extra = intent.getExtras();
			String location = extra.getString("ap_location");
			location = location.replace("APLocation [", "");
			location = location.replace("]", "");

			// PARSE string from APLocation object retrieved
			String[] location_split = location.split(", ");
			for (int i = 0; i < location_split.length; i++) {
				int end = location_split[i].length();
				int index = location_split[i].indexOf("=");
				 if(location_split[i].substring(0, index).equals("building"))
				 {
				 return_location.setBuilding(location_split[i].substring(index+1,
				 end));
				 }
				// if(location_split[i].substring(0, index).equals("ap_name")) {
				// return_location.setAp_name(location_split[i].substring(index+1,
				// end));
				// }
				if (location_split[i].substring(0, index).equals("ap_location")) {
					return_location.setAp_location(location_split[i].substring(
							index + 1, end));
				}
				// if(location_split[i].substring(0, index).equals("accuracy"))
				// {
				// return_location.setAccuracy(Double.valueOf(location_split[i].substring(index+1,
				// end)));
				// }
				if (location_split[i].substring(0, index).equals("ap_lat")) {
					return_location.setAp_lat(Double.valueOf(location_split[i]
							.substring(index + 1, end)));

				}
				if (location_split[i].substring(0, index).equals("ap_long")) {
					return_location.setAp_long(Double.valueOf(location_split[i]
							.substring(index + 1, end)));

				}
			}

			Log.v("RETURN_MSG", return_location.getAp_location());
//			Toast.makeText(getApplicationContext(),
//					"I am at : " + return_location.getAp_location(),
//					Toast.LENGTH_SHORT).show();

//			Log.v("RETURN_MSG LAT", String.valueOf(return_location.getAp_lat()));
//			Toast.makeText(getApplicationContext(),
//					"Current Lat : " + return_location.getAp_lat(),
//					Toast.LENGTH_LONG).show();
//			Log.v("RETURN_MSG LONG",
//					String.valueOf(return_location.getAp_long()));
//			Toast.makeText(getApplicationContext(),
//					"Current Long : " + return_location.getAp_long(),
//					Toast.LENGTH_LONG).show();
//			

			// }
		}

	}

	// CLOSE SERVICE
	public void stopService() {
		if (stopService(new Intent(Directory.this, ServiceLocation.class)))
			Toast.makeText(this, "stopService success", Toast.LENGTH_SHORT);
		else
			Toast.makeText(this, "stopService unsuccess", Toast.LENGTH_SHORT);
	}
	
	
		
		
	
	
	
}