package com.app.lomo;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

//Background service class to track locations
public class TrackLocation extends Service {

	public LocationManager locationManager;
	private long checkTime = 1200000;// The service will check the location once
										// checkTime
	private ArrayList<Task> taskArray = new ArrayList<>();// Array to store
															// tasks added so
															// far
	private ArrayList<Task> nearLocations = new ArrayList<>();// Array to store
																// tasks wich
																// are to be
																// notified

	private ArrayList<Location> locatonData = new ArrayList<>();
	private IBinder mBinder = new MyBinder();
	Intent intent;
	int counter = 0;
	PendingIntent pending;
	// For checking the availability of the location providers
	boolean gps_available = false;
	boolean network_vailable = false;

	private double proximityDistance = 1000;
	TaskStackBuilder stackBuilder;

	// this method is called only once when the service is called for the first
	// time
	@Override
	public void onCreate() {
		super.onCreate();
		// startMyAct();
		// deleteExpiredTasks();

	}

	// this method is called when the service is invoked
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		while (!databaseExist()) {
			// checks whether the database exists
		}
		// opening the database
		SQLiteDatabase myDb = openOrCreateDatabase("lomo", MODE_PRIVATE, null);
		// get secure device details
		Cursor res = myDb.rawQuery("select * from secureDevice where id='1'",
				null);
		res.moveToFirst();
		// get alert frequency
		String time = res.getString(11);
		// get the status of the application 'on' or 'off'
		String alert = res.getString(9);
		// closing the database
		myDb.close();

		// converting the alert time into milliseconds
		checkTime = Integer.parseInt(time) * 1000;

		if (alert.equalsIgnoreCase("on")) {
			// if lomo service is 'on'

			// removing the location updates which were stored
			Calendar cal = Calendar.getInstance();
			if (cal.get(Calendar.HOUR_OF_DAY) <= 6
					|| cal.get(Calendar.HOUR_OF_DAY) >= 22) {
				try {
					// clearing the array
					locatonData.clear();
				} catch (Exception e) {
					Toast.makeText(getBaseContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}

			addLocData();// Inserting the available task details into an array

			// initializing the location manager
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);// Initializing
																							// the
																							// location
																							// manager
			// Detecting whether the providers are available
			gps_available = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);// gps
																		// provider
			network_vailable = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);// network
																			// provider

			if (gps_available) {// If gps provider is enabled
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, checkTime, 0,
						gpsLocationListener);// seek gps location updates
			} else if (network_vailable) {
				invokeNetwork();// else network location updates are invoked
			} else {
				AlertDialog.Builder alertD = new AlertDialog.Builder(
						TrackLocation.this);
				alertD.setTitle("Provider not available");
				alertD.setMessage("Please turn on the GPS settings");
				alertD.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(intent);// Opens the GPS settings
														// page
							}
						});

			}
		}

		return START_STICKY;
	}

	// Invokes this method if
	private void invokeNetwork() {
		Log.i("Network", "Invoked");
		// request location updates from the network provider
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, checkTime, 0,
				networkLocationListener);

	}

	@Override
	public IBinder onBind(Intent intent) {
		// returns the binder object
		return mBinder;
	}

	@Override
	public void onDestroy() {
		// handler.removeCallbacks(sendUpdatesToUI);
		super.onDestroy();
		// at the end service is stopped
		Log.v("STOP_SERVICE", "DONE");
		if (locationManager != null) {
			// removes the location updates
			locationManager.removeUpdates(gpsLocationListener);
		}
	}

	private final LocationListener gpsLocationListener = new LocationListener() {
		// gps location listener
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {// checks whether gps available
			case LocationProvider.AVAILABLE:
				Toast.makeText(TrackLocation.this, "GPS Available",
						Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.OUT_OF_SERVICE:// checks whether the service
													// is out
				Toast.makeText(TrackLocation.this, "GPS Out of service",
						Toast.LENGTH_SHORT).show();
				break;

			// checks whether the service is temporary unavailable
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Toast.makeText(TrackLocation.this,
						"GPS Temorarily unavailable", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(TrackLocation.this, "GPS provider enabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(TrackLocation.this, "GPS provider disabled",
					Toast.LENGTH_SHORT).show();
			invokeNetwork();
		}

		@Override
		public void onLocationChanged(Location location) {
			// when the current location is changed
			Toast.makeText(getBaseContext(),
					location.getLatitude() + " " + location.getLongitude(),
					Toast.LENGTH_SHORT).show();
			// adding the location into the loationData array
			locatonData.add(location);
			// notifying the new location tracked
			Toast.makeText(getBaseContext(), locatonData.size() + "",
					Toast.LENGTH_SHORT).show();
			Log.i("**************************************", "Location changed ");

			double latitude;
			double longitude;
			try {
				// passing the location details to the intent variable
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				locationManager.removeUpdates(gpsLocationListener);
				intent.putExtra("Latitude", latitude);// latitude value
				intent.putExtra("Longitude", longitude);// longitude value
				intent.putExtra("Provider", location.getProvider());// provider

			} catch (Exception e) {
			}

			// parsing the latitude and longitude values into double
			double cLat = location.getLatitude();
			double cLon = location.getLongitude();

			Toast.makeText(
					TrackLocation.this,
					"New GPS location: " + String.format("%9.6f", cLat) + ", "
							+ String.format("%9.6f", cLon), Toast.LENGTH_SHORT)
					.show();
			Log.i("================", "New GPS location: " + cLat + ", " + cLon);

			seekDistance(cLon, cLat);
			secureDevice(location);// secure device option
			sendMsg();// send message option
		}
	};
	private final LocationListener networkLocationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			case LocationProvider.AVAILABLE:
				Toast.makeText(TrackLocation.this, "Network Available",
						Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.OUT_OF_SERVICE:
				Toast.makeText(TrackLocation.this, "Network out of service",
						Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Toast.makeText(TrackLocation.this,
						"Network Temporarily unavailable", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(TrackLocation.this, "Network provider enabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(TrackLocation.this, "Network provider disabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.i("================", "On Location Changed Method");
			locatonData.add(location);
			Toast.makeText(getBaseContext(), locatonData.size() + "",
					Toast.LENGTH_SHORT).show();
			Log.i("**************************************", "Location changed");

			try {
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
				locationManager.removeUpdates(networkLocationListener);
				intent.putExtra("Latitude", latitude);
				intent.putExtra("Longitude", longitude);
				intent.putExtra("Provider", location.getProvider());
			} catch (Exception e) {

			}

			double cLat = location.getLatitude();
			double cLon = location.getLongitude();

			Toast.makeText(TrackLocation.this,
					"New Network location: " + cLat + ", " + cLon,
					Toast.LENGTH_SHORT).show();
			Log.i("================", "New Network location: " + cLat + ", "
					+ cLon);
			seekDistance(cLon, cLat);
			secureDevice(location);
			sendMsg();
		}
	};

	private void sendMsg() {
		SQLiteDatabase db = openOrCreateDatabase("lomo", MODE_PRIVATE, null);
		Cursor result = db.rawQuery("select * from securedevice where id='1'",
				null);
		result.moveToFirst();
		String time = result.getString(11);
		db.close();
		intent = new Intent(TrackLocation.this, MyScheduleReceiver.class);
		// intent.putExtra("Time", time);
		Bundle bundle = new Bundle();
		bundle.putString("Time", time);
		intent.putExtras(bundle);
		pending = PendingIntent.getBroadcast(TrackLocation.this, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				pending);
	}

	// Algorithms to detect the distances using the location data found
	private void addLocData() {
		taskArray.clear();
		SQLiteDatabase db = openOrCreateDatabase("lomo", MODE_PRIVATE, null);
		Cursor result = db.rawQuery("select * from task", null);
		if (result.getCount() != 0) {
			result.moveToFirst();
			while (!result.isAfterLast()) {
				Task task = new Task(result.getString(0), result.getString(1),
						result.getString(2), result.getString(3),
						result.getString(4), result.getString(5),
						result.getString(6), result.getString(7),
						result.getString(8));
				taskArray.add(task);
				result.moveToNext();
			}
		}
		db.close();
	}

	//
	private double distance(double lat1, double lng1, double lat2, double lng2) {
		//earth radius
		double earthRadius = 6371; // kilometers
		//calculating the difference between latitudes and longitudes
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		//calculating the distance
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = (double) (earthRadius * c);
		return dist * 1000;// Getting the distance between two points in meters
	}

	private void seekDistance(double currentLon, double currentLat) {
		//opening the database
		SQLiteDatabase db = openOrCreateDatabase("lomo", MODE_PRIVATE, null);
		//getting the settings data from the database
		Cursor result = db.rawQuery("select * from securedevice where id='1'",
				null);
		result.moveToFirst();
		//getting the alert radius from the database
		proximityDistance = Double.parseDouble(result.getString(4));
		db.close();//closing the database
		//clearing the array to remove the old data present within it
		nearLocations.clear();
		Task task;
		double dist;
		for (int i = 0; i < taskArray.size(); i++) {
			task = taskArray.get(i);
			//calculating the distance
			Log.i("******************************************************Task", task.getTaskName()+" "+task.getDate()+" "+task.getDescription());
			dist = distance(currentLat, currentLon,
					Double.parseDouble(task.getLatitude()),
					Double.parseDouble(task.getLongitude()));
			task.setDistance(dist + "");
			//setting the distance in the task objects
			Log.i(task.getTaskName(), dist + "");
			//if the task is within the alert cirle
			if (dist < proximityDistance) {
				//add the task to nearLocations array
				nearLocations.add(task);
				Log.i(task.getTaskId(),
						dist + " " + task.getDescription() + " Lat: "
								+ task.getLatitude() + " Long: "
								+ task.getLongitude());
			}
		}
		//if nearLocations array is empty, no task to be notified
		if (!nearLocations.isEmpty()) {//if the array is not empty
			Intent intent = new Intent(getBaseContext(), Notification.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//if there is only one task to be notified, there is no need to sort
			if (nearLocations.size() == 1) {
				Task nearTask = nearLocations.get(0);
				//putting additional information into the intent
				intent.putExtra("Task", nearTask.getTaskName());
				intent.putExtra("TaskId", nearTask.getTaskId());
				intent.putExtra("Distance", nearTask.getDistance());
				//displaying the notification in the status bar
				displayNotification(nearTask.getTaskName());
				startActivity(intent);//starting the activity to view the notification
			} else {
				boolean highTask = false;
				Task nTask;
				//creating a separate arraylist to store high priority tasks
				ArrayList<Task> highArray = new ArrayList<>();
				for (int i = 0; i < nearLocations.size(); i++) {
					nTask = nearLocations.get(i);
					//check whether the priority is high or low
					if (nTask.getPriority() == "High") {
						//if the priority is high, add that task into the created array
						highTask = true;
						highArray.add(nTask);
					}

				}
				//if there are no high priority tasks in the arraylist
				if (!highTask) {
					//we get the task with the minimum distance
					nTask = getMinTask(nearLocations);
					//adding additional information to the intent to be passed
					intent.putExtra("Task", nTask.getTaskName());
					intent.putExtra("TaskId", nTask.getTaskId());
					intent.putExtra("Distance", nTask.getDistance());
					//displaying the notification in the status bar
					displayNotification(nTask.getTaskName());
					startActivity(intent);//starting the notification activity
				} else {
					//if there are high priority tasks available
					nTask = getMinTask(highArray);
					//adding additional information to intent
					intent.putExtra("Task", nTask.getTaskName());
					intent.putExtra("TaskId", nTask.getTaskId());
					intent.putExtra("Distance", nTask.getDistance());
					//displaying the notification in the status bar
					displayNotification(nTask.getTaskName());
					startActivity(intent);//start activity
				}
			}

		}
	}

	// ordering the tasks to be notified in ascending order
	private Task getMinTask(ArrayList<Task> array) {
		Task minTask = array.get(0);
		double distance = Double.parseDouble(minTask.getDistance());
		// sorting the tasks according to the ascending order
		for (int i = 1; i < array.size(); i++) {
			Task task = array.get(i);
			double value = Double.parseDouble(task.getDistance());
			if (value < distance) {
				minTask = task;
				distance = value;
			}
		}
		// returns the array
		return minTask;
	}

	// secure device option
	private void secureDevice(Location curraentLocation) {
		// opening the database
		SQLiteDatabase myDb = openOrCreateDatabase("lomo", MODE_PRIVATE, null);
		// getting the details from secureDevice table
		Cursor result = myDb.rawQuery(
				"select * from secureDevice where id='1'", null);
		result.moveToFirst();
		// checks the alert method
		String alertType = result.getString(1);
		if (!alertType.equalsIgnoreCase("off")) {
			// if the alert method is not 'off'
			String radius = result.getString(2);// gets the secure radius
			String receiver = result.getString(3);// gets the receiver mobile
													// number
			String locationName = result.getString(5);// gets the secure
														// location name
			String latitude = result.getString(6);// gets the secure location
													// latitude
			String longitude = result.getString(7);// gets the secure location
													// longitude
			String deviceName = getPhoneName();// gets the device name
			double alertRadius = 1000;
			// assigning alert radius details to variables
			switch (radius) {
			// if alert radius==500m
			case "500 m":
				alertRadius = 500;
				break;
			// if alert radius==1000m
			case "1000 m":
				alertRadius = 1000;
				break;
			// if alert radius==1500m
			case "1500 m":
				alertRadius = 1500;
				break;
			}

			// calculates the distance between the secure location and the
			// current location
			double dist = distance(Double.parseDouble(latitude),
					Double.parseDouble(longitude),
					curraentLocation.getLatitude(),
					curraentLocation.getLongitude());

			// gets the current time
			Calendar cal = Calendar.getInstance();
			String time = "";
			if (cal.get(Calendar.AM_PM) == 0) {
				time = cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE)
						+ " AM";
			} else if (cal.get(Calendar.AM_PM) == 1) {
				time = cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE)
						+ " PM";
			}

			// alert type=='alert'
			if (alertType.equalsIgnoreCase("alert")) {
				// distance between the current location and the secure location
				// should be less than alert radius
				if (dist <= alertRadius) {
					// checks whether the distance difference is less
					// message to be sent to the recipient
					String message = "The " + deviceName + " is "
							+ String.format("%.2f", dist) + " m away from "
							+ locationName + ".Current location is Lat: "
							+ curraentLocation.getLatitude() + " Lon: "
							+ curraentLocation.getLongitude() + " (" + time
							+ ")";
					// sends a text message
					sendSMS(receiver, message);
				}

			} else if (alertType.equalsIgnoreCase("contain")) {
				// if the alert type=='contain'
				// distance between secure point and the current location should
				// be more than alert radius to notify
				if (dist >= alertRadius) {
					// composes the message to be sent
					String message = "The " + deviceName + " is insecure and "
							+ String.format("%.2f", dist) + " m away from "
							+ locationName + ".Current location is Lat: "
							+ curraentLocation.getLatitude() + " Lon: "
							+ curraentLocation.getLongitude() + " (" + time
							+ ")";
					// sends the text message
					sendSMS(receiver, message);
				}
			}
		}
		myDb.close();

	}

	// sending text message to a recipient
	private void sendSMS(String phoneNumber, String message) {
		Log.i("Message", message);
		// sms manager
		SmsManager sms = SmsManager.getDefault();
		// dividing the message into separate messages if the length of the
		// message is big
		ArrayList<String> parts = sms.divideMessage(message);
		// sending the divided messages
		sms.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
	}

	public class MyBinder extends Binder {
		TrackLocation getService() {
			return TrackLocation.this;
		}
	}

	// returns the locationdata array from the service
	public ArrayList<Location> getLocationList() {
		// returns the array which contains the current location details tracked
		return locatonData;
	}

	// getting the device name from device settings
	public String getPhoneName() {
		BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
		// getting the name
		String deviceName = myDevice.getName();
		return deviceName;// returns the name as a string
	}

	// database initialization
	public boolean databaseExist() {
		SQLiteDatabase checkDB = null;
		try {
			// opening the database/creating the database if not exists
			checkDB = openOrCreateDatabase("lomo", MODE_PRIVATE, null);
			// creating the tales if not exists
			checkDB.execSQL("CREATE TABLE IF NOT EXISTS task (taskid integer primary key, taskName VARCHAR(15) not null, description VARCHAR(30),location VARCHAR(50),longitude VARCHAR(25),latitude VARCHAR(25),date VARCHAR(15),time VARCHAR(8),priority VARCHAR(4));");
			checkDB.execSQL("CREATE TABLE IF NOT EXISTS secureDevice (id VARCHAR(1) not null, active VARCHAR(10), s_perimeter VARCHAR(6), receiver VARCHAR(15), t_perimeter VARCHAR(6),location VARCHAR(50),latitude VARCHAR(25),longitude VARCHAR(25),device VARCAHR(10),status VARCHAR(3),sound VARCHAR(3),time VARCHAR(3));");
			// getting data from secureDevice table
			Cursor cu = checkDB.rawQuery("select * from secureDevice", null);
			if (cu.getCount() == 0) {
				// if there are no data in secureDevice table,initial data is
				// inserted beforehand
				checkDB.execSQL("insert into secureDevice values ('1','off','1000','none','1000','none','0','0','Sisiru Galaxy','off','on','20');");
			}
			// close the database
			checkDB.close();
			// returns true if connection was successful
			return true;
		} catch (Exception e) {
			// Log an info message stating database doesn't exist.
		}
		// returns false if an error occured
		return false;
	}

	private void startMyAct() {
		Calendar cal = Calendar.getInstance(); // Create a calendar
		cal.set(Calendar.HOUR_OF_DAY, 8);
		cal.set(Calendar.MINUTE, 0); // Add 120 seconds to the current time

		Intent dialogIntent = new Intent(this, ViewAll.class);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		dialogIntent.putExtra("Day", "today");
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 1234,
				dialogIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
	}

	// Displays a notification when a new location is found
	private void displayNotification(String title) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Builder noti = new NotificationCompat.Builder(this);
		noti.setContentTitle("Reminder");// Title of the notification
		noti.setContentText(title);// Content of the notification
		noti.setSmallIcon(R.drawable.ic_launcher);// sets the icon
		notificationManager.notify(1234, noti.build());// displaing the
														// notification
	}
}
