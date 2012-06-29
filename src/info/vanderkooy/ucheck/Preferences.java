package info.vanderkooy.ucheck;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Preferences {
	private SharedPreferences pref;
	private Editor editor;

	public Preferences(Context ctx) {
		pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		editor = pref.edit();
	}

	public boolean setUsername(String username) {
		editor.putString("username", username);
		return editor.commit();
	}

	public String getUsername() {
		return pref.getString("username", "");
	}

	public boolean setStorePass(boolean store) {
		editor.putBoolean("storePass", store);
		return editor.commit();
	}

	public boolean getStorePass() {
		return pref.getBoolean("storePass", false);
	}

	public boolean clearKey() {
		editor.remove("key");
		return editor.commit();
	}

	// Functions to determine if user is going to info screen, so login is not
	// unnecessarily validated
	public boolean setGoingToInfo(boolean b) {
		editor.putBoolean("info", b);
		return editor.commit();
	}

	public boolean getGoingToInfo() {
		return pref.getBoolean("info", false);
	}

	public String getKey() {
		return pref.getString("key", "");
	}

	public Editor edit() {
		return editor;
	}
	
	public boolean setKillApp() {
		editor.putBoolean("killapp", true);
		return editor.commit();
	}
	
	public boolean getKillApp() {
		return pref.getBoolean("killapp", false);
	}
	
	public boolean clearKillApp() {
		editor.remove("killapp");
		return editor.commit();
	}
	
	/***************************************************************
	 * What follow is really ugly code to keep track
	 * of when a particular tab was last updated,
	 * to check if it needs to happen again. For lack
	 * of iOS-like pull-to-refresh.
	 * 
	 * Maybe someone had a better idea and wants to patch it. :)
	 * 
	 **************************************************************/
	
	public boolean forceNewData() {
		return (forceNewGrades() && forceNewClasses() && forceNewProgress());
	}
	
	public boolean forceNewGrades() {
		editor.putString("lastGradesUpdate", "200001010900");
		return editor.commit();
	}
	
	public boolean forceNewClasses() {
		editor.putString("lastClassesUpdate", "200001010900");
		return editor.commit();
	}
	
	public boolean forceNewProgress() {
		editor.putString("lastProgressUpdate", "200001010900");
		return editor.commit();
		
	}
	
	public boolean setLastGradesUpdate() {
		editor.putString("lastGradesUpdate", nowToString());
		return editor.commit();
	}

	public boolean setLastClassesUpdate() {
		editor.putString("lastClassesUpdate", nowToString());
		return editor.commit();
	}

	public boolean setLastProgressUpdate() {
		editor.putString("lastProgressUpdate", nowToString());
		return editor.commit();
	}

	public boolean gradesNeedUpdate() {
		Date lastUpdate = stringToDate(pref.getString("lastGradesUpdate",
				"200001010900"));
		Date dateNow = new Date();
		if(minutesDiff(lastUpdate, dateNow) > 60)
			return true;
		return false;
	}
	
	public boolean classesNeedUpdate() {
		Date lastUpdate = stringToDate(pref.getString("lastClassesUpdate",
				"200001010900"));
		Date dateNow = new Date();
		if(minutesDiff(lastUpdate, dateNow) > 60)
			return true;
		return false;
	}
	
	public boolean progressNeedUpdate() {
		Date lastUpdate = stringToDate(pref.getString("lastProgressUpdate",
				"200001010900"));
		Date dateNow = new Date();
		if(minutesDiff(lastUpdate, dateNow) > 60)
			return true;
		return false;
	}

	private String nowToString() {
		Date dateNow = new Date();

		SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmm");

		StringBuilder now = new StringBuilder(date.format(dateNow));
		return now.toString();
	}

	private Date stringToDate(String s) {
		try {
			return new SimpleDateFormat("yyyyMMddHHmm").parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
	}

	private int minutesDiff(Date earlierDate, Date laterDate) {
		if (earlierDate == null || laterDate == null)
			return 0;

		return (int) ((laterDate.getTime() / 60000) - (earlierDate.getTime() / 60000));
	}
}
