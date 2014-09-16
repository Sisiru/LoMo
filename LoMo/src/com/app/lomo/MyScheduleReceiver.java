package com.app.lomo;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MyScheduleReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String bundleTime=bundle.getString("Time");
		int time=0;
		if(bundleTime==null){
			time=1;
		}else{
			time = Integer.parseInt(bundleTime);
		}
		
		Intent i = new Intent(context, MyStartServiceReceiver.class);

		PendingIntent pending = PendingIntent.getBroadcast(context, 0, i, 0);
		
		Calendar cal = Calendar.getInstance();
		// start 30 seconds after boot completed

		cal.add(Calendar.MINUTE, time);

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		// fetch every REPEAT_TIME period
		alarmManager.set(AlarmManager.RTC, cal.getTimeInMillis(), pending);
		
		
	}

}
