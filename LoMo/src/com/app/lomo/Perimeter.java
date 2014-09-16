package com.app.lomo;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class Perimeter extends Activity {

	private Button btEnable;
	private Button btRadius;
	private Button btPoint;
	private Button btMap;
	private Button btAdvance;
	private Button btDone;
	private boolean enabled = false;
	private int alert = 0;
	private int radius = 0;
	private String alertS, radiusS;
	private String latitude;
	private String longitude;
	private Button btCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.perimeter);// Sets the layout
		initialize();

		clickDone();
		clickEnableButton();
		clickRadius();
		clickLocation();
		clickDrawMap();
		clickCountButton();
		clickSettings();
	}

	private void initialize() {
		btEnable = (Button) findViewById(R.id.btEnable);
		btRadius = (Button) findViewById(R.id.btRadius);
		btPoint = (Button) findViewById(R.id.btPoint);
		btMap = (Button) findViewById(R.id.btMap);
		btAdvance = (Button) findViewById(R.id.btAdvanced);
		btDone = (Button) findViewById(R.id.btDone);
		btCount=(Button)findViewById(R.id.btCount);
		
		SQLiteDatabase myDb = openOrCreateDatabase("lomo", MODE_PRIVATE, null);
		Cursor resultSet = myDb.rawQuery("select * from task", null);// Get all the tasks
		DecimalFormat dec = new DecimalFormat("00");
		btCount.setText(dec.format(resultSet.getCount()) + "");// Set the count
		Cursor cursor = myDb.rawQuery("select * from secureDevice", null);
		cursor.moveToFirst();

		//
		if (cursor.getString(1).equalsIgnoreCase("off")) {
			enabled = false;
			btEnable.setBackgroundResource(0);
			btRadius.setBackgroundResource(0);
			btPoint.setBackgroundResource(0);
			btDone.setBackgroundResource(0);
			btPoint.setText("");
		} else {
			enabled = true;
			alertS = cursor.getString(1);
			radiusS = cursor.getString(2);
			switch (alertS) {
			case "alert":
				alert = 1;
				break;

			case "contain":
				alert = 2;
				break;

			}

			switch (radiusS) {
			case "1000":
				radius = 2;
				break;
			case "0":
				radius = 0;
				break;
			case "500":
				radius = 1;
				break;
			case "1500":
				radius = 3;
				break;
			}

			if (alert == 1) {
				btEnable.setBackgroundResource(R.drawable.alert);
			} else if (alert == 2) {
				btEnable.setBackgroundResource(R.drawable.containn);
			}

			if (radius == 1) {
				btRadius.setBackgroundResource(R.drawable.m500);
			} else if (radius == 2) {
				btRadius.setBackgroundResource(R.drawable.m1000);
			} else if (radius == 3) {
				btRadius.setBackgroundResource(R.drawable.m1500);
			}

			btDone.setBackgroundResource(R.drawable.deactivate);
			if (!cursor.getString(5).equalsIgnoreCase("none")) {
				btPoint.setBackgroundResource(R.drawable.current_selected);
				btPoint.setText(cursor.getString(5));
			}
			latitude = cursor.getString(6);
			longitude = cursor.getString(7);
		}
		myDb.close();
	}


	private void clickDone() {
		btDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SQLiteDatabase myDb = openOrCreateDatabase("lomo", MODE_PRIVATE, null);
				Cursor cursor = myDb.rawQuery("select * from secureDevice", null);
				cursor.moveToFirst();
				String location = btPoint.getText().toString();
				if (!cursor.getString(1).equalsIgnoreCase("off")) {
					alertS = "off";
					alert = 0;
					enabled = false;
					radius = 0;
					radiusS = "0";
					location = "none";
					latitude = "0";
					longitude = "0";
					Log.i("====================", alertS + " " + radiusS
							+ " " + location + " " + longitude + " "
							+ latitude);
					myDb.execSQL("update secureDevice set  active='"
							+ alertS + "', s_perimeter='" + radiusS
							+ "', location='" + location + "', latitude='"
							+ latitude + "',longitude='" + longitude
							+ "' where id='1'");
					btDone.setBackgroundResource(0);
				} else if(enabled){
					if (location.length() == 0) {
						AlertDialog.Builder alertD = new AlertDialog.Builder(
								Perimeter.this);
						alertD.setTitle("Fix Location");
						alertD.setMessage("You must fix a location for the functionlity");
						alertD.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
										if (enabled) {
											Intent intent = new Intent(
													Perimeter.this,
													GoogleMap.class);
											startActivityForResult(intent, 1234);
										}
									}
								});
						alertD.create().show();
					} else {
						cursor = myDb
								.rawQuery(
										"select * from secureDevice where id='1'",
										null);
						cursor.moveToFirst();
						/*
						 * Toast.makeText(Perimeter.this, cursor.getCount()+"",
						 * Toast.LENGTH_SHORT).show(); if
						 * (cursor.getString(3).equalsIgnoreCase("none")) {
						 * AlertDialog.Builder alert = new
						 * AlertDialog.Builder(Perimeter.this);
						 * alert.setTitle("Recipient Number");
						 * alert.setMessage("Do you wish to add a recipient number?"
						 * ); alert.setPositiveButton("Yes", new
						 * DialogInterface.OnClickListener() {
						 * 
						 * @Override public void onClick(DialogInterface dialog,
						 * int which) { dialog.cancel(); // Open the settings to
						 * enter the recipient number
						 * 
						 * } }); alert.setNegativeButton("No", new
						 * DialogInterface.OnClickListener() {
						 * 
						 * @Override public void onClick(DialogInterface dialog,
						 * int which) { dialog.cancel();
						 * 
						 * } }); alert.create().show(); }
						 */

						if (alertS == null)
							alertS = "off";
						if (radiusS == null)
							radiusS = "0";
						if (latitude == null || longitude == null) {
							alertS = "off";
							radiusS = "0";
							location = "none";
							latitude = "0";
							longitude = "0";
						}
						Log.i("====================", alertS + " " + radiusS
								+ " " + location + " " + longitude + " "
								+ latitude);
						myDb.execSQL("update secureDevice set  active='"
								+ alertS + "', s_perimeter='" + radiusS
								+ "', location='" + location + "', latitude='"
								+ latitude + "',longitude='" + longitude
								+ "' where id='1'");
						btDone.setBackgroundResource(R.drawable.deactivate);

					}

				}
				myDb.close();
				AlertDialog.Builder alert=new AlertDialog.Builder(Perimeter.this);
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

	private void clickEnableButton() {
		btEnable.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (enabled && alert == 1) {
					btEnable.setBackgroundResource(R.drawable.containn);
					alert = 2;
					alertS = "contain";
				} else if (enabled && alert == 2) {
					alertS = "off";
					alert = 0;
					enabled = false;
					btEnable.setBackgroundResource(0);
					btRadius.setBackgroundResource(0);
					btPoint.setBackgroundResource(0);
					btDone.setBackgroundResource(0);
					radius = 0;
					radiusS = "0";
					btPoint.setText("");
				} else if (!enabled) {
					btEnable.setBackgroundResource(R.drawable.alert);
					alert = 1;
					alertS = "alert";
					enabled = true;

					btRadius.setBackgroundResource(R.drawable.m1000);
					radius = 2;
					radiusS = "1000";

					btPoint.setBackgroundResource(R.drawable.current);

				}
			}
		});
	}

	private void clickRadius() {
		btRadius.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (enabled && radius == 0) {
					btRadius.setBackgroundResource(R.drawable.m1000);
					radius = 2;
					radiusS = "1000";
				} else if (enabled && radius == 2) {
					btRadius.setBackgroundResource(R.drawable.m1500);
					radius = 3;
					radiusS = "1500";
				} else if (enabled && radius == 3) {
					btRadius.setBackgroundResource(R.drawable.m500);
					radius = 1;
					radiusS = "500";
				} else if (enabled && radius == 1) {
					btRadius.setBackgroundResource(R.drawable.m1000);
					radius = 2;
					radiusS = "1000";
				}

			}
		});
	}

	private void clickDrawMap() {
		btMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Perimeter.this, DrawRoute.class);
				startActivity(intent);
			}
		});
	}

	private void clickLocation() {
		btPoint.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (enabled) {
					Intent intent = new Intent(Perimeter.this, GoogleMap.class);
					startActivityForResult(intent, 1234);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1234) {
			String address = data.getStringExtra("Address");
			longitude = data.getDoubleExtra("Longitude", 1) + "";
			latitude = data.getDoubleExtra("Latitude", 1) + "";

			// Toast.makeText(Perimeter.this,
			// address+" "+longitude+" "+latitude, Toast.LENGTH_SHORT).show();
			if (address.length() == 0) {// If address could not be found
				// Toast.makeText(PlanDay.this, "2", Toast.LENGTH_SHORT).show();
				address = "Lon:" + longitude + " Lat:" + latitude;// Address set
																	// as the
																	// lat and
																	// lon
																	// location
			}
			if (longitude == null || latitude == null) {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						Perimeter.this);
				alert.setTitle("Location Selection");
				alert.setMessage("Location could not be tracked.Please select it again");
				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

							}
						});
			}
			btPoint.setBackgroundResource(R.drawable.current_selected);
			btPoint.setText(address);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	private void clickCountButton() {
		btCount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Perimeter.this, ViewAll.class);
				startActivityForResult(intent,1);
				finish();
			}
		});
	}
	
	private void clickSettings(){
		btAdvance.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(Perimeter.this,Settings.class);
				startActivity(intent);
				
			}
		});
	}
}
