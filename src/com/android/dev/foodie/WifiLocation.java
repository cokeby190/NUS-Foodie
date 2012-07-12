package com.android.dev.foodie;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import sg.edu.nus.ami.wifilocation.APLocation;
import sg.edu.nus.ami.wifilocation.BasicWifiLocation;
import sg.edu.nus.ami.wifilocation.CirIntersec;
import sg.edu.nus.ami.wifilocation.CmpScan;
import sg.edu.nus.ami.wifilocation.Geoloc;
import sg.edu.nus.ami.wifilocation.NUSGeoloc;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

public class WifiLocation {
	
	Handler getPosHandler;
	APLocation apLocation;
	Geoloc geoloc;
	
	BroadcastReceiver receiver;
	Vector<ScanResult> wifinus = new Vector<ScanResult>();
	Vector<APLocation> v_apLocation;
	WifiManager wifimgr;
	CirIntersec CI;
	
	Context mContext;
	
	String final_location;
	
	public WifiLocation(Context mContext) {
		getPosHandler = new Handler();
	    apLocation = new APLocation();
	    geoloc = new Geoloc();
	    
	    this.mContext = mContext;
	}
	    
	public String find_location() {    
		
	  //setup wifi
        wifimgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	    
	  //register broadcast receiver
        if(receiver == null){
        	receiver = new BroadcastReceiver() {
				
				@Override
				public void onReceive(Context context, Intent intent) {
					
					//clear the nus official ap list record every time when refresh
					wifinus.removeAllElements();
					
					//non-nus point
					StringBuilder sb1 = new StringBuilder();
					StringBuilder sb2 = new StringBuilder();
					StringBuilder sb3 = new StringBuilder();
					
					//for nus point
					StringBuilder sb1_1 = new StringBuilder();
					StringBuilder sb2_1 = new StringBuilder();
					StringBuilder sb3_1 = new StringBuilder();
					
					List<ScanResult> wifilist = wifimgr.getScanResults();
					//sorting
					Collections.sort(wifilist,  new CmpScan());
					
					for (ScanResult wifipoint : wifilist){
						sb1.append(wifipoint.SSID + "\n");
						sb2.append(wifipoint.BSSID + "\n");
						sb3.append(wifipoint.level + "\n");
						
						if (wifipoint.SSID.equals("NUS") || wifipoint.SSID.equals("NUSOPEN")) {
							wifinus.add(wifipoint);
							sb1_1.append(wifipoint.SSID + "\n");
							sb2_1.append(wifipoint.BSSID + "\n");
							sb3_1.append(wifipoint.level + "\n");
						}
					}
					
					//check for duplicate address				
					 // wifi=(Vector<ScanResult>) wifinus.clone();
					  Log.d("check", "wifinus size before "+String.valueOf(wifinus.size()));
					  
					//removing duplicate (?)
					for ( int k=0;k<wifinus.size();k++){

						for(int i=(k+1);i<wifinus.size();i++){

							if(wifinus.get(k).BSSID.substring(0, 16).equals(wifinus.get(i).BSSID.substring(0, 16))){
								wifinus.removeElementAt(i);
								i--;
							
								Log.d("check", "remove wifi at "+String.valueOf(i));
							}
						}

					}
					
					if (wifinus.size()>0) {
						new Thread(getPosRunnable).start();
					}
					
					//loop the wifi scan
					//TODO: qinfeng to improve the delayed time
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						
						public void run() {
							wifimgr.startScan();
						}
					}, 2000);
				}
			};
			
