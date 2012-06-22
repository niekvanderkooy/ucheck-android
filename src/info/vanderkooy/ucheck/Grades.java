package info.vanderkooy.ucheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class Grades extends Activity {
	private APIHandler handler;
	private Preferences prefs;
	private JSONObject data;
	private JSONArray subjects;
	private List<String> studies;
	private Spinner spinner;
	private ProgressDialog dialog;

	private Map<String, String> studieLijst = new HashMap<String, String>();

	private int numberOfStudies;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grades);

		spinner = (Spinner) findViewById(R.id.spinner);
		handler = new APIHandler(getApplicationContext());
		prefs = new Preferences(getApplicationContext());
		studies = new ArrayList<String>();
		
		prefs.forceNewGrades();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (prefs.gradesNeedUpdate()) {
			load();
		}
	}

	private void load() {
		dialog = ProgressDialog.show(Grades.this, "", "Data wordt opgehaald.", true);

		Thread thread = new Thread(new Runnable() {
			public void run() {
				fillStudieLijst();
				data = handler.getGrades();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						int success = processData();
						if (dialog.isShowing()) {
							dialog.hide();
							dialog.dismiss();
						}
						if(success == -1) {
							Intent loginIntent = new Intent().setClass(Grades.this, Login.class);
							Grades.this.startActivity(loginIntent);
						}
					}
				});
			}
		});
		thread.start();
	}

	private int processData() {
		if (data == null) {
			Toast toast = Toast
					.makeText(
							getApplicationContext(),
							"Er is iets mis gegaan bij het ophalen van cijferdata. Probeer het later nog een keer.",
							6);
			toast.show();
		}
		String loginerror = "uninit";
		try {
			loginerror = data.getString("error");
		} catch (JSONException e1) {
			// No error, so password was correct
		}
		if(loginerror.equals("loginerror")) {
			return -1;			
		}
		prefs.setLastGradesUpdate();
		try {
			subjects = data.getJSONArray("vakken");
		} catch (JSONException e) {
			Toast toast = Toast
					.makeText(
							getApplicationContext(),
							"Er is iets mis gegaan bij het ophalen van cijferdata. Probeer het later nog een keer.",
							6);
			toast.show();
			e.printStackTrace();
		}
		numberOfStudies = 0;
		studies.clear();
		for (int i = 0; i < subjects.length(); i++) {
			String vak = "";
			try {
				vak = (String) subjects.getJSONObject(i).get("studie");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!studies.contains(vak) && !vak.equals("")) {
				studies.add(vak);
				numberOfStudies++;
			}
		}
		if (numberOfStudies > 1) {
			spinner.setVisibility(0);
			updateSpinner();
		} else {
			spinner.setVisibility(8);
			makeList("Alle");
		}
		return 0;
	}

	private void updateSpinner() {
		ArrayList<String> spinnerArray = new ArrayList<String>();
		spinnerArray.add("Alle cijfers");
		for (int i = 0; i < studies.size(); i++) {
			if (studieLijst.get(studies.get(i)) != null)
				spinnerArray.add(studieLijst.get(studies.get(i)));
			else
				spinnerArray.add(studies.get(i));
		}

		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, spinnerArray);
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerArrayAdapter);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	}

	private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
		for (Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public void makeList(String subject) {
		ListView list = (ListView) findViewById(R.id.list);
		String studie = "";

		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;
		map = new HashMap<String, String>();
		map.put("subject", "Vak");
		map.put("grade", "Cijfer");
		map.put("EC", "EC");
		mylist.add(map);
		for (int i = 0; i < subjects.length(); i++) {
			map = new HashMap<String, String>();
			try {
				map.put("subject", (String) subjects.getJSONObject(i)
						.get("vak"));
				map.put("grade",
						(String) subjects.getJSONObject(i).get("cijfer"));
				map.put("EC", (String) subjects.getJSONObject(i).get("ects"));
				if (!(Boolean) subjects.getJSONObject(i).get("gehaald"))
					map.put("gehaald", "false");
				studie = (String) subjects.getJSONObject(i).get("studie");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (subject.equals("Alle cijfers") || subject.equals(studie)) {
				mylist.add(map);
			}
		}
		ListAdapter mSchedule = new ListAdapter(this, mylist,
				R.layout.rowgrades, new String[] { "subject", "grade", "EC" },
				new int[] { R.id.subject, R.id.grade, R.id.EC });
		list.setAdapter(mSchedule);
		list.setSelector(android.R.color.transparent);

	}

	private class MyOnItemSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			String value = getKeyByValue(studieLijst,
					parent.getItemAtPosition(pos).toString());
			String subject = "";
			if (value != null)
				subject = value;
			else
				subject = parent.getItemAtPosition(pos).toString();
			makeList(subject);
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// Do nothing.
		}
	}

	private void fillStudieLijst() {
		studieLijst.put("ALG", "Algemeen vakgebied");
		studieLijst.put("ARAB", "Arabische talen en culturen");
		studieLijst.put("ARCH", "Archeologie en prehistorie");
		studieLijst.put("ASA", "Area Studies Asia");
		studieLijst.put("BSKE", "Bestuurskunde");
		studieLijst.put("BFW", "Bio-farmaceutische wetenschap");
		studieLijst.put("BIO", "Biologie");
		studieLijst.put("BIOM", "Biomedische wetenschappen");
		studieLijst.put("BOEK", "Boek en digitale media");
		studieLijst.put("CHE", "Chemistry");
		studieLijst.put("CLANEC", "Classics Ancient Near East Civ");
		studieLijst.put("CANS", "Cult. antropologie ontw. soc.");
		studieLijst.put("DUITS", "Duitse taal en cultuur");
		studieLijst.put("DUTCHST", "Dutch Studies (Nederlandkunde)");
		studieLijst.put("EGYPTE", "Egyptische taal en cultuur");
		studieLijst.put("ENGELS", "Engelse taal en cultuur");
		studieLijst.put("EUS", "European Union Studies");
		studieLijst.put("FGWALG", "FGW Algemeen");
		studieLijst.put("W&N", "Faculteit W&N");
		studieLijst.put("PHOTOGS", "Film and Photographic studies");
		studieLijst.put("FRANS", "Franse taal en cultuur");
		studieLijst.put("GNK", "Geneeskunde");
		studieLijst.put("GS", "Geschiedenis");
		studieLijst.put("GODG", "Godgeleerdheid");
		studieLijst.put("GRIEKLAT", "Griekse Latijnse Taal & Cult.");
		studieLijst.put("HJS", "Hebreeuwse en Joodse Studies");
		studieLijst.put("HERV", "Hervormde Kerk");
		studieLijst.put("INDECO", "Industrial Ecology");
		studieLijst.put("INF", "Informatica");
		studieLijst.put("ISLM", "Islamic Studies");
		studieLijst.put("ISLT", "Islamitische theologie");
		studieLijst.put("ITAL", "Italiaanse taal en cultuur");
		studieLijst.put("JOURNIME", "Journalistiek en nieuwe media");
		studieLijst.put("FGWKERN", "Kerncurriculum FGW");
		studieLijst.put("FDK", "Kunsten");
		studieLijst.put("KG", "Kunstgeschiedenis");
		studieLijst.put("LAAS", "Latin American Amerindian Stud");
		studieLijst.put("LEIALG", "Leiden algemeen");
		studieLijst.put("LO", "Lerarenopleiding");
		studieLijst.put("FLEBYVAK", "Letteren Bijvak");
		studieLijst.put("FLEALG", "Letteren algemeen");
		studieLijst.put("LETTERK", "Letterkunde");
		studieLijst.put("LA%26S", "Liberal Arts & Sciences");
		studieLijst.put("LST", "Life Science and technology");
		studieLijst.put("LITW", "Literatuurwetenschap");
		studieLijst.put("MANAGEME", "Management");
		studieLijst.put("MIDOOST", "Midden Oosten Studies");
		studieLijst.put("MST", "Molecular science &Technology");
		studieLijst.put("MUZIEK", "Muziek");
		studieLijst.put("NSC", "Nanoscience");
		studieLijst.put("NTK", "Natuurkunde");
		studieLijst.put("NED", "Nederlandse taal en cultuur");
		studieLijst.put("NP", "Nieuwperzische taal en cultuur");
		studieLijst.put("OCMW", "Oude Culturen van de Mediter W");
		studieLijst.put("PEDA", "Pedagogische wetenschappen");
		studieLijst.put("POWE", "Politicologie");
		studieLijst.put("PKST", "Praktijkstudies");
		studieLijst.put("PREUNIV", "Pre-university");
		studieLijst.put("PSYC", "Psychologie");
		studieLijst.put("LAW", "Rechten");
		studieLijst.put("SEMI", "Semitische talen en culturen");
		studieLijst.put("SLAV", "Slavische talen cult/Ruslandk.");
		studieLijst.put("STK", "Sterrenkunde");
		studieLijst.put("TCIA", "T&C van Indiaans Amerika");
		studieLijst.put("TCMA", "T&C van Mesopota & Anatolië");
		studieLijst.put("TAALK", "Taalkunde");
		studieLijst.put("TW", "Taalwetenschap");
		studieLijst.put("INDTIBET", "Talen en Cult. India en Tibet");
		studieLijst.put("INDONES", "Talen en Culturen Indonesië");
		studieLijst.put("AFRIKA", "Talen en Culturen van Afrika");
		studieLijst.put("TCLA", "Talen en culturen Latijns Am");
		studieLijst.put("CHINA", "Talen en culturen van China");
		studieLijst.put("JAPAN", "Talen en culturen van Japan");
		studieLijst.put("KOREA", "Talen en culturen van Korea");
		studieLijst.put("TCC", "Talencentrum: Communicatie");
		studieLijst.put("THEA", "Theater- en filmwetenschap");
		studieLijst.put("TURK", "Turkse talen en culturen");
		studieLijst.put("VIET", "Vergelijkende Indo-Europese TW");
		studieLijst.put("VTW", "Vergelijkende Taalwetenschap");
		studieLijst.put("LAVA", "Vitality and ageing");
		studieLijst.put("WYSB", "Wijsbegeerte");
		studieLijst.put("WSK", "Wiskunde");
		studieLijst.put("ZZOAZIE", "Zuid en Zuid-Oost Azië");
	}

}
