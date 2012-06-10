package info.vanderkooy.ucheck;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
//import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class UcheckAndroidMain extends TabActivity {
	private APIHandler handler;
	private Preferences prefs;
	private TabHost tabHost;
	private ProgressDialog dialog;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    handler = new APIHandler(getApplicationContext());
	    prefs = new Preferences(getApplicationContext());
	    //Resources res = getResources();
		tabHost = getTabHost(); // The activity TabHost
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
		
	}
	
	public void onResume() {
		super.onResume();
		if(!prefs.getGoingToInfo()) {
			dialog = ProgressDialog.show(UcheckAndroidMain.this, "", "Login gegevens worden nagekeken.", true);

			Thread thread = new Thread(new Runnable() {
				public void run() {
					if(handler.verifyLogin() == 1) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (dialog.isShowing()) {
									dialog.hide();
									dialog.dismiss();
								}
								tabHost.setCurrentTab(0);
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (dialog.isShowing()) {
									dialog.hide();
									dialog.dismiss();
								}
								Intent loginIntent = new Intent().setClass(UcheckAndroidMain.this, Login.class);
								UcheckAndroidMain.this.startActivity(loginIntent);
							}
						});
					}
				}
			});
			thread.start();
		} else {
			prefs.setGoingToInfo(false);
		}
		
	}
	
	public void onPause() {
		super.onPause();
		if(!prefs.getStorePass())
			prefs.clearPassword();		
	}

}