			mContext.registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
			
        }
        
        return final_location;
	}
	
	private Runnable getPosRunnable = new Runnable() {
		
		public void run() {
			getPosHandler.post(new Runnable() {
				public void run() {
					//tv_location.setText("Getting position");
				}
			});
			
			NUSGeoloc nusGeoloc = new NUSGeoloc();
			Vector<String> mac = new Vector<String>(wifinus.size());
			Vector<Double> strength = new Vector<Double>(wifinus.size());
			v_apLocation = new Vector<APLocation>(wifinus.size());
			
			//wifisort(mac, strength, wifinus);
			//haha...more intelligient sort...
//			Collections.sort(wifinus,  new CmpScan());
			
			for ( ScanResult temp_wifi:wifinus){
				mac.add(temp_wifi.BSSID);
				strength.add(Double.valueOf(temp_wifi.level));
			}
	
			
			v_apLocation = nusGeoloc.getLocationBasedOnAP(mac, strength);
			if(v_apLocation.isEmpty()){
				getPosHandler.post(new Runnable() {
					
					public void run() {
						//tv_location.setText("No location is available");
					}
				});
			}else{
				
				apLocation = v_apLocation.firstElement();//choose the nearest location
				
				//int test = v_apLocation.size();// get second location??
				//tv_location.setText(String.valueOf(test));
				//update main UI via handler
				getPosHandler.post(new Runnable() {
					DecimalFormat df = new DecimalFormat("###.###");
					public void run() {
						//						tv_location.setText(apLocation.toString());

						//String text = "You are near the room of "+apLocation.getAp_location()+" in the building of "+apLocation.getBuilding()+"\n";
						//							String text = wifinus.get(0).level+" You are about: "+DfromCenter(wifinus.get(0).level)+" m from the room of "+apLocation.getAp_location()+" in the building of "+apLocation.getBuilding()+"\n";
						//							tv_location.setText(text);
						StringBuilder build=new StringBuilder();
						StringBuilder Loc=new StringBuilder();
						StringBuilder dist=new StringBuilder();
						ArrayList <String> temp = null;
						int g=0;
						for(APLocation apoint:v_apLocation){

							build.append(apoint.getBuilding()+"\n"+apoint.getAp_lat()+"\n");
							Loc.append(apoint.getAp_location()+"\n"+apoint.getAp_long()+"\n");
							dist.append(String.valueOf(df.format(CI.SignalD(wifinus.get(g).level))+"m \n"+wifinus.get(g).level+"\n"));
							temp.add(apoint.getAp_location());
							g++;
						}
						
						// find distance between two strongest point
					
						
						//double DT=CI.geoDist_m(v_apLocation.get(0).getAp_lat(),v_apLocation.get(0).getAp_long(),v_apLocation.get(1).getAp_lat(),v_apLocation.get(1).getAp_long());
						
						double []P1xy=new double[2] ;
						double []P2xy=new double[2] ;
						double []P3_inter= new double [3];
						double []P3_latlon= new double [2];
						double Distance,r1,r2;
						P1xy=CI.LatLonToMeters(v_apLocation.get(0).getAp_lat(),v_apLocation.get(0).getAp_long());
						P2xy=CI.LatLonToMeters(v_apLocation.get(1).getAp_lat(),v_apLocation.get(1).getAp_long());
						r1=CI.SignalD(wifinus.get(0).level);
						r2=CI.SignalD(wifinus.get(1).level);
						
						Distance=CI.geoDist_xy(P1xy, P2xy);
						//double XY=CI.geoDist_xy(v_apLocation.get(0).getAp_lat(),v_apLocation.get(0).getAp_long(),v_apLocation.get(1).getAp_lat(),v_apLocation.get(1).getAp_long());
						//df.format(DT);
						boolean check=CI.CheckInt(r1,r2, Distance);
						
						//if does intersect ,cal intersect point.
						if(check){
							P3_inter=CI.Intersectcenter(r1, r2, P1xy[0],P1xy[1] , P2xy[0],P2xy[1], Distance);
							P3_latlon=CI.MetersToLatLon(P3_inter[0],P3_inter[1]);
						}
							
					
						//tv_location.setText("Dist between two APs:  "+String.valueOf(df.format(Distance)).replace(",",".")+"m "+check+"\nIntersection Pt:"+df.format(P3_latlon[0])+" "+df.format(P3_latlon[1])+" Radius: "+df.format(P3_inter[2]));	

						//tv_building.setText(build);
						//tv_loc.setText(Loc);
						//tv_distance.setText(dist);
						
						final_location = temp.get(0);

					}
				});
				
//				SharedPreferences.Editor prefEditor = preferences.edit();
//				prefEditor.putString(BasicWifiLocation.LOCATION_BUILDING, apLocation.getBuilding());
//				prefEditor.putString(BasicWifiLocation.LOCATION_ACCURACY, String.valueOf(apLocation.getAccuracy()));
//				prefEditor.putString(BasicWifiLocation.LOCATION_AP_NAME, apLocation.getAp_name());
//				prefEditor.putString(BasicWifiLocation.LOCATION_AP_LOCATION, apLocation.getAp_location());
//				prefEditor.putString(BasicWifiLocation.LOCATION_AP_LAT, String.valueOf(apLocation.getAp_lat()));
//				prefEditor.putString(BasicWifiLocation.LOCATION_AP_LONG, String.valueOf(apLocation.getAp_long()));
//				prefEditor.commit();
			}
		}
	};
	
}
