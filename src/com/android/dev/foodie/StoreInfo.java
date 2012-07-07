package com.android.dev.foodie;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
    
    //UI Elements
    TextView store_name, store_location, store_canteen;
    TabHost tabs;
    
    //search information extracted from XmlAct
    String store_name_data, location, canteen_name;
    String[] store_info;
    
    //XML parser objects
    ArrayList<HashMap<String, String>> menuItems;
    XmlFunction parser;
    NodeList nl;
    
    //loading bar
    long start = 0, stop = 0;
    
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
//-------------------------------END CUSTOM MENU SLIDER---------------------------------------------------------//
        
        //initialise UI elements 
        initialise(); 
     
        //getting back returned data, passed from SearchAct
        store_info = getData();
        
        store_name.setText(store_info[0]);
    	store_location.setText(store_info[1]);
    	store_canteen.setText(store_info[2]);
        
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
				//Intent send_dir = new Intent(Store_Info.this, Store_Info.class);
				//startActivity(send_dir);
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
		}
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
		tabs = (TabHost)findViewById(R.id.tabhost);
	}
    
    /*FUNCTION* =============================================================================//
	 *  FOR RECEIVING DATA FROM SEARCHACT THROUGH INTENT
	 */
    private String[] getData() {
		
    	Bundle getMessage = getIntent().getExtras();
		store_name_data = getMessage.getString("store_name");
		location = getMessage.getString("location");
		canteen_name = getMessage.getString("canteen_name");
		
		String[] return_data = {store_name_data, location, canteen_name};
		
		return return_data;
	}
    
	
//	/*FUNCTION* =============================================================================//
//	 *  FOR loading progress bar while results are loading
//	 *  PreExecute : display progress bar
//	 *  DoInBackground : perform the task that is time consuming and hence requires AsyncTask
//	 */
//	//extended AsyncTask class
//			//<String, Integer, String>
//			//1st - what is passed in, since we pass in nothing
//			//2nd - for Progress / Update bar
//			//3rd - what we are returning, which is also a void...
//	private class loadData extends AsyncTask <Void, Integer, Void>{
//	//private class loadSpinner extends AsyncTask <Void, Integer, Void>{
//		
//		ProgressDialog progress_bar;
//		
//		// will be called first
//		protected void onPreExecute() {
//			//setting up variables, initialising, etc
//
//			progress_bar = new ProgressDialog(Store_Info.this);
//			//set style
//			progress_bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//			progress_bar.setMessage("LOADING...");
//			progress_bar.show();
//			
//			start = System.currentTimeMillis();
//		}
//		
//		@Override
//		protected Void doInBackground(Void... arg0) {
//			
//			//------Do the time consuming task---------------------------------------//
//
//	        // looping through all <food_stall>'s
//	        for (int i = 0; i < nl.getLength(); i++) {
//	            // creating new HashMap
//	            HashMap<String, String> map = new HashMap<String, String>();
//	            Element e = (Element) nl.item(i);
//	            // adding each child node to HashMap key => value
//	            //hashmap.put(KEY, VALUE)
//	            map.put(CANTEEN_NAME, parser.getValue(e, CANTEEN_NAME));
//	            map.put(STORE_NAME, parser.getValue(e, STORE_NAME));
//	            map.put(LOCATION, parser.getValue(e, LOCATION));
//	            map.put(ROOM_CODE, parser.getValue(e, ROOM_CODE));
//	            map.put(STORE_TYPE, parser.getValue(e, STORE_TYPE));
//	            map.put(CUISINE, parser.getValue(e, CUISINE));
//	            map.put(HALAL, parser.getValue(e, HALAL));
//	            map.put(AIRCON, parser.getValue(e, AIRCON));
//	            // adding HashList to ArrayList
//	            menuItems.add(map);
//	        }
//	        
//	        //within DoInBackground cannot change UI ELEMENTS 
//	        	//so need to runOnUiThread function
//	        runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					
//				}
//	        	
//	        });
//	        
//			//------END OF time consuming task---------------------------------------//
//			
//			stop = System.currentTimeMillis();
//			
//			
//			// if duration of loading is too fast, sleep for a while
//			long period = stop - start;
//			
//			if (period < 500) {
//
//				try {
//					Thread.sleep(600);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//					
//			}
//			//then dismiss the loading dialog once it has completed
//			progress_bar.dismiss();
//
//			return null;
//		}
//
//	}
}    