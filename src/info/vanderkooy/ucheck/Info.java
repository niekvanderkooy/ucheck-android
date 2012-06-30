package info.vanderkooy.ucheck;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.os.Bundle;

public class Info extends Activity {
	private GoogleAnalyticsTracker tracker;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.info);
	    tracker = GoogleAnalyticsTracker.getInstance();
	    // TODO Auto-generated method stub
	}
	
	@Override
	public void onResume() {
		super.onResume();
		tracker.trackPageView("/appInfo");
	}

}
