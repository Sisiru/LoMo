package com.app.lomo;

import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class DrawRoute extends FragmentActivity {
	private GoogleMap map;// Googlemap variable
	private TrackLocation myService;
	ArrayList<Location> myList;// list to store the location points
	ArrayList<LatLng> markerPoints;
	private boolean enabled = false;
	private Button btPath;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.myroute);// sets the layout
		initialize();// initializing views in the layout
		myList = new ArrayList<>();
		// get the list which contains the tracked locations from the
		// TrackLocation service
		getList();
		// displaying the progress window
		progressDisplay();
		clickButton();// button click even

	}

	// displaying the progress window
	private void progressDisplay() {
		final ProgressDialog progress = new ProgressDialog(DrawRoute.this);
		// sets the message of the progress window
		progress.setMessage("Loading...");
		progress.show();// displaying the window
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// thread is active only for 10 seconds
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// after 10 seconds the window will be closed
				progress.dismiss();
			}
			// starting the thread
		}).start();

	}

	private void drawMyRoute() {
		if (enabled && myList.size() > 0) {
			// Drawing the route on the map
			LatLng source = null, destinetion = null;
			Location tempSource = null, tempDestination = null;

			for (int i = 0; i < myList.size() - 1; i++) {
				// picks two points at each iteration
				tempSource = myList.get(i);
				tempDestination = myList.get(i + 1);
				source = new LatLng(tempSource.getLatitude(),
						tempSource.getLongitude());
				destinetion = new LatLng(tempDestination.getLatitude(),
						tempDestination.getLongitude());
				// if the two points are not equal to each other
				if (tempSource != tempDestination) {
					String url = getDirectionsUrl(source, destinetion);
					DownloadTask downloadTask = new DownloadTask();
					try {
						downloadTask.execute(url);
					} catch (Exception e) {

					}

				}
			}

		}

	}

	// when the button is clicked
	private void clickButton() {
		btPath.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (enabled && myList.size() > 0) {

					// Markinh the locations on the map using markers
					Location markerLocation;
					MarkerOptions options = new MarkerOptions();
					// get the first location-strating location
					markerLocation = myList.get(0);
					// marking the source locatin on the map
					options.position(
							new LatLng(markerLocation.getLatitude(),
									markerLocation.getLongitude()))
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
					map.addMarker(options);// adding the marker

					// If there are locations available in between the source
					// and the
					// destination
					// they are marked on the map using a different coloured
					// pointer
					CameraUpdate yourLocation = null;
					if (myList.size() > 1) {
						for (int i = 1; i < myList.size() - 1; i++) {
							markerLocation = myList.get(i);
							// creating the marker
							options.position(
									new LatLng(markerLocation.getLatitude(),
											markerLocation.getLongitude()))
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
							// adding the marker to the GoogleMap
							map.addMarker(options);
						}

						// Marking the final location tracked using a different
						// coloured
						// marker
						markerLocation = myList.get(myList.size() - 1);
						// creating the marker
						options.position(
								new LatLng(markerLocation.getLatitude(),
										markerLocation.getLongitude()))
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_RED));
						// adding the marker t the map
						map.addMarker(options);
						yourLocation = CameraUpdateFactory.newLatLngZoom(
								options.getPosition(), 15);// Zooming the camera
															// to the
															// current location

					} else {
						yourLocation = CameraUpdateFactory.newLatLngZoom(
								options.getPosition(), 15);// Zooming the camera
															// to the
															// current location
					}
					if (yourLocation != null) {
						map.animateCamera(yourLocation);// Animating the camera
														// to the
						// current location
					}
				}
				drawMyRoute();
			}
		});
	}

	private void initialize() {
		btPath = (Button) findViewById(R.id.btRoute);

		if (!hasConnection()) {
			setMobileDataEnabled(DrawRoute.this, true);
		}
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);

		// Getting Map for the SupportMapFragment
		map = fm.getMap();

		if (map != null) {

			// Enable MyLocation Button in the Map
			//map.setMyLocationEnabled(true);
		}
	}
	
	//when the back key is pressed
	@Override
	public void onBackPressed() {
		//mobile data connection is disabled
		setMobileDataEnabled(DrawRoute.this, false);
		super.onBackPressed();
	}

	//method to disconnect the mobile data connection
	private void setMobileDataEnabled(Context context, boolean enabled) {
		try {
			final ConnectivityManager conman = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final Class conmanClass = Class
					.forName(conman.getClass().getName());
			final Field connectivityManagerField = conmanClass
					.getDeclaredField("mService");
			connectivityManagerField.setAccessible(true);
			final Object connectivityManager = connectivityManagerField
					.get(conman);
			final Class connectivityManagerClass = Class
					.forName(connectivityManager.getClass().getName());
			final Method setMobileDataEnabledMethod = connectivityManagerClass
					.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);
			setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
		} catch (Exception e) {

		}

	}

	//checks whether the device is cooected to a working data connection
	private boolean hasConnection() {
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

	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		Log.i("The Created URL", url);
		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String> {

		// Downloading data in non-ui thread

		@Override
		protected String doInBackground(String... urls) {
			String data = "";
			try {
				data = downloadUrl(urls[0]);
			} catch (IOException e) {
				data = "Unable to retrieve web page. URL may be invalid.";
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			ParserTask parserTask = new ParserTask();
			// Invokes the thread for parsing the JSON data

			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			Log.i("********************map", "Error");
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;

			// Traversing through all the routes
			if (result != null) {
				for (int i = 0; i < result.size(); i++) {
					points = new ArrayList<LatLng>();
					lineOptions = new PolylineOptions();

					// Fetching i-th route
					List<HashMap<String, String>> path = result.get(i);

					// Fetching all the points in i-th route
					for (int j = 0; j < path.size(); j++) {
						HashMap<String, String> point = path.get(j);

						double lat = Double.parseDouble(point.get("lat"));
						double lng = Double.parseDouble(point.get("lng"));
						LatLng position = new LatLng(lat, lng);

						points.add(position);
					}

					// Adding all the points in the route to LineOptions
					lineOptions.addAll(points);
					lineOptions.width(12);
					lineOptions.color(0x652E64FE);
				}

				// Drawing polyline in the Google Map for the i-th route
				map.addPolyline(lineOptions);
			} else {
				drawMyRoute();
			}

		}
	}

	@Override
	protected void onPause() {
		Log.d("activity", "onPause");
		if (myService != null) {
			unbindService(mCnnection);
			myService = null;
		}
		super.onPause();

	}

	@Override
	protected void onResume() {
		Log.i("========bind", "Resume");
		Log.d("activity", "onResume");
		if (myService == null) {
			doBindService();
		}
		super.onResume();

	}

	private ServiceConnection mCnnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			myService = null;

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			TrackLocation.MyBinder b = (TrackLocation.MyBinder) service;
			//getting the TrackLocation service
			myService = b.getService();
			try {
				//getting the list which stores the details of locations tracked
				myList = myService.getLocationList();
			} catch (Exception e) {

			}
			enabled = true;
			Toast.makeText(DrawRoute.this, myList.size() + "",
					Toast.LENGTH_SHORT).show();
		}
	};

	public void getList() {
		Log.i("========bind", "Get List");
		try {
			myList = myService.getLocationList();
		} catch (Exception e) {
			Log.i("========bind", "Exception");
		}

		if (myList != null) {
		}

	}

	public void doBindService() {
		Intent intent = null;
		intent = new Intent(this, TrackLocation.class);
		bindService(intent, mCnnection, Context.BIND_AUTO_CREATE);
	}

	public void locatePoints() {
		Toast.makeText(DrawRoute.this, myList.size() + " ", Toast.LENGTH_SHORT)
				.show();
		if (enabled && myList.size() > 0) {

		}
	}

}
