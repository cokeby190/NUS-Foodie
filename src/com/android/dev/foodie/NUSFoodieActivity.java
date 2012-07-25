package com.android.dev.foodie;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class NUSFoodieActivity extends Activity implements OnClickListener{
	
	private Button searchButton, dirButton, nearbyButton, crowdButton;
	
	//titlebar
	boolean customTitleSupport = true;
	TextView title_bar;
	
	//wifi_check
	WifiManager wifimgr;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
      //titlebar
        customTitleSupport = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        setContentView(R.layout.main);
        
        if(customTitleSupport) {
        	customTitleBar_simple(getText(R.string.app_name).toString());
        }
        
        initialise_elmts();
        
        //setup wifi to ensure user connected to nus network
        wifimgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifi_info;
        
        String wifi_disabled = "This application requires a Wifi Connection to the NUS network. Please enable it in the Settings button.";
        String wifi_not_nus = "You are currently connected to Wifi but not to the NUS network. Please try again.";
        
        if(wifimgr.isWifiEnabled() == false){
        	CreateAlertDialog dialog = new CreateAlertDialog();
        	AlertDialog alert = dialog.newdialog(this, wifi_disabled);
        	alert.show();
        } else {
     
        	wifi_info = wifimgr.getConnectionInfo();
        	String wifi_ssid = wifi_info.getSSID();
        	
        	if(!wifi_ssid.equals("NUS")) {
        		CreateAlertDialog dialog = new CreateAlertDialog();
            	AlertDialog alert = dialog.newdialog(this, wifi_not_nus);
            	alert.show();
        	}
        }
    }
    
   	/*FUNCTION* =============================================================================//
	 *  CUSTOM TITLE BAR
	 */
	public void customTitleBar_simple(String title) {
			
		//setFeatureInt(int featureId, int value)
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_simple);
		
		title_bar = (TextView) findViewById(R.id.tv_title);
		title_bar.setText(title);
	}
    
    public void initialise_elmts() { 
    	searchButton = (Button)findViewById(R.id.b_main_search);
    	searchButton.setOnClickListener(this);
    	
    	dirButton = (Button)findViewById(R.id.b_main_dir);
    	dirButton.setOnClickListener(this);
    	
    	nearbyButton = (Button)findViewById(R.id.b_main_nearby);
    	nearbyButton.setOnClickListener(this);
    	
    	crowdButton = (Button)findViewById(R.id.b_main_crowd);
    	crowdButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.b_main_search:
				Intent open_search = new Intent(NUSFoodieActivity.this, SearchAct.class);
				startActivity(open_search);
				break;
				
			case R.id.b_main_dir:
				Intent open_dir = new Intent(NUSFoodieActivity.this, TitleBar.class);
				startActivity(open_dir);
				break;
				
			case R.id.b_main_nearby:
				Intent open_nearby = new Intent(NUSFoodieActivity.this, NearbyAct.class);
				startActivity(open_nearby);
				break;
				
			case R.id.b_main_crowd:
				Intent open_crowd = new Intent(NUSFoodieActivity.this, CrowdAct.class);
				startActivity(open_crowd);
				break;
		}
	}
}