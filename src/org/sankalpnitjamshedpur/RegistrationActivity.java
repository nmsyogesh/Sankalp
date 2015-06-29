package org.sankalpnitjamshedpur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.sankalpnitjamshedpur.constants.LoginConstants;
import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.db.HttpRequestHandler;
import org.sankalpnitjamshedpur.db.RegistrationStage;
import org.sankalpnitjamshedpur.db.RemoteDatabaseConfiguration;
import org.sankalpnitjamshedpur.entity.User;
import org.sankalpnitjamshedpur.helper.SharedPreferencesKey;
import org.sankalpnitjamshedpur.helper.ValidationException;
import org.sankalpnitjamshedpur.helper.Validator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

public class RegistrationActivity extends Activity implements OnClickListener,
		UserAuthenticationActivity {
	Button registerButton;
	EditText nameBox, rollNoBox, emailBox, passwordBox, mobileNoBox;
	Spinner batchSpinner, branchSpinner;
	String batch = "2012", branch = "EC";
	DatabaseHandler dbHandler;
	User registeredUser;
	ProgressDialog progressDialog;
	boolean error = false;

	User userToBeRegistered;

	boolean emailExists = true, mobileNoExists = true,
			volunteerIdExists = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_page);
		dbHandler = new DatabaseHandler(this);
		registerButton = (Button) findViewById(R.id.registerButton);
		nameBox = (EditText) findViewById(R.id.name);
		rollNoBox = (EditText) findViewById(R.id.rollNo);
		emailBox = (EditText) findViewById(R.id.email);
		batchSpinner = (Spinner) findViewById(R.id.batch_spinner);
		branchSpinner = (Spinner) findViewById(R.id.branch_spinner);
		passwordBox = (EditText) findViewById(R.id.password);
		mobileNoBox = (EditText) findViewById(R.id.mobileNo);
		registerButton.setOnClickListener(this);

		initiateListeners();

	}

	private void initiateListeners() {

		ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
				.createFromResource(this, R.array.branch_array,
						android.R.layout.simple_spinner_item);
		staticAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		branchSpinner.setAdapter(staticAdapter);

		ArrayAdapter<CharSequence> staticAdapter2 = ArrayAdapter
				.createFromResource(this, R.array.batch_array,
						android.R.layout.simple_spinner_item);
		staticAdapter2
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		batchSpinner.setAdapter(staticAdapter2);

		batchSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				batch = (String) parent.getItemAtPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		branchSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				branch = (String) parent.getItemAtPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		nameBox.addTextChangedListener(new CustomTextWatcher(nameBox));
		rollNoBox.addTextChangedListener(new CustomTextWatcher(rollNoBox));
		passwordBox.addTextChangedListener(new CustomTextWatcher(passwordBox));
		mobileNoBox.addTextChangedListener(new CustomTextWatcher(mobileNoBox));
		emailBox.addTextChangedListener(new CustomTextWatcher(emailBox));
	}

	@Override
	public void onClick(View v) {
		if (v == registerButton) {
			checkfields();
			if (error) {
				Toast.makeText(getApplicationContext(),
						"Please correct your registration details",
						Toast.LENGTH_SHORT).show();
				return;
			}
			emailExists = true;
			mobileNoExists = true;
			volunteerIdExists = true;

			userToBeRegistered = captureUser();
			HttpRequestHandler requestHandler = new HttpRequestHandler(this,
					RegistrationStage.EMAIL);
			requestHandler.execute(getHttpRegistrationGetRequest(
					userToBeRegistered.getEmailId(),
					RemoteDatabaseConfiguration.KEY_EMAIL_ID));

			progressDialog = ProgressDialog.show(this, "Please Wait",
					"We are registering you!!");
		}
	}

	private void checkfields() {
		// Setting text boxes with their values so that their onTextChanged
		// Method is triggered
		nameBox.setText(nameBox.getText().toString());
		rollNoBox.setText(rollNoBox.getText().toString());
		emailBox.setText(emailBox.getText().toString());
		passwordBox.setText(passwordBox.getText().toString());
		mobileNoBox.setText(mobileNoBox.getText().toString());
	}

	private User captureUser() {
		return new User(nameBox.getText().toString(),
				Integer.parseInt(rollNoBox.getText().toString()), emailBox
						.getText().toString().toLowerCase(),
				Integer.parseInt(batch), branch, passwordBox.getText()
						.toString(), Long.parseLong(mobileNoBox.getText()
						.toString()));
	}

	private HttpUriRequest getHttpRegistrationPostRequest(
			User userToBeRegistered) {

		HttpPost postRequest = new HttpPost(RemoteDatabaseConfiguration.URL);
		postRequest.setHeader("User-Agent",
				RemoteDatabaseConfiguration.USER_AGENT);

		try {
			postRequest.setEntity(new UrlEncodedFormEntity(
					getUrlParameters(userToBeRegistered)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		postRequest.setHeader("Authorization",
				RemoteDatabaseConfiguration.getApiKey());

		return postRequest;
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

	List<NameValuePair> getUrlParameters(User userToBeRegistered) {
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_NAME, userToBeRegistered
						.getName()));
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_BRANCH, userToBeRegistered
						.getBranch()));
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_ROLLNO, String
						.valueOf(userToBeRegistered.getRollNo())));
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_MOBILE_NO, String
						.valueOf(userToBeRegistered.getMobileNo())));
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_BATCH, String
						.valueOf(userToBeRegistered.getBatch())));
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_VOLUNTEERID, userToBeRegistered
						.getVolunteerId()));
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_EMAIL_ID, userToBeRegistered
						.getEmailId()));
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_PASSWORD, userToBeRegistered
						.getPassword()));
		return urlParameters;
	}

	public void onRequestResult(HttpResponse httpResponse,
			RegistrationStage registrationStage) {
		if (httpResponse == null) {
			progressDialog.dismiss();
			Toast.makeText(getApplicationContext(),
					"Registration failed. Please check Internet connectivity.",
					Toast.LENGTH_LONG).show();
			return;
		}
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
			if (registrationStage == RegistrationStage.UNREGISTERED) {

				if (mainJsonObj.get("status").equals("SUCCESS")) {
					registerUser(mainJsonObj);
					progressDialog.dismiss();
					Toast.makeText(getApplicationContext(),
							"Yippppiieeee!!!!!", Toast.LENGTH_LONG).show();
					startHomePageActivityWithUser();
				}
			} else if (registrationStage == RegistrationStage.EMAIL) {
				if (mainJsonObj.length() == 0) {
					emailExists = false;
					if (!checkUserRegistrationFinalStatusAndRegister()) {
						HttpRequestHandler requestHandler = new HttpRequestHandler(
								this, RegistrationStage.MOBILENO);
						requestHandler
								.execute(getHttpRegistrationGetRequest(
										String.valueOf(userToBeRegistered
												.getMobileNo()),
										RemoteDatabaseConfiguration.KEY_MOBILE_NO));
					}
				} else {
					progressDialog.dismiss();
					startLoginActivity(userToBeRegistered.getEmailId(),
							LoginConstants.KEY_EMAILID);
					Toast.makeText(getApplicationContext(),
							"Email Exists. Login with that", Toast.LENGTH_SHORT)
							.show();
					// Fire Login Activity with email Id
				}
			} else if (registrationStage == RegistrationStage.MOBILENO) {

				if (mainJsonObj.length() == 0) {
					mobileNoExists = false;
					if (!checkUserRegistrationFinalStatusAndRegister()) {
						HttpRequestHandler requestHandler = new HttpRequestHandler(
								this, RegistrationStage.VOLUNTEERID);
						requestHandler.execute(getHttpRegistrationGetRequest(
								userToBeRegistered.getVolunteerId(),
								RemoteDatabaseConfiguration.KEY_VOLUNTEERID));
					}
				} else {
					progressDialog.dismiss();
					startLoginActivity(
							String.valueOf(userToBeRegistered.getMobileNo()),
							LoginConstants.KEY_MOBILENO);
					Toast.makeText(getApplicationContext(),
							"Mobile number Exists", Toast.LENGTH_SHORT).show();
				}
			} else if (registrationStage == RegistrationStage.VOLUNTEERID) {
				if (mainJsonObj.length() == 0) {
					volunteerIdExists = false;
					checkUserRegistrationFinalStatusAndRegister();
				} else {
					startLoginActivity(userToBeRegistered.getVolunteerId(),
							LoginConstants.KEY_VOLUNTEERID);
					Toast.makeText(getApplicationContext(),
							"VolunteerId exists!! Please Login",
							Toast.LENGTH_SHORT).show();
					progressDialog.dismiss();
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

	boolean checkUserRegistrationFinalStatusAndRegister() {
		if (!emailExists && !mobileNoExists && !volunteerIdExists) {
			HttpRequestHandler requestHandler = new HttpRequestHandler(this,
					RegistrationStage.UNREGISTERED);
			requestHandler
					.execute(getHttpRegistrationPostRequest(userToBeRegistered));
			return true;
		}
		return false;
	}

	private void startHomePageActivityWithUser() {
		try {
			Intent homePageActivityIntent = new Intent(this,
					Class.forName("org.sankalpnitjamshedpur.HomePage"));
			// Pass this registered user in sharedPrefernces for further usage
			setPrefernces();
			startActivity(homePageActivityIntent);
			finish();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void startLoginActivity(String input, String inputType) {
		try {
			Intent loginPageActivityIntent = new Intent(this,
					Class.forName("org.sankalpnitjamshedpur.LoginActivity"));
			loginPageActivityIntent.putExtra(
					LoginConstants.KEY_REGISTERED_TYPE, inputType);
			loginPageActivityIntent.putExtra(inputType, input);
			startActivity(loginPageActivityIntent);
			finish();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void registerUser(JSONObject json) {
		try {
			JSONObject data = (JSONObject) json.get("data");
			registeredUser = new User(
					data.getString(RemoteDatabaseConfiguration.KEY_NAME),
					data.getInt(RemoteDatabaseConfiguration.KEY_ROLLNO),
					data.getString(RemoteDatabaseConfiguration.KEY_EMAIL_ID),
					data.getInt(RemoteDatabaseConfiguration.KEY_BATCH),
					data.getString(RemoteDatabaseConfiguration.KEY_BRANCH),
					data.getString(RemoteDatabaseConfiguration.KEY_PASSWORD),
					data.getLong(RemoteDatabaseConfiguration.KEY_MOBILE_NO));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	void setPrefernces() {
		SharedPreferencesKey.putInSharedPreferences(
				SharedPreferencesKey.KEY_NAME, registeredUser.getName(), this);
		SharedPreferencesKey.putInSharedPreferences(
				SharedPreferencesKey.KEY_BATCH,
				String.valueOf(registeredUser.getBatch()), this);
		SharedPreferencesKey.putInSharedPreferences(
				SharedPreferencesKey.KEY_BRANCH, registeredUser.getBranch(),
				this);
		SharedPreferencesKey.putInSharedPreferences(
				SharedPreferencesKey.KEY_EMAIL_ID, registeredUser.getEmailId(),
				this);
		SharedPreferencesKey.putInSharedPreferences(
				SharedPreferencesKey.KEY_ROLLNO,
				String.valueOf(registeredUser.getRollNo()), this);
		SharedPreferencesKey.putInSharedPreferences(
				SharedPreferencesKey.KEY_MOBILE_NO,
				String.valueOf(registeredUser.getMobileNo()), this);
		SharedPreferencesKey.putInSharedPreferences(
				SharedPreferencesKey.KEY_VOLUNTEERID,
				registeredUser.getVolunteerId(), this);
	}

	public Context getApplicationContext() {
		return this;
	}

	private class CustomTextWatcher implements TextWatcher {

		EditText editText;

		public CustomTextWatcher(EditText editText) {
			this.editText = editText;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			try {
				if (editText == rollNoBox) {
					Validator.validateRollNo(editText.getText().toString());
				} else if (editText == emailBox) {
					Validator.validateEmail(editText.getText().toString());
				} else if (editText == passwordBox) {
					Validator.validatePassword(editText.getText().toString());
				} else if (editText == mobileNoBox) {
					Validator
							.validateMobileNo(mobileNoBox.getText().toString());
				} else if (editText == nameBox) {
					if (nameBox.getText().toString().trim()
							.equalsIgnoreCase("")) {
						nameBox.setError("Name can not be blank");
						error = true;
						return;
					}
				}
				error = false;
			} catch (ValidationException e) {
				error = true;
				editText.setError(e.getMessage());
			}
		}
	}
}
