package com.android.dev.foodie;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

public class SnapShot extends Activity implements OnTouchListener {
	/** Called when the activity is first created. */
	private static final String TAG = "Touch";
	//private String path = "http://137.132.145.136/Camera/TechnoEdgeCanteen/TechnoEdge_Cam04.jpg";
	private String path = "http://172.18.101.125:8080/geoserver/nus/wms?service=WMS&version=1.1.0&"
			+ "request=GetMap&layers=nus:floors&styles=&"
			+ "bbox=103.771520782468,1.29205751696442,103.776169751949,1.29931030264545&"
			+ "width=540&height=960" + "&srs=EPSG:4326&format=image%2Fpng";
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
		
		if (store_name.equals("Indonesia Panggang")) {
			Toast.makeText(SnapShot.this, "You are in HERE! " + "Indonesia Panggang", Toast.LENGTH_LONG).show();
			
//			try {
//				Bitmap image = getRemoteImage(new URL(path));
//				view.setImageBitmap(image);
//				view.setOnTouchListener(this);
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			}
//			
			
			BitmapDrawable image = (BitmapDrawable) ImageOperations(this, path, "image");
			Bitmap bitmap = image.getBitmap();
			
			view.setImageBitmap(bitmap);
			view.setOnTouchListener(this);
		}

	}
	
	private void getData() {
		Bundle getMessage = getIntent().getExtras();
		store_name = getMessage.getString("store_name");
		location = getMessage.getString("location");
		canteen = getMessage.getString("canteen");
		
	}

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		dumpEvent(event);
		ImageView view = (ImageView) v;
		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			Log.d(TAG, "mode=DRAG");
			mode = DRAG;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			Log.d(TAG, "mode=NONE");
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				Log.d(TAG, "newDist=" + newDist);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode=ZOOM");
			}
			break;

		}
		view.setImageMatrix(matrix);
		return true;
	}

	private void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
				"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
		Log.d(TAG, sb.toString());
	}
	
//	public Bitmap getRemoteImage(final URL aURL) {
//	    try {
//	        final URLConnection conn = aURL.openConnection();
//	        conn.connect();
//	        final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
//	        final Bitmap bm = BitmapFactory.decodeStream(bis);
//	        bis.close();
//	        return bm;
//	    } catch (IOException e) {}
//	    return null;
//	}

	private Drawable ImageOperations(Context ctx, String url, String saveFilename) {
		try {
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	public Object fetch(String address) throws MalformedURLException,
			IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}