package com.android.dev.foodie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sg.edu.nus.ami.wifilocation.APLocation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class SearchAct extends Activity implements OnClickListener, OnItemSelectedListener, OnItemClickListener{
	
//
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
    static final String FOOD_LIST = "food_list"; 
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
	
//-------------------------------START SPINNER---------------------------------------------------------//
    //list for spinner
    private List <String> fac_list = new ArrayList<String>();
    private List <String> store_list = new ArrayList<String>();
    private List <String> cuisine_list = new ArrayList<String>();
    private String[] range_list = {"Range", "200m", "500m", "1000m", "2000m"};
    
	private TabHost tabs;
	private Spinner fac, store, cuisine, range;
	private ImageButton search;
	private Button search_adv;
	private EditText et_search;
	private CheckBox halal, aircon, cb_range;
	
	//SPINNER SELECTION POSITION 
	private int fac_pos, store_pos, cuisine_pos, range_pos;
	private ArrayAdapter <String> adapter_fac, adapter_store, adapter_cuisine, adapter_range;
//-------------------------------END SPINNER-----------------------------------------------------------//
	
	//for Service
	receive_service msg_receive;
	boolean receiver_register = false;
	double ap_lat = 1.296469, ap_lon = 103.776373;
	
	private Thread thread;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

      //-------------------------------START CUSTOM MENU SLIDER---------------------------------------------------------//
        //titlebar
        customTitleSupport = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        setContentView(R.layout.search_page);
        
        if(customTitleSupport) {
        	customTitleBar(getText(R.string.app_name).toString());
        } 
      //-------------------------------END CUSTOM MENU SLIDER---------------------------------------------------------//
        
        //initialise UI elements 
        initialise();  
        
//------Setup tabs--------------------------------------------------------------------------//
        tabs.setup();  

        TabSpec tab_one = tabs.newTabSpec("search_tab"); 
        tab_one.setContent(R.id.search_tab); 
        tab_one.setIndicator("Search"); 
        tabs.addTab(tab_one);  

        TabSpec tab_two = tabs.newTabSpec("advanced_search_tab");
        tab_two.setContent(R.id.adv_search_tab);
        tab_two.setIndicator("Advanced Search"); 
        tabs.addTab(tab_two);
        
//        //Call service to start
//        int counter = 1;
//        Intent intent = new Intent(this, ServiceLocation.class);
//        intent.putExtra("counter", counter++);
//        startService(intent);

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
		ArrayAdapter <String> adapter_sd = new ArrayAdapter <String> (SearchAct.this, android.R.layout.simple_list_item_1, menu_list);
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
				Intent send_search = new Intent(SearchAct.this, NUSFoodieActivity.class);
				startActivity(send_search);
				break;
			case 1:
				Intent send_dir = new Intent(SearchAct.this, SearchAct.class);
				startActivity(send_dir);
				//Intent send_dir = new Intent(SearchAct.this, SearchAct.class);
				//startActivity(send_dir);
				break;
			case 2:
				//Intent send_crowd = new Intent(SearchAct.this, SearchAct.class);
				//startActivity(send_crowd);
				break;
			case 3:
				//Intent send_nearby = new Intent(SearchAct.this, SearchAct.class);
				//startActivity(send_nearby);
				break;
			case 4:
				Intent send_nearby = new Intent(SearchAct.this, NearbyAct.class);
				startActivity(send_nearby);
				break;
		}
	}
	

