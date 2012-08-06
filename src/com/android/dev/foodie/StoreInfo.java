package com.android.dev.foodie;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.NodeList;

import sg.edu.nus.ami.wifilocation.APLocation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SlidingDrawer;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class StoreInfo extends Activity implements OnClickListener, OnItemClickListener{

//-------------------------------START CUSTOM MENU SLIDER-------------------------------------------------//
	//sliding drawer
	private SlidingDrawer sd;
	private ListView sd_content;
	//titlebar
	boolean customTitleSupport = true;
	TextView title_bar;
	Button menu;
	//menu list
	private String[] menu_list = {"Home", "Search" , "Directory" , "Crowd" , "Nearby"};
//-------------------------------END CUSTOM MENU SLIDER-------------------------------------------------//	
	
	//stating the BASE URL
    static final String URL_base = "http://172.18.101.125:8080/wte/wte?";
    
    // XML node keys
    static final String DIST = "dist";
    static final String FOOD_STALL = "food_stall"; // parent node
    static final String CANTEEN_NAME = "canteen_name";
    static final String STORE_NAME = "store_name";
    static final String LOCATION = "location";
    static final String ROOM_CODE = "room_code";
    static final String STORE_TYPE = "store_type";
    static final String CUISINE = "cuisine";
    static final String HALAL = "halal";
    static final String MENU = "menu";
    static final String AIRCON = "aircon";
    static final String AVAILABILITY_WEEKDAY = "availability_weekday";
    static final String AVAILABILITY_WEEKEND = "availability_weekend";
    static final String AVAILABILITY_VAC_WEEKDAY = "availability_vac_weekday";
    static final String AVAILABILITY_VAC_WEEKEND = "availability_vac_weekend";
    static final String AVAILABILITY_PUBHOL = "availability_pubhol";
    static final String IMG_PATH = "img_path";
    static final String CAM_NO = "cam_no";
    
    //UI Elements
    TextView store_name, store_location, store_canteen, store_dist;
    Button b_crowd, button2;
    ImageView store_img;
    GetImage show_img;
    	//UI TABLET
    	TabHost tabs;
    	//UI NORMAL
    	Button b_info, b_review;
    
    //search information extracted from XmlAct
    String store_name_data, location, canteen_name;
    
    //XML parser objects
    HashMap<String, String> menuItems;
    XmlFunction parser;
    NodeList nl;
    
    //loading bar
    long start = 0, stop = 0;
    
    //TabHost
    TextView tv_cuisine, tv_halal, tv_menu, tv_aircon; 
    TextView sch_wd, sch_we, vac_wd, vac_we, pubhol;
    
    //String for xml data
    String halal_str = "No don't have ):", menu_str = "No don't have ):", aircon_str = "No don't have ):";
    String sch_wd_str, sch_we_str, vac_wd_str, vac_we_str, pubhol_str;
    
    //for Service
  	receive_service msg_receive;
  	boolean receiver_register = false;
    
    //Toasts
    Toast show_location, show_lat, show_long;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
//-------------------------------START CUSTOM MENU SLIDER---------------------------------------------------------//
        //titlebar
        customTitleSupport = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        setContentView(R.layout.store);
        
        if(customTitleSupport) {
        	customTitleBar(getText(R.string.app_name).toString());
        } 
        
        //getting back returned data, passed from SearchAct
        getData();
        
//-------------------------------END CUSTOM MENU SLIDER---------------------------------------------------------//
        
        if((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
        	
        	layout_large();
        	
        } else if((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_NORMAL) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
        	
        	layout_normal();
        }
        
        //TEST
        msg_receive = new receive_service();
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceLocation.BROADCAST_ACTION);
        registerReceiver(msg_receive, filter);
       
        receiver_register = true;
    }
    
   	/*FUNCTION* =============================================================================//
	 *  CUSTOM TITLE BAR
	 */
	public void customTitleBar(String title) {
			
		//setFeatureInt(int featureId, int value)
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		
		title_bar = (TextView) findViewById(R.id.tv_title);
		menu = (Button) findViewById(R.id.b_menu);
		
		title_bar.setText(title);
		menu.setOnClickListener(this);
		
		//sliding drawer initialisation
		sd = (SlidingDrawer) findViewById(R.id.slidingDrawer1);
		sd_content = (ListView) findViewById(R.id.sd_list);
		//list for menu
			//ArrayAdapter constructor : ArrayAdapter <String> (Context, int layout, String list)
		ArrayAdapter <String> adapter_sd = new ArrayAdapter <String> (StoreInfo.this, android.R.layout.simple_list_item_1, menu_list);
		sd_content.setAdapter(adapter_sd);
		sd_content.setOnItemClickListener(this);
	}
	
	/*FUNCTION* =============================================================================//
	 *  FOR MENU FUNCTION
	 *  FUNCTION PARAMETER : onItemClick (AdapterView<?> parent, View view, int position, long id)
	 *  CRITERIA : OnItemClickListener
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch(position) {
			case 0:
				Intent send_search = new Intent(StoreInfo.this, NUSFoodieActivity.class);
				startActivity(send_search);
				break;
			case 1:
				Intent send_dir = new Intent(StoreInfo.this, SearchAct.class);
				startActivity(send_dir);
				break;
			case 2:
				//Intent send_crowd = new Intent(Store_Info.this, Store_Info.class);
				//startActivity(send_crowd);
				break;
			case 3:
				Intent send_crowd = new Intent(StoreInfo.this, CrowdAct.class);
				startActivity(send_crowd);
				break;
			case 4:
				Intent send_nearby = new Intent(StoreInfo.this, NearbyAct.class);
				startActivity(send_nearby);
				break;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		//------button to toggle menu slide open/close---------------------------------//
			case R.id.b_menu:
				sd.animateToggle();
				break;
				
			case R.id.b_store_info:
				//send intent to dialog to show data
		        Intent in = new Intent(getApplicationContext(), DialogAct.class);
		        in.putExtra("menuItems", menuItems);
		        startActivity(in);
				break;
			
			case R.id.b_store_review:
				//TESTING ONLY!
				//CLOSE THREAD
				Log.v("STOP SERVICE", "button pressed to stop service");
				stopService();				 
				Toast.makeText(getApplicationContext(), "Stop Service button clicked!", Toast.LENGTH_LONG).show();
				unregisterReceiver(msg_receive);
				break;
			
			case R.id.b_store_crowd:
				//send intent to dialog to show data
		        Intent snap_shot = new Intent(getApplicationContext(), SnapShot.class);
		        snap_shot.putExtra("cam_no", menuItems.get(CAM_NO));
		        startActivity(snap_shot);
				break;
				
			case R.id.button2: 
				//TESTING ONLY!
				//CLOSE THREAD
				Log.v("STOP SERVICE", "button pressed to stop service");
				stopService();
				Toast.makeText(getApplicationContext(), "Stop Service button clicked!", Toast.LENGTH_LONG).show();
				unregisterReceiver(msg_receive);
				break;
		}
	}
	
	/*FUNCTION* =============================================================================//
	 *  INITIALISE UI ELEMENTS
	 */
	private void initialise_large() {
		
		store_img = (ImageView) findViewById(R.id.iv_store);
		store_name = (TextView) findViewById(R.id.tv_store_name);
		store_location = (TextView) findViewById(R.id.tv_store_location);
		store_canteen = (TextView) findViewById(R.id.tv_store_cname);
		store_dist = (TextView) findViewById(R.id.tv_store_nearby);
		
		RatingBar rating = (RatingBar) findViewById(R.id.ratingBar1);
		
		tabs = (TabHost)findViewById(R.id.th_store);
		
		//tab 1
		tv_cuisine = (TextView) findViewById(R.id.tv_store_cuisine);
		tv_halal = (TextView) findViewById(R.id.tv_store_halal);
		tv_menu = (TextView) findViewById(R.id.tv_store_menu);
		tv_aircon = (TextView) findViewById(R.id.tv_store_aircon);
		
		//tab 2
		sch_wd = (TextView) findViewById(R.id.tv_store_sch_wd);
		sch_we = (TextView) findViewById(R.id.tv_store_sch_we);
		vac_wd = (TextView) findViewById(R.id.tv_store_vac_wd);
		vac_we = (TextView) findViewById(R.id.tv_store_vac_we);
		pubhol = (TextView) findViewById(R.id.tv_store_ph);
		
		b_crowd = (Button) findViewById(R.id.b_store_crowd);
		
		b_crowd.setOnClickListener(this);
		
		button2 = (Button) findViewById(R.id.button2);

		button2.setOnClickListener(this);
		
	}
    
	/*FUNCTION* =============================================================================//
	 *  INITIALISE UI ELEMENTS
	 */
	private void initialise() {
		
		store_img = (ImageView) findViewById(R.id.iv_store);
		store_name = (TextView) findViewById(R.id.tv_store_name);
		store_location = (TextView) findViewById(R.id.tv_store_location);
		store_canteen = (TextView) findViewById(R.id.tv_store_cname);
		store_dist = (TextView) findViewById(R.id.tv_store_nearby);
		
		RatingBar rating = (RatingBar) findViewById(R.id.ratingBar1);
		
		b_info = (Button) findViewById(R.id.b_store_info);
		b_review = (Button) findViewById(R.id.b_store_review);
		b_crowd = (Button) findViewById(R.id.b_store_crowd);
		
		b_info.setOnClickListener(this);
		b_review.setOnClickListener(this);
		b_crowd.setOnClickListener(this);
		
	}
	
    /*FUNCTION* =============================================================================//
	 *  FOR RECEIVING DATA FROM SEARCHACT THROUGH INTENT
	 */
    private void getData() {

		menuItems = (HashMap<String, String>) getIntent().getSerializableExtra("menuItems");
		
	}  
    
    private boolean parse_operating(String hours) {
    	
    	String[] avail = hours.split("-");
    	String[] first = avail[0].split(":");
    	String[] second = avail[1].split(":");
    	
    	if(first[0].equals("00") && second[0].equals("00") 
    			&& first[1].equals("00") && second[1].equals("00")) {
    		return false;
    	}
    	
    	return true;
    }
    
    private void layout_large() {
    	
    	//initialise UI elements 
        initialise_large(); 
        
        store_name.setText(menuItems.get(STORE_NAME));
    	store_location.setText(menuItems.get(LOCATION));
    	store_canteen.setText(menuItems.get(CANTEEN_NAME));
    	
		try {
			InputStream in;
			in = new java.net.URL(menuItems.get(IMG_PATH)).openStream();
			byte [] content = convertInputStreamToByteArray(in);
			Bitmap bmp = BitmapFactory.decodeByteArray(content, 0, content.length);
			store_img.setImageBitmap(bmp);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	if(!menuItems.get(DIST).equals(null)) 
    		store_dist.setText(menuItems.get(DIST));
    	else 
    		store_dist.setText("Dist Unknown");
    	
    	//------Setup tabs--------------------------------------------------------------------------//
        tabs.setup();  

        TabSpec tab_one = tabs.newTabSpec("info_tab"); 
        tab_one.setContent(R.id.info_tab); 
        tab_one.setIndicator("Information"); 
        tabs.addTab(tab_one);  

        TabSpec tab_two = tabs.newTabSpec("operating_tab");
        tab_two.setContent(R.id.operating_tab);
        tab_two.setIndicator("Operating Hours"); 
        tabs.addTab(tab_two);
        
        TabSpec tab_three = tabs.newTabSpec("reviews_tab");
        tab_three.setContent(R.id.review_tab);
        tab_three.setIndicator("Reviews"); 
        tabs.addTab(tab_three);
        
      //BOOLEAN STORE INFO
        if(menuItems.get(HALAL).equals("Y")) {
        	halal_str = "Yah lah!";
        }
        
        if(menuItems.get(MENU).equals("Y")) {
        	menu_str = "Yah lah!";
        }
        
        if(menuItems.get(AIRCON).equals("Y")) {
        	aircon_str = "Yah lah!";
        }
        
        sch_wd_str = menuItems.get(AVAILABILITY_WEEKDAY);
		sch_we_str = menuItems.get(AVAILABILITY_WEEKEND); 
		vac_wd_str = menuItems.get(AVAILABILITY_VAC_WEEKDAY); 
		vac_we_str = menuItems.get(AVAILABILITY_VAC_WEEKEND); 
		pubhol_str = menuItems.get(AVAILABILITY_PUBHOL);
        
        //OPERATING HOURS
        if(!parse_operating(sch_wd_str)) {
        	sch_wd_str = "Not Operating";
        }
        
        if(!parse_operating(sch_we_str)) {
        	sch_we_str = "Not Operating";
        }
        
        if(!parse_operating(vac_wd_str)) {
        	vac_wd_str = "Not Operating";
        }
        
        if(!parse_operating(vac_we_str)) {
        	vac_we_str = "Not Operating";
        }
        
        if(!parse_operating(pubhol_str)) {
        	pubhol_str = "Not Operating";
        }
        
        //TAB1 - INFORMATION TAB
	        	        
	        tv_cuisine.setText(menuItems.get(CUISINE));
	        tv_halal.setText(halal_str);
	        tv_menu.setText(menu_str);
	        tv_aircon.setText(aircon_str);
	        
	    //TAB2 - OPERATING HOURS TAB
	        
	        sch_wd.setText(sch_wd_str);
	        sch_we.setText(sch_we_str);
	        vac_wd.setText(vac_wd_str);
	        vac_we.setText(vac_we_str);
	        pubhol.setText(pubhol_str);
    }
    
    private void layout_normal() {
    	
    	//initialise UI elements 
        initialise(); 

        store_name.setText(menuItems.get(STORE_NAME));
    	store_location.setText(menuItems.get(LOCATION));
    	store_canteen.setText(menuItems.get(CANTEEN_NAME));
    	
    	try {
			InputStream in;
			in = new java.net.URL(menuItems.get(IMG_PATH)).openStream();
			byte [] content = convertInputStreamToByteArray(in);
			Bitmap bmp = BitmapFactory.decodeByteArray(content, 0, content.length);
			store_img.setImageBitmap(bmp);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	if(!menuItems.get(DIST).equals(null)) 
    		store_dist.setText(menuItems.get(DIST));
    	else 
    		store_dist.setText("Dist Unknown");
    }
    
    //TEST
    public class receive_service extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			
			APLocation return_location = new APLocation();
			
			//if(action.equals("LOCATION")) {
				Bundle extra = intent.getExtras();
				String location = extra.getString("ap_location");
				location = location.replace("APLocation [", "");
				location = location.replace("]", "");
				
				//PARSE string from APLocation object retrieved
				String[] location_split = location.split(", ");
				for(int i=0; i< location_split.length; i++) {
					int end  = location_split[i].length();
					int index = location_split[i].indexOf("=");
										if(location_split[i].substring(0, index).equals("building")) {
						return_location.setBuilding(location_split[i].substring(index+1, end));
					}
					if(location_split[i].substring(0, index).equals("ap_name")) {
						return_location.setAp_name(location_split[i].substring(index+1, end));
					}
					if(location_split[i].substring(0, index).equals("ap_location")) {
						return_location.setAp_location(location_split[i].substring(index+1, end));
					}
					if(location_split[i].substring(0, index).equals("accuracy")) {
						return_location.setAccuracy(Double.valueOf(location_split[i].substring(index+1, end)));
					}
					if(location_split[i].substring(0, index).equals("ap_lat")) {
						return_location.setAp_lat(Double.valueOf(location_split[i].substring(index+1, end)));
					}
					if(location_split[i].substring(0, index).equals("ap_long")) {
						return_location.setAp_long(Double.valueOf(location_split[i].substring(index+1, end)));
					}
				}
				
				Log.v("RETURN_MSG", return_location.getAp_location());
				show_location = Toast.makeText(getApplicationContext(), "I am at : " + return_location.getAp_location(), Toast.LENGTH_SHORT);
				show_location.show();
				
				Log.v("RETURN_MSG LAT", String.valueOf(return_location.getAp_lat()));
				show_lat = Toast.makeText(getApplicationContext(), "Current Lat : " + return_location.getAp_lat(), Toast.LENGTH_SHORT);
				show_lat.show();
				Log.v("RETURN_MSG LONG", String.valueOf(return_location.getAp_long()));
				show_long = Toast.makeText(getApplicationContext(), "Current Long : " + return_location.getAp_long(), Toast.LENGTH_SHORT);
				show_long.show();
			//}
		}
		
	}
    
  //CLOSE SERVICE
  	public void stopService() {
  		if(stopService(new Intent(StoreInfo.this, ServiceLocation.class)))
  			Toast.makeText(this, "stopService success", Toast.LENGTH_LONG);
  		else
  			Toast.makeText(this, "stopService unsuccess", Toast.LENGTH_LONG);
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