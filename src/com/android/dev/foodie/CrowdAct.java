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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

public class CrowdAct extends ListActivity implements OnClickListener, OnItemClickListener{
	
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
    static final String URL_base = "http://172.18.101.125:8080/crowd_api/metric?";
	
	// XML node keys
    static final String CROWD = "crowd";
    static final String LOCATION = "location";
    static final String CROWD_METRIC = "crowd_metric";
	
	//UI Elements
    ListView lv;
    ListAdapter filter_adapter;
    
    //OnItemClickListener class
    listener onclick_obj;
    
    //XML parser objects
    ArrayList<HashMap<String, String>> menuItems;
    XmlFunction parser;
    NodeList nl;
    
    //wifi_check
    WifiManager wifimgr;
    
    //loading bar
    long start = 0, stop = 0;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//-------------------------------START CUSTOM MENU SLIDER---------------------------------------------------------//
        //titlebar
        customTitleSupport = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        setContentView(R.layout.crowd);
        
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
     
        	//to store the list of stores to show as results
            menuItems = new ArrayList<HashMap<String, String>>();

            //creating new parser class
            parser = new XmlFunction();
            	
            parse_results(URL_base + "mode=Select");

    	    setListAdapter(filter_adapter);
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
		ArrayAdapter <String> adapter_sd = new ArrayAdapter <String> (CrowdAct.this, android.R.layout.simple_list_item_1, menu_list);
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
				Intent send_search = new Intent(CrowdAct.this, NUSFoodieActivity.class);
				startActivity(send_search);
				break;
			case 1:
				Intent send_dir = new Intent(CrowdAct.this, SearchAct.class);
				startActivity(send_dir);
				//Intent send_dir = new Intent(NearbyAct.this, NearbyAct.class);
				//startActivity(send_dir);
				break;
			case 2:
				//Intent send_crowd = new Intent(NearbyAct.this, SearchAct.class);
				//startActivity(send_crowd);
				break;
			case 3:
				Intent send_crowd = new Intent(CrowdAct.this, CrowdAct.class);
				startActivity(send_crowd);
				break;
			case 4:
				Intent send_nearby = new Intent(CrowdAct.this, NearbyAct.class);
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
		}
	}
	
	/*FUNCTION* =============================================================================//
	 *  INITIALISE UI ELEMENTS
	 */
	private void initialise() {
		
		lv = (ListView) findViewById(android.R.id.list);
        
        lv.setOnItemClickListener(onclick_obj);
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

			progress_bar = new ProgressDialog(CrowdAct.this);
			//set style
			progress_bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress_bar.setMessage("LOADING...");
			progress_bar.show();
			
			start = System.currentTimeMillis();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
	        
	        //within DoInBackground cannot change UI ELEMENTS 
	        	//so need to runOnUiThread function
	        runOnUiThread(new Runnable() {

				@Override
				public void run() {
					
					// looping through all <food_stall>'s
			        for (int i = 0; i < nl.getLength(); i++) {
			            // creating new HashMap
			            HashMap<String, String> map = new HashMap<String, String>();
			            Element e = (Element) nl.item(i);
			            // adding each child node to HashMap key => value
			            //hashmap.put(KEY, VALUE)
			            map.put(LOCATION, parser.getValue(e, LOCATION));
			            map.put(CROWD_METRIC, parser.getValue(e, CROWD_METRIC));
			            // adding HashList to ArrayList
			            menuItems.add(map);
			        }
				}
	        	
	        });
	        
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
			
	        //CONSTRUCTOR FOR SimpleAdapter
	        //SimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
	        	//takes another XML layout row_view.xml to populate the UI layout for 1 list item in the ListView
//	        filter_adapter = new SimpleAdapter(CrowdAct.this, menuItems, R.layout.crowd_view, 
//	        		new String[] { LOCATION }, new int[] {R.id.tv_crowd_canteen});
	        
	        filter_adapter = new CustomAdapterCrowd(CrowdAct.this, R.layout.crowd_view, menuItems);
			
			setListAdapter(filter_adapter);
		}


	}
	

	//function to parse XML results into ListView for User to view
	private void parse_results(String URL) {
		//String xml = parser.getXML(URL_base); // getting XML
        String xml = parser.getXML(URL); // getting XML
        Document doc = parser.XMLfromString(xml); // parsing XML to document so we can read it
 
        Toast.makeText(getApplicationContext(), URL, Toast.LENGTH_SHORT).show();
        
        //Returns a NodeList of all the Elements with a given tag name in the order in which 
        //they are encountered in a preorder traversal of the Document tree.
        nl = doc.getElementsByTagName(CROWD);
        
        new loadData().execute();

//        //CONSTRUCTOR FOR SimpleAdapter
//        //SimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
//        	//takes another XML layout row_view.xml to populate the UI layout for 1 list item in the ListView
//        filter_adapter = new SimpleAdapter(this, menuItems, R.layout.crowd_view, 
//        		new String[] { LOCATION }, new int[] {R.id.tv_crowd_canteen});
	}
}
