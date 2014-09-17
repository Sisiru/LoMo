package com.app.lomo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
//broadcast receiver 
public class MyStartServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//this method is called when the MyScheduleReceiver wants to communicate
		//MyScheduleReceiver waits for a particular period and broadcasts that 'Waiting time is over'
		//This is heard by the MyStartServuceReceiver and calls the service class to start the service again
		Log.i("Entry", "Broadcast receiver to call service");
		Toast.makeText(context, "Starts the service", Toast.LENGTH_SHORT).show();
		Intent service = new Intent(context, TrackLocation.class);//calls the tracklocation service class
	    context.startService(service);//starts the service    
	}
}
