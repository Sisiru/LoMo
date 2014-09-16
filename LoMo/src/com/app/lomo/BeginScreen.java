package com.app.lomo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class BeginScreen extends Activity {
	// Begin screen of the software
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.begin);// sets the layout
		// runs new thread in order to run this activity only for 5 secons
		Thread myThread = new Thread() {
			@Override
			public void run() {
				try {
					sleep(5000);// sleeps for 5 seconds
				} catch (Exception e) {

				} finally {
					// After 5 seconds time opens the Main Screen of the
					// application
					Intent intent = new Intent("com.app.lomo.MAINSCREEN");
					startActivity(intent);
				}
			}
		};
		myThread.start();// starting the thread
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();

	}
}
