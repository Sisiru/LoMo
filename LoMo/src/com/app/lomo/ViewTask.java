package com.app.lomo;

import java.text.DecimalFormat;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class ViewTask extends FragmentActivity {
	EditText txtTask;
	EditText txtDescription;
	Button btLocation;
	Button btDate;
	Button btStart;
	Button btEnd;
	Button btDone;
	Button btCancel;
	private Button btCount;
	boolean filled = true;
	int start = -1;
	private String position;
	private boolean priority=false;
	String longitude="";
	String latitude="";
	String address="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_task);
		initialize();
		initDatabase();
		clickCancelButton();
		clickDoneButton();
		clickCountButton();
		clickDateButton();
		clickEndTimeButton();
		clickLocationButton();
		clickStartTimeButton();
	}

	private void initialize() {
		txtTask = (EditText) findViewById(R.id.txtTask);
		txtDescription = (EditText) findViewById(R.id.txtDescrition);
		btLocation = (Button) findViewById(R.id.btLocation);
		btDate = (Button) findViewById(R.id.btDate);
		btStart = (Button) findViewById(R.id.btStart);
		btEnd = (Button) findViewById(R.id.btEnd);
		btDone = (Button) findViewById(R.id.btDone);
		btCancel = (Button) findViewById(R.id.btCancel);
		position=getIntent().getStringExtra("Position");
		btCount=(Button)findViewById(R.id.btPlanCount);
		//Toast.makeText(ViewTask.this, position, Toast.LENGTH_SHORT).show();
		
	}

	private void clickDoneButton() {
		btDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String priorityString="";
				String task = txtTask.getText().toString();
				String description = txtDescription.getText().toString();
				String location = btLocation.getText().toString();
				String date = btDate.getText().toString();
				String startTime = btStart.getText().toString();
				if(priority){
					priorityString="high";
				}else{
					priorityString="low";
				}

				if (task.length() == 0 || description.length() == 0
						|| location.length() == 0 || date.length() == 0
						|| startTime.length() == 0) {
					AlertDialog.Builder fill = new AlertDialog.Builder(
							ViewTask.this);
					fill.setTitle("Incomplete Procedure");
					fill.setMessage("Please fill the incomplete data");
					fill.setNegativeButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();

								}
							});
					fill.create().show();
				}else{
					Log.i("=============================================", task+" "+description+" "+location+" "+latitude+" "+longitude+" "+date+" "+startTime+" "+priorityString);
					SQLiteDatabase db=openOrCreateDatabase("lomo", MODE_PRIVATE, null);
					db.execSQL("update task set taskName='"+task+"',description='"+description+"',location='"+location+"',latitude='"+latitude+"',longitude='"+longitude+"',date='"+date+"',time='"+startTime+"',priority='"+priorityString+"' where taskid='"+position+"'");
					db.close();
					finish();
				}

			}
		});
	}

	private void clickCancelButton() {
		btCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder cancel = new AlertDialog.Builder(
						ViewTask.this);
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
	
	private void initDatabase(){
		SQLiteDatabase db=openOrCreateDatabase("lomo", MODE_PRIVATE, null);
		Cursor resultTask=db.rawQuery("select * from task where taskid='"+position+"';", null);
		resultTask.moveToFirst();
		txtTask.setText(resultTask.getString(1));
		txtDescription.setText(resultTask.getString(2));
		btLocation.setText(resultTask.getString(3));
		btDate.setText(resultTask.getString(6));
		btStart.setText(resultTask.getString(7));
		if(resultTask.getString(8).equalsIgnoreCase("high")){
			btEnd.setBackgroundResource(R.drawable.highbutton);
			priority=true;
		}else{
			btEnd.setBackgroundResource(R.drawable.lowbutton);
			priority=false;
		}
		db.close();
	}
	
	private void clickCountButton() {
		btCount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ViewTask.this, ViewAll.class);
				startActivityForResult(intent,1);

			}
		});
	}
	
	@SuppressLint("ValidFragment")
	private void clickDateButton() {
		btDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerdialog datepicker = new DatePickerdialog();
				datepicker.show(getSupportFragmentManager(), "MyDate");

			}
		});

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

	private void clickStartTimeButton() {// Seetting the time
		btStart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment fra = new TimePickerdialog();
				fra.show(getSupportFragmentManager(), "TimePicker");
				start = 0;
			}
		});
	}

	private void clickEndTimeButton() {
		btEnd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!priority){
					btEnd.setBackgroundResource(R.drawable.highbutton);
					priority=true;
				}else{
					btEnd.setBackgroundResource(R.drawable.lowbutton);
					priority=false;
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

	private void clickLocationButton() {
		btLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ViewTask.this, GoogleMap.class);
				startActivityForResult(intent, 1);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requstCode, int resultCode,
			Intent intent) {
		if (resultCode == 1234) {
			// Toast.makeText(PlanDay.this, "1", Toast.LENGTH_SHORT).show();
			address = intent.getStringExtra("Address");
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
			longitude = lon;// Getting the longitude value of the location
			latitude = lat;// Getting the latitude value of the location
			Log.i("=============================================", latitude+" "+longitude);
		}

		super.onActivityResult(requstCode, resultCode, intent);
	}
	



}
