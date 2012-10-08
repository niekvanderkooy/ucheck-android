package info.vanderkooy.ucheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.google.common.collect.BiMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Enroll extends Activity {
	private Tracker tracker;
	private Preferences prefs;
	private JSONObject data;
	private JSONObject subjectInfo;
	private JSONArray cat;
	private APIHandler handler;
	private Spinner studies;
	private String loopbaanString;
	private Spinner loopbaan;
	private ProgressDialog dialog;
	private BiMap<String, String> keyToStudie;
	private BiMap<String, String> studieToKey;
	private ArrayList<String> spinnerArray;
	private ArrayList<String> loopbanenArray;
	private String filterText;
	private String subID;
	private String subNumber;
	private CharSequence[] categories;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enroll);
		tracker = GoogleAnalytics.getInstance(getApplicationContext())
				.getDefaultTracker();
		prefs = new Preferences(getApplicationContext());
		handler = new APIHandler(getApplicationContext());
		loopbaan = (Spinner) findViewById(R.id.loopbaan);
		data = null;
		initStudiesSpinner();
		initFilter();
	}

	private void initFilter() {
		filterText = "";
		EditText editText = (EditText) findViewById(R.id.filter);
        editText.addTextChangedListener(new TextWatcher() {
          public void afterTextChanged(Editable s) {
            filterText = s.toString();
            if(data != null)
            	makeList();
          }
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
          public void onTextChanged(CharSequence s, int start, int before, int count) {}
       });
	}

	@Override
	public void onResume() {
		super.onResume();
		prefs.forceNewClasses();
		tracker.trackView("/enroll");
	}

	private void initStudiesSpinner() {
		studies = (Spinner) findViewById(R.id.studies);

		keyToStudie = (BiMap<String, String>) Meta.getStudieLijst();
		studieToKey = keyToStudie.inverse();

		ArrayList<String> studieList = getIntent().getExtras()
				.getStringArrayList("studies");

		String[] studieLijst = new String[studieToKey.size()];
		studieLijst = (String[]) studieToKey.keySet().toArray(studieLijst);
		spinnerArray = new ArrayList<String>();
		for (int i = 0; i < studieLijst.length; i++) {
			if (!studieList.contains(studieToKey.get(studieLijst[i])))
				spinnerArray.add(studieLijst[i]);
		}
		java.util.Collections.sort(spinnerArray);

		for (int i = 0; i < studieList.size(); i++) {
			if (!spinnerArray.contains(keyToStudie.get((String) studieList
					.get(i))))
				spinnerArray
						.add(0, keyToStudie.get((String) studieList.get(i)));
		}

		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, spinnerArray);
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		studies.setAdapter(spinnerArrayAdapter);
		studies.setOnItemSelectedListener(new StudieSelectedListener());
	}

	private void load(final String value) {
		loopbaanString = "Alles";
		dialog = ProgressDialog.show(Enroll.this, "",
				"Vakken worden opgehaald", true);
		dialog.setCancelable(true);

		Thread thread = new Thread(new Runnable() {
			public void run() {
				if (handler.isNetworkAvailable()) {
					data = handler.getSubjects(value);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (dialog.isShowing()) {
								processData();
								dialog.hide();
								dialog.dismiss();
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

	private void processData() {
		if(data != null) {
			getLoopbanen();
			makeList();
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Data kon niet worden opgehaald. Wellicht is uSis down, of heb je een slechte internetverbinding.", Toast.LENGTH_LONG);
			toast.show();
		}

	}
	
	private OnItemClickListener subjectClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(arg2 == 0) {
				//First row, not a subject
			} else {
				final String subjectID = (String) ((TextView) arg1.findViewById(R.id.info)).getText();
				dialog = ProgressDialog.show(Enroll.this, "",
						"Vakinfo wordt opgehaald", true);
				dialog.setCancelable(true);

				Thread thread = new Thread(new Runnable() {
					public void run() {
						if (handler.isNetworkAvailable()) {
							subjectInfo = handler.getSubjectInfo(subjectID);
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (dialog.isShowing()) {
										subID = subjectID;
										subject(subjectInfo, subjectID);
										dialog.hide();
										dialog.dismiss();
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
		}
	};
	
	private void subject(JSONObject subjectInfo, String subjectID) {
		if(subjectInfo != null) {
			int aantal = subjectInfo.length();
			if(aantal > 1) {
				AlertDialog.Builder builder = new AlertDialog.Builder(Enroll.this);
				Iterator itr = subjectInfo.keys();
				categories = new CharSequence[aantal];
				for(int i = 0; i < aantal; i++)
					categories[i] = (CharSequence) itr.next();
				
				builder.setTitle("Kies een categorie").setItems(categories, categoryListener).show();
			} else {
				try {
					enroll(subjectInfo.getJSONArray((String) subjectInfo.keys().next()));
				} catch (JSONException e) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Er is iets mis gegaan. Probeer het later nog een keer.", Toast.LENGTH_LONG);
					toast.show();
					e.printStackTrace();
				}
			}	
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Data kon niet worden opgehaald. Wellicht is uSis down, of heb je een slechte internetverbinding.", Toast.LENGTH_LONG);
			toast.show();
		}
	}
	
	private DialogInterface.OnClickListener categoryListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.v("which", Integer.valueOf(which).toString());
			try {
				enroll(subjectInfo.getJSONArray((String) categories[which]));
			} catch (JSONException e) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Er is iets mis gegaan. Probeer het later nog een keer.", Toast.LENGTH_LONG);
				toast.show();
				e.printStackTrace();
			}
			
		}
	};
	
	private void enroll(JSONArray category) {
		cat = category;
		if(category.length() == 1)
			try {
				confirm((JSONObject) category.get(0));
			} catch (JSONException e1) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Er is iets mis gegaan. Probeer het later nog een keer.", Toast.LENGTH_LONG);
				toast.show();
				e1.printStackTrace();
			}
		else {
			AlertDialog.Builder builder = new AlertDialog.Builder(Enroll.this);
			CharSequence[] cs = new CharSequence[category.length()];
			for(int i = 0; i < category.length(); i++) {
				JSONObject specific;
				try {
					specific = (JSONObject) category.get(i);
					cs[i] = specific.getString("info");
				} catch (JSONException e) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Er is iets mis gegaan. Probeer het later nog een keer.", Toast.LENGTH_LONG);
					toast.show();
					e.printStackTrace();
				}
			}
			
			builder.setTitle("Kies een vak").setItems(cs, enrollListener).show();
		}
	}
	
	private DialogInterface.OnClickListener enrollListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			try {
				confirm((JSONObject) cat.get(which));
			} catch (JSONException e) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Er is iets mis gegaan. Probeer het later nog een keer.", Toast.LENGTH_LONG);
				toast.show();
				e.printStackTrace();
			}
			
		}
	};

	private void confirm(JSONObject specific) {
		String title = "";
		String info = "";
		subNumber = "";
		try {
			title = (String) specific.get("titel");
			info = (String) specific.get("info");
			subNumber = ((Integer) specific.get("nummer")).toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(Enroll.this);
		builder.setMessage("Weet je zeker dat je je wilt inschrijven voor: " + title + ", " + info).setPositiveButton("Ja", confirmClickListener)
		    .setNegativeButton("Nee", confirmClickListener).show();
		
	}
	
	DialogInterface.OnClickListener confirmClickListener = new DialogInterface.OnClickListener() {
		String response = "";
		
	    @Override
	    public void onClick(DialogInterface _dialog, int which) {
	    	switch (which){
	        case DialogInterface.BUTTON_POSITIVE:
	        	dialog = ProgressDialog.show(Enroll.this, "",
	    				"Bezig...", true);
	        	
	        	Thread thread = new Thread(new Runnable() {
	    			public void run() {
	    				if(handler.isNetworkAvailable()) {
	    					response = handler.enroll(subID, subNumber);
	    					runOnUiThread(new Runnable() {
	    						@Override
	    						public void run() {
	    							if (dialog.isShowing()) {
	    								dialog.hide();
	    								dialog.dismiss();
	    							}
	    							Toast toast = Toast.makeText(getApplicationContext(),
	    									"Antwoord van uSis: " + response, Toast.LENGTH_LONG);
	    							toast.show();
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
	            break;

	        case DialogInterface.BUTTON_NEGATIVE:
	            //No button clicked
	            break;
	        }
	    }
	};

	private void getLoopbanen() {
		loopbanenArray = new ArrayList<String>();
		loopbanenArray.add("Alles");
		for (int i = 1; i <= data.length(); i++) {
			try {
				JSONObject subject = data.getJSONObject(Integer.valueOf(i)
						.toString());
				if (!loopbanenArray.contains(subject.getString("loopbaan")))
					loopbanenArray.add(subject.getString("loopbaan"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, loopbanenArray);
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		loopbaan.setAdapter(spinnerArrayAdapter);
		loopbaan.setOnItemSelectedListener(new LoopbaanSelectedListener());

	}

	public void makeList() {
		ListView list = (ListView) findViewById(R.id.list);
		String subLoopbaan = "";
		String subject = "";
		String id = "";

		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;
		map = new HashMap<String, String>();
		map.put("subject", "Vak");
		map.put("id", "ID");
		mylist.add(map);
		for (int i = 1; i <= data.length(); i++) {
			map = new HashMap<String, String>();
			try {
				subLoopbaan = (String) data.getJSONObject(Integer.valueOf(i).toString()).get("loopbaan");
				subject = (String) data.getJSONObject(Integer.valueOf(i).toString()).get("titel");
				subject = subject.replace("&amp;", "&");
				id = (String) data.getJSONObject(Integer.valueOf(i).toString()).get("gidsnummer");
				map.put("subject", subject);
				map.put("id", id);
			} catch (JSONException e) {
				tracker.trackEvent("Exception", "Classes",
						"makeList JSONException", (long) 0);
				e.printStackTrace();
			}
			if (loopbaanString.equals(subLoopbaan) || loopbaanString.equals("Alles")) {
				if(filterText.equals(""))
					mylist.add(map);
				else
					if(subject.toLowerCase().contains(filterText.toLowerCase()) || id.toLowerCase().contains(filterText.toLowerCase()))
						mylist.add(map);
			}
		}
		ListAdapter mSchedule = new ListAdapter(this, mylist,
				R.layout.rowclasses, new String[] { "subject", "id" },
				new int[] { R.id.classes, R.id.info });
		list.setAdapter(mSchedule);
		list.setOnItemClickListener(subjectClickListener);
		list.setSelector(android.R.color.transparent);

	}

	private class StudieSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			String value = studieToKey.get(parent.getItemAtPosition(pos).toString());
			load(value);
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing.
		}
	}

	private class LoopbaanSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			loopbaanString = parent.getItemAtPosition(pos).toString();
			makeList();
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing.
		}
	}

}
