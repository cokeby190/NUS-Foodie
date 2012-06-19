package com.android.dev.foodie;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ListActivity;
import android.content.Intent;
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

public class xml_act extends ListActivity implements TextWatcher{

    //stating the URL
    static final String URL = "http://172.18.101.125:8080/wte/wte?";
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
    //SimpleAdapter filter_adapter;
    ListAdapter filter_adapter;
    EditText filterText = null;
    
    listener onclick_obj;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xml_display);
        lv = (ListView) findViewById(android.R.id.list);
 
        ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
 
        //creating new parser class
        xml_functions parser = new xml_functions();
        String xml = parser.getXML(URL); // getting XML
        Document doc = parser.XMLfromString(xml); // parsing XML to document so we can read it
 
        //Returns a NodeList of all the Elements with a given tag name in the order in which 
        //they are encountered in a preorder traversal of the Document tree.
        NodeList nl = doc.getElementsByTagName(FOOD_STALL);
        
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
 
        filterText = (EditText) findViewById(R.id.search_box);
        filterText.addTextChangedListener(this);
        
        //CONSTRUCTOR FOR SimpleAdapter
        //SimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
        filter_adapter = new SimpleAdapter(this, menuItems, R.layout.row_view, 
        		new String[] { STORE_NAME, LOCATION, CANTEEN_NAME }, new int[] {R.id.textView1, R.id.textView2, R.id.textView3});
        
        //filter_adapter = new SimpleAdapter(this, menuItems, R.layout.row_view, 
        //		new String[] { STORE_NAME, LOCATION, CANTEEN_NAME }, new int[] {R.id.textView1});
        
        
        //filter_adapter = new SimpleAdapter(this, menuItems, R.layout.xml_display, 
        //		new String[] { STORE_NAME, LOCATION, CANTEEN_NAME }, new int[] {R.id.list_text});
        setListAdapter(filter_adapter);
        
        onclick_obj = new listener();
        
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(onclick_obj);
        
//        // listening to single listitem click
//        lv.setOnItemClickListener(new OnItemClickListener() {
// 
//            //@Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            	
//                // getting values from selected ListItem
//                //String store_name = ((TextView) view.findViewById(R.id.textView1)).getText().toString();
//                //String location = ((TextView) view.findViewById(R.id.textView2)).getText().toString();
//                //String canteen_name = ((TextView) view.findViewById(R.id.textView3)).getText().toString();
// 
//                // Starting new intent
//                Intent in = new Intent(getApplicationContext(), sensor_act.class);
//                //in.putExtra(STORE_NAME, store_name);
//                //in.putExtra(LOCATION, location);
//                //in.putExtra(CANTEEN_NAME, canteen_name);
//                startActivity(in);
// 
//            }
//        });
        
        
    }
    
    public class listener implements OnItemClickListener {

    	@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// Starting new intent
            Intent in = new Intent(getApplicationContext(), NUSFoodieActivity.class);
            //in.putExtra(STORE_NAME, store_name);
            //in.putExtra(LOCATION, location);
            //in.putExtra(CANTEEN_NAME, canteen_name);
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
}    