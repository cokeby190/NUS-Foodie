package com.android.dev.foodie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

public class DialogNotif {

	public AlertDialog newdialog(final Context context, String message) {
		AlertDialog.Builder alert_dialog = new AlertDialog.Builder(context); 

		alert_dialog.setMessage(message)
		.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	        	dialog.cancel();
	        }
	    });
		AlertDialog alert = alert_dialog.create();
		
		return alert;
	}
}
