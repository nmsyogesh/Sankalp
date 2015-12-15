package org.sankalpnitjamshedpur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.entity.Centre;
import org.sankalpnitjamshedpur.entity.Exam;
import org.sankalpnitjamshedpur.entity.StudentClass;
import org.sankalpnitjamshedpur.entity.Subject;
import org.sankalpnitjamshedpur.helper.NetworkStatusChangeReceiver;
import org.sankalpnitjamshedpur.helper.SharedPreferencesKey;
import org.sankalpnitjamshedpur.helper.TAGS;
import org.sankalpnitjamshedpur.tabs.TabPagerAdapter;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class HomePage extends FragmentActivity implements ActionBar.TabListener {

	private ViewPager viewPager;
	private TabPagerAdapter tabPagerAdapter;
	private ActionBar actionBar;
	private DatabaseHandler dbHandler;
	private Context context;
	// Tab titles
	private String[] tabs = { "Profile", "Take a Class", "Report a Issue",
			"Class records", "Marks Entry" };

	Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homepage);
		context = this;
		dbHandler = new DatabaseHandler(this);

		viewPager = (ViewPager) findViewById(R.id.pager);
		tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(tabPagerAdapter);

		actionBar = getActionBar();

		// Enable Tabs on Action Bar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			/**
			 * on swipe select the respective tab
			 * */
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		if (SharedPreferencesKey.getBooleanFromSharedPreferences(
				TAGS.SYNC_CENTRES, true, this)) {
			SyncHandler syncHandler = new SyncHandler(TAGS.SYNC_CENTRES);
			syncHandler.execute(new HttpGet(TAGS.CENTRES_LIST_URL));
		}
		if (SharedPreferencesKey.getBooleanFromSharedPreferences(
				TAGS.SYNC_CLASSES, true, this)) {
			SyncHandler syncHandler = new SyncHandler(TAGS.SYNC_CLASSES);
			syncHandler.execute(new HttpGet(TAGS.CLASS_LIST_URL));
		}
		if (SharedPreferencesKey.getBooleanFromSharedPreferences(
				TAGS.SYNC_SUBJECTS, true, this)) {
			SyncHandler syncHandler = new SyncHandler(TAGS.SYNC_SUBJECTS);
			syncHandler.execute(new HttpGet(TAGS.SUBJECTS_LIST_URL));
		}
		if (SharedPreferencesKey.getBooleanFromSharedPreferences(
				TAGS.SYNC_EXAMS, true, this)) {
			SyncHandler syncHandler = new SyncHandler(TAGS.SYNC_EXAMS);
			syncHandler.execute(new HttpGet(TAGS.EXAMS_LIST_URL));
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.logout_action:

			new AlertDialog.Builder(this)
					.setTitle("Logging Out")
					.setMessage("Press OK to Logout!!")
					.setCancelable(true)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent;
									try {
										intent = new Intent(
												getApplicationContext(),
												Class.forName("org.sankalpnitjamshedpur.LoginActivity"));
										SharedPreferencesKey
												.putInSharedPreferences(
														TAGS.KEY_IS_LOGGED_IN,
														false,
														getApplicationContext());
										startActivity(intent);
										finish();
									} catch (ClassNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}).create().show();
			return true;

		case R.id.help:

			AlertDialog.Builder aB = new AlertDialog.Builder(this);

			aB.setTitle("Direction to use:");
			aB.setView(getDirectionsView());
			aB.setCancelable(true);

			aB.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			}).create().show();

			return true;

		case R.id.sync:
			if (!NetworkStatusChangeReceiver.isConnected(context)) {
				Toast.makeText(context,
						"No Internet Connectivity. Please Check.",
						Toast.LENGTH_SHORT).show();
				return false;
			} else {
				Toast.makeText(context,
						"Syncing now!!!",
						Toast.LENGTH_SHORT).show();
			}
			SyncHandler syncCentreHandler = new SyncHandler(TAGS.SYNC_CENTRES);
			syncCentreHandler.execute(new HttpGet(TAGS.CENTRES_LIST_URL));

			SyncHandler syncClassHandler = new SyncHandler(TAGS.SYNC_CLASSES);
			syncClassHandler.execute(new HttpGet(TAGS.CLASS_LIST_URL));

			SyncHandler syncSubjectsHandler = new SyncHandler(
					TAGS.SYNC_SUBJECTS);
			syncSubjectsHandler.execute(new HttpGet(TAGS.SUBJECTS_LIST_URL));
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	ScrollView getDirectionsView() {
		ScrollView sV = new ScrollView(this);
		TextView tv = new TextView(this);
		tv.setPadding(20, 10, 10, 20);
		tv.setTextSize(15);
		tv.setText(R.string.directions);

		sV.addView(tv);
		return sV;
	}

	private class SyncHandler extends
			AsyncTask<HttpUriRequest, String, JSONObject> {
		private String type;

		public SyncHandler(String string) {
			super();
			type = string;
		}

		@Override
		protected JSONObject doInBackground(HttpUriRequest... httprequests) {
			if (!NetworkStatusChangeReceiver.isConnected(context)) {
				return null;
			}
			HttpUriRequest httpRequest = httprequests[0];
			HttpClient client = new DefaultHttpClient();
			client.getParams()
					.setParameter(
							CoreProtocolPNames.USER_AGENT,
							"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			httpRequest.setHeader(HTTP.CONTENT_TYPE,
					"application/x-www-form-urlencoded;charset=UTF-8");
			HttpResponse response = null;
			try {
				response = client.execute(httpRequest);
			} catch (ClientProtocolException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			if (response != null && response.getEntity() != null) {
				// Mark the report sent in the db by record
				if (response.getStatusLine().getStatusCode() == 200) {
					StringBuffer result = new StringBuffer();
					BufferedReader rd;
					try {
						rd = new BufferedReader(new InputStreamReader(response
								.getEntity().getContent()));
						String line = "";
						while ((line = rd.readLine()) != null) {
							result.append(line);
						}
						JSONObject mainJsonObj = new JSONObject(
								result.toString());
						return mainJsonObj;
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		protected void onPostExecute(JSONObject mainJsonObj) {
			try {
				if (mainJsonObj != null
						&& mainJsonObj.getInt(TAGS.KEY_SUCCESS) == 1) {
					if (type.equals(TAGS.SYNC_CENTRES)) {
						JSONArray centres;

						centres = mainJsonObj.getJSONObject(TAGS.KEY_DETAILS)
								.getJSONArray(TAGS.KEY_CENTRES);

						dbHandler.deleteCentresTable();
						
						for (int i = 0; i < centres.length(); i++) {
							JSONObject centre = centres.getJSONObject(i);
							dbHandler.addCentre(new Centre(centre
									.getInt(TAGS.KEY_CENTRE_ID), centre
									.getString(TAGS.KEY_CENTRE_NAME)));
						}
						SharedPreferencesKey.putInSharedPreferences(
								TAGS.SYNC_CENTRES, true, context);
						Toast.makeText(getApplicationContext(),
								"Centres synched", Toast.LENGTH_SHORT).show();
					}

					if (type.equals(TAGS.SYNC_CLASSES)) {
						JSONArray classes;
						classes = mainJsonObj.getJSONObject(TAGS.KEY_DETAILS)
								.getJSONArray(TAGS.KEY_CLASSES);

						dbHandler.deleteClassesTable();
						
						for (int i = 0; i < classes.length(); i++) {
							JSONObject studentClass = classes.getJSONObject(i);
							dbHandler.addClass(new StudentClass(studentClass
									.getInt(TAGS.KEY_CLASS_ID), studentClass
									.getString(TAGS.KEY_CLASS_NAME)));
						}
						SharedPreferencesKey.putInSharedPreferences(
								TAGS.SYNC_CLASSES, true, context);
						Toast.makeText(getApplicationContext(),
								"Classes synched", Toast.LENGTH_SHORT).show();
					}

					if (type.equals(TAGS.SYNC_SUBJECTS)) {
						JSONArray subjects;

						subjects = mainJsonObj.getJSONObject(TAGS.KEY_DETAILS)
								.getJSONArray(TAGS.KEY_SUBJECTS);

						dbHandler.deleteSubjectsTable();
						
						for (int i = 0; i < subjects.length(); i++) {
							JSONObject subject = subjects.getJSONObject(i);
							dbHandler.addSubject(new Subject(subject
									.getInt(TAGS.KEY_SUBJECT_ID), subject
									.getString(TAGS.KEY_SUBJECT_NAME), subject
									.getInt(TAGS.KEY_CLASS_ID)));
						}
						SharedPreferencesKey.putInSharedPreferences(
								TAGS.SYNC_SUBJECTS, true, context);
						Toast.makeText(getApplicationContext(),
								"Subjects synched", Toast.LENGTH_SHORT).show();
					}
					
					if (type.equals(TAGS.SYNC_EXAMS)) {
						JSONArray exams;

						exams = mainJsonObj.getJSONObject(TAGS.KEY_DETAILS)
								.getJSONArray(TAGS.KEY_EXAMS);
						
						dbHandler.deleteExamsTable();
						
						for (int i = 0; i < exams.length(); i++) {
							JSONObject exam = exams.getJSONObject(i);							
							dbHandler.addExam(new Exam(
									exam.getInt(TAGS.KEY_EXAM_ID), exam
									.getString(TAGS.KEY_EXAM_DATE),
							exam.getString(TAGS.KEY_EXAM_TYPE)));
						}
						SharedPreferencesKey.putInSharedPreferences(
								TAGS.SYNC_EXAMS, true, context);
						Toast.makeText(getApplicationContext(),
								"Exams synched", Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
