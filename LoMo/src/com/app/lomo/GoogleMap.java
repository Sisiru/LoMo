package com.app.lomo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GoogleMap extends FragmentActivity implements LocationListener {
	private com.google.android.gms.maps.GoogleMap googleMap;
	private EditText txtLocation;
	private LocationManager locationManager;
	private String provider;
	private Marker addedMarker = null;
	private Button btSet;
	private List<Address> addressList;
	private Geocoder coder;
	private MarkerOptions marker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			// Remove title bar
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.mapfragment);// Setting the layout
		} catch (Exception e) {
			setMobileDataEnabled(GoogleMap.this, false);
			finish();
		}
		initialize();
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// checkService();
		initilizeMap();// initializing the map
		getCurrentLocation();// getting the current location
		clickSetButton();// when set button is clicked

	}

	private void initialize() {
		if (!hasConnection()) {
			setMobileDataEnabled(GoogleMap.this, true);
		}
		// initializing the view objects from the layout
		txtLocation = (EditText) findViewById(R.id.txtLocation);
		btSet = (Button) findViewById(R.id.btSet);
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	private void initilizeMap() {// initializing the googleMap object
		if (googleMap == null) {// If google map is not loaded
			try {
				googleMap = ((SupportMapFragment) getSupportFragmentManager()
						.findFragmentById(R.id.map)).getMap();// getting the
																// google map
				// googleMap.setMyLocationEnabled(true);// setting the my
				// location
				// button

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Sorry!",
						Toast.LENGTH_SHORT).show();// Noticing that an exception
													// has occured
			}

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),// display a toast
														// message if the map is
														// not loaded
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			} else {
				// If there are no errors in loading the map, following methods
				// will be called
				longMapClick();// when map is clicked
				mapClick();// when a marker is clicked
				dragMarker();// when dragging the marker
			}
		}
	}

	private void getCurrentLocation() {
		Location loc;
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
		if (matchingProviders.contains("gps")) {// Uses the gps provider as
												// the last option
			provider = "gps";
			LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
			boolean enabled = service
					.isProviderEnabled(LocationManager.GPS_PROVIDER);// Checks
																		// whether
																		// the
																		// GPS
																		// service
																		// is
																		// enabled
			if (!enabled) {// If the service is not available, instructs the
							// user to turn on the GPS
				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);// Opens the GPS settings page
			}
		} else {
			provider = "network";
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
			}
			// Gets the current location
			Location location = locationManager.getLastKnownLocation(provider);
			if (location == null) {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						GoogleMap.this);
				alert.setTitle("LoMo Service");
				alert.setMessage("Please turn on the LoMo service and retry again");
				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();

							}
						});
				alert.create().show();
			} else {
				lat = location.getLatitude();// Getting the latitude value of
												// the
				// location
				lon = location.getLongitude();// Getting the longitude value of
												// the
				// location
				// txtLocation.setText(provider + " " + lat + " , " + lon);
				//lat=0;lon=0;
			}

		}
		

		LatLng coordinate = new LatLng(lat, lon);// Creating the location using
													// location data
		// Creating a marker to represent the current location
		marker = new MarkerOptions()
				.position(coordinate)
				.title("Current Location ")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.marker1_1));
		// Instantiates a new CircleOptions object and defines the center and
		// radius
		CircleOptions circleOptions = new CircleOptions().center(coordinate)
				.fillColor(0x5581BEF7).strokeWidth(3).strokeColor(0x9908298A)
				.radius(1000); // In meters

		// Get back the mutable Circle
		googleMap.addCircle(circleOptions);

		googleMap.addMarker(marker);// Adding a marker to the current location
		CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(
				coordinate, 13);// Zooming the camera to the current location
		googleMap.animateCamera(yourLocation);// Animating the camera to the
												// current location

	}

	@Override
	public void onLocationChanged(Location location) {

		// Getting latitude of the current location
		double latitude = location.getLatitude();

		// Getting longitude of the current location
		double longitude = location.getLongitude();

		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);

		// Showing the current location in Google Map
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		// Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

		// Setting latitude and longitude in the TextView txtLocation
		txtLocation.setText(latitude + " " + longitude);

		Log.d("A", "GPS LocationChanged");
		Log.d("A", "Received GPS request for " + String.valueOf(latitude) + ","
				+ String.valueOf(longitude) + " , ready to rumble!");

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	private void mapClick() {
		// What happens if a marker on the map is clicked
		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// A message will be displayed
				String message = marker.getTitle() + " ("
						+ marker.getPosition().latitude + " , "
						+ marker.getPosition().longitude + ")";
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_LONG).show();
				return false;
			}
		});
	}

	private void locateMarker(LatLng location, String address) {
		if (addedMarker != null) {
			addedMarker.remove();
		}
		MarkerOptions myMarker = new MarkerOptions()
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.marker2_1)).position(location)
				.draggable(true).title(address);
		if (address != "") {
			txtLocation.setText(address);
		} else {
			txtLocation.setText("Address cannot be found");
		}
		addedMarker = googleMap.addMarker(myMarker);
		clickSetButton();
	}

	private void longMapClick() {
		googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng location) {
				Geocoder gcd = new Geocoder(GoogleMap.this);
				List<Address> addresses = null;

				try {
					addresses = gcd.getFromLocation(location.latitude,
							location.longitude, 1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
				try {
					if (addresses.size() > 0) {
						Address address = addresses.get(0);
						String line1 = address.getAddressLine(0);
						String line2 = address.getAddressLine(1);
						if (line1.equalsIgnoreCase(line2)) {
							line2 = address.getSubAdminArea();
						}
						String addressText = String.format("%s, %s",
						// If there's a street address, add it
								address.getMaxAddressLineIndex() > 0 ? line1
										: "",
								// Locality is usually a city
								line2);
						// The country of the address
						// address.getCountryName());
						locateMarker(location, addressText);
					}
				} catch (Exception e) {
					locateMarker(location, "");// Address cannot be found due to
												// network problems
				}

			}
		});

		/*
		 * Might be useful
		 * 
		 * public GeoPoint getLocationFromAddress(String strAddress){
		 * 
		 * Geocoder coder = new Geocoder(this); List<Address> address; GeoPoint
		 * p1 = null;
		 * 
		 * try { address = coder.getFromLocationName(strAddress,5); if (address
		 * == null) { return null; } Address location = address.get(0);
		 * location.getLatitude(); location.getLongitude();
		 * 
		 * p1 = new GeoPoint((int) (location.getLatitude() * 1E6), (int)
		 * (location.getLongitude() * 1E6));
		 * 
		 * return p1; }
		 */
	}

	private void dragMarker() {
		googleMap.setOnMarkerDragListener(new OnMarkerDragListener() {

			@Override
			public void onMarkerDragStart(Marker arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				Geocoder gcd = new Geocoder(GoogleMap.this);
				LatLng location = marker.getPosition();
				List<Address> addresses = null;

				try {
					addresses = gcd.getFromLocation(location.latitude,
							location.longitude, 1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (addresses.size() > 0) {
					Address address = addresses.get(0);
					String line1 = address.getAddressLine(0);
					String line2 = address.getAddressLine(1);
					if (line1.equalsIgnoreCase(line2)) {
						line2 = address.getSubAdminArea();
					}
					String addressText = String.format("%s, %s",
					// If there's a street address, add it
							address.getMaxAddressLineIndex() > 0 ? line1 : "",
							// Locality is usually a city
							line2);
					marker.setTitle(addressText);
					txtLocation.setText(addressText);
				}

			}

			@Override
			public void onMarkerDrag(Marker arg0) {
			}
		});
	}

	private void clickSetButton() {
		btSet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (addedMarker == null) {
					String text = txtLocation.getText().toString();
					if (text.length() == 0) {
						double latitude = marker.getPosition().latitude;
						double longitude = marker.getPosition().longitude;
						String address = "";
						try {
							address = getLocAddress(marker.getPosition());
						} catch (Exception e) {

						}
						if (address == null)
							address = "";// Address could not be found
						Intent intent = new Intent();
						intent.putExtra("Address", address);
						intent.putExtra("Latitude", latitude);
						intent.putExtra("Longitude", longitude);
						setResult(1234, intent);
						setMobileDataEnabled(GoogleMap.this, false);
						finish();
					} else {
						coder = new Geocoder(GoogleMap.this);
						try {
							addressList = coder.getFromLocationName(text, 5);
							registerForContextMenu(btSet);
							openContextMenu(btSet);
						} catch (Exception e) {

						}

					}

				} else if (addedMarker != null) {
					double latitude = addedMarker.getPosition().latitude;
					double longitude = addedMarker.getPosition().longitude;
					String address = addedMarker.getTitle();
					// Toast.makeText(GoogleMap.this,
					// latitude+" "+longitude+" "+address,
					// Toast.LENGTH_LONG).show();
					if (address == null)
						address = "";// Address could not be found
					Intent intent = new Intent();
					intent.putExtra("Address", address);
					intent.putExtra("Latitude", latitude);
					intent.putExtra("Longitude", longitude);
					setResult(1234, intent);
					setMobileDataEnabled(GoogleMap.this, false);
					finish();
				}

			}
		});
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int index = item.getItemId();
		Address select = addressList.get(index);
		// Toast.makeText(GoogleMap.this, select.toString(),
		// Toast.LENGTH_LONG).show();
		LatLng coordinate = new LatLng(select.getLatitude(),
				select.getLongitude());
		locateMarker(coordinate, item.getTitle().toString());
		CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(
				coordinate, 13);// Zooming the camera to the current location
		googleMap.animateCamera(yourLocation);// Animating the camera to the
												// current location
		// Toast.makeText(GoogleMap.this,
		// select.getLatitude()+" "+select.getLongitude(),
		// Toast.LENGTH_LONG).show();
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Address");
		menu.setHeaderIcon(R.drawable.home);
		for (int i = 0; i < addressList.size(); i++) {
			Address add = addressList.get(i);
			String myAdd;
			myAdd = add.getAddressLine(0) + ", " + add.getSubAdminArea() + ", "
					+ add.getCountryName();

			menu.add(0, i, 0, myAdd);

		}
		if(addressList.size()==0){
			AlertDialog.Builder alert=new AlertDialog.Builder(GoogleMap.this);
			alert.setTitle("Verify");
			alert.setMessage("Please enter a valid location");
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					
				}
			});alert.create().show();
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	private String getLocAddress(LatLng location) {
		String addressText = "";
		Geocoder gcd = new Geocoder(GoogleMap.this);
		List<Address> addresses = null;

		try {
			addresses = gcd.getFromLocation(location.latitude,
					location.longitude, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (addresses.size() > 0) {
			Address address = addresses.get(0);
			String line1 = address.getAddressLine(0);
			String line2 = address.getAddressLine(1);
			if (line1.equalsIgnoreCase(line2)) {
				line2 = address.getSubAdminArea();
			}
			addressText = String.format("%s, %s",
			// If there's a street address, add it
					address.getMaxAddressLineIndex() > 0 ? line1 : "",
					// Locality is usually a city
					line2);
		}
		return addressText;
	}

	// method to disconnect the mobile data connection
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

	// checks whether the device is cooected to a working data connection
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

	@Override
	public void onBackPressed() {
		// mobile data connection is disabled
		setMobileDataEnabled(GoogleMap.this, false);
		super.onBackPressed();
	}

}