//------SEARCH TAB--------------------------------------------------------------------------//
	
	/*FUNCTION* =============================================================================//
	 *  INITIALISE UI ELEMENTS
	 */
	private void initialise() {
		tabs = (TabHost)findViewById(R.id.tabhost);
		
		search = (ImageButton)findViewById(R.id.ib_search_basic);
		search_adv = (Button)findViewById(R.id.b_search_adv);
		
		search.setOnClickListener(this);
		search_adv.setOnClickListener(this);
		
		et_search = (EditText) findViewById(R.id.et_search_bar);
		
		range = (Spinner)findViewById(R.id.sp_search_range);
		
		cb_range = (CheckBox) findViewById(R.id.CheckBox01);
		
		//make first option invisible after spinner is selected by User
		adapter_range = new ArrayAdapter <String> (SearchAct.this, android.R.layout.simple_spinner_item, range_list) {
			@Override
		    public View getDropDownView(int position, View convertView, ViewGroup parent)
		    {
		        View v = null;

		        // If this is the initial dummy entry, make it hidden
		        if (position == 0) {
		            TextView tv = new TextView(getContext());
		            tv.setHeight(0);
		            tv.setVisibility(View.GONE);
		            v = tv;
		        }
		        else {
		            // Pass convertView as null to prevent reuse of special case views
		            v = super.getDropDownView(position, null, parent);
		        }

		        // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling 
		        parent.setVerticalScrollBarEnabled(false);
		        return v;
		    }
		};
		adapter_range.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		range.setAdapter(adapter_range);

		//ADVANCED
			fac = (Spinner)findViewById(R.id.sp_search_adv_fac);
			store = (Spinner)findViewById(R.id.sp_search_adv_store);
			cuisine = (Spinner)findViewById(R.id.sp_search_adv_cuisine);

			halal = (CheckBox) findViewById(R.id.cb_search_adv_halal);
			aircon = (CheckBox) findViewById(R.id.cb_search_adv_aircon);
			
			//make first option invisible after spinner is selected by User
			adapter_fac = new ArrayAdapter <String> (SearchAct.this, android.R.layout.simple_spinner_item, populate_spinner((URL_base + "distinct=distinct&query_key=location"), fac_list, "location")) {
				
				@Override
			    public View getDropDownView(int position, View convertView, ViewGroup parent)
			    {
			        View v = null;

			        // If this is the initial dummy entry, make it hidden
			        if (position == 0) {
			            TextView tv = new TextView(getContext());
			            tv.setHeight(0);
			            tv.setVisibility(View.GONE);
			            v = tv;
			        }
			        else {
			            // Pass convertView as null to prevent reuse of special case views
			            v = super.getDropDownView(position, null, parent);
			        }

			        // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling 
			        parent.setVerticalScrollBarEnabled(false);
			        return v;
			    }
			}; 
			adapter_fac.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			fac.setAdapter(adapter_fac);
			
			adapter_store = new ArrayAdapter <String> (SearchAct.this, android.R.layout.simple_spinner_item, populate_spinner((URL_base + "distinct=distinct&query_key=store_type"), store_list, "store_type")) {
				
				@Override
			    public View getDropDownView(int position, View convertView, ViewGroup parent)
			    {
			        View v = null;

			        // If this is the initial dummy entry, make it hidden
			        if (position == 0) {
			            TextView tv = new TextView(getContext());
			            tv.setHeight(0);
			            tv.setVisibility(View.GONE);
			            v = tv;
			        }
			        else {
			            // Pass convertView as null to prevent reuse of special case views
			            v = super.getDropDownView(position, null, parent);
			        }

			        // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling 
			        parent.setVerticalScrollBarEnabled(false);
			        return v;
			    }
			}; 
			adapter_store.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
			store.setAdapter(adapter_store);
			
			adapter_cuisine = new ArrayAdapter <String> (SearchAct.this, android.R.layout.simple_spinner_item, populate_spinner((URL_base + "distinct=distinct&query_key=cuisine"), cuisine_list, "cuisine")) {
				
				@Override
			    public View getDropDownView(int position, View convertView, ViewGroup parent)
			    {
			        View v = null;

			        // If this is the initial dummy entry, make it hidden
			        if (position == 0) {
			            TextView tv = new TextView(getContext());
			            tv.setHeight(0);
			            tv.setVisibility(View.GONE);
			            v = tv;
			        }
			        else {
			            // Pass convertView as null to prevent reuse of special case views
			            v = super.getDropDownView(position, null, parent);
			        }

			        // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling 
			        parent.setVerticalScrollBarEnabled(false);
			        return v;
			    }
			}; 
			adapter_cuisine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
			cuisine.setAdapter(adapter_cuisine);
			
			fac.setOnItemSelectedListener(this);
			store.setOnItemSelectedListener(this);
			cuisine.setOnItemSelectedListener(this);
			range.setOnItemSelectedListener(this);
	}
	
	
	//CLOSE SERVICE
	public void stopService() {
		if(stopService(new Intent(SearchAct.this, ServiceLocation.class)))
			Toast.makeText(this, "stopService success", Toast.LENGTH_LONG);
		else
			Toast.makeText(this, "stopService unsuccess", Toast.LENGTH_LONG);
	}
	
	/*FUNCTION* =============================================================================//
	 *  ONCLICK FUNCTION
	 *  CRITERIA : OnClickListener
	 */
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		
			//SEARCH BASIC
			case R.id.ib_search_basic:
				
				if(!cb_range.isChecked()) {
			//------send intent to results page with query---------------------------------//
			//------bundle search query with intent----------------------------------------//
					String message = et_search.getText().toString();
					Bundle sending = new Bundle();
					sending.putString("search_type", "basic");
					sending.putString("search_intent", message);
					Intent send_intent = new Intent(SearchAct.this, XmlAct.class);
					send_intent.putExtras(sending);
					startActivity(send_intent);
					
				} else if (cb_range.isChecked()) {
					
					//Call service to start
			        int counter = 1;
			        Intent intent = new Intent(this, ServiceLocation.class);
			        intent.putExtra("counter", counter++);
			        startService(intent);
			        
			        //sleep for 3 seconds
			        thread =  new Thread(){
			            @Override
			            public void run(){
			                try {
			                    synchronized(this){
			                        wait(3000);
			                    }
			                }
			                catch(InterruptedException ex){                    
			                }        
			            }
			        };

			        thread.start();        
				
					//new class to accomodate nearby search? or existing?
					String range = range_list[range_pos];

					Log.v("RANGE", range);
					
					String message = et_search.getText().toString();
					Bundle sending = new Bundle();
					sending.putString("search_type", "nearby");
					sending.putString("search_intent", message);
					sending.putString("range", range);
					sending.putString("lat", String.valueOf(ap_lat));
					sending.putString("lon", String.valueOf(ap_lon));
					Intent send_intent = new Intent(SearchAct.this, XmlAct.class);
					send_intent.putExtras(sending);
					startActivity(send_intent);
				}
				
				break;
				
			//SEARCH ADVANCED
			case R.id.b_search_adv:
				
				//checkbox halal and aircon option
				String bool_halal = "", bool_aircon = "";
				
				//get selected spinner option from User into string
				String faculty = fac_list.get(fac_pos);
				String store = store_list.get(store_pos);
				String cuisine = cuisine_list.get(cuisine_pos);
				
				if(halal.isChecked()) {
					bool_halal = "Y";
				}
				if(aircon.isChecked()) {
					bool_aircon = "Y";
				}
				
		//------send intent to results page with query---------------------------------//
		//------bundle various search conditions with query----------------------------//
				Bundle adv_sending = new Bundle();
				adv_sending.putString("search_type", "advanced");
				adv_sending.putString("search_fac", faculty);
				adv_sending.putString("search_store", store);
				adv_sending.putString("search_cuisine", cuisine);
				adv_sending.putString("search_halal", bool_halal);
				adv_sending.putString("search_aircon", bool_aircon);
				Intent adv_send_intent = new Intent(SearchAct.this, XmlAct.class);
				adv_send_intent.putExtras(adv_sending);
				startActivity(adv_send_intent);
				
				break;
				
		//------button to toggle menu slide open/close---------------------------------//
			case R.id.b_menu:
				sd.animateToggle();
				break;
		}
	}
	
	/*FUNCTION* =============================================================================//
	 *   ++ ADVANCED SEARCH ++
	 *  TO RETURN LIST OF OBJECTS FROM DATABASE TO POPULATE SPINNER
	 *  FUNCTION PARAMETER : (URL, list, key)
	 *  URL: URL to pass in to parse XML from httpresponse
	 *  list: list extracted from XML to populate spinner with
	 *  key: which attribute we want to extract from the XML
	 *  	(i.e. to return a list of faculties in NUS we call for a "location" key)
	 */
	private List<String> populate_spinner(String URL, List<String> list, String key) {
		
		//creating new parser class
        XmlFunction parser = new XmlFunction();
        String xml = parser.getXML(URL); // getting XML
        Document doc = parser.XMLfromString(xml); // parsing XML to document so we can read it
 
        //Returns a NodeList of all the Elements with a given tag name in the order in which 
        //they are encountered in a preorder traversal of the Document tree.
        NodeList nl = doc.getElementsByTagName(FOOD_LIST);
        
        // looping through all <food_stall>'s
        for (int i = 0; i < nl.getLength(); i++) {
        	Element e = (Element) nl.item(i);
        	list.add(parser.getValue(e, key));
        }
        
        //sort list then add the default option at the very top
        Collections.sort(list);
        
        list.add(0, "Select an Option");
        
		return list;
	}
	

	/*FUNCTION* =============================================================================//
	 *  FOR SPINNER CLASS
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		
		fac_pos = fac.getSelectedItemPosition();
		store_pos = store.getSelectedItemPosition();
		cuisine_pos = cuisine.getSelectedItemPosition();
		
		range_pos = range.getSelectedItemPosition();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
	
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
				
				ap_lat = return_location.getAp_lat();
				ap_lon = return_location.getAp_long();
				
				Log.v("RETURN_MSG", return_location.getAp_location());
				Toast.makeText(getApplicationContext(), "I am at : " + return_location.getAp_location(), Toast.LENGTH_LONG).show();
				
				Log.v("RETURN_MSG LAT", String.valueOf(return_location.getAp_lat()));
				Toast.makeText(getApplicationContext(), "Current Lat : " + return_location.getAp_lat(), Toast.LENGTH_LONG).show();
				Log.v("RETURN_MSG LONG", String.valueOf(return_location.getAp_long()));
				Toast.makeText(getApplicationContext(), "Current Long : " + return_location.getAp_long(), Toast.LENGTH_LONG).show();
			//}
		}
		
	}

}

