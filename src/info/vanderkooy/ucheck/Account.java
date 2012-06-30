package info.vanderkooy.ucheck;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class Account extends Activity {
	private Preferences prefs;
	private EditText username;
	private EditText password;
	private CheckBox storePass;
	private Button infoButton;
	private Button loginButton;
	private GoogleAnalyticsTracker tracker;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account);

		prefs = new Preferences(getApplicationContext());
		tracker = GoogleAnalyticsTracker.getInstance();
		
		infoButton = (Button) findViewById(R.id.info);
		loginButton = (Button) findViewById(R.id.login);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		storePass = (CheckBox) findViewById(R.id.remember);

		loginButton.setText(getString(R.string.logout));
		username.setKeyListener(null);
		password.setKeyListener(null);
		username.setTextColor(Color.GRAY);
		password.setTextColor(Color.GRAY);
		storePass.setOnClickListener(storePassListener);
		loginButton.setOnClickListener(logoutListener);
		infoButton.setOnClickListener(infoButtonListener);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		tracker.trackPageView("/account");
		username.setText(prefs.getUsername());
		password.setText("........");
		storePass.setChecked(prefs.getStorePass());		
	}
	
	private OnClickListener storePassListener = new OnClickListener() {
		public void onClick(View v) {
			tracker.trackEvent("Account", "Click", "storePass", 0);
			prefs.setStorePass(storePass.isChecked());
		}
	};

	private OnClickListener logoutListener = new OnClickListener() {
		public void onClick(View v) {
			prefs.clearKey();
			tracker.trackEvent("Account", "Click", "logout", 0);
			Intent loginIntent = new Intent().setClass(Account.this,
					Login.class);
			Account.this.startActivity(loginIntent);
		}
	};
	
	private OnClickListener infoButtonListener = new OnClickListener() {
		public void onClick(View v) {
			prefs.setGoingToInfo(true);
			tracker.trackEvent("Account", "Click", "info", 0);
			Intent infoIntent = new Intent().setClass(Account.this,
					Info.class);
			Account.this.startActivity(infoIntent);
		}
	};

}
