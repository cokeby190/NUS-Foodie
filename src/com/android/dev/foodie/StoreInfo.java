package com.android.dev.foodie;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.sax.Element;
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
    
    //UI Elements
    TextView store_name, store_location, store_canteen;
    Button b_crowd;
    	//UI TABLET
    	TabHost tabs;
    	//UI NORMAL
    	Button b_info, b_review;
    
    //search information extracted from XmlAct
    String store_name_data, location, canteen_name;
    int store_info;
    int position;
    
    //XML parser objects
    ArrayList<HashMap<String, String>> menuItems;
    XmlFunction parser;
    NodeList nl;
    
    //loading bar
    long start = 0, stop = 0;
    
    //TabHost
    TextView tv_cuisine, tv_halal, tv_menu, tv_aircon; 
    TextView sch_wd, sch_we, vac_wd, vac_we, pubhol;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//-------------------------------START CUSTOM MENU SLIDER---------------------------------------------------------//
        //titlebar
        customTitleSupport = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        setContentView(R.layout.store);
        
        if(customTitleSupport) {
        	customTitleBar(getText(R.string.app_name).toString());
        } 
        
        //getting back returned data, passed from SearchAct
        store_info = getData();
        
//-------------------------------END CUSTOM MENU SLIDER---------------------------------------------------------//

        if((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
        	
        	layout_large();
        	
        } else if((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_NORMAL) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
        	
        	layout_normal();
        }
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
				//Intent send_nearby = new Intent(Store_Info.this, Store_Info.class);
				//startActivity(send_nearby);
				break;
			case 4:
				//Intent send_nearby = new Intent(Store_Info.this, Store_Info.class);
				//startActivity(send_nearby);
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
				Intent dialog = new Intent(StoreInfo.this, DialogAct.class);
				startActivity(dialog);
				break;
			
			case R.id.b_store_review:
				break;
			
			case R.id.b_store_crowd:
				break;
		}
	}
	
	/*FUNCTION* =============================================================================//
	 *  INITIALISE UI ELEMENTS
	 */
	private void initialise_large() {
		
		ImageView store_img = (ImageView) findViewById(R.id.iv_store);
		store_name = (TextView) findViewById(R.id.tv_store_name);
		store_location = (TextView) findViewById(R.id.tv_store_location);
		store_canteen = (TextView) findViewById(R.id.tv_store_cname);
		
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
		
	}
    
	/*FUNCTION* =============================================================================//
	 *  INITIALISE UI ELEMENTS
	 */
	private void initialise() {
		
		ImageView store_img = (ImageView) findViewById(R.id.iv_store);
		store_name = (TextView) findViewById(R.id.tv_store_name);
		store_location = (TextView) findViewById(R.id.tv_store_location);
		store_canteen = (TextView) findViewById(R.id.tv_store_cname);
		
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
    private int getData() {
		
    	Bundle getMessage = getIntent().getExtras();
		position = getMessage.getInt("position");
		
		menuItems =(ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("menuItems");
		
		return position;
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
        
        store_name.setText(menuItems.get(store_info).get(STORE_NAME));
    	store_location.setText(menuItems.get(store_info).get(LOCATION));
    	store_canteen.setText(menuItems.get(store_info).get(CANTEEN_NAME));
    	
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
        
        //TAB1 - INFORMATION TAB
	        String halal = "No don't have ):", menu = "No don't have ):", aircon = "No don't have ):";
	        
	        if(menuItems.get(store_info).get(HALAL).equals("Y")) {
	        	halal = "Yah lah!";
	        }
	        
	        if(menuItems.get(store_info).get(MENU).equals("Y")) {
	        	menu = "Yah lah!";
	        }
	        
	        if(menuItems.get(store_info).get(AIRCON).equals("Y")) {
	        	aircon = "Yah lah!";
	        }
	        
	        tv_cuisine.setText(menuItems.get(store_info).get(CUISINE));
	        tv_halal.setText(halal);
	        tv_menu.setText(menu);
	        tv_aircon.setText(aircon);
	        
	    //TAB2 - OPERATING HOURS TAB
	        String sch_wd_str = menuItems.get(store_info).get(AVAILABILITY_WEEKDAY),
	        		sch_we_str = menuItems.get(store_info).get(AVAILABILITY_WEEKEND), 
	        		vac_wd_str = menuItems.get(store_info).get(AVAILABILITY_VAC_WEEKDAY), 
	        		vac_we_str = menuItems.get(store_info).get(AVAILABILITY_VAC_WEEKEND), 
	        		pubhol_str = menuItems.get(store_info).get(AVAILABILITY_PUBHOL);
	        
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
	        
	        sch_wd.setText(sch_wd_str);
	        sch_we.setText(sch_we_str);
	        vac_wd.setText(vac_wd_str);
	        vac_we.setText(vac_we_str);
	        pubhol.setText(pubhol_str);
    }
    
    private void layout_normal() {
    	
    	//initialise UI elements 
        initialise(); 

        store_name.setText(menuItems.get(store_info).get(STORE_NAME));
    	store_location.setText(menuItems.get(store_info).get(LOCATION));
    	store_canteen.setText(menuItems.get(store_info).get(CANTEEN_NAME));
    	
    }
}