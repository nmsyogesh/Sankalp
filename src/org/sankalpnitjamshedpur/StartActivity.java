package org.sankalpnitjamshedpur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.entity.Centre;
import org.sankalpnitjamshedpur.entity.ClassRecord;
import org.sankalpnitjamshedpur.entity.StudentClass;
import org.sankalpnitjamshedpur.helper.SharedPreferencesKey;
import org.sankalpnitjamshedpur.helper.TAGS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StartActivity extends Activity implements OnClickListener {
	Button loginButton;
	Button registrationButton;
	DatabaseHandler dbHandler;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_activity);
		context = this;
		dbHandler = new DatabaseHandler(this);
		loginButton = (Button) findViewById(R.id.loginActivity);
		registrationButton = (Button) findViewById(R.id.registerActivity);
		loginButton.setOnClickListener(this);
		registrationButton.setOnClickListener(this);
		if (SharedPreferencesKey.getBooleanFromSharedPreferences(
				TAGS.KEY_FIRST_INSTALL, true, this)) {
			HttpRequestHandler httpRequestHandler = new HttpRequestHandler(
					"centres");
			httpRequestHandler.execute(new HttpGet(TAGS.CENTRES_LIST_URL));
			httpRequestHandler = new HttpRequestHandler("classes");
			httpRequestHandler.execute(new HttpGet(TAGS.CLASS_LIST_URL));
		}
	}

	@Override
	public void onClick(View v) {
		if (v == loginButton) {
			try {
				startActivity(new Intent(this,
						Class.forName("org.sankalpnitjamshedpur.LoginActivity")));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		} else if (v == registrationButton) {
			try {
				startActivity(new Intent(
						this,
						Class.forName("org.sankalpnitjamshedpur.RegistrationActivity")));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private class HttpRequestHandler extends
			AsyncTask<HttpUriRequest, String, JSONObject> {
		String type;

		public HttpRequestHandler(String string) {
			super();
			type = string;
		}

		@Override
		protected JSONObject doInBackground(HttpUriRequest... httprequests) {
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
				if (mainJsonObj.getInt(TAGS.KEY_SUCCESS) == 1) {
					if (type.equals("centres")) {
						JSONArray centres;

						centres = mainJsonObj.getJSONObject(TAGS.KEY_DETAILS)
								.getJSONArray(TAGS.KEY_CENTRES);

						for (int i = 0; i < centres.length(); i++) {
							JSONObject centre = centres.getJSONObject(i);
							dbHandler.addCentre(new Centre(centre
									.getInt(TAGS.KEY_CENTRE_ID), centre
									.getString(TAGS.KEY_CENTRE_NAME)));
						}
					}
					
					if (type.equals("classes")) {
						JSONArray classes;

						classes = mainJsonObj.getJSONObject(TAGS.KEY_DETAILS)
								.getJSONArray(TAGS.KEY_CLASSES);

						for (int i = 0; i < classes.length(); i++) {
							JSONObject studentClass = classes.getJSONObject(i);
							dbHandler.addClass(new StudentClass(studentClass
									.getInt(TAGS.KEY_CLASS_ID), studentClass
									.getString(TAGS.KEY_CLASS_NAME)));
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			SharedPreferencesKey.putInSharedPreferences(TAGS.KEY_FIRST_INSTALL,
					false, context);
		}
	}

}
