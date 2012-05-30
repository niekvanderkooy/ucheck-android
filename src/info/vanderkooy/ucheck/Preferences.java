package info.vanderkooy.ucheck;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Preferences {
	private SharedPreferences pref;
	private SafeSharedPrefs safePref;
	private Editor editor;
	private Editor safeEditor;

	public Preferences(Context ctx) {
		pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		safePref = new SafeSharedPrefs(ctx, pref);
		editor = pref.edit();
		safeEditor = safePref.edit();
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

	public boolean setPassword(String password) {	
		safeEditor.putString("password", password);
		return safeEditor.commit();
	}

	public String getPassword() {
		return safePref.getString("password", "");
	}
	
	public String getPasswordUnsafe() {
		return pref.getString("password", "");
	}
	
	public boolean clearPassword() {
		safeEditor.remove("password");
		return safeEditor.commit();
	}
	
	//Functions to determine if user is going to info screen, so login is not unnecessarily validated
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
}
