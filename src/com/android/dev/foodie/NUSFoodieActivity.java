package com.android.dev.foodie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NUSFoodieActivity extends Activity implements OnClickListener{
	
	private Button searchButton, dirButton;
	
	//titlebar
	boolean customTitleSupport = true;
	TextView title_bar;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
      //titlebar
        customTitleSupport = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        setContentView(R.layout.main);
        
        if(customTitleSupport) {
        	customTitleBar_simple(getText(R.string.app_name).toString());
        }
        
        initialise_elmts();
    }
    
    /*FUNCTION* =============================================================================//
	 *  CUSTOM TITLE BAR
	 */
	public void customTitleBar_simple(String title) {
			
		//setFeatureInt(int featureId, int value)
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_simple);
		
		title_bar = (TextView) findViewById(R.id.tv_title);
		title_bar.setText(title);
	}
    
    public void initialise_elmts() { 
    	searchButton = (Button)findViewById(R.id.b_main_search);
    	searchButton.setOnClickListener(this);
    	
    	dirButton = (Button)findViewById(R.id.b_main_dir);
    	dirButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.b_main_search:
				Intent open_search = new Intent(NUSFoodieActivity.this, SearchAct.class);
				startActivity(open_search);
				break;
				
			case R.id.b_main_dir:
				Intent open_dir = new Intent(NUSFoodieActivity.this, TitleBar.class);
				startActivity(open_dir);
				break;
		}
	}
}