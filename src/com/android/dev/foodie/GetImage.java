package com.android.dev.foodie;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GetImage {
	
	public Bitmap return_img(String path) {
		try {
			InputStream in;
			in = new java.net.URL(path).openStream();

			byte [] content = convertInputStreamToByteArray(in);
			Bitmap bmp = BitmapFactory.decodeByteArray(content, 0, content.length);
			
			//doesnt work for Nextiva server snapshot
			//Bitmap bmp = BitmapFactory.decodeStream(in);
			
			return bmp;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
}
