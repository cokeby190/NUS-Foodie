package com.android.dev.foodie;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class ServiceLocation extends Service{
	
	@Override
	public void onCreate() {
		super.onCreate();

		Toast.makeText(this, "ServiceLocation CREATED", Toast.LENGTH_LONG).show();
	}
	

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Toast.makeText(this, "ServiceLocation Started.", Toast.LENGTH_LONG).show();
		
		//DO SOMETHING
		
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Toast.makeText(this, "ServiceLocation Done.", Toast.LENGTH_LONG).show();
	}

	
}
