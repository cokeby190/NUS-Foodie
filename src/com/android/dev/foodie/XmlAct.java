package com.android.dev.foodie;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class XmlAct extends ListActivity implements TextWatcher{

    //stating the URL
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
    
    ListView lv;
    ListAdapter filter_adapter;
    EditText filterText = null;
    String getMsg, search_type;
    
    listener onclick_obj;
    
    ArrayList<HashMap<String, String>> menuItems;
    xml_functions parser;
    NodeList nl;
    
    //loading bar
    long start = 0, stop = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xml_display);
        lv = (ListView) findViewById(android.R.id.list);
 
        filterText = (EditText) findViewById(R.id.search_box);
        filterText.addTextChangedListener(this);
        
        String[] search_string = getData();
        
        menuItems = new ArrayList<HashMap<String, String>>();

        //creating new parser class
        parser = new xml_functions();
        
        if(search_string[0].equals("basic")) {
		
	        //String xml = parser.getXML(URL_base); // getting XML
	        String xml = parser.getXML(URL_base + "search=basic&search_string=" + search_string[1]); // getting XML
	        Document doc = parser.XMLfromString(xml); // parsing XML to document so we can read it
	 
	        //Returns a NodeList of all the Elements with a given tag name in the order in which 
	        //they are encountered in a preorder traversal of the Document tree.
	        nl = doc.getElementsByTagName(FOOD_STALL);
	        
	        new loadData().execute();
	        
	        //CONSTRUCTOR FOR SimpleAdapter
	        //SimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
	        filter_adapter = new SimpleAdapter(this, menuItems, R.layout.row_view, 
	        		new String[] { STORE_NAME, LOCATION, CANTEEN_NAME }, new int[] {R.id.textView1, R.id.textView2, R.id.textView3});
	        
        } else if (search_string[0].equals("advanced")) {
        	
        	Toast t = Toast.makeText(getApplicationContext(), URL_base + "search=advanced&search_string=" + search_string[1] + 
	        		"&location=" + search_string[2] + "&store_type=" + search_string[3] + "&cuisine=" + search_string[4] +
	        				"&halal=" + search_string[5] + "&aircon=" + search_string[6] , Toast.LENGTH_LONG);
	        t.show();
	        
	        search_string[1] = search_string[1].replace(" ", "%20");
	        search_string[2] = search_string[2].replace(" ", "%20");
	        search_string[3] = search_string[3].replace(" ", "%20");
	        search_string[4] = search_string[4].replace(" ", "%20");
	        
	        String xml = parser.getXML(URL_base + "search=advanced&search_string=" + search_string[1] + 
	        		"&location=" + search_string[2] + "&store_type=" + search_string[3] + "&cuisine=" + search_string[4] +
    				"&halal=" + search_string[5] + "&aircon=" + search_string[6]); // getting XML
	        Document doc = parser.XMLfromString(xml); // parsing XML to document so we can read it
	 
	        //Returns a NodeList of all the Elements with a given tag name in the order in which 
	        //they are encountered in a preorder traversal of the Document tree.
	        nl = doc.getElementsByTagName(FOOD_STALL);
	        
	        new loadData().execute();
	        
	        //CONSTRUCTOR FOR SimpleAdapter
	        //SimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
	        filter_adapter = new SimpleAdapter(this, menuItems, R.layout.row_view, 
	        		new String[] { STORE_NAME, LOCATION, CANTEEN_NAME }, new int[] {R.id.textView1, R.id.textView2, R.id.textView3});
        }
	        
	    setListAdapter(filter_adapter);
	        
	    onclick_obj = new listener();
        
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(onclick_obj);
        
    }
    
    //function to receive data from intent from search page
    private String[] getData() {
		Bundle getMessage = getIntent().getExtras();
		search_type = getMessage.getString("search_type");
		getMsg = getMessage.getString("search_intent");
		
		String fac = getMessage.getString("search_fac");
		String store = getMessage.getString("search_store");
		String cuisine = getMessage.getString("search_cuisine");
		
		String halal = getMessage.getString("search_halal");
		String aircon = getMessage.getString("search_aircon");
	
        fac = process_default(fac);
		store = process_default(store);
		cuisine = process_default(cuisine);
		
		if(search_type.equals("basic")) {
			String[] return_data = {search_type, getMsg};
			return return_data;
		} else if (search_type.equals("advanced")) {
			String[] return_data = {search_type, getMsg, fac, store, cuisine, halal, aircon};
			return return_data;
		}
		
		return null;	
	}
    
    private String process_default(String option) {
    	
    	if(option.equals("Select an Option")) {
    		option = "";
    	}
    	
    	return option;
    }
    
    public class listener implements OnItemClickListener {

    	@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// Starting new intent
            Intent in = new Intent(getApplicationContext(), NUSFoodieActivity.class);
            startActivity(in);
		}
    	
    }
    
	public void afterTextChanged(Editable s) {
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		((SimpleAdapter) filter_adapter).getFilter().filter(s.toString());
	}
	
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
				
				//------Do the time consuming task---------------------------------------//

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
		            map.put(AIRCON, parser.getValue(e, AIRCON));
		            // adding HashList to ArrayList
		            menuItems.add(map);
		        }

				//------END OF time consuming task---------------------------------------//
				
				stop = System.currentTimeMillis();
				
				long period = stop - start;
				
				if (period < 500) {

					try {
						Thread.sleep(600);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
						
				}
				progress_bar.dismiss();

				return null;
			}

		}
}    