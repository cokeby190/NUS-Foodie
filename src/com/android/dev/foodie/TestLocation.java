package com.android.dev.foodie;

import sg.edu.nus.ami.wifilocation.APLocation;
import sg.edu.nus.ami.wifilocation.Geoloc;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;

public class TestLocation {

	WifiManager wifimgr;
	Context mContext;
	Handler getPosHandler;
	APLocation apLocation;
	Geoloc geoloc;
	
	TestLocation(Context mContext) {
		this.mContext = mContext;
	}
	
	public void find_location() {
		//setup wifi
	    wifimgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	    
	    if(wifimgr.isWifiEnabled()==false){
	    	wifimgr.setWifiEnabled(true);
	    }
	    
	    getPosHandler = new Handler();
	    apLocation = new APLocation();
	    geoloc = new Geoloc();
	}
	
}
