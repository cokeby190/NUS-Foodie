package com.android.dev.foodie;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

public class TestDirectory extends ListActivity implements OnClickListener, OnItemClickListener{
	
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
    static final String FOOD_LIST = "food_list"; 
    static final String LOCATION = "location";
    
    //UI Elements
    ListView lv;
    ListAdapter filter_adapter;
    
    //OnItemClickListener class
    listener onclick_obj;
    
    //XML parser objects
    ArrayList<HashMap<String, String>> menuItems;
    XmlFunction parser;
    NodeList nl;
    
    //loading bar
    long start = 0, stop = 0;
    
    private String[] fac_list;

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
        
        //to store the list of stores to show as results
        menuItems = new ArrayList<HashMap<String, String>>();

        //creating new parser class
        parser = new XmlFunction();
        	
        parse_results(URL_base + "distinct=distinct&query_key=location");
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
		ArrayAdapter <String> adapter_sd = new ArrayAdapter <String> (TestDirectory.this, android.R.layout.simple_list_item_1, menu_list);
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
				Intent send_search = new Intent(TestDirectory.this, NUSFoodieActivity.class);
				startActivity(send_search);
				break;
			case 1:
				Intent send_dir = new Intent(TestDirectory.this, SearchAct.class);
				startActivity(send_dir);
				//Intent send_dir = new Intent(NearbyAct.this, NearbyAct.class);
				//startActivity(send_dir);
				break;
			case 2:
				//Intent send_crowd = new Intent(NearbyAct.this, SearchAct.class);
				//startActivity(send_crowd);
				break;
			case 3:
				Intent send_crowd = new Intent(TestDirectory.this, CrowdAct.class);
				startActivity(send_crowd);
				break;
			case 4:
				Intent send_nearby = new Intent(TestDirectory.this, NearbyAct.class);
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
        
		onclick_obj = new listener();
		
        lv.setOnItemClickListener(onclick_obj);
	}
	
	//OnItemClickListener for list item clicked by User
    public class listener implements OnItemClickListener {

    	@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		Log.v("ONCLICK", "directory");
    		
			// Starting new intent
            Intent in = new Intent(getApplicationContext(), XmlAct.class);
            in.putExtra("directory", fac_list[position]);
            startActivity(in);
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

			progress_bar = new ProgressDialog(TestDirectory.this);
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
					
					fac_list = new String[nl.getLength()];
					
					// looping through all <food_stall>'s
			        for (int i = 0; i < nl.getLength(); i++) {
			            Element e = (Element) nl.item(i);
			            fac_list[i] = parser.getValue(e, LOCATION);
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
		
			ArrayAdapter <String> list_adapter = new ArrayAdapter <String> (TestDirectory.this, android.R.layout.simple_list_item_1, fac_list);
			
			lv.setAdapter(list_adapter);
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
          nl = doc.getElementsByTagName(FOOD_LIST);
          
          new loadData().execute();

  	}
    
}
