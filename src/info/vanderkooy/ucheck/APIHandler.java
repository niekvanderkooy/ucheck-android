package info.vanderkooy.ucheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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
		return pref.getString("username", "");
	}

	public boolean setPassword(String password) {	
		return false;
	}

	public String getPassword() {
		return safePref.getString("password", "");
	}

	public boolean verifyLogin() {
		String username = getUsername();
		String password = getPassword();
		if (username == "" || password == "")
			return false;
		
		String key = getWebPage("https://ucheck.nl/api/login.php?user=" + URLEncoder.encode(username) + "&pass=" + URLEncoder.encode(password));
		if(key.length() >= 3 && !key.substring(0, 3).equalsIgnoreCase("err")) {
			pref.edit().putString("key", key);
			return pref.edit().commit();
		} else {
			return false;
		}
	}

	private String getWebPage(String page) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(page);
		HttpResponse response;
		try {
			response = httpClient.execute(httpGet, localContext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		String result = "";

		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				result += line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return result;
	}
}
