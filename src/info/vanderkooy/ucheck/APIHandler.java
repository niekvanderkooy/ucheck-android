package info.vanderkooy.ucheck;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class APIHandler {
	private SharedPreferences pref;
	private SafeSharedPrefs safePref;
	
	public APIHandler(Context ctx) {
		pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		safePref = new SafeSharedPrefs(ctx, pref);
	}
}
