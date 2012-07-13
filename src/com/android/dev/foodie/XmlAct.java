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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

public class XmlAct extends ListActivity implements TextWatcher, OnClickListener, OnItemClickListener{

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
    ListView lv;
    ListAdapter filter_adapter;
    EditText filterText = null;
    TextView result_count;
    
    //search information extracted from SearchAct
    String getMsg, search_type;
    
    //OnItemClickListener class
    listener onclick_obj;
    
    //XML parser objects
    ArrayList<HashMap<String, String>> menuItems;
    XmlFunction parser;
    NodeList nl;
    
    //loading bar
    long start = 0, stop = 0;
    
    //wifi_check
    WifiManager wifimgr;
    WifiInfo wifi_info;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//-------------------------------START CUSTOM MENU SLIDER---------------------------------------------------------//
        //titlebar
        customTitleSupport = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        setContentView(R.layout.xml_display);
        
        if(customTitleSupport) {
        	customTitleBar(getText(R.string.app_name).toString());
        } 
//-------------------------------END CUSTOM MENU SLIDER---------------------------------------------------------//
        
        //initialise UI elements 
        initialise(); 
        
        //setup wifi to ensure user connected to nus network
        wifimgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        
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
        	} else {
        		
        		//getting back returned data, passed from SearchAct
                String[] search_string = getData();
                
                //to store the list of stores to show as results
                menuItems = new ArrayList<HashMap<String, String>>();

                //creating new parser class
                parser = new XmlFunction();
                
        //------BASIC SEARCH FUNCTION--------------------------------------------------------------------------//
                if(search_string[0].equals("basic")) {
                	
                	parse_results(URL_base + "search=basic&search_string=" + search_string[1]);
        	
        //------ADVANCED SEARCH FUNCTION--------------------------------------------------------------------------//
                } else if (search_string[0].equals("advanced")) {
                	
                	//checking if halal and aircon options are checked
                	String query_halal = "", query_aircon = "";
                	
                	if(!search_string[5].equals("")) {
                		query_halal = "&halal=" + search_string[5];
                	}
                	
                	if(!search_string[6].equals("")) {
                		query_aircon = "&aircon=" + search_string[6];
                	}
                	
                	//TOAST!
                	Toast t = Toast.makeText(getApplicationContext(), URL_base + "search=advanced&search_string=" + search_string[1] + 
        	        		"&location=" + search_string[2] + "&store_type=" + search_string[3] + "&cuisine=" + search_string[4] +
        	        		query_halal + query_aircon, Toast.LENGTH_LONG);
        	        t.show();
        	        
        	        //URL encoding white spaces to its ASCII equivalent
        	        search_string[1] = search_string[1].replace(" ", "%20");
        	        search_string[2] = search_string[2].replace(" ", "%20");
        	        search_string[3] = search_string[3].replace(" ", "%20");
        	        search_string[4] = search_string[4].replace(" ", "%20");
        	        
        	        parse_results(URL_base + "search=advanced&search_string=" + search_string[1] + 
        	        		"&location=" + search_string[2] + "&store_type=" + search_string[3] + "&cuisine=" + search_string[4] +
        	        		query_halal + query_aircon);
                }
        	        
        	    setListAdapter(filter_adapter);
        	}
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
		ArrayAdapter <String> adapter_sd = new ArrayAdapter <String> (XmlAct.this, android.R.layout.simple_list_item_1, menu_list);
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
				Intent send_search = new Intent(XmlAct.this, NUSFoodieActivity.class);
				startActivity(send_search);
				break;
			case 1:
				Intent send_dir = new Intent(XmlAct.this, SearchAct.class);
				startActivity(send_dir);
				//Intent send_dir = new Intent(XmlAct.this, XmlAct.class);
				//startActivity(send_dir);
				break;
			case 2:
				//Intent send_crowd = new Intent(XmlAct.this, SearchAct.class);
				//startActivity(send_crowd);
				break;
			case 3:
				//Intent send_nearby = new Intent(XmlAct.this, SearchAct.class);
				//startActivity(send_nearby);
				break;
			case 4:
				Intent send_nearby = new Intent(XmlAct.this, NearbyAct.class);
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
        result_count = (TextView) findViewById(R.id.tv_search_result_count);
 
        //for text filter
        filterText = (EditText) findViewById(R.id.search_box);
        filterText.addTextChangedListener(this);
        
        onclick_obj = new listener();
        
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(onclick_obj);
	}
    
    /*FUNCTION* =============================================================================//
	 *  FOR RECEIVING DATA FROM SEARCHACT THROUGH INTENT
	 */
    private String[] getData() {
		Bundle getMessage = getIntent().getExtras();
		search_type = getMessage.getString("search_type");
		getMsg = getMessage.getString("search_intent");
		
		//RETURN the string array according to what search is required
		if(search_type.equals("basic")) {
			String[] return_data = {search_type, getMsg};
			return return_data;
		} else if (search_type.equals("advanced")) {
			
			String fac = getMessage.getString("search_fac");
			String store = getMessage.getString("search_store");
			String cuisine = getMessage.getString("search_cuisine");
			
			String halal = getMessage.getString("search_halal");
			String aircon = getMessage.getString("search_aircon");
		
	        fac = process_default(fac);
			store = process_default(store);
			cuisine = process_default(cuisine);
			
			String[] return_data = {search_type, getMsg, fac, store, cuisine, halal, aircon};
			return return_data;
		}
		
		return null;	
	}
    
    /*FUNCTION* =============================================================================//
	 *  FOR DEFAULT OPTION PROCESSING
	 *  If default option is chosen, database should return results that DONT CARE that option
	 *  FUNCTION PARAMETER: process_default(String option)
	 *  	option: individual spinner selected string 
	 *  So if spinner option selected is default it should ignore the selection
	 */
    
    /**
     * est function name
     * @param option
     * @return String
     */
    private String process_default(String option) {
    	
    	if(option.equals("Select an Option")) {
    		option = "";
    	}
    	
    	return option;
    }
    
    //OnItemClickListener for list item clicked by User
    public class listener implements OnItemClickListener {

    	@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// Starting new intent
    		
    		int send_position = position;

    		Bundle sending = new Bundle();
    		sending.putInt("position", send_position);
            Intent in = new Intent(getApplicationContext(), StoreInfo.class);
            in.putExtras(sending);
            in.putExtra("menuItems", menuItems);
            startActivity(in);
		}	
    	
    }
    
    
    //for filter text
	public void afterTextChanged(Editable s) {
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		((SimpleAdapter) filter_adapter).getFilter().filter(s.toString());
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

			progress_bar = new ProgressDialog(XmlAct.this);
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
			            map.put(CANTEEN_NAME, parser.getValue(e, CANTEEN_NAME));
			            map.put(STORE_NAME, parser.getValue(e, STORE_NAME));
			            map.put(LOCATION, parser.getValue(e, LOCATION));
			            map.put(ROOM_CODE, parser.getValue(e, ROOM_CODE));
			            map.put(STORE_TYPE, parser.getValue(e, STORE_TYPE));
			            map.put(CUISINE, parser.getValue(e, CUISINE));
			            map.put(HALAL, parser.getValue(e, HALAL));
			            map.put(MENU, parser.getValue(e, MENU));
			            map.put(AIRCON, parser.getValue(e, AIRCON));
			            map.put(AVAILABILITY_WEEKDAY, parser.getValue(e, AVAILABILITY_WEEKDAY));
			            map.put(AVAILABILITY_WEEKEND, parser.getValue(e, AVAILABILITY_WEEKEND));
			            map.put(AVAILABILITY_VAC_WEEKDAY, parser.getValue(e, AVAILABILITY_VAC_WEEKDAY));
			            map.put(AVAILABILITY_VAC_WEEKEND, parser.getValue(e, AVAILABILITY_VAC_WEEKEND));
			            map.put(AVAILABILITY_PUBHOL, parser.getValue(e, AVAILABILITY_PUBHOL));
			            // adding HashList to ArrayList
			            menuItems.add(map);
			        }
					
					//int value to string
			        result_count.setText("Search returned " + String.valueOf(nl.getLength()) + " results. (;");
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

	}
	
	//function to parse XML results into ListView for User to view
	private void parse_results(String URL) {
		//String xml = parser.getXML(URL_base); // getting XML
        String xml = parser.getXML(URL); // getting XML
        Document doc = parser.XMLfromString(xml); // parsing XML to document so we can read it
 
        //Returns a NodeList of all the Elements with a given tag name in the order in which 
        //they are encountered in a preorder traversal of the Document tree.
        nl = doc.getElementsByTagName(FOOD_STALL);
        
        new loadData().execute();
        
        //CONSTRUCTOR FOR SimpleAdapter
        //SimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
        	//takes another XML layout row_view.xml to populate the UI layout for 1 list item in the ListView
        filter_adapter = new SimpleAdapter(this, menuItems, R.layout.row_view, 
        		new String[] { STORE_NAME, LOCATION, CANTEEN_NAME }, new int[] {R.id.textView1, R.id.textView2, R.id.textView3});
	}
}    