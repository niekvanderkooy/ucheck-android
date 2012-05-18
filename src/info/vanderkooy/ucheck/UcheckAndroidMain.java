package info.vanderkooy.ucheck;

import android.app.TabActivity;
import android.content.Intent;
//import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class UcheckAndroidMain extends TabActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //Resources res = getResources();
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, Grades.class);

		//TODO: Change setIndicator(getString()) to setIndicator(getString(), res.getDrawable(R.drawable.ic_tab_settings) to ad icons
		
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("grades").setIndicator(getString(R.string.gradesTab))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, Classes.class);
		spec = tabHost.newTabSpec("classes").setIndicator(getString(R.string.classesTab))
				.setContent(intent);
		tabHost.addTab(spec);
		

		intent = new Intent().setClass(this, Progress.class);
		spec = tabHost.newTabSpec("progress").setIndicator(getString(R.string.progressTab))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Account.class);
		spec = tabHost.newTabSpec("account").setIndicator(getString(R.string.accountTab))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}

}
