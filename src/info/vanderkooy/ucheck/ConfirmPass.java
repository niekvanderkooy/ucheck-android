package info.vanderkooy.ucheck;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfirmPass extends Activity {
	private Preferences prefs;
	private EditText username;
	private EditText password;
	private Button confirmButton;
	private Tracker tracker;
	private APIHandler handler;
	private ProgressDialog dialog;
	private boolean confirmed;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm);

		prefs = new Preferences(getApplicationContext());
		handler = new APIHandler(getApplicationContext());
		tracker = GoogleAnalytics.getInstance(getApplicationContext()).getDefaultTracker();
		
		confirmButton = (Button) findViewById(R.id.confirm);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		password.setTextSize(13);
		
		confirmed = false;

		confirmButton.setText("Bevestigen");
		username.setKeyListener(null);
		username.setTextColor(Color.GRAY);
		confirmButton.setOnClickListener(confirmListener);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		tracker.trackView("/confirmPass");
		username.setText(prefs.getUsername());
	}
	

	private OnClickListener confirmListener = new OnClickListener() {
		public void onClick(View v) {
			dialog = ProgressDialog.show(ConfirmPass.this, "",
					"Wachtwoord wordt bevestigd...", true);

			Thread thread = new Thread(new Runnable() {
				public void run() {
					if(handler.isNetworkAvailable()) {
						confirmed = handler.verifyLogin(password.getText().toString());
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (confirmed) {
								    try {
										dialog.hide();
								        dialog.dismiss();
								        dialog = null;
								    } catch (Exception e) {
								        // nothing
								    }
									finish();
								} else {
									Toast toast = Toast.makeText(getApplicationContext(),"Wachtwoord was onjuist",Toast.LENGTH_LONG);
									toast.show();
									try {
										dialog.hide();
								        dialog.dismiss();
								        dialog = null;
								    } catch (Exception e) {
								        // nothing
								    }
									finish();
								}
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
								handler.noNetworkToast();
							}
						});
					}
				}
			});
			thread.start();
		}
	};
	
	@Override
	public void finish() {
	    Intent data = new Intent();
	    setResult(confirmed ? 1 : 0, data);
	    super.finish();
	}

}
