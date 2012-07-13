package com.android.dev.foodie;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import sg.edu.nus.ami.wifilocation.APLocation;
import sg.edu.nus.ami.wifilocation.CirIntersec;
import sg.edu.nus.ami.wifilocation.CmpScan;
import sg.edu.nus.ami.wifilocation.Geoloc;
import sg.edu.nus.ami.wifilocation.NUSGeoloc;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

public class test_location {

	WifiManager wifimgr;
	BroadcastReceiver receiver;
	
	Handler getPosHandler;
	APLocation apLocation;
	Vector<ScanResult> wifi;
	Geoloc geoloc;
	int maxLevel = -100;
	ScanResult nearestAP = null;
	Vector<ScanResult> wifinus = new Vector<ScanResult>();
	Vector<APLocation> v_apLocation;
	Context mContext;
	
	test_location(Context mContext) {
		
		getPosHandler = new Handler();
	    apLocation = new APLocation();
	    geoloc = new Geoloc();
		this.mContext = mContext;
	}	

	//setup wifi
    wifimgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    
    if(wifimgr.isWifiEnabled()==false){
    	wifimgr.setWifiEnabled(true);
    }
	
    
}
