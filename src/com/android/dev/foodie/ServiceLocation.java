package com.android.dev.foodie;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import sg.edu.nus.ami.wifilocation.APLocation;
import sg.edu.nus.ami.wifilocation.CmpScan;
import sg.edu.nus.ami.wifilocation.Geoloc;
import sg.edu.nus.ami.wifilocation.NUSGeoloc;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Service Class to call Localisation Services provided By : Qinfeng & Jianbin 
 * Created as a Local Service as it is only required to run as a background task
 * Remote Services are for interprocess communication between applications
 * @author Cher Lia
 *
 */
public class ServiceLocation extends Service{
	
	public static String BROADCAST_ACTION = "com.android.dev.foodie.SHOW_LOCATION";
	
	private static final String TAG = "ServiceLocation";
	
	private ThreadGroup myThreads = new ThreadGroup("ServiceWorker");
	private NotificationManager notifmgr;
	
	WifiManager wifimgr;
	BroadcastReceiver receiver;
	
	Handler getPosHandler;
	APLocation apLocation;
	Vector<ScanResult> wifi;
	Geoloc geoloc;
	
	Vector<ScanResult> wifinus = new Vector<ScanResult>();
	Vector<APLocation> v_apLocation;
	
	@Override
	public void onCreate() {
		super.onCreate();

		Toast.makeText(this, "ServiceLocation CREATED", Toast.LENGTH_LONG).show();
		notifmgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		displayNotificationMessage("Background Service 'NUS Foodie' is running.");
	}
	
	/**
	 * Local Service hence no need for binding, as binding is for remote services
	 */
	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Toast.makeText(this, "ServiceLocation Started.", Toast.LENGTH_LONG).show();
		
		//DO SOMETHING
		int counter = intent.getExtras().getInt("counter");
		new Thread(myThreads, new ServiceWorker(counter), "ServiceLocation").start();
		
