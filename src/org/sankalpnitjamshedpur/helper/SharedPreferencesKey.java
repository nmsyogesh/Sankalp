package org.sankalpnitjamshedpur.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesKey {
	
	public static final String PREFS_NAME = "org.sankalpnitjamshedpur";
	
	public static void putInSharedPreferences(String key, String value, Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				SharedPreferencesKey.PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putString(key,value);
		editor.commit();
	}
	
	public static void putInSharedPreferences(String key, boolean value, Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				SharedPreferencesKey.PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putBoolean(key,value);
		editor.commit();
	}
	
	public static String getStringFromSharedPreferences(String key, String defaultValue, Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				SharedPreferencesKey.PREFS_NAME, Context.MODE_PRIVATE);
		return settings.getString(key, defaultValue);
	}
	
	public static boolean getBooleanFromSharedPreferences(String key, boolean defaultValue, Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				SharedPreferencesKey.PREFS_NAME, Context.MODE_PRIVATE);
		return settings.getBoolean(key, defaultValue);
	}
}
