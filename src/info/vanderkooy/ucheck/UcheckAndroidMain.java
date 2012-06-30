package info.vanderkooy.ucheck;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class UcheckAndroidMain extends TabActivity {
	private Preferences prefs;
	private TabHost tabHost;
	private int lastTab;
	private GoogleAnalyticsTracker tracker;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    prefs = new Preferences(getApplicationContext());
		if(prefs.getKillApp()) {
			prefs.clearKillApp();
			finish();
		}
		
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-33051377-1", this);
		tracker.trackPageView("/startup");
		
	    //Resources res = getResources();
		tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Reusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab
		
		lastTab = 0;

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, Grades.class);

		//TODO: Change setIndicator(getString()) to setIndicator(getString(), res.getDrawable(R.drawable.ic_tab_settings) to ad icons
		
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("grades").setIndicator(getString(R.string.gradesTab), getResources().getDrawable(R.drawable.badge))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, Classes.class);
		spec = tabHost.newTabSpec("classes").setIndicator(getString(R.string.classesTab), getResources().getDrawable(R.drawable.index_cards))
				.setContent(intent);
		tabHost.addTab(spec);
		

		intent = new Intent().setClass(this, Progress.class);
		spec = tabHost.newTabSpec("progress").setIndicator(getString(R.string.progressTab), getResources().getDrawable(R.drawable.line_chart))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Account.class);
		spec = tabHost.newTabSpec("account").setIndicator(getString(R.string.accountTab), getResources().getDrawable(R.drawable.user))
				.setContent(intent);
		tabHost.addTab(spec);
		
	}
	
	public void onResume() {
		super.onResume();
		if(!prefs.getGoingToInfo()) {
			//Only tracks resumes that are actually coming from the Android home screen,
			//not from the info screen
			tracker.trackPageView("/resume");
			if(prefs.getKey().equals("")) {
				Intent loginIntent = new Intent().setClass(UcheckAndroidMain.this, Login.class);
				UcheckAndroidMain.this.startActivity(loginIntent);
			} else {
				tabHost.setCurrentTab(lastTab);
			}
		} else {
			prefs.setGoingToInfo(false);
		}
		
	}
	
	public void onPause() {
		super.onPause();
		lastTab = tabHost.getCurrentTab();
		if(!prefs.getStorePass())
			prefs.clearKey();	
	}
	
	public void onDestroy() {
		super.onDestroy();
		tracker.dispatch();	
		tracker.stopSession();
	}

}
