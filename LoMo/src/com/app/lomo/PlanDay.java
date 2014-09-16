package com.app.lomo;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class PlanDay extends FragmentActivity {

	private EditText txtTask;
	private EditText txtDescription;
	private Button btLocation;
	private Button btDate;
	private Button btStart;
	private Button btEnd;
	private Button btDone;
	private Button btCancel;
	private Button btCount;
	private String longtitude;
	private String latitude;
	int start = -1;
	private String formatteddate="";
	private String dbDate="";
	private boolean priority = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.plan_day);
		initialize();
		connectDb();
		clickDateButton();
		clickStartTimeButton();
		clickEndTimeButton();
		clickLocationButton();
		clickCancelButton();
		clickDoneButton();
		clickCountButton();
	}

	public void initialize() {// Initializing
		// Assigning the text fields and the buttons to variables
		txtTask = (EditText) findViewById(R.id.txtTask);
		txtDescription = (EditText) findViewById(R.id.txtDescrition);
		btLocation = (Button) findViewById(R.id.btLocation);
		btDate = (Button) findViewById(R.id.btDate);
		btStart = (Button) findViewById(R.id.btStart);
		btEnd = (Button) findViewById(R.id.btEnd);
		btDone = (Button) findViewById(R.id.btDone);
		btCancel = (Button) findViewById(R.id.btCancel);
		btCount = (Button) findViewById(R.id.btPlanCount);
	}

	public void connectDb() {// Finding the total number of tasks in the
								// database
		SQLiteDatabase db = openOrCreateDatabase("lomo", MODE_PRIVATE, null);// Opening
																				// the
		// database
		// Checking the number of data filled to the table
		Cursor resultSet = db.rawQuery("select * from task", null);// Get all
																	// the tasks
		DecimalFormat dec = new DecimalFormat("00");
		btCount.setText(dec.format(resultSet.getCount()) + "");// Set the count
		db.close();
	}

	@SuppressLint("ValidFragment")
	public void clickDateButton() {
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat sd = new SimpleDateFormat("dd MMMM, yyyy",
				Locale.ENGLISH);
		SimpleDateFormat sd2 = new SimpleDateFormat("yyyy-MM-dd",
				Locale.ENGLISH);
		
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			formatteddate=bundle.getString("Display");
			dbDate=bundle.getString("Database");
		}else{
			dbDate=sd2.format(date);
			formatteddate=sd.format(date);;
		}
		btDate.setText(formatteddate);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint({ "ValidFragment", "NewApi" })
	public class DatePickerdialog extends DialogFragment implements
			OnDateSetListener {
		private String date = "-1";

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			DecimalFormat formatMonth = new DecimalFormat("00");
			DecimalFormat formatYear = new DecimalFormat("0000");
			date = formatYear.format(year) + "-"
					+ formatMonth.format(monthOfYear + 1) + "-"
					+ formatMonth.format(dayOfMonth);
			btDate.setText(date);

		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			DatePickerDialog dateP = new DatePickerDialog(getActivity(), this,
					year, month, day);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				dateP.getDatePicker().setMinDate(
						System.currentTimeMillis() - 1000);
			}
			return dateP;
		}

	}

	public void clickStartTimeButton() {// Seetting the time
		btStart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment fra = new TimePickerdialog();
				fra.show(getSupportFragmentManager(), "TimePicker");
				start = 0;
			}
		});
	}

	public void clickEndTimeButton() {
		btEnd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!priority) {
					btEnd.setBackgroundResource(R.drawable.highbutton);
					priority = true;
				} else {
					btEnd.setBackgroundResource(R.drawable.lowbutton);
					priority = false;
				}
			}
		});
	}

	@SuppressLint("ValidFragment")
	public class TimePickerdialog extends DialogFragment implements
			OnTimeSetListener {
		String time = "";

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			DecimalFormat formatTime = new DecimalFormat("00");
			time = formatTime.format(hourOfDay) + ":"
					+ formatTime.format(minute);
			if (start == 0) {
				btStart.setText(time);
			} else if (start == 1) {
				btEnd.setText(time);
			}
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int hours = c.get(Calendar.HOUR_OF_DAY);
			int minutes = c.get(Calendar.MINUTE);
			return new TimePickerDialog(getActivity(), this, hours, minutes,
					DateFormat.is24HourFormat(getActivity()));
		}

	}

	public void clickLocationButton() {
		btLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PlanDay.this, GoogleMap.class);
				startActivityForResult(intent, 1);
			}
		});
	}

	public void clickDoneButton() {// Click done button
		btDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SQLiteDatabase db = openOrCreateDatabase("lomo", MODE_PRIVATE,
						null);// Opening the
				// database
				// Checking the number of data filled to the table
				Cursor resultSet = db.rawQuery("select * from task", null);// Get
																			// all
																			// the
																			// tasks
				// Getting the data from text fields and buttons
				String task = txtTask.getText().toString();
				String description = txtDescription.getText().toString();
				String location = btLocation.getText().toString();
				// String date = btDate.getText().toString();
				String startTime = btStart.getText().toString();
				String priority = "";
				if (PlanDay.this.priority) {
					priority = "high";
				} else {
					priority = "low";
				}

				// Checking whether all the text fields and buttons are filled
				// with data
				if (task.length() == 0 || description.length() == 0
						|| location.length() == 0
						|| dbDate.length() == 0
						|| startTime.length() == 0 || priority.length() == 0) {
					// If the task data is incomplete, user is instructed to
					// fill them using an alert dialog
					AlertDialog.Builder fill = new AlertDialog.Builder(
							PlanDay.this);
					fill.setTitle("Incomplete Procedure");
					fill.setMessage("Please fill the incomplete data");
					// Alert dialog asks the user to fill the incomplete data
					fill.setNegativeButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();

								}
							});
					fill.create().show();// Viewing the alert dialog
				} else {
					int count = 1;
					if (resultSet.getCount() == 0) {
						count = 1;
					} else {
						resultSet.moveToFirst();
						count = resultSet.getCount() + 1;// Incresing the count
															// by 1 to insert
															// into db
					}
					// Toast.makeText(PlanDay.this, count+"",
					// Toast.LENGTH_SHORT).show();
					try {
						// Adding data into the task table
						db.execSQL("insert into task values (" + count + ",'"
								+ task + "','" + description + "','" + location
								+ "','" + longtitude + "','" + latitude + "','"
								+ dbDate + "','" + startTime + "','"
								+ priority + "');");
						// setAlarmData();
						db.close();
						finish();
					} catch (Exception e) {
						txtTask.setText(count);
						txtDescription.setText(" ");
					}
				}

				db.close();
			}
		});

	}

	public boolean hasConnection() {
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}

		return false;
	}

	public void clickCancelButton() {
		btCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder cancel = new AlertDialog.Builder(
						PlanDay.this);
				cancel.setTitle("Cancel the Task");
				cancel.setMessage("Are you sure you want to cancel this task?");
				cancel.setNegativeButton("No",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

							}
						});
				cancel.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();

							}
						});
				cancel.create().show();

			}
		});

	}

	@Override
	protected void onActivityResult(int requstCode, int resultCode,
			Intent intent) {
		if (resultCode == 1234) {
			// Toast.makeText(PlanDay.this, "1", Toast.LENGTH_SHORT).show();
			String address = intent.getStringExtra("Address");
			String lon = intent.getDoubleExtra("Longitude", 1) + "";
			String lat = intent.getDoubleExtra("Latitude", 1) + "";
			// Toast.makeText(PlanDay.this, address+" "+lon+" "+lat,
			// Toast.LENGTH_SHORT).show();
			if (address.length() == 0) {// If address could not be found
				// Toast.makeText(PlanDay.this, "2", Toast.LENGTH_SHORT).show();
				address = "Lon:" + lon + " Lat:" + lat;// Address set as the lat
														// and lon location
			}
			btLocation.setText(address);// Setting the location from the google
										// map
			longtitude = lon;// Getting the longitude value of the location
			latitude = lat;// Getting the latitude value of the location
		}

		super.onActivityResult(requstCode, resultCode, intent);
	}

	public void clickCountButton() {
		btCount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PlanDay.this, ViewAll.class);
				startActivityForResult(intent, 1);
				finish();
			}
		});
	}

}
