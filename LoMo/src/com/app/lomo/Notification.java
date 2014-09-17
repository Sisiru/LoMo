package com.app.lomo;

import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

//this is the notification window shown to notify the user
public class Notification extends Activity {
	private TextView txtTask;
	private TextView txtDistance;
	private TextView txtTime;
	private Button btDone;
	private Button btSnooze;
	private Button btNavigate;
	private Button btDelete;
	private String taskID;
	private MediaPlayer mMediaPlayer;
	private String latitude;
	private String longitude;
	private String notificationTime = "20";
	ProgressDialog progress = null;
	private String sound = "on";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// removing the tile
															// bar
		setContentView(R.layout.notification);// setting the layout
		unlockScreen();// unlocking the screen
		initialize();// initializing
		bundleData();
		// checks whether user has turned on the alert sound, if activated
		// playing the alarm tone
		if (sound.equalsIgnoreCase("on"))
			playSound(Notification.this, getAlarmUri());
		clickNavigation();// navigation
		clickDone();// completed task
		clickSnooze();// snoozing
		clickDelete();// deleting the task

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// waits for the respond of the user
				if (sound.equalsIgnoreCase("on"))
					mMediaPlayer.stop();
				finish();
			}
		}, 30000);// If the user doesn't respond, the notification goes off
					// after 30 seconds
	}

	private void unlockScreen() {
		Window window = this.getWindow();
		window.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
		window.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		window.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
	}

	// initializing
	private void initialize() {
		txtTask = (TextView) findViewById(R.id.txtTask);
		txtDistance = (TextView) findViewById(R.id.txtDistance);
		txtTime = (TextView) findViewById(R.id.txtTime);
		btDone = (Button) findViewById(R.id.btDone);
		btSnooze = (Button) findViewById(R.id.btSnooze);
		btNavigate = (Button) findViewById(R.id.btNavigate);
		btDelete = (Button) findViewById(R.id.btDelete);
	}

	private void bundleData() {
		// gets the data sent by the service
		Bundle bundle = getIntent().getExtras();
		String taskName = bundle.getString("Task");// task name
		taskID = bundle.getString("TaskId");// task id
		String distance = bundle.getString("Distance");// distance to the task
														// location from the
														// current location
		txtTask.setText(taskName);// setting the name of the task
		// setting the distance to the task location
		txtDistance.setText(String.format("%.2f", Double.parseDouble(distance))
				+ " m away from current location");

		// opening the database
		SQLiteDatabase myDB = openOrCreateDatabase("lomo", MODE_PRIVATE, null);
		// gets all the data from task
		Cursor cursor = myDB.rawQuery("select * from task where taskid='"
				+ taskID + "'", null);
		cursor.moveToFirst();
		txtTime.setText("Expired at : " + cursor.getString(6));// gets expiry
																// time
		// Toast.makeText(Notification.this,
		// cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2)+" "+cursor.getString(3)+" "+cursor.getString(4)+" "+cursor.getString(5)+" "+cursor.getString(6)+" "+cursor.getString(7)+" "+cursor.getString(8),
		// Toast.LENGTH_LONG).show();
		// gets the latitude and longitude location of the task location
		latitude = cursor.getString(5);
		longitude = cursor.getString(4);

		// gets the settings
		Cursor result = myDB.rawQuery(
				"select * from securedevice where id='1'", null);
		result.moveToFirst();
		sound = result.getString(10);// checks whether use has activated the
										// alarm tone or not
		myDB.close();// closing the database
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	//when the done button is clicked
	private void clickDone() {
		btDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// mMediaPlayer.stop();
				//alert dialog is displayed
				AlertDialog.Builder alert = new AlertDialog.Builder(
						Notification.this);
				alert.setTitle("Confirm");
				alert.setMessage("Did you finish this task??");
				alert.setPositiveButton("Done",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (sound.equalsIgnoreCase("on"))
									mMediaPlayer.stop();//stopping the alarm tone
								deleteTask();//deleting the task from the database
								finish();//finishing the activity
							}
						});
				alert.setNegativeButton("No",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
								if (sound.equalsIgnoreCase("on"))
									mMediaPlayer.stop();//stopping the alerm tone
							}
						});
				alert.create().show();//creating the alert dialog

			}
		});
	}

	//when the snooze button is clicked
	private void clickSnooze() {
		btSnooze.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//open the database
				SQLiteDatabase mydb = openOrCreateDatabase("lomo",
						MODE_PRIVATE, null);
				Cursor result = mydb.rawQuery(
						"select * from securedevice where id='1'", null);//access settings 
				result.moveToFirst();
				notificationTime = result.getString(11);//alert time
				mydb.close();//closing the database
				if (sound.equalsIgnoreCase("on"))
					mMediaPlayer.stop();//
				AlertDialog.Builder alert = new AlertDialog.Builder(
						Notification.this);
				alert.setTitle("Snooze Task");
				alert.setMessage("The task will be notified after "
						+ notificationTime + " minutes");
				alert.setPositiveButton("Do",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// mMediaPlayer.stop();
								finish();

							}
						});
				alert.create().show();

			}
		});
	}

	private void playSound(Context context, Uri alert) {
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(context, alert);
			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			}
		} catch (IOException e) {
			// Problem with the media player
		}
	}

	private Uri getAlarmUri() {
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alert == null) {
			alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) {
				alert = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}
		return alert;
	}

	private void deleteTask() {
		SQLiteDatabase mydatbase = openOrCreateDatabase("lomo", MODE_PRIVATE,
				null);
		mydatbase.execSQL("delete from task where  taskid= '" + taskID + "'");
		mydatbase
				.execSQL("UPDATE task set taskid = (taskid - 1) WHERE taskid > "
						+ taskID);
		mydatbase.close();
	}

	private void clickNavigation() {
		btNavigate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// progressDisplay();
				Intent intent = new Intent(Notification.this, DrawPath.class);
				intent.putExtra("Latitude", latitude);
				intent.putExtra("Longitude", longitude);
				startActivity(intent);
				if (sound.equalsIgnoreCase("on"))
					mMediaPlayer.stop();
				finish();
			}
		});
	}

	private void clickDelete() {
		btDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						Notification.this);
				alert.setTitle("Confirm");
				alert.setMessage("Do you want to delete the task??");
				alert.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (sound.equalsIgnoreCase("on"))
									mMediaPlayer.stop();
								deleteTask();
								finish();
							}
						});
				alert.setNegativeButton("No",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
								if (sound.equalsIgnoreCase("on"))
									mMediaPlayer.stop();
							}
						});
				alert.create().show();

			}
		});
	}

}
