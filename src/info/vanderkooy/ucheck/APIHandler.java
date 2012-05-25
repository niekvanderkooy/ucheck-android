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
	
	public boolean setUsername(String username) {
		return false;
	}
	
	public String getUsername() {
		String username = "";
		return username;		
	}
	
	public boolean setPassword(String password) {
		
		return false;
	}
	
	public String getPassword() {
		String password = "";
		return password;
	}
	
}
