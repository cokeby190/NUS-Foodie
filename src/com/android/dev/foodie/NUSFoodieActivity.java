package com.android.dev.foodie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NUSFoodieActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button searchButton = (Button)findViewById(R.id.search);

        searchButton.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View view) {
				Intent open_search = new Intent(NUSFoodieActivity.this, search_act.class);
				startActivity(open_search);
			}

        });
        
        
        
    }
}