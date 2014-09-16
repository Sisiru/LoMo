package com.app.lomo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//To start the service as soon as the device turns on

public class AutoStart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// Opens the TrackPath service to start location tracking
		Intent startServiceIntent = new Intent(context, TrackPath.class);
		context.startService(startServiceIntent);// starts the service
	}

}