		return START_STICKY;
	}
	
	/**
	 * Cannot touch UI directly
	 *
	 */
	class ServiceWorker implements Runnable {
		private int counter = -1;
		public ServiceWorker(int counter) {
			this.counter = counter;
		}

		@Override
		public void run() {
			final String TAG2 = "ServiceWorker:" + Thread.currentThread().getId();
//			int ct = 0;
//			for(int t=0; t<1000; t++) {
//				ct++;
//				System.out.println(ct);
//				Log.v(TAG, "Testing ct = " + ct);
//			}	
				//setup wifi
			    wifimgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			    
			    if(wifimgr.isWifiEnabled()==false){
			    	wifimgr.setWifiEnabled(true);
			    }
			    
			    //getPosHandler = new Handler();
			    apLocation = new APLocation();
			    geoloc = new Geoloc();
				
			    wifimgr.startScan();
			    
			    Log.v(TAG, "WIFI SCANNING! ");
			    
			  //register broadcast receiver
		        if(receiver == null) {
		        	receiver = new BroadcastReceiver() {
						
						@Override
						public void onReceive(Context context, Intent intent) {
							
							//clear the nus official ap list record every time when refresh
							wifinus.removeAllElements();
							
							StringBuilder sb1 = new StringBuilder();
							StringBuilder sb2 = new StringBuilder();
							StringBuilder sb3 = new StringBuilder();
							
							StringBuilder sb1_1 = new StringBuilder();
							StringBuilder sb2_1 = new StringBuilder();
							StringBuilder sb3_1 = new StringBuilder();
							
							List<ScanResult> wifilist = wifimgr.getScanResults();
							//haha...more intelligient sort...
							Collections.sort(wifilist,  new CmpScan());
							
							for (ScanResult wifipoint : wifilist){
								sb1.append(wifipoint.SSID + "\n");
								sb2.append(wifipoint.BSSID + "\n");
								sb3.append(wifipoint.level + "\n");
								
								if (wifipoint.SSID.equals("NUS") || wifipoint.SSID.equals("NUSOPEN")) {
									wifinus.add(wifipoint);
									
									Log.v("TAG + wifi_point", "wifi_point = " + wifipoint.SSID);
									
									sb1_1.append(wifipoint.SSID + "\n");
									sb2_1.append(wifipoint.BSSID + "\n");
									sb3_1.append(wifipoint.level + "\n");
								}
							}//for
													
							//check for duplicate address				
							 // wifi=(Vector<ScanResult>) wifinus.clone();
							  Log.v(TAG, "wifinus size before "+String.valueOf(wifinus.size()));
							for ( int k=0;k<wifinus.size();k++){

								for(int i=(k+1);i<wifinus.size();i++){

									if(wifinus.get(k).BSSID.substring(0, 16).equals(wifinus.get(i).BSSID.substring(0, 16))){
										wifinus.removeElementAt(i);
										i--;
									
										Log.v(TAG, "remove wifi at "+String.valueOf(i));
									}
								}

							}


							if (wifinus.size()>0) {

								NUSGeoloc nusGeoloc = new NUSGeoloc();
								Vector<String> mac = new Vector<String>(wifinus.size());
								Vector<Double> strength = new Vector<Double>(wifinus.size());
								v_apLocation = new Vector<APLocation>(wifinus.size());
								
								for ( ScanResult temp_wifi:wifinus){
									mac.add(temp_wifi.BSSID);
									strength.add(Double.valueOf(temp_wifi.level));
								}
						
								
								v_apLocation = nusGeoloc.getLocationBasedOnAP(mac, strength);
								if(v_apLocation.isEmpty()){
									Log.v(TAG + "NO_LOCATION","No location is available");
								}else{
									
									apLocation = v_apLocation.firstElement();//choose the nearest location
									
									DecimalFormat df = new DecimalFormat("###.###");
									
									StringBuilder build=new StringBuilder();
									StringBuilder Loc=new StringBuilder();
									StringBuilder dist=new StringBuilder();
									StringBuilder temp = new StringBuilder();
									
									int g=0;
									for(APLocation apoint:v_apLocation){

										build.append(apoint.getBuilding()+"\n"+apoint.getAp_lat()+"\n");
										Loc.append(apoint.getAp_location()+"\n"+apoint.getAp_long()+"\n");
										temp.append(apoint.getAp_location() + "\n");
										//dist.append(String.valueOf(df.format(CI.SignalD(wifinus.get(g).level))+"m \n"+wifinus.get(g).level+"\n"));
										g++;

									}
									
//									double []P1xy=new double[2] ;
//									double []P2xy=new double[2] ;
//									double []P3_inter= new double [3];
//									double []P3_latlon= new double [2];
//									double Distance,r1,r2;
//									P1xy=CI.LatLonToMeters(v_apLocation.get(0).getAp_lat(),v_apLocation.get(0).getAp_long());
//									P2xy=CI.LatLonToMeters(v_apLocation.get(1).getAp_lat(),v_apLocation.get(1).getAp_long());
//									r1=CI.SignalD(wifinus.get(0).level);
//									r2=CI.SignalD(wifinus.get(1).level);
//									
//									Distance=CI.geoDist_xy(P1xy, P2xy);
//									//double XY=CI.geoDist_xy(v_apLocation.get(0).getAp_lat(),v_apLocation.get(0).getAp_long(),v_apLocation.get(1).getAp_lat(),v_apLocation.get(1).getAp_long());
//									//df.format(DT);
//									boolean check=CI.CheckInt(r1,r2, Distance);
//									
//									//if does intersect ,cal intersect point.
//									if(check){
//										P3_inter=CI.Intersectcenter(r1, r2, P1xy[0],P1xy[1] , P2xy[0],P2xy[1], Distance);
//										P3_latlon=CI.MetersToLatLon(P3_inter[0],P3_inter[1]);
//									}


									Log.v(TAG + "final_location", temp.toString());
									
									Intent return_intent = new Intent();
									return_intent.setAction(BROADCAST_ACTION);
									return_intent.putExtra("ap_location", apLocation.toString());
									//return_intent.putExtra("LOCATION", temp.toString());
									//Bundle send_obj = new Bundle();
									//send_obj.putSerializable("ap_location", apLocation);
									//return_intent.putExtras(send_obj);
									//return_intent.putExtra("ap_location", apLocation);
									sendBroadcast(return_intent);
									
									Log.v(TAG,"Sending Object over.");
									
								}
								
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
					
					registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
					Log.v(TAG, "onCreate()");
		        }
		        
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Toast.makeText(this, "ServiceLocation Done.", Toast.LENGTH_LONG).show();
		myThreads.interrupt();
		notifmgr.cancelAll();
		unregisterReceiver(receiver);
		
	}

	private void displayNotificationMessage(String message) {
		Notification notification = new Notification(R.drawable.linus_icon, message, System.currentTimeMillis());
		
		//prevent user from clearing notification. Keep notification until we destroy it ourselves
		notification.flags = Notification.FLAG_NO_CLEAR;
		
		/**
		 * PendingIntent.getActivity : Retrieve a PendingIntent that will start a new activity, like calling Context.startActivity(Intent). 
		 * @params context 
		 * @params requestCode Private request code for the sender (currently not used). 
		 * @params intent Intent of the activity to be launched.
		 * @params flags
		 * 
		 */
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,SearchAct.class), 0);
		/**
		 * Deprecated 
		 * 
		 * @params context
		 * @params contentTitle The title that goes in the expanded entry
		 * @params contentText The text that goes in the expanded entry
		 * @params contentIntent
		 */
		notification.setLatestEventInfo(this, TAG, message, contentIntent);
		
		notifmgr.notify(0, notification);
	}
	
}
