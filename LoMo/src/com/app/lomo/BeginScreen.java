package com.app.lomo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

@SuppressLint("NewApi")
public class BeginScreen extends Activity {
	Button bt;
	LinearLayout linear;
	private Animation animation;

	// Begin screen of the software
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.begin);// sets the layout
		// runs new thread in order to run this activity only for 5 secons
		bt = (Button) findViewById(R.id.mybt);
		linear = (LinearLayout) findViewById(R.id.layout);

		animationInit();
		startingActivity();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();

	}

	// animation in the begin screen
	private void animationInit() {
		AnimationSet set = new AnimationSet(true);

		animation = new AlphaAnimation(0.0f, 100.0f);
		animation.setDuration(5000);// duration of the animation
		set.addAnimation(animation);
		// sets the from location and to location of the animation
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,
				Animation.RELATIVE_TO_SELF, 0.55f, Animation.RELATIVE_TO_SELF,
				8.0f, Animation.RELATIVE_TO_SELF, 8.0f);
		animation.setDuration(4000);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(
				set, 10.25f);

		linear.setLayoutAnimation(controller);
		controller.start();// starting the animation

	}

	private void startingActivity() {
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent("com.app.lomo.MAINSCREEN");
				startActivity(intent);//starting the new activity when the animation is over
			}
		});
	}
}
