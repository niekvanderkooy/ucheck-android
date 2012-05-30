package info.vanderkooy.ucheck;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Account extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account);

		Button infoButton = (Button) findViewById(R.id.info);
		infoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent infoIntent = new Intent().setClass(Account.this,
						Info.class);
				Account.this.startActivity(infoIntent);
			}
		});

	}

}
