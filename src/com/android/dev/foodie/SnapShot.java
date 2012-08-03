package com.android.dev.foodie;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class SnapShot extends Activity {

	private String URL_base = "http://137.132.145.136/Camera/TechnoEdgeCanteen/";
	
	String cam_no = null; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_snapshot);

		ImageView view = (ImageView) findViewById(R.id.iv_store_snap);
		
		getData();

		if(!cam_no.equals("Not Available")) {
			try {
				InputStream in;
				in = new java.net.URL(URL_base + cam_no + ".jpg").openStream();

				byte [] content = convertInputStreamToByteArray(in);
				Bitmap bmp = BitmapFactory.decodeByteArray(content, 0, content.length);
				
				view.setImageBitmap(bmp);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			
			String message = "No SnapShot available at the moment. ): Please try again later."; 
			DialogNotif dialog = new DialogNotif();
        	AlertDialog alert = dialog.newdialog(this, message);
        	alert.show();
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
		cam_no = getMessage.getString("cam_no");
	}

}