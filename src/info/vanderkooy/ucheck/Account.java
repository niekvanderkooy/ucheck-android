package info.vanderkooy.ucheck;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Account extends Activity {
	private APIHandler handler;
	private Preferences prefs;
	private EditText username;
	private EditText password;
	private ProgressDialog dialog;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account);

		handler = new APIHandler(getApplicationContext());
		prefs = new Preferences(getApplicationContext());
		Button infoButton = (Button) findViewById(R.id.info);
		Button loginButton = (Button) findViewById(R.id.login);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);

		dialog = ProgressDialog.show(Account.this, "", "", true);
		dialog.show();

		if (handler.verifyLogin()) {
			loginButton.setText("Uitloggen");
			username.setText(prefs.getUsername());
			password.setText("........");
			loginButton.setOnClickListener(logoutListener);
		} else {
			loginButton.setText("Inloggen");
			username.setHint("Studentnummer");
			password.setHint("uSis wachtwoord");
			loginButton.setOnClickListener(loginListener);
		}
		
		dialog.hide();

		infoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent infoIntent = new Intent().setClass(Account.this,
						Info.class);
				Account.this.startActivity(infoIntent);
			}
		});
	}
	
	private OnClickListener logoutListener = new OnClickListener() {
		public void onClick(View v) {
			prefs.clearPassword();
			Intent loginIntent = new Intent().setClass(Account.this, Account.class);
			Account.this.startActivity(loginIntent);
		}
	};
	
	private OnClickListener loginListener = new OnClickListener() {
		public void onClick(View v) {
			String usernameString = username.getText().toString();
			if(!(usernameString.substring(0, 1) == "s"))
				usernameString = "s" + usernameString;
			prefs.setUsername(usernameString);
			prefs.setPassword(password.getText().toString());
			dialog.show();
			boolean success = handler.verifyLogin();
			dialog.hide();
			if(success)
				finish();
			else {
				Toast toast = Toast.makeText(getApplicationContext(), "Ongeldige gebruikersnaam en/of wachtwoord", 3);
				toast.show();
			}
		}
	};

}
