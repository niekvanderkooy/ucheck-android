package info.vanderkooy.ucheck;

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account);

		prefs = new Preferences(getApplicationContext());
		Button infoButton = (Button) findViewById(R.id.info);
		Button loginButton = (Button) findViewById(R.id.login);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		storePass = (CheckBox) findViewById(R.id.remember);

		loginButton.setText("Uitloggen");
		username.setKeyListener(null);
		password.setKeyListener(null);
		username.setTextColor(Color.GRAY);
		password.setTextColor(Color.GRAY);
		storePass.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				prefs.setStorePass(storePass.isChecked());
			}
		});
		loginButton.setOnClickListener(logoutListener);

		infoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				prefs.setGoingToInfo(true);
				Intent infoIntent = new Intent().setClass(Account.this,
						Info.class);
				Account.this.startActivity(infoIntent);
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		username.setText(prefs.getUsername());
		password.setText("........");
		storePass.setChecked(prefs.getStorePass());		
	}

	private OnClickListener logoutListener = new OnClickListener() {
		public void onClick(View v) {
			prefs.clearKey();
			Intent loginIntent = new Intent().setClass(Account.this,
					Login.class);
			Account.this.startActivity(loginIntent);
		}
	};

}
