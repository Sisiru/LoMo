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

//The main screen of the application
public class MainScreen extends Activity {
	private Button btCount;
	private Button btPlan;
	private Button btPhoto;
	private Button btPerimeter;
	private Button btSettings;
	private Button btAbout;
	private Button btHelp;
	private LinearLayout lDisplay;
	private boolean status = false;
	private boolean help=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//removes the title bar
		setContentView(R.layout.fragment_main);//setting the layout
		databaseInit();//initializing database
		initialize();//initializing the variables
		clickPlanButton();//add a task for current day
		clickCountButton();//view all
		clickFutureTaskButton();//add a task for a future day
		clickPerimeterButton();//secure device option
		clickEnable();//enabling and disabling the lomo action
		clickAboutButton();//view about
		checkReceiverNumber();//check whether a receiver no added or not
		// LocationTracker lTrack=new LocationTracker(MainScreen.this);
		clickHelpButton();

	}

	//starting the background service
	public void startService() {
		Intent intent = new Intent(MainScreen.this, TrackLocation.class);
		startService(intent);//starting the service
	}

	//stopping the background service
	public void stopService() {
		Intent intent = new Intent(MainScreen.this, TrackLocation.class);
		stopService(intent);//stop service
	}

	//when clicking the enable/disable button 
	public void clickEnable() {
		btSettings.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//opening the database
				SQLiteDatabase mydatbase = openOrCreateDatabase("lomo",
						MODE_PRIVATE, null);
				//check whether the lomo service activated or not
				if (status) {
					//if the lomo app is in on state,set it to off 
					status = false;
					lDisplay.setBackgroundResource(R.drawable.main2);//background change
					//updating the database
					mydatbase
							.execSQL("update secureDevice set status='off' where id='1'");
					stopService();//stop service
				} else {
					//if the lomo app is in off state, activate it
					status = true;
					lDisplay.setBackgroundResource(R.drawable.main1);//background change
					//updating the database
					mydatbase
							.execSQL("update secureDevice set status='on' where id='1'");
					startService();//start service
				}
				mydatbase.close();//close database
			}
		});
	}

	//initializing
	public void initialize() {
		btCount = (Button) findViewById(R.id.btCount);
		btPlan = (Button) findViewById(R.id.btPlan);
		btPhoto = (Button) findViewById(R.id.btFuture);
		btPerimeter = (Button) findViewById(R.id.btPerimeter);
		btSettings = (Button) findViewById(R.id.btSettings);
		btAbout = (Button) findViewById(R.id.btAbout);
		btHelp=(Button)findViewById(R.id.btHelp);

		SQLiteDatabase db = openOrCreateDatabase("lomo", MODE_PRIVATE, null);// Opening
		// the
		// database
		// Checking the number of data filled to the table
		Cursor resultSet = db.rawQuery("select * from task", null);
		DecimalFormat dec = new DecimalFormat("00");
		//set the total count of the tasks to be completed
		btCount.setText(dec.format(resultSet.getCount()) + "");

		lDisplay = (LinearLayout) findViewById(R.id.myback);
		//check whether lomo app is ativated at the beginning
		Cursor result = db.rawQuery("select * from secureDevice where id='1'",
				null);
		result.moveToFirst();
		if (result.getString(9).equalsIgnoreCase("on")) {
			//if the lomo is activated
			status = true;
			lDisplay.setBackgroundResource(R.drawable.main1);//set the relevant background
		} else {
			//if deactivated
			status = false;
			lDisplay.setBackgroundResource(R.drawable.main2);//set the relevant background
		}
		db.close();//closing the database

	}

	//adding a new task for current day
	public void clickPlanButton() {
		btPlan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainScreen.this, PlanDay.class);
				startActivityForResult(intent, 1);//starting the add task activity
			}
		});
	}

	//when the count button is clicked
	public void clickCountButton() {
		btCount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainScreen.this, ViewAll.class);
				intent.putExtra("Day", "every");//passes extra data
				startActivityForResult(intent, 1);//starting the view all activity

			}
		});
	}

	//when user wants to add a task for future
	public void clickFutureTaskButton() {
		btPhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//starting the activity
				Intent intent = new Intent(MainScreen.this, PlanFuture.class);
				startActivityForResult(intent, 1);

			}
		});
	}

	//secure my device option
	public void clickPerimeterButton() {
		btPerimeter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainScreen.this, Perimeter.class);
				startActivity(intent);

			}
		});
	}
	
	public void clickHelpButton(){
		btHelp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!help){
					if(status){
						lDisplay.setBackgroundResource(R.drawable.help1);
					}else{
						lDisplay.setBackgroundResource(R.drawable.help2);
					}
					help=true;
				}else{
					if(status){
						lDisplay.setBackgroundResource(R.drawable.main1);
					}else{
						lDisplay.setBackgroundResource(R.drawable.main2);
					}
					help=false;
				}
				
				
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
