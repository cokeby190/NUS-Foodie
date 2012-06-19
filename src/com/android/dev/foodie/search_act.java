package com.android.dev.foodie;

import android.app.Activity;
import android.os.Bundle;

import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class search_act extends Activity{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);
        
        TabHost tabs= (TabHost)findViewById(R.id.tabhost);  

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
}
