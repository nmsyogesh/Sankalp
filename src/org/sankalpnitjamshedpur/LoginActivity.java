package org.sankalpnitjamshedpur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.db.HttpRequestHandler;
import org.sankalpnitjamshedpur.db.RegistrationStage;
import org.sankalpnitjamshedpur.db.RemoteDatabaseConfiguration;
import org.sankalpnitjamshedpur.entity.User;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener,
		UserAuthenticationActivity {
	Button loginButton;
	EditText inputParam, password;
	DatabaseHandler dbHandler;
	Spinner loginOptionSpinner;
	String option;
	String passwordText;
	User loggedInUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);
		dbHandler = new DatabaseHandler(this);
		inputParam = (EditText) findViewById(R.id.inputParam);
		password = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.loginButton);
		loginOptionSpinner = (Spinner) findViewById(R.id.login_option);
		loginButton.setOnClickListener(this);

		ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
				.createFromResource(this, R.array.login_options,
						android.R.layout.simple_spinner_item);
		staticAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		loginOptionSpinner.setAdapter(staticAdapter);

		loginOptionSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						option = (String) parent.getItemAtPosition(position);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub
					}
				});
	}

	@Override
	public void onClick(View v) {
		if (v == loginButton) {
			String inputString = inputParam.getText().toString();
			passwordText = password.getText().toString();
			HttpRequestHandler requestHandler = new HttpRequestHandler(this,
					RegistrationStage.LOGIN);
			if (option.equalsIgnoreCase("Volunteer Id")) {
				requestHandler.execute(getHttpRegistrationGetRequest(
						inputString,
						RemoteDatabaseConfiguration.KEY_VOLUNTEERID));
			} else if (option.equalsIgnoreCase("Email Id")) {
				requestHandler.execute(getHttpRegistrationGetRequest(
						inputString, RemoteDatabaseConfiguration.KEY_EMAIL_ID));
			} else if (option.equalsIgnoreCase("Mobile No")) {
				requestHandler
						.execute(getHttpRegistrationGetRequest(inputString,
								RemoteDatabaseConfiguration.KEY_MOBILE_NO));
			}

			Toast.makeText(getApplicationContext(), "Login Request created",
					Toast.LENGTH_SHORT).show();
		}
	}

	private HttpUriRequest getHttpRegistrationGetRequest(String value,
			String fieldId) {

		HttpGet getRequest = new HttpGet(RemoteDatabaseConfiguration.URL
				+ "&where=" + fieldId + ",eq," + value);
		getRequest.setHeader("User-Agent",
				RemoteDatabaseConfiguration.USER_AGENT);
		getRequest.setHeader("Authorization",
				RemoteDatabaseConfiguration.getApiKey());

		return getRequest;
	}

	@Override
	public void onRequestResult(HttpResponse httpResponse,
			RegistrationStage registrationStage) {
		if (RegistrationStage.LOGIN == registrationStage) {
			StringBuffer result = new StringBuffer();
			BufferedReader rd;
			try {
				rd = new BufferedReader(new InputStreamReader(httpResponse
						.getEntity().getContent()));

				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}

				JSONObject mainJsonObj = new JSONObject(result.toString());
				if (mainJsonObj.length() == 0) {
					if (option.equalsIgnoreCase("Volunteer Id")) {
						Toast.makeText(getApplicationContext(),
								"No user Exists with this volunteer Id",
								Toast.LENGTH_SHORT).show();
					} else if (option.equalsIgnoreCase("Email Id")) {
						Toast.makeText(getApplicationContext(),
								"No user Exists with this Email",
								Toast.LENGTH_SHORT).show();
					} else if (option.equalsIgnoreCase("Mobile No")) {
						Toast.makeText(getApplicationContext(),
								"No user Exists with this Mobile No",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					loggedInUser = checkPasswordAndCreateUser(mainJsonObj);
					if (loggedInUser != null) {
						startHomePageActivityWithUser();
					} else {
						Toast.makeText(getApplicationContext(),
								"Wrong password!! try again", Toast.LENGTH_LONG)
								.show();
					}
				}
			} catch (IllegalStateException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}

	private User checkPasswordAndCreateUser(JSONObject mainJsonObj)
			throws JSONException {
		Iterator<String> keysIterator = mainJsonObj.keys();
		if (keysIterator.hasNext()) {
			JSONObject dataObject = mainJsonObj.getJSONObject(keysIterator
					.next());
			if (!passwordText.equals(dataObject.getString("Password"))) {
				return null;
			}

			// TODO branch value is not coming.
			return new User(dataObject.getString("Name"),
					Integer.parseInt(dataObject.getString("RollNo")),
					dataObject.getString("EmailId"),
					Integer.parseInt(dataObject.getString("Batch")),
					"branch not found", dataObject.getString("Password"),
					Long.parseLong(dataObject.getString("MobileNo")));
		}
		return null;
	}

	private void startHomePageActivityWithUser() {
		try {
			Intent homePageActivityIntent = new Intent(this,
					Class.forName("org.sankalpnitjamshedpur.HomePage"));
			// Pass this registered user in sharedPrefernces for further usage
			setPrefernces();
			startActivity(homePageActivityIntent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	void setPrefernces() {
		SharedPreferences settings;
		Editor editor;
		settings = getApplicationContext().getSharedPreferences(
				SharedPreferencesKey.PREFS_NAME, Context.MODE_PRIVATE); 
		editor = settings.edit();

		editor.putString(SharedPreferencesKey.KEY_NAME, loggedInUser.getName()); 
		editor.putString(SharedPreferencesKey.KEY_BATCH, loggedInUser.getName());
		editor.putString(SharedPreferencesKey.KEY_BRANCH, loggedInUser.getName());
		editor.putString(SharedPreferencesKey.KEY_EMAIL_ID, loggedInUser.getName());
		editor.putString(SharedPreferencesKey.KEY_ROLLNO, loggedInUser.getName());
		editor.putString(SharedPreferencesKey.KEY_MOBILE_NO, loggedInUser.getName());
		editor.putString(SharedPreferencesKey.KEY_VOLUNTEERID, loggedInUser.getVolunteerId());
		
		editor.commit(); 
	}
}
