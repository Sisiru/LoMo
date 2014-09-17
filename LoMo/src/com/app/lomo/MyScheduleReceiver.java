package com.app.lomo;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

//Broadcast receiver
public class MyScheduleReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//when receiving the broadcast signal this method will be called
		Bundle bundle = intent.getExtras();//receiving the data from the TrackLocation activity
		String bundleTime=bundle.getString("Time");
		int time=0;
		if(bundleTime==null){
			time=1;//this will be assigned when the device reboots
		}else{
			time = Integer.parseInt(bundleTime);//this is assigned all the other time
		}
		
		//next intent that will be started
		Intent i = new Intent(context, MyStartServiceReceiver.class);
		//pending intent to wait for a predefined period
		PendingIntent pending = PendingIntent.getBroadcast(context, 0, i, 0);
		
		Calendar cal = Calendar.getInstance();
		//here we use the pending intent to wait a particular time before starting the background service again
		cal.add(Calendar.MINUTE, time);

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);//getting the alarm service
		alarmManager.set(AlarmManager.RTC, cal.getTimeInMillis(), pending);
	}

}
