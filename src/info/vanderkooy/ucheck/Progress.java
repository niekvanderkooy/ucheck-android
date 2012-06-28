package info.vanderkooy.ucheck;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class Progress extends Activity {
	private WebView webView;
	private APIHandler handler;
	private Preferences prefs;
	private ProgressDialog dialog;
	private String progressData;
	private Button refreshButton;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.progress);
	    webView = (WebView) findViewById(R.id.webView);
	    refreshButton = (Button) findViewById(R.id.refresh);
	    handler = new APIHandler(getApplicationContext());
	    prefs = new Preferences(getApplicationContext());
	    
	    refreshButton.setOnClickListener(refreshListener);

	    prefs.forceNewProgress();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(prefs.progressNeedUpdate()) {
			load();			
		}
	}
	
	private void load() {
		dialog = ProgressDialog.show(Progress.this, "", getString(R.string.getProgress), true);

		Thread thread = new Thread(new Runnable() {
			public void run() {
				progressData = handler.getProgress();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						processData();
						if (dialog.isShowing()) {
							dialog.hide();
							dialog.dismiss();
						}
					}
				});
			}
		});
		thread.start();
	}
	
	private void processData() {
	    String webData = "";
		try {
			//When not using URLEncoder, webView.loadData() tries to load the HTML as a URL.
			webData = URLEncoder.encode(progressData,"utf-8").replaceAll("\\+"," ");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if(progressData.equals("") || webData.equals("")) {
	    	Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.loadError), Toast.LENGTH_LONG);
	    	toast.show();
	    }
	    webView.loadData(webData, "text/html", null);
	    prefs.setLastProgressUpdate();		
	}
	
	private OnClickListener refreshListener = new OnClickListener() {
		public void onClick(View v) {
			load();
		}
	};

}
