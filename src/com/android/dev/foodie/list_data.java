package com.android.dev.foodie;

import java.util.ArrayList;
import java.util.HashMap;

public class list_data {
	
	HashMap<String, String> data;
	
	public list_data(){
        super();
    }
    
    public list_data(HashMap<String, String> data) {
        super();
        this.data = data;
    }
    
    public String get(String parameter) {
    	return data.get(parameter);
    }
}
