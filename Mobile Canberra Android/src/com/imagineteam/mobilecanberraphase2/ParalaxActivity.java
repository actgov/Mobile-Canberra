package com.imagineteam.mobilecanberraphase2;

import com.insideoutlier.glass.lib.Glass;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.widget.FrameLayout;

public class ParalaxActivity extends Activity {

	private Glass g;
	Intent home;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paralax);
		FrameLayout fl = (FrameLayout) findViewById(R.id.glass);
		Drawable my_background = (Drawable) getResources().getDrawable(
				R.drawable.parallaxpic);
		Double scale_img = 5.0;
		g = new Glass(this, fl, my_background, scale_img);
		try {
			g.start();
		} catch (Exception e) {

		}
		home = new Intent(this, DatasetsActivity.class);
		final Message msg = new Message();
		msg.what = STOPSPLASH;
		splashHandler.sendMessageDelayed(msg, SPLASHTIME);
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			g.stop();
		} catch (Exception e) {

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			g.start();
		} catch (Exception e) {

		}
	}

	private static final int STOPSPLASH = 0;
	// time in milliseconds
	private static final long SPLASHTIME = 2000;

	// handler for splash screen
	private final Handler splashHandler = new Handler() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STOPSPLASH:
				finish();
				startActivity(home);

				break;
			}
			super.handleMessage(msg);
		}
	};

}
