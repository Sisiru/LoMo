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
		initialize();// initializing

		clickDone();// clicking the done button
		clickEnableButton();// clicking the enable button
		clickRadius();// clicking thr radius button
		clickLocation();// vlivking the location button
		clickDrawMap();// clicking the drwMap button
		clickCountButton();// clicking the count button
		clickSettings();// clicking the settings button
	}

	private void initialize() {// initializing
		btEnable = (Button) findViewById(R.id.btEnable);
		btRadius = (Button) findViewById(R.id.btRadius);
		btPoint = (Button) findViewById(R.id.btPoint);
		btMap = (Button) findViewById(R.id.btMap);
		btAdvance = (Button) findViewById(R.id.btAdvanced);
		btDone = (Button) findViewById(R.id.btDone);
		btCount = (Button) findViewById(R.id.btCount);

		// opening the database
		SQLiteDatabase myDb = openOrCreateDatabase("lomo", MODE_PRIVATE, null);
		Cursor resultSet = myDb.rawQuery("select * from task", null);// Get all
																		// the
																		// tasks
		DecimalFormat dec = new DecimalFormat("00");
		btCount.setText(dec.format(resultSet.getCount()) + "");// Set the count
		Cursor cursor = myDb.rawQuery("select * from secureDevice", null);// get
																			// the
																			// settings
		cursor.moveToFirst();

		//
		if (cursor.getString(1).equalsIgnoreCase("off")) {
			enabled = false;// secure mode off state
			btEnable.setBackgroundResource(0);
			btRadius.setBackgroundResource(0);
			btPoint.setBackgroundResource(0);
			btDone.setBackgroundResource(0);
			btPoint.setText("");
		} else {
			// secure mode on state
			enabled = true;
			alertS = cursor.getString(1);// default alert mode
			radiusS = cursor.getString(2);
			switch (alertS) {
			case "alert":
				alert = 1;
				break;

			case "contain":
				alert = 2;
				break;

			}

			// setting the radius
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

			// setting the secure mode button background
			if (alert == 1) {
				btEnable.setBackgroundResource(R.drawable.alert);
			} else if (alert == 2) {
				btEnable.setBackgroundResource(R.drawable.containn);
			}

			// setting the radius button background
			if (radius == 1) {
				btRadius.setBackgroundResource(R.drawable.m500);
			} else if (radius == 2) {
				btRadius.setBackgroundResource(R.drawable.m1000);
			} else if (radius == 3) {
				btRadius.setBackgroundResource(R.drawable.m1500);
			}

			// set the background image
			btDone.setBackgroundResource(R.drawable.deactivate);
			if (!cursor.getString(5).equalsIgnoreCase("none")) {
				btPoint.setBackgroundResource(R.drawable.current_selected);
				btPoint.setText(cursor.getString(5));
			}
			// gets the secure location coordinates
			latitude = cursor.getString(6);
			longitude = cursor.getString(7);
		}
		myDb.close();// closing the database
	}

	// when done button is clicked
	private void clickDone() {
		btDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// opening the database
				SQLiteDatabase myDb = openOrCreateDatabase("lomo",
						MODE_PRIVATE, null);
				Cursor cursor = myDb.rawQuery("select * from secureDevice",
						null);// gets settings
				cursor.moveToFirst();
				// gets the secure point location
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
					Log.i("====================", alertS + " " + radiusS + " "
							+ location + " " + longitude + " " + latitude);
					// updating the details
					myDb.execSQL("update secureDevice set  active='" + alertS
							+ "', s_perimeter='" + radiusS + "', location='"
							+ location + "', latitude='" + latitude
							+ "',longitude='" + longitude + "' where id='1'");
					btDone.setBackgroundResource(0);
				} else if (enabled) {
					// if the secure point is not fixed
					if (location.length() == 0) {
						// alert dialog is displyed
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
											// if not fixed opening the map
											// activity to set a location point
											Intent intent = new Intent(
													Perimeter.this,
													GoogleMap.class);
											startActivityForResult(intent, 1234);// starting
																					// the
																					// new
																					// activity
										}
									}
								});
						alertD.create().show();// craeting the alert dialog
					} else {
						cursor = myDb
								.rawQuery(
										"select * from secureDevice where id='1'",
										null);// gets the settings
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
						// updating the details
						myDb.execSQL("update secureDevice set  active='"
								+ alertS + "', s_perimeter='" + radiusS
								+ "', location='" + location + "', latitude='"
								+ latitude + "',longitude='" + longitude
								+ "' where id='1'");
						btDone.setBackgroundResource(R.drawable.deactivate);

					}

				}
				myDb.close();// closing the database
				AlertDialog.Builder alert = new AlertDialog.Builder(
						Perimeter.this);
				// creating an alert dialog
				alert.setTitle("Updated");
				alert.setMessage("The data were updated successfully");
				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();// finishing the activity

							}
						});
				alert.create().show();// craeting the alert dialog
			}
		});
	}

	// when the enable button is clicked
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

	// when the radius button is clicked
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

	// displaying the route followed
	private void clickDrawMap() {
		btMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Perimeter.this, DrawRoute.class);
				startActivity(intent);// opening the new activity
			}
		});
	}

	// to open the map to set a secure point
	private void clickLocation() {
		btPoint.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (enabled) {
					Intent intent = new Intent(Perimeter.this, GoogleMap.class);
					startActivityForResult(intent, 1234);// opening the new
															// activity
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
				startActivityForResult(intent, 1);// opening the new activity
				finish();
			}
		});
	}

	private void clickSettings() {
		btAdvance.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Perimeter.this, Settings.class);
				startActivity(intent);// opening the new activity

			}
		});
	}
}
