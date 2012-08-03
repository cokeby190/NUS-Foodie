package com.android.dev.foodie;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class SnapShot extends Activity {
	/** Called when the activity is first created. */
	private String URL_base = "http://137.132.145.136/Camera/TechnoEdgeCanteen/";
	
	String store_name = null, location = null, canteen = null; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_snapshot);

		ImageView view = (ImageView) findViewById(R.id.iv_store_snap);
		
		getData();
		
		//if (store_name.equals("Starbucks Coffee")) {
		//	Log.v("STARBUCKS", "you are at starbucks!");
		//	Toast.makeText(SnapShot.this, "You are in HERE! " + "Starbucks Coffee", Toast.LENGTH_LONG).show();

			try {
				InputStream in;
				in = new java.net.URL(URL_base + "TechnoEdge_Cam04.jpg").openStream();

				byte [] content = convertInputStreamToByteArray(in);
				Bitmap bmp = BitmapFactory.decodeByteArray(content, 0, content.length);
				
				view.setImageBitmap(bmp);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		//}
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