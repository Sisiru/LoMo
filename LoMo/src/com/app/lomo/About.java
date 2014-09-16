package com.app.lomo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class About extends Activity {
	// This is the activity which contains the details of the owner of the
	// application

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);// sets the layout for the activity
	}

}
