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
import org.sankalpnitjamshedpur.db.HttpRequestHandler;
import org.sankalpnitjamshedpur.db.RegistrationStage;
import org.sankalpnitjamshedpur.db.RemoteDatabaseConfiguration;
import org.sankalpnitjamshedpur.entity.User;
import org.sankalpnitjamshedpur.helper.SharedPreferencesKey;
import org.sankalpnitjamshedpur.helper.ValidationException;
import org.sankalpnitjamshedpur.helper.Validator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener,
		UserAuthenticationActivity {
	Button loginButton;
	EditText inputParam, password;
	Spinner loginOptionSpinner;
	String option = "";
	String passwordText;
	User loggedInUser;
	boolean error = false;
	TextView volunteerIdHelp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);
		inputParam = (EditText) findViewById(R.id.inputParam);
		password = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.loginButton);
		loginOptionSpinner = (Spinner) findViewById(R.id.login_option);
		volunteerIdHelp= (TextView) findViewById(R.id.volunteerIdHelp);
		loginButton.setOnClickListener(this);
		initiateErrorListeners();

		ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
				.createFromResource(this, R.array.login_options,
						android.R.layout.simple_spinner_item);
		staticAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		loginOptionSpinner.setAdapter(staticAdapter);
		password.setText("");

		loginOptionSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						option = (String) parent.getItemAtPosition(position);
						inputParam.setText("");
						if (option.equalsIgnoreCase("Volunteer Id")) {
							volunteerIdHelp.setVisibility(View.VISIBLE);
							inputParam.setHint("Enter your Volunteer Id");
						} else if (option.equalsIgnoreCase("Email Id")) {
							volunteerIdHelp.setVisibility(View.GONE);
							inputParam.setHint("Enter your Email Id");
						} else if (option.equalsIgnoreCase("Mobile No")) {
							volunteerIdHelp.setVisibility(View.GONE);
							inputParam.setHint("Enter your Mobile No");
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub
					}
				});
	}

	private void initiateErrorListeners() {

		inputParam.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				inputParam.setError(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				inputParam.setError(null);
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				inputParam.setError(null);

				try {
					if (option.equalsIgnoreCase("Email Id")) {
						Validator
								.validateEmail(inputParam.getText().toString());
					} else if (option.equalsIgnoreCase("Volunteer Id")) {
						Validator.validateVolunteerId(inputParam.getText()
								.toString());
					} else if (option.equalsIgnoreCase("Mobile No")) {
						Validator.validateMobileNo(inputParam.getText()
								.toString());
					}
					error = false;
				} catch (ValidationException e) {
					error = true;
					inputParam.setError(e.getMessage());
				}
			}

		});

		password.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				password.setError(null);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				password.setError(null);
			}

			@Override
			public void afterTextChanged(Editable s) {
				password.setError(null);
				try {
					Validator.validatePassword(password.getText().toString());
					error = false;
				} catch (ValidationException e) {
					error = true;
					password.setError(e.getMessage());
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v == loginButton) {
			checkfields();
			if (error) {
				Toast.makeText(getApplicationContext(),
						"Please Correct Login Details", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			String inputString = inputParam.getText().toString();
			passwordText = password.getText().toString();
			HttpRequestHandler requestHandler = new HttpRequestHandler(this,
					RegistrationStage.LOGIN);
			if (option.equalsIgnoreCase("Volunteer Id")) {
				requestHandler.execute(getHttpRegistrationGetRequest(
						inputString.toUpperCase(),
						RemoteDatabaseConfiguration.KEY_VOLUNTEERID));
			} else if (option.equalsIgnoreCase("Email Id")) {
				requestHandler.execute(getHttpRegistrationGetRequest(
						inputString.toLowerCase(), RemoteDatabaseConfiguration.KEY_EMAIL_ID));
			} else if (option.equalsIgnoreCase("Mobile No")) {
				requestHandler
						.execute(getHttpRegistrationGetRequest(inputString,
								RemoteDatabaseConfiguration.KEY_MOBILE_NO));
			}

			Toast.makeText(getApplicationContext(), "Login Request created",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	private void checkfields() {
		// Setting text boxes with their values so that their onTextChanged Method is triggered		
		inputParam.setText(inputParam.getText().toString());
		password.setText(password.getText().toString());
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
		if(httpResponse == null) {
			Toast.makeText(getApplicationContext(),
					"Login failed. Please check Internet connectivity.", Toast.LENGTH_LONG).show();	
			return;
		}
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
		editor.putString(SharedPreferencesKey.KEY_BATCH,
				String.valueOf(loggedInUser.getBatch()));
		editor.putString(SharedPreferencesKey.KEY_BRANCH,
				loggedInUser.getBranch());
		editor.putString(SharedPreferencesKey.KEY_EMAIL_ID,
				loggedInUser.getEmailId());
		editor.putString(SharedPreferencesKey.KEY_ROLLNO,
				String.valueOf(loggedInUser.getRollNo()));
		editor.putString(SharedPreferencesKey.KEY_MOBILE_NO,
				String.valueOf(loggedInUser.getMobileNo()));
		editor.putString(SharedPreferencesKey.KEY_VOLUNTEERID,
				loggedInUser.getVolunteerId());

		editor.commit();
	}
	
	public Context getApplicationContext(){
		return this;
	}
}
