package com.app.lomo;

import java.text.DecimalFormat;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainScreen extends Activity {
	private Button btCount;
	private Button btPlan;
	private Button btPhoto;
	private Button btPerimeter;
	private Button btSettings;
	private Button btAbout;
	private LinearLayout lDisplay;
	private boolean status = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_main);
		databaseInit();
		initialize();
		clickPlanButton();
		clickCountButton();
		clickPhotoButton();
		clickPerimeterButton();
		clickSettings();
		clickAboutButton();
		checkReceiverNumber();
		// LocationTracker lTrack=new LocationTracker(MainScreen.this);

	}

	public void startService() {
		Intent intent = new Intent(MainScreen.this, TrackLocation.class);
		startService(intent);
	}

	public void stopService() {
		Intent intent = new Intent(MainScreen.this, TrackLocation.class);
		stopService(intent);
	}

	public void clickSettings() {
		btSettings.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				SQLiteDatabase mydatbase = openOrCreateDatabase("lomo",
						MODE_PRIVATE, null);

				if (status) {
					status = false;
					lDisplay.setBackgroundResource(R.drawable.main2);
					mydatbase
							.execSQL("update secureDevice set status='off' where id='1'");
					stopService();
				} else {
					status = true;
					lDisplay.setBackgroundResource(R.drawable.main1);
					mydatbase
							.execSQL("update secureDevice set status='on' where id='1'");
					startService();
				}
				mydatbase.close();
			}
		});
	}

	public void initialize() {
		btCount = (Button) findViewById(R.id.btCount);
		btPlan = (Button) findViewById(R.id.btPlan);
		btPhoto = (Button) findViewById(R.id.btFuture);
		btPerimeter = (Button) findViewById(R.id.btPerimeter);
		btSettings = (Button) findViewById(R.id.btSettings);
		btAbout = (Button) findViewById(R.id.btAbout);

		SQLiteDatabase db = openOrCreateDatabase("lomo", MODE_PRIVATE, null);// Opening
		// the
		// database
		// Checking the number of data filled to the table
		Cursor resultSet = db.rawQuery("select * from task", null);
		DecimalFormat dec = new DecimalFormat("00");
		btCount.setText(dec.format(resultSet.getCount()) + "");

		lDisplay = (LinearLayout) findViewById(R.id.myback);
		Cursor result = db.rawQuery("select * from secureDevice where id='1'",
				null);
		result.moveToFirst();
		if (result.getString(9).equalsIgnoreCase("on")) {
			status = true;
			lDisplay.setBackgroundResource(R.drawable.main1);
		} else {
			status = false;
			lDisplay.setBackgroundResource(R.drawable.main2);
		}
		db.close();

	}

	public void clickPlanButton() {
		btPlan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainScreen.this, PlanDay.class);
				startActivityForResult(intent, 1);
			}
		});
	}

	public void clickCountButton() {
		btCount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainScreen.this, ViewAll.class);
				intent.putExtra("Day", "every");
				startActivityForResult(intent, 1);

			}
		});
	}

	public void clickPhotoButton() {
		btPhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainScreen.this, PlanFuture.class);
				startActivityForResult(intent, 1);

			}
		});
	}

	public void clickPerimeterButton() {
		btPerimeter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainScreen.this, Perimeter.class);
				startActivity(intent);

			}
		});
	}

	public void databaseInit() {
		SQLiteDatabase mydatbase = openOrCreateDatabase("lomo", MODE_PRIVATE,
				null);// Creating
		// the
		// database
		// if
		// not
		// created
		try {// Initializing the database

			// Adding task table if not created before
			mydatbase
					.execSQL("CREATE TABLE IF NOT EXISTS task (taskid integer primary key, taskName VARCHAR(15) not null, description VARCHAR(30),location VARCHAR(50),longitude VARCHAR(25),latitude VARCHAR(25),date VARCHAR(15),time VARCHAR(8),priority VARCHAR(4));");
			mydatbase
					.execSQL("CREATE TABLE IF NOT EXISTS secureDevice (id VARCHAR(1) not null, active VARCHAR(10), s_perimeter VARCHAR(6), receiver VARCHAR(15), t_perimeter VARCHAR(6),location VARCHAR(50),latitude VARCHAR(25),longitude VARCHAR(25),device VARCAHR(10),status VARCHAR(3),sound VARCHAR(3),time VARCHAR(3));");
			Cursor cu = mydatbase.rawQuery("select * from secureDevice", null);
			if (cu.getCount() == 0) {
				mydatbase
						.execSQL("insert into secureDevice values ('1','off','1000','none','1000','none','0','0','Sisiru Galaxy','off','on','20');");
			}

		} catch (Exception e) {
			// Check whether any error has occured in creating the databse
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
		mydatbase.close();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		SQLiteDatabase db = openOrCreateDatabase("lomo", MODE_PRIVATE, null);// Opening
		// the
		// database
		// Checking the number of data filled to the table
		Cursor resultSet = db.rawQuery("select * from task", null);
		DecimalFormat dec = new DecimalFormat("00");
		btCount.setText(dec.format(resultSet.getCount()) + "");
		db.close();
	}

	@Override
	protected void onResume() {

		super.onResume();
		SQLiteDatabase db = openOrCreateDatabase("lomo", MODE_PRIVATE, null);// Opening
		// the
		// database
		// Checking the number of data filled to the table
		Cursor resultSet = db.rawQuery("select * from task", null);
		DecimalFormat dec = new DecimalFormat("00");
		btCount.setText(dec.format(resultSet.getCount()) + "");
		db.close();
	}


	public void clickAboutButton() {
		btAbout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainScreen.this, About.class);
				startActivity(intent);

			}
		});
	}

	// checking whether a receiver mobile number has been added or not
	public void checkReceiverNumber() {
		// opening the database
		SQLiteDatabase db = openOrCreateDatabase("lomo", MODE_PRIVATE, null);
		Cursor result = db.rawQuery("select * from secureDevice where id=1",
				null);
		result.moveToFirst();
		// if the receiver number is not added
		if (result.getString(3).equalsIgnoreCase("none")) {
			// creating an alert dialog
			AlertDialog.Builder alert = new AlertDialog.Builder(MainScreen.this);
			alert.setMessage("Set Alert Receiver Number");
			alert.setTitle("Set");
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// go to the settings page to edit the details
							Intent intent = new Intent(MainScreen.this,
									Settings.class);
							startActivity(intent);

						}
					});
			alert.create().show();
		}
		db.close();// closing the database
	}
}
