package info.vanderkooy.ucheck;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import android.app.Activity;
import android.os.Bundle;

public class Info extends Activity {
	private Tracker tracker;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.info);
	    tracker = GoogleAnalytics.getInstance(getApplicationContext()).getDefaultTracker();
	    // TODO Auto-generated method stub
	}
	
	@Override
	public void onResume() {
		super.onResume();
		tracker.trackView("/appInfo");
	}

}
