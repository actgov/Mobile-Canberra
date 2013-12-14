package com.imagineteam.mobilecanberraphase2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;



public class SplashScreen extends Activity {

	private static final int STOPSPLASH = 0;
	//time in milliseconds
	private static final long SPLASHTIME = 1000;

	Intent home;

	//handler for splash screen
	private final Handler splashHandler = new Handler() {
		/* (non-Javadoc)
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_splash_screen);
		
		
		home = new Intent(SplashScreen.this, ParalaxActivity.class);

		
		final Message msg = new Message();
		msg.what = STOPSPLASH;
		splashHandler.sendMessageDelayed(msg, SPLASHTIME);
	}


}