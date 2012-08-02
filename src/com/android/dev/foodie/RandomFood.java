package com.android.dev.foodie;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class RandomFood extends ListActivity implements OnClickListener, OnItemClickListener{

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
    static final String DIST = "dist"; 
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
    
    String[] location, canteen, store;
    
    //UI Elements
    ListView lv, lv2;
    //ListAdapter filter_adapter;
    CustomAdapter filter_adapter;
    Button randomB, randomB2;
    
    //search information extracted from SearchAct
    String getMsg, search_type;
    
    //OnItemClickListener class
    listener onclick_obj;
    
    //XML parser objects
    ArrayList<HashMap<String, String>> menuItems;
    
    XmlFunction parser, parser2;
    NodeList nl, nl2;
    
    //loading bar
    long start = 0, stop = 0;
    
    //wifi_check
    WifiManager wifimgr;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//-------------------------------START CUSTOM MENU SLIDER---------------------------------------------------------//
        //titlebar
        customTitleSupport = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        setContentView(R.layout.random_food);
        
        if(customTitleSupport) {
        	customTitleBar(getText(R.string.app_name).toString());
        } 
//-------------------------------END CUSTOM MENU SLIDER---------------------------------------------------------//
        
        //initialise UI elements 
        initialise(); 
        
        //setup wifi to ensure user connected to nus network
        wifimgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        
        String wifi_disabled = "This application requires a Wifi Connection to the NUS network. Please enable it in the Settings button.";
        
        if(wifimgr.isWifiEnabled() == false){
        	CreateAlertDialog dialog = new CreateAlertDialog();
        	AlertDialog alert = dialog.newdialog(this, wifi_disabled);
        	alert.show();
        } else {
     
        	parser = new XmlFunction();
            parser2 = new XmlFunction();

            parse_results(URL_base + "distinct=distinct&query_key=location", URL_base + "distinct=distinct&query_key=canteen_name");

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
		ArrayAdapter <String> adapter_sd = new ArrayAdapter <String> (RandomFood.this, android.R.layout.simple_list_item_1, menu_list);
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
				Intent send_search = new Intent(RandomFood.this, NUSFoodieActivity.class);
				startActivity(send_search);
				break;
			case 1:
				Intent send_dir = new Intent(RandomFood.this, SearchAct.class);
				startActivity(send_dir);
				//Intent send_dir = new Intent(RandomFood.this, RandomFood.class);
				//startActivity(send_dir);
				break;
			case 2:
				//Intent send_crowd = new Intent(RandomFood.this, SearchAct.class);
				//startActivity(send_crowd);
				break;
			case 3:
				//Intent send_nearby = new Intent(RandomFood.this, SearchAct.class);
				//startActivity(send_nearby);
				break;
			case 4:
				Intent send_nearby = new Intent(RandomFood.this, NearbyAct.class);
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
		
		case R.id.b_rand:
			
			lv.smoothScrollToPosition((int) Math.ceil(Math.random()*(location.length)));
			
			break;
			
		case R.id.b_rand_2:
			
			lv2.smoothScrollToPosition((int) Math.ceil(Math.random()*(canteen.length)));
			
			break;
	
		}
	}
	
	/*FUNCTION* =============================================================================//
	 *  INITIALISE UI ELEMENTS
	 */
	private void initialise() {
		
		lv = (ListView) findViewById(android.R.id.list);
		lv2 = (ListView) findViewById(R.id.lv_rand);
		randomB = (Button) findViewById(R.id.b_rand);
		randomB2 = (Button) findViewById(R.id.b_rand_2);
		
		lv.setVerticalFadingEdgeEnabled(true);
		
        onclick_obj = new listener();

        lv.setOnItemClickListener(onclick_obj);
        lv2.setOnItemClickListener(onclick_obj);
        
        randomB.setOnClickListener(this);
        randomB2.setOnClickListener(this);
	}
    
       
    //OnItemClickListener for list item clicked by User
    public class listener implements OnItemClickListener {

    	@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// Starting new intent
    		
//    		int send_position = position;
//
//    		Bundle sending = new Bundle();
//    		sending.putInt("position", send_position);
//            Intent in = new Intent(getApplicationContext(), StoreInfo.class);
//            in.putExtras(sending);
//            in.putExtra("menuItems", menuItems);
//            startActivity(in);
		}	
    	
    }
	
	/*FUNCTION* =============================================================================//
	 *  FOR loading progress bar while results are loading
	 *  PreExecute : display progress bar
	 *  DoInBackground : perform the task that is time consuming and hence requires AsyncTask
	 */
	//extended AsyncTask class
			//<String, Integer, String>
			//1st - what is passed in, since we pass in nothing
			//2nd - for Progress / Update bar
			//3rd - what we are returning, which is also a void...
	private class loadData extends AsyncTask <Void, Integer, Void>{
	//private class loadSpinner extends AsyncTask <Void, Integer, Void>{
		
		ProgressDialog progress_bar;
		
		// will be called first
		protected void onPreExecute() {
			//setting up variables, initialising, etc

			progress_bar = new ProgressDialog(RandomFood.this);
			//set style
			progress_bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress_bar.setMessage("LOADING...");
			progress_bar.show();
			
			start = System.currentTimeMillis();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
					
			location = new String[nl.getLength()];
			canteen = new String[nl2.getLength()];

			// looping through all <food_stall>'s
	        for (int i = 0; i < nl.getLength(); i++) {
	            Element e = (Element) nl.item(i);

	            location[i] = parser.getValue(e, LOCATION);
	        }
	        
	        // looping through all <food_stall>'s
	        for (int j = 0; j < nl2.getLength(); j++) {
	            Element e = (Element) nl2.item(j);
	            
	            canteen[j] = parser2.getValue(e, CANTEEN_NAME);
	        }

	        
			//------END OF time consuming task---------------------------------------//
			
			stop = System.currentTimeMillis();
			
			
			// if duration of loading is too fast, sleep for a while
			long period = stop - start;
			
			if (period < 500) {

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
					
			}
			//then dismiss the loading dialog once it has completed
			progress_bar.dismiss();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			ArrayAdapter <String> list_adapter = new ArrayAdapter <String>(RandomFood.this, android.R.layout.simple_list_item_1, location);
    		lv.setAdapter(list_adapter);
    		
    		ArrayAdapter <String> list_adapter_canteen = new ArrayAdapter <String>(RandomFood.this, android.R.layout.simple_list_item_1, canteen);
    		lv2.setAdapter(list_adapter_canteen);
		}

	}
	
	//function to parse XML results into ListView for User to view
	private void parse_results(String URL, String URL2) {
		
		//String xml = parser.getXML(URL_base); // getting XML
        String xml = parser.getXML(URL); // getting XML
        Document doc = parser.XMLfromString(xml); // parsing XML to document so we can read it
 
        //Returns a NodeList of all the Elements with a given tag name in the order in which 
        //they are encountered in a preorder traversal of the Document tree.

        nl = doc.getElementsByTagName("food_list");
        
        String xml2 = parser2.getXML(URL2); // getting XML
        Document doc2 = parser2.XMLfromString(xml2); // parsing XML to document so we can read it
        nl2 = doc2.getElementsByTagName("food_list");
        
        new loadData().execute();
	}

}    