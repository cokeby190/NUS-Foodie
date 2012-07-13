package com.android.dev.foodie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TestService extends Activity{

	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        
	        super.onCreate(savedInstanceState);
	        
	        Intent intent = new Intent(this, ServiceLocation.class);
	        startService(intent);
	    }
	    
	   
	    @Override 
	    protected void onDestroy() {
	          super.onDestroy();
	          
	    }
}
