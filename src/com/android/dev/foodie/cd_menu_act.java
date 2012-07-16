package com.android.dev.foodie;

//add comments for testing git in eclipse

import android.app.Activity;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class cd_menu_act extends Activity{

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cd_menu);
        
        TabHost tabs= (TabHost)findViewById(R.id.tabhost);  

        tabs.setup();  

        TabSpec tab_one = tabs.newTabSpec("breakfast"); 
        tab_one.setContent(R.id.breakfast_tab); 
        tab_one.setIndicator("Breakfast"); 
        tabs.addTab(tab_one);  

        TabSpec tab_two = tabs.newTabSpec("dinner");
        tab_two.setContent(R.id.dinner_tab);
        tab_two.setIndicator("Dinner"); 
        tabs.addTab(tab_two);
    }
}


