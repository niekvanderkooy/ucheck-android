package info.vanderkooy.ucheck;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Grades extends Activity {
	private APIHandler handler;
	private Preferences prefs;
	private JSONObject data;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.grades);
	    handler = new APIHandler(getApplicationContext());
	    prefs = new Preferences(getApplicationContext());
	    load();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(prefs.gradesNeedUpdate()) {
			load();			
		}
		Log.w("ucheck", data.toString());
	}
	
	private void load() {
		data = handler.getGrades();
	    if(data == null) {
	    	Toast toast = Toast.makeText(getApplicationContext(), "Er is iets mis gegaan bij het ophalen van cijferdata. Probeer het later nog een keer.", 6);
	    	toast.show();
	    }
	    prefs.setLastGradesUpdate();
	}

}
