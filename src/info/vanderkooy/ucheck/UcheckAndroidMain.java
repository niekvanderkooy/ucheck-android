package info.vanderkooy.ucheck;

import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class UcheckAndroidMain extends TabActivity {
	private Preferences prefs;
	private TabHost tabHost;
	private int lastTab;
	private GoogleAnalytics instance;
	private Tracker tracker;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    prefs = new Preferences(getApplicationContext());
		if(prefs.getKillApp()) {
			prefs.clearKillApp();
			finish();
		}
		
		GAServiceManager.getInstance().setDispatchPeriod(-1);
		instance = GoogleAnalytics.getInstance(getApplicationContext());
		tracker = instance.getTracker("UA-33051377-2");
		instance.setDefaultTracker(tracker);
		tracker.trackView("/startup");
		
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
		
		if(prefs.getLastTab() != -1) {
			tabHost.setCurrentTab(prefs.getLastTab());
			prefs.setLastTab(-1);
		}
		
	}
	
	public void onResume() {
		super.onResume();
		if(!prefs.getGoingToInfo()) {
			//Only tracks resumes that are actually coming from the Android home screen,
			//not from the info screen
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
		if(!prefs.getGoingToInfo())
			GAServiceManager.getInstance().dispatch();
	}
	
	public void onDestroy() {
		super.onDestroy();
		GAServiceManager.getInstance().dispatch();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    prefs.setLastTab(tabHost.getCurrentTab());
	    
	    
	}

}
