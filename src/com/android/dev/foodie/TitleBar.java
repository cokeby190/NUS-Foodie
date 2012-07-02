package com.android.dev.foodie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class TitleBar extends Activity implements OnClickListener{

	boolean customTitleSupport;
	
	TextView title_bar;
	Button menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		customTitleSupport = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
		setContentView(R.layout.titlebar);
		customTitleBar(getText(R.string.app_name).toString());
		
	}

	public void customTitleBar(String title) {
		if(customTitleSupport) {
			
			//setFeatureInt(int featureId, int value)
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
			
			title_bar = (TextView) findViewById(R.id.tv_title);
			menu = (Button) findViewById(R.id.b_menu);
			
			title_bar.setText(title);
			menu.setOnClickListener(this);
			
		}
	}

	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.b_menu) {
			Intent open_search = new Intent(TitleBar.this, SearchAct.class);
			startActivity(open_search);
		}
	}
}
