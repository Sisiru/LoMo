package com.app.lomo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

//Background service class to track locations
public class TrackPath extends Service {
	Intent intent;
	int counter = 0;
	PendingIntent pending;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		sendMsg();
		return START_STICKY;
	}

		
	@Override
	public void onDestroy() {
		// handler.removeCallbacks(sendUpdatesToUI);
		super.onDestroy();
		Log.v("STOP_SERVICE", "DONE");
	}

	
	private void sendMsg() {
		intent = new Intent(this, MyScheduleReceiver.class);
		intent.putExtra("Time", "1");
		pending = PendingIntent.getBroadcast(this.getApplicationContext(), 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				pending);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
