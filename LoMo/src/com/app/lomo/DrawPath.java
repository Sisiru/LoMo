package com.app.lomo;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
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
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Window;

public class DrawPath extends FragmentActivity {
	private GoogleMap map;// GoogleMap variable
	private ProgressDialog dialog = null;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mappath);// sets the layout
		initialize();// initializing the layout objects
		drawPath();// Drawing the path between the two points
	}

	private void drawPath() {
		Bundle bundle = getIntent().getExtras();// Getting the latitude and
												// longitude sent from the
												// Notification
		MarkerOptions options = new MarkerOptions();// Marker on the map
		// Notification sends the values of the relevant tasks when starting the
		// intent
		String latitude = bundle.getString("Latitude");// These are the location
														// values of the task
		String longitude = bundle.getString("Longitude");// Longitude location
		// Creating a Latlng object with the location values
		LatLng destination = new LatLng(Double.parseDouble(latitude),
				Double.parseDouble(longitude));
		// Creating a marker to be added into the map
		options.position(destination).icon(
				BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		map.addMarker(options);// Adding the marker for destination

		LatLng currentLocation = getCurrentLocation();// Current location of the
														// user
		options.position(currentLocation).icon(
				BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		map.addMarker(options);// Adding the marker for the urrent location

		CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(
				currentLocation, 15);// Zooming the camera to the
										// current location
		map.animateCamera(yourLocation);// Animating the camera to the
										// current location

		if (destination != currentLocation) {
			// creates the url to download from the network
			String url = getDirectionsUrl(currentLocation, destination);
			DownloadTask downloadTask = new DownloadTask();// DownloadTask
															// object
			// downloading the JSON file by sending the created url
			try {
				downloadTask.execute(url);
			} catch (Exception e) {

			}

		}

	}

	private void initialize() {// initializing
		if (!hasConnection()) {// checks whether the device is connected to
								// internet
			// if the device is not connected to a working data network,
			// connecting automatically
			setMobileDataEnabled(DrawPath.this, true);
		}
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		// Getting Map for the SupportMapFragment
		map = fm.getMap();
		if (map != null) {
			// Enable MyLocation Button in the Map
			map.setMyLocationEnabled(true);
		}
	}

	// when the back button of the device is pressed
	@Override
	public void onBackPressed() {
		// the data network is made disable
		setMobileDataEnabled(DrawPath.this, false);
		super.onBackPressed();
	}

	// enabling mobile data option of the device
	public void setMobileDataEnabled(Context context, boolean enabled) {
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

	// checks whether the device is connected to a working data network
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

	// creating the url to get the JSON file containing the directions
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
		return url;// return the created url
	}

	// download json data from url
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
			Log.i("---------------------------------------------------------",
					data);
		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			if (iStream != null && urlConnection != null) {
				iStream.close();
				urlConnection.disconnect();
			}
		}
		return data;
	}

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String> {

		// Downloading data in non-ui thread
		@Override
		protected void onPreExecute() {
			// runs before starting the backgorund service
			dialog = new ProgressDialog(DrawPath.this);// new progress dialog
			dialog.setMessage("Processing...");
			dialog.show();// opening the progress dialog
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {
			// while the progress dialog is open, the backgound service runs
			String data = "";
			try {
				data = downloadUrl(urls[0]);// downloading the data
			} catch (Exception e) {
				data = "Unable to retrieve web page. URL may be invalid.";
			}
			return data;// returning the downloaded data
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			// this method runs after completing the background service
			super.onPostExecute(result);
			try {
				if ((dialog != null) && dialog.isShowing()) {
					dialog.dismiss();// closes the progress dialog
				}
			} catch (final IllegalArgumentException e) {
				// Handle or log or ignore
			} catch (final Exception e) {
				// Handle or log or ignore
			} finally {
				dialog = null;
			}
			ParserTask parserTask = new ParserTask();
			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);// executes the result string
		}
	}

	// A class to parse the Google Places in JSON format
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;// json file
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// finally gets the directins parsed into a List object
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
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
						// getting each longitude and latitude
						double lat = Double.parseDouble(point.get("lat"));
						double lng = Double.parseDouble(point.get("lng"));
						LatLng position = new LatLng(lat, lng);
						// adding the LatLng object into the array
						points.add(position);
					}

					// Adding all the points in the route to LineOptions
					lineOptions.addAll(points);
					lineOptions.width(12);// width of the line drawn on the map
					lineOptions.color(0x852E64FE);// colour of the drawn line
				}
				// Drawing polyline in the Google Map for the i-th route
				map.addPolyline(lineOptions);
			} else {
				// if an error has occured while retrieving the json file
				drawPath();// again tries to draw the path by repeating the
							// above steps
			}

		}
	}

	// Getting the current location of the user
	private LatLng getCurrentLocation() {
		Location loc;
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String provider = "network";
		double lat = 0, lon = 0;
		// Finding the available location providers according to the current
		// instance
		List<String> matchingProviders = locationManager.getProviders(true);// Inserting
																			// the
																			// providers
																			// to
																			// an
																			// array
		// Toast.makeText(GoogleMap.this,
		// matchingProviders.get(0),Toast.LENGTH_SHORT).show();

		if (matchingProviders.contains("gps")) {// Gives the network
												// provider the priority
			provider = "gps";
		} else if (matchingProviders.contains("network")) {// If the network
															// provider is not
															// available uses
															// the passive
															// provider
			provider = "network";
		} else {
			provider = "passive";
		}

		loc = locationManager.getLastKnownLocation(provider);// Gets the last
																// known
																// location
		if (loc != null) {
			lat = loc.getLatitude();
			lon = loc.getLongitude();
			// txtLocation.setText(provider + " " + lat + " , " + lon);
		} else if (loc == null) {
			Criteria criteria = new Criteria();
			provider = locationManager.getBestProvider(criteria, false);
			if (provider == "gps") {
				LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
				boolean enabled = service
						.isProviderEnabled(LocationManager.GPS_PROVIDER);// activating
																			// the
																			// gps
																			// provider
				if (!enabled) {
					Intent intent = new Intent(
							Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}
			}
			// Gets the current location
			Location location = locationManager.getLastKnownLocation(provider);
			lat = location.getLatitude();// Getting the latitude value of the
											// location
			lon = location.getLongitude();// Getting the longitude value of the
											// location
			// txtLocation.setText(provider + " " + lat + " , " + lon);
		}

		LatLng coordinate = new LatLng(lat, lon);// Creating the location using
													// location data
		// returns the LatLng object which contains the current location
		// coordinates
		return coordinate;
	}

}
