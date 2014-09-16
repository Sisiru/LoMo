package com.app.lomo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyStartServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("Entry", "Broadcast receiver to call service");
		Toast.makeText(context, "Start Again", Toast.LENGTH_SHORT).show();
		Intent service = new Intent(context, TrackLocation.class);
	    context.startService(service);
	    
	}

}
