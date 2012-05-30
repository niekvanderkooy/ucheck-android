package info.vanderkooy.ucheck;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

public class Progress extends Activity {
	private WebView webView;
	private APIHandler handler;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.progress);
	    webView = (WebView) findViewById(R.id.webView);
	    handler = new APIHandler(getApplicationContext());
	    String progressData = handler.getProgress();
	    if(progressData.equals("")) {
	    	Toast toast = Toast.makeText(getApplicationContext(), "Er is iets mis gegaan bij het ophalen van voortgangsdata. Probeer het later nog een keer.", 6);
	    	toast.show();
	    }
	    webView.loadData(progressData, "text/html", null);
	}

}
