package com.android.dev.foodie;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

public class SnapShot extends Activity{
	/** Called when the activity is first created. */
	private static final String TAG = "Touch";
	private String path = "http://137.132.145.136/Camera/TechnoEdgeCanteen/TechnoEdge_Cam04.jpg";
	
	//private String path = "http://137.132.145.136/Camera/TechnoEdgeCanteen/ss.png";
	//private String path = "http://137.132.145.136/Camera/TechnoEdgeCanteen/20120718144648.jpg";
	
	//private String path = "http://www.opera.com/bitmaps/products/mobile/next/android/10.1b/android-robog-alone.png";
	//private String path = "http://www.desktopwallpaperhd.com/wallpapers/29/33905.jpg";
	
//	private String path = "http://172.18.101.125:8080/geoserver/nus/wms?service=WMS&version=1.1.0&"
//			+ "request=GetMap&layers=nus:floors&styles=&"
//			+ "bbox=103.771520782468,1.29205751696442,103.776169751949,1.29931030264545&"
//			+ "width=540&height=960" + "&srs=EPSG:4326&format=image%2Fpng";
	
	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	String building;
	
	String store_name = null, location = null, canteen = null; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_snapshot);

		ImageView view = (ImageView) findViewById(R.id.iv_store_snap);
		
		getData();
		
		if (store_name.equals("Starbucks Coffee")) {
			Log.v("STARBUCKS", "you are at starbucks!");
			Toast.makeText(SnapShot.this, "You are in HERE! " + "Starbucks Coffee", Toast.LENGTH_LONG).show();

			try {
				InputStream in;
				in = new java.net.URL(path).openStream();

				byte [] content = convertInputStreamToByteArray(in);
				Bitmap bmp = BitmapFactory.decodeByteArray(content, 0, content.length);
				
				//doesnt work for Nextiva server snapshot
				//Bitmap bmp = BitmapFactory.decodeStream(in);

				//resizing the snapshot
				int width = SnapShot.this.getWindowManager().getDefaultDisplay().getWidth();
				float image_aspect = bmp.getWidth()/bmp.getHeight();
				
				bmp = Bitmap.createScaledBitmap(bmp, width, Math.round(width*image_aspect), true);
				
				//set bitmap processed to imageview
				view.setImageBitmap(bmp);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
	
	public static byte[] convertInputStreamToByteArray(InputStream is)
			throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result = bis.read();
		while (result != -1) {
			byte b = (byte) result;
			buf.write(b);
			result = bis.read();
		}
		return buf.toByteArray();
	}

	private void getData() {
		Bundle getMessage = getIntent().getExtras();
		store_name = getMessage.getString("store_name");
		location = getMessage.getString("location");
		canteen = getMessage.getString("canteen");
		
	}

}