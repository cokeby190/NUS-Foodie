package com.android.dev.foodie;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class SearchAct extends Activity implements OnClickListener, OnItemSelectedListener, OnDrawerOpenListener, OnItemClickListener{
	
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
	
    //list for spinner
    private List <String> fac_list = new ArrayList<String>();
    private List <String> store_list = new ArrayList<String>();
    private List <String> cuisine_list = new ArrayList<String>();
    
	private TabHost tabs;
	private Spinner fac, store, cuisine;
	private ImageButton search, search_adv;
	private EditText et_search, et_search_adv;
	
	//sliding drawer
	private SlidingDrawer sd;
	private ListView sd_content;
	
	//titlebar
	boolean customTitleSupport = true;
	TextView title_bar;
	Button menu;
	
	//menu list
	private String[] menu_list = {"Search" , "Directory" , "Crowd" , "Nearby"}; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //titlebar
        customTitleSupport = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        setContentView(R.layout.search_page);
        
        if(customTitleSupport) {
        	customTitleBar(getText(R.string.app_name).toString());
        }
        
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
	}

	/*FUNCTION* =============================================================================//
	 *  INITIALISE UI ELEMENTS
	 */
	private void initialise() {
		tabs = (TabHost)findViewById(R.id.tabhost);
		fac = (Spinner)findViewById(R.id.sp_search_adv_fac);
		store = (Spinner)findViewById(R.id.sp_search_adv_store);
		cuisine = (Spinner)findViewById(R.id.sp_search_adv_cuisine);
		
		search = (ImageButton)findViewById(R.id.ib_search_basic);
		search_adv = (ImageButton)findViewById(R.id.ib_search_adv);
		
		search.setOnClickListener(this);
		search_adv.setOnClickListener(this);
		
		et_search = (EditText) findViewById(R.id.et_search_bar);
		et_search_adv = (EditText) findViewById(R.id.et_search_adv_bar);
		
		//sliding drawer initialisation
		sd = (SlidingDrawer) findViewById(R.id.slidingDrawer1);
		sd.setOnDrawerOpenListener(this);
		sd_content = (ListView) findViewById(R.id.sd_list);
		//list for menu
			//ArrayAdapter constructor : ArrayAdapter <String> (Context, int layout, String list)
		ArrayAdapter <String> adapter_sd = new ArrayAdapter <String> (SearchAct.this, android.R.layout.simple_list_item_1, menu_list);
		sd_content.setAdapter(adapter_sd);
		sd_content.setOnItemClickListener(this);
		
		
//------INITIALISE AND POPULATE SPINNER FROM DATABASE---------------------------------------//
		
		ArrayAdapter <String> adapter_fac = new ArrayAdapter <String> (SearchAct.this, android.R.layout.simple_spinner_item, populate_spinner((URL_base + "distinct=distinct&query_key=location"), fac_list, "location")); 
		fac.setAdapter(adapter_fac);
		fac.setOnItemSelectedListener(this);

		ArrayAdapter <String> adapter_store = new ArrayAdapter <String> (SearchAct.this, android.R.layout.simple_spinner_item, populate_spinner((URL_base + "distinct=distinct&query_key=store_type"), store_list, "store_type")); 
		store.setAdapter(adapter_store);
		store.setOnItemSelectedListener(this);

		ArrayAdapter <String> adapter_cuisine = new ArrayAdapter <String> (SearchAct.this, android.R.layout.simple_spinner_item, populate_spinner((URL_base + "distinct=distinct&query_key=cuisine"), cuisine_list, "cuisine")); 
		cuisine.setAdapter(adapter_cuisine);
		cuisine.setOnItemSelectedListener(this);
		
	}
	
	
	/*FUNCTION* =============================================================================//
	 *  TO RETURN LIST OF OBJECTS FROM DATABASE TO POPULATE SPINNER
	 */
	private List<String> populate_spinner(String URL, List<String> list, String key) {
		
		//creating new parser class
        xml_functions parser = new xml_functions();
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
        
		return list;
	}
	
	/*FUNCTION* =============================================================================//
	 *  ONCLICK FUNCTION
	 */
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.ib_search_basic:

		//------send intent to results page with query---------------------------------//
				String message = et_search.getText().toString();
				Bundle sending = new Bundle();
				sending.putString("search_intent", message);
				Intent send_intent = new Intent(SearchAct.this, XmlAct.class);
				send_intent.putExtras(sending);
				startActivity(send_intent);
				
				break;
			case R.id.ib_search_adv:
				break;
				
			case R.id.b_menu:
				sd.animateToggle();
				break;
		}
	}

	/*FUNCTION* =============================================================================//
	 *  FOR SPINNER CLASS
	 */
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}

	/*FUNCTION* =============================================================================//
	 *  FOR SLIDING DRAWER
	 */
	@Override
	public void onDrawerOpened() {
		
	}
	
	/*FUNCTION* =============================================================================//
	 *  FOR MENU FUNCTION
	 *  FUNCTION PARAMETER : onItemClick (AdapterView<?> parent, View view, int position, long id)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch(position) {
			case 0:
				Intent send_search = new Intent(SearchAct.this, SearchAct.class);
				startActivity(send_search);
				break;
			case 1:
				Intent send_dir = new Intent(SearchAct.this, NUSFoodieActivity.class);
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
		}
	}
}

