package com.app.lomo;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;

public class PlanFuture extends Activity{
	private CalendarView calendar;
	private Button btCount;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.future);
		initialize();
		connectDb();
		clickCalendar();
		clickCountButton();
	}
	
	@SuppressLint("NewApi")
	private void initialize(){//Initializing
		calendar=(CalendarView)findViewById(R.id.cvCal);//Getting the calendar view
		long todayDate=Calendar.getInstance().getTimeInMillis();
		calendar.setMinDate(todayDate-1000);
		btCount=(Button)findViewById(R.id.btCount);
	}
	
	private void connectDb(){//Finding the total number of tasks in the database
		SQLiteDatabase db = openOrCreateDatabase("lomo", MODE_PRIVATE, null);//Opening the database
		//Checking the number of data filled to the table
		Cursor resultSet = db.rawQuery("select * from task", null);//Get all the tasks
		DecimalFormat dec=new DecimalFormat("00");
		btCount.setText(dec.format(resultSet.getCount())+"");//Set the count
		db.close();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	private void clickCalendar(){
		calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month,
					int dayOfMonth) {
				DecimalFormat dc=new DecimalFormat("00");
				String dbDate=year+"-"+dc.format(month)+"-"+dc.format(dayOfMonth);
				String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
				String selectedDate=dc.format(dayOfMonth)+" "+monthNames[month]+", "+year;
				Intent intent=new Intent(PlanFuture.this,PlanDay.class);
				intent.putExtra("Database", dbDate);
				intent.putExtra("Display", selectedDate);
				startActivityForResult(intent, 1234);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==1234){
			connectDb();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void clickCountButton(){
		btCount.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(PlanFuture.this, ViewAll.class);
				startActivityForResult(intent, 1234);
				finish();
			}
		});
	}
}
