package com.android.dev.foodie;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DialogAct extends Activity{

	static final String CUISINE = "cuisine";
    static final String HALAL = "halal";
    static final String MENU = "menu";
    static final String AIRCON = "aircon";
	static final String AVAILABILITY_WEEKDAY = "availability_weekday";
    static final String AVAILABILITY_WEEKEND = "availability_weekend";
    static final String AVAILABILITY_VAC_WEEKDAY = "availability_vac_weekday";
    static final String AVAILABILITY_VAC_WEEKEND = "availability_vac_weekend";
    static final String AVAILABILITY_PUBHOL = "availability_pubhol";
	
	HashMap<String, String> menuItems;
	
	//UI Elements
	TextView tv_cuisine, tv_halal, tv_menu, tv_aircon; 
    TextView sch_wd, sch_we, vac_wd, vac_we, pubhol;
    
    //String for xml data
    String halal_str = "No don't have ):", menu_str = "No don't have ):", aircon_str = "No don't have ):";
    String sch_wd_str, sch_we_str, vac_wd_str, vac_we_str, pubhol_str;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_info);
		
		getData();
		
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
		
		//process string
		
			//BOOLEAN STORE INFO
	        if(menuItems.get(HALAL).equals("Y")) {
	        	halal_str = "Yah lah!";
	        }
	        
	        if(menuItems.get(MENU).equals("Y")) {
	        	menu_str = "Yah lah!";
	        }
	        
	        if(menuItems.get(AIRCON).equals("Y")) {
	        	aircon_str = "Yah lah!";
	        }
	        
	        sch_wd_str = menuItems.get(AVAILABILITY_WEEKDAY);
			sch_we_str = menuItems.get(AVAILABILITY_WEEKEND); 
			vac_wd_str = menuItems.get(AVAILABILITY_VAC_WEEKDAY); 
			vac_we_str = menuItems.get(AVAILABILITY_VAC_WEEKEND); 
			pubhol_str = menuItems.get(AVAILABILITY_PUBHOL);
	        
	        //OPERATING HOURS
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
	        
        //setting Text
        tv_cuisine.setText(menuItems.get(CUISINE));
        tv_halal.setText(halal_str);
        tv_menu.setText(menu_str);
        tv_aircon.setText(aircon_str);
        
        //TAB2 - OPERATING HOURS TAB
        
        sch_wd.setText(sch_wd_str);
        sch_we.setText(sch_we_str);
        vac_wd.setText(vac_wd_str);
        vac_we.setText(vac_we_str);
        pubhol.setText(pubhol_str);
}

	/*FUNCTION* =============================================================================//
	 *  FOR RECEIVING DATA FROM STOREINFO THROUGH INTENT
	 */
    private void getData() {

		menuItems =(HashMap<String, String>) getIntent().getSerializableExtra("menuItems");
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
}
