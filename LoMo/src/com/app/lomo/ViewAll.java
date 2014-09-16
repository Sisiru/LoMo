package com.app.lomo;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.app.lomo.RowTask.EditTask;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.WindowManager.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ViewAll extends ListActivity implements EditTask {
	private Button btAdd;
	private Button btCancel;
	private Button btCount;
	private int request_code = 12;
	private ListView list;
	private LinearLayout background;
	private boolean today = false;
	private String sound="off";
	private MediaPlayer mMediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list_view);
		initialize();
		retrieveDb();
		addButtonClick();
		cancelButtonClick();
		if(today){
			new Handler().postDelayed(new Runnable() {
		        @Override
		        public void run() {
		        	unlockScreen();
		        	if(sound.equalsIgnoreCase("on")) mMediaPlayer.stop();
		            finish();
		        }
		    }, 30000);//If the user doesn't reapond, the notification goes off after 30 seconds
		}
		// clickTask();
	}

	@Override
	public void onBackPressed() {
		if(today){
			if(sound.equalsIgnoreCase("on")) mMediaPlayer.stop();
		}
		finish();
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		// super.onCreateOptionsMenu(menu);
		MenuInflater inf = getMenuInflater();
		inf.inflate(R.menu.my_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.insert:
			createReminder();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Object o = this.getListAdapter().getItem(position);
		// String keyword = o.toString();
		Intent i = new Intent(ViewAll.this, ViewTask.class);
		i.putExtra("TaskID", id);
		i.putExtra("Class", false);
		startActivity(i);
	}

	private void initialize() {
		btAdd = (Button) findViewById(R.id.btAdd);
		btCancel = (Button) findViewById(R.id.btCancel);
		btCount = (Button) findViewById(R.id.btTaskCount);
		list = (ListView) findViewById(android.R.id.list);
		background = (LinearLayout) findViewById(R.id.myback);
		SQLiteDatabase myDB=openOrCreateDatabase("lomo", MODE_PRIVATE, null);
		Cursor result=myDB.rawQuery("select * from securedevice where id='1'", null);
		result.moveToFirst();
		sound=result.getString(10);
		myDB.close();
		Bundle bun = getIntent().getExtras();
		if(bun!=null){
			if (bun.getString("Day").equalsIgnoreCase("today")) {
				background.setBackgroundResource(R.drawable.today);
				today = true;
				if(sound.equalsIgnoreCase("on")) playSound(ViewAll.this, getAlarmUri());
			}
		}
	}

	private static final int ACTIVITY_CREATE = 0;

	private void createReminder() {
		Intent i = new Intent(this, ViewTask.class);
		i.putExtra("Class", true);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		retrieveDb();
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// super.onCreateContextMenu(menu, v, menuInfo);

	}

	public void retrieveDb() {
		SQLiteDatabase mydatbase = openOrCreateDatabase("lomo", MODE_PRIVATE,
				null);

		Cursor resultSet = mydatbase.rawQuery("Select * from task", null);
		ArrayList<String> priority = new ArrayList<>();
		DecimalFormat formatCount = new DecimalFormat("00");// Formatting the
															// count for two
															// digits
		if (!today)
			btCount.setText(formatCount.format(resultSet.getCount()) + "");// Setting
																			// the
																			// task
																			// count

		resultSet.moveToFirst();

		ArrayList<String> array = new ArrayList<>();
		ArrayList<String> idArray=new ArrayList<>();
		
		if(!today){
			String taskName = "Ruveni";
			while (!resultSet.isAfterLast()) {
				taskName = resultSet.getString(1);
				priority.add(resultSet.getString(8));
				idArray.add(resultSet.getString(0));
				array.add(taskName);
				resultSet.moveToNext();
			}
		}else{
			String taskName = "Ruveni";
			while (!resultSet.isAfterLast()) {
				if(resultSet.getString(6).equalsIgnoreCase(getTodayDate())){
					taskName = resultSet.getString(1);
					priority.add(resultSet.getString(8));
					idArray.add(resultSet.getString(0));
					array.add(taskName);
				}
				resultSet.moveToNext();
			}
			if(array.size()==0){
				finish();
			}
		}
		RowTask rTask = new RowTask(this, R.layout.row_reminder, array,
				priority,idArray);
		rTask.setCallback(this);
		list.setAdapter(rTask);

		mydatbase.close();
	}

	private void addButtonClick() {
		btAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ViewAll.this, PlanDay.class);
				startActivityForResult(intent, request_code);

			}
		});
	}

	private void cancelButtonClick() {
		btCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void deletePressed(String position) {
		SQLiteDatabase mydatbase = openOrCreateDatabase("lomo", MODE_PRIVATE,
				null);
		mydatbase.execSQL("delete from task where  taskid= '" + position + "'");
		mydatbase.execSQL("UPDATE task set taskid = (taskid - 1) WHERE taskid > "+ position);
		retrieveDb();
		mydatbase.close();
		// mydatbase.execSQL("UPDATE SQLITE_SEQUENCE SET seq = this.ID -1 WHERE name = TABLE_NUMS");

	}

	@Override
	public void viewTask(String position) {
		Intent intent = new Intent(ViewAll.this, ViewTask.class);
		intent.putExtra("Position", position + "");
		startActivity(intent);

	}

	@Override
	protected void onResume() {
		retrieveDb();
		super.onResume();
	}

	@Override
	public void updatePriority(String position, String priority) {
		SQLiteDatabase mydatbase = openOrCreateDatabase("lomo", MODE_PRIVATE,
				null);
		mydatbase.execSQL("update task set priority='" + priority
				+ "' where taskid='" + position + "'");
		retrieveDb();
		mydatbase.close();

	}
	
	private String getTodayDate(){
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat sd2 = new SimpleDateFormat("yyyy-MM-dd",
				Locale.ENGLISH);
		return sd2.format(date);
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

	private void unlockScreen() {
        Window window = this.getWindow();
        window.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
    }


}
