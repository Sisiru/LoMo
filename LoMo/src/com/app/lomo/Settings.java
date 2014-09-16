package com.app.lomo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends Activity {
	private Button btRadius;
	private Button btAlertTime;
	private EditText txtCell;
	private Button btReport;
	private Button btAlertSound;
	private Button btDefault;
	private Button btSet;
	private int radius = 1000;
	private int time = 20;
	private boolean sound = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);//Setting the layout
		initialize();//Initializing
		clickDefaultButton();//Clicking Default button
		clickRadius();//Clicking alert radius button
		clickAlertTime();//Clicking alert time button
		clickSound();//Clicking alert sound button
		clickSet();//Clicking set button
	}

	private void initialize(){//Initializing
		//Assigning variables for buttons and the edit text
		btRadius=(Button)findViewById(R.id.btRadius);
		btAlertTime=(Button)findViewById(R.id.btAlert);
		txtCell=(EditText)findViewById(R.id.txtCell);
		btReport=(Button)findViewById(R.id.btReport);
		btAlertSound=(Button)findViewById(R.id.btSound);
		btDefault=(Button)findViewById(R.id.btDefault);
		btSet=(Button)findViewById(R.id.btSet);
		
		//Open the database
		SQLiteDatabase myDb=openOrCreateDatabase("lomo", MODE_PRIVATE, null);
		//Accessing the settings table, 'secureDevice'
		Cursor result=myDb.rawQuery("select * from secureDevice where id='1'", null);
		result.moveToFirst();
		//Taking the stored values for settings
		String radius=result.getString(4);//alert radius
		String alertTime=result.getString(11);//alert time
		String receiver=result.getString(3);//receiver number
		String alertSound=result.getString(10);//alert sound
		
		//Setting the background of the btRadius according to the stored value
		switch (radius) {
		case "1000"://alert radius is 1000 m
			btRadius.setBackgroundResource(0);
			this.radius=1000;
			break;

		case "1500"://alert radius is 1500 m
			btRadius.setBackgroundResource(R.drawable.rad2);
			this.radius=1500;
			break;
			
		case "500"://alert radius is 500 m
			btRadius.setBackgroundResource(R.drawable.rad1);
			this.radius=500;
			break;
		}
		
		//Setting the background of the btAlertTime according to the stored value
		switch (alertTime) {
		case "20"://alert time is 20 minutes
			btAlertTime.setBackgroundResource(0);
			time=20;
			break;

		case "30"://alert time is 30 minutes
			btAlertTime.setBackgroundResource(R.drawable.time1);
			time=30;
			break;
			
		case "60"://alert time is 60 minutes
			btAlertTime.setBackgroundResource(R.drawable.time2);
			time=60;
			break;
			
		case "90"://alert time is 90 minutes
			btAlertTime.setBackgroundResource(R.drawable.time4);
			time=90;
			break;
			
		case "120"://alert time is 120 minutes
			btAlertTime.setBackgroundResource(R.drawable.time3);
			time=120;
			break;
		}
		
		//Setting the value for the receiver mobile number
		txtCell.setText(receiver);
		
		//Setting the background of the btAlertSound according to the stored value
		switch (alertSound) {
		case "on"://alert sound is on
			btAlertSound.setBackgroundResource(0);
			sound=true;
			break;

		case "off"://alert sound is off
			btAlertSound.setBackgroundResource(R.drawable.sound);
			sound=false;
			break;
		}
	}

	//If the button, btDefault is clicked
	private void clickDefaultButton() {
		btDefault.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//Removing the backgrounds which were set for the buttons
				btRadius.setBackgroundResource(0);
				btAlertTime.setBackgroundResource(0);
				btReport.setBackgroundResource(0);
				btAlertSound.setBackgroundResource(0);
				radius = 1000;
				time = 20;
				sound = true;
			}
		});
	}

	//If the button btRadius is clicked
	private void clickRadius() {
		btRadius.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//changing the backgrounds of the btRadius when the button is clicked
				if (radius == 1000) {
					radius = 1500;
					btRadius.setBackgroundResource(R.drawable.rad2);
				} else if (radius == 1500) {
					radius = 500;
					btRadius.setBackgroundResource(R.drawable.rad1);
				} else {
					radius = 1000;
					btRadius.setBackgroundResource(0);
				}
			}
		});
	}

	//when btAlertTime is clicked
	private void clickAlertTime() {
		btAlertTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (time == 20) {
					time = 30;//alert time=30 minutes
					btAlertTime.setBackgroundResource(R.drawable.time1);
				} else if (time == 30) {
					time = 60;//alert time=60 minutes
					btAlertTime.setBackgroundResource(R.drawable.time2);
				} else if (time == 60) {
					time = 90;//alert time=90 minutes
					btAlertTime.setBackgroundResource(R.drawable.time4);
				} else if (time == 90) {
					time = 120;//alert time=120 minutes
					btAlertTime.setBackgroundResource(R.drawable.time3);
				} else {
					time = 20;//alert time=20 minutes
					btAlertTime.setBackgroundResource(0);
				}

			}
		});
	}

	//when btAlertSound is clicked
	private void clickSound() {
		btAlertSound.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sound) {
					sound = false;//sound off
					btAlertSound.setBackgroundResource(R.drawable.sound);
				} else {
					sound = true;//sound on
					btAlertSound.setBackgroundResource(0);
				}

			}
		});
	}

	//when btSet is clicked
	private void clickSet() {
		btSet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String sound = "on";
				if (Settings.this.sound) {
					sound = "on";
				} else {
					sound = "off";
				}
				//Getting the receiver number from the edit text
				String receiver = "";
				if (txtCell.getText().toString().equals("")) {
					receiver = "none";
				} else {
					receiver = txtCell.getText().toString();
				}
				Log.i("update info", radius + " " + time + " " + receiver + " "
						+ sound);
				//open database
				SQLiteDatabase myDB = openOrCreateDatabase("lomo",
						MODE_PRIVATE, null);
				//updating the information
				myDB.execSQL("update secureDevice set t_perimeter='" + radius
						+ "',time='" + time + "',receiver='" + receiver
						+ "',sound='" + sound + "'");
				myDB.close();//closing the database
				
				AlertDialog.Builder alert=new AlertDialog.Builder(Settings.this);
				alert.setTitle("Updated");
				alert.setMessage("The data were updated successfully");
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						
					}
				});
				alert.create().show();
			}
		});
	}

}
