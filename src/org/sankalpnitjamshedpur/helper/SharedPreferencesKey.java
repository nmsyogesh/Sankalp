package org.sankalpnitjamshedpur.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesKey {
	public static final String KEY_NAME = "Name";
	public static final String KEY_ROLLNO = "RollNo";
	public static final String KEY_BATCH = "Batch";
	public static final String KEY_MOBILE_NO = "MobileNo";
	public static final String KEY_EMAIL_ID = "EmailId";
	public static final String KEY_PASSWORD = "Password";
	public static final String KEY_VOLUNTEERID = "VolunteerId";
	public static final String KEY_BRANCH = "Branch";
	
	public static final String PREFS_NAME = "org.sankalpnitjamshedpur";
	
	public static void putInSharedPreferences(String key, String value, Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				SharedPreferencesKey.PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putString(key,value);
		editor.commit();
	}
	
	public static String getStringFromSharedPreferences(String key, String defaultValue, Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				SharedPreferencesKey.PREFS_NAME, Context.MODE_PRIVATE);
		return settings.getString(key, defaultValue);
	}
	
	
}
