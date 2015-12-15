package org.sankalpnitjamshedpur;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.sankalpnitjamshedpur.db.HttpRequestHandler;
import org.sankalpnitjamshedpur.db.RegistrationStage;
import org.sankalpnitjamshedpur.entity.User;
import org.sankalpnitjamshedpur.helper.NetworkStatusChangeReceiver;
import org.sankalpnitjamshedpur.helper.SharedPreferencesKey;
import org.sankalpnitjamshedpur.helper.TAGS;
import org.sankalpnitjamshedpur.helper.ValidationException;
import org.sankalpnitjamshedpur.helper.Validator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener,
		OnTouchListener, UserAuthenticationActivity {
	Button loginButton;
	EditText inputText, passwordText;
	String loginType;
	String passwordValue;
	User loggedInUser;

	boolean error = false;
	ProgressDialog progressDialog;
	TextView errorView;

	Toast toast;
	private HttpRequestHandler requestHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);

		if (SharedPreferencesKey.getBooleanFromSharedPreferences(
				TAGS.KEY_IS_LOGGED_IN, false, getApplicationContext())) {
			startHomePageActivityWithUser();
			return;
		}

		String registeredType = getIntent().getStringExtra(
				TAGS.KEY_REGISTERATION_TYPE);

		errorView = (TextView) findViewById(R.id.errorView);
		errorView.setVisibility(View.VISIBLE);
		inputText = (EditText) findViewById(R.id.inputParam);
		passwordText = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(this);
		inputText.setOnTouchListener(this);
		passwordText.setOnTouchListener(this);
		passwordText.setText("");

		if (registeredType != null) {
			loginType = registeredType;
			String text = getIntent().getStringExtra(registeredType);
			inputText.setText(text);
		}
	}

	public void setError(String errorText) {
		errorView.setError("");
		errorView.setVisibility(View.VISIBLE);
		errorView.setText(errorView.getText() + "\n\n" + errorText);
	}

	private void removeError() {
		error = false;
		errorView.setVisibility(View.GONE);
		errorView.setText(null);
		errorView.setError(null);
	}

	private void validateInputParam() {
		try {
			Validator.validateEmail(inputText.getText().toString());
			loginType = TAGS.KEY_EMAIL_ID;
		} catch (ValidationException e) {
			try {
				Validator.validateVolunteerId(inputText.getText().toString());
				loginType = TAGS.KEY_VOLUNTEER_ID;
			} catch (ValidationException ex) {
				try {
					Validator.validateMobileNo(inputText.getText().toString());
					loginType = TAGS.KEY_MOBILE_NO;
				} catch (ValidationException exc) {
					error = true;
					setError("Invalid Email/Mobile/VolunteerId");
				}
			}
		}
	}

	private void validatePassword() {
		try {
			Validator.validatePassword(passwordText.getText().toString());
		} catch (ValidationException e) {
			error = true;
			setError(e.getMessage());
		}
	}

	@Override
	public void onClick(View v) {
		removeError();

		if (v == loginButton) {
			validateInputParam();
			validatePassword();
			if (error) {
				toast = Toast.makeText(getApplicationContext(),
						"Please Correct Login Details", Toast.LENGTH_SHORT);
				toast.show();
				return;
			}
			if (!NetworkStatusChangeReceiver.isConnected(this)) {
				setError("No internet connectivity.");
				return;
			}
			String inputString = inputText.getText().toString();
			passwordValue = passwordText.getText().toString();

			requestHandler = new HttpRequestHandler(this,
					RegistrationStage.LOGIN);
			requestHandler.execute(getHttpRegistrationPostRequest(
					inputString.toUpperCase(), loginType));
			progressDialog = ProgressDialog.show(this, "Please Wait",
					"We are logging you in!!");
			progressDialog.setCancelable(true);
		} 
	}

	private HttpUriRequest getHttpRegistrationPostRequest(String value,
			String loginType) {

		HttpPost postRequest = new HttpPost(TAGS.VOLUNTEERS_LOGIN_URL);
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(loginType, value));
		urlParameters.add(new BasicNameValuePair(TAGS.KEY_PASSWORD, TAGS
				.generateHash(passwordValue)));

		try {
			postRequest.setEntity(new UrlEncodedFormEntity(urlParameters,
					"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return postRequest;
	}

	@Override
	public void onRequestResult(StringBuffer result,
			RegistrationStage registrationStage) {

		if (progressDialog != null)
			progressDialog.dismiss();
		if (result == null) {
			setError("Error while login, Try again!!");
			return;
		}
		JSONObject mainJsonObj, details;
		try {
			mainJsonObj = new JSONObject(result.toString());

			if (mainJsonObj.getInt(TAGS.KEY_SUCCESS) == 1) {
				details = mainJsonObj.getJSONObject(TAGS.KEY_DETAILS);
				// login successfully, save user details in preferences
				loggedInUser = createUser(details);
				Log.i("LOGIN",
						"login successful for " + loggedInUser.getEmailId());
				setPrefernces();
				startHomePageActivityWithUser();
			} else if (mainJsonObj.getInt(TAGS.KEY_SUCCESS) == 0) {
				// some error occurred
				String message = mainJsonObj.getString(TAGS.KEY_MESSAGE);

				if (message.equals("NO_USER")) {
					details = mainJsonObj.getJSONObject(TAGS.KEY_DETAILS);
					// prompt the user to login with returned input parameter
					final String inputType = details.getJSONArray(
							TAGS.KEY_PARAMS).getString(0);
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							this);

					alertDialog.setTitle("ALERT!!!");
					alertDialog
							.setMessage(inputType.toUpperCase()
									+ ": "
									+ inputText.getText().toString()
									+ " does not exists, Do you want to register yourself?");

					alertDialog.setPositiveButton("Yes, Create me",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startRegistrationActivity(inputText
											.getText().toString(), inputType);
								}
							});

					alertDialog.setNegativeButton("No Thanks",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});

					alertDialog.show();
				} else if (message.equals("INVALID_LOGIN")) {
					toast = Toast.makeText(getApplicationContext(),
							"Wrong password!! try again", Toast.LENGTH_LONG);
					toast.show();
					setError("Wrong password!!");
				} else if (message.equals("INTERNAL_ERROR")) {
					// prompt the user to wait
					Toast.makeText(
							getApplicationContext(),
							"Some internal error occured, please try again later",
							Toast.LENGTH_LONG).show();
				}
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}

	private void startRegistrationActivity(String inputValue, String inputType) {
		try {
			Intent registartionPageActivityIntent = new Intent(
					this,
					Class.forName("org.sankalpnitjamshedpur.RegistrationActivity"));
			if (inputType != null) {
				registartionPageActivityIntent.putExtra(
						TAGS.KEY_REGISTERATION_TYPE, inputType);
				registartionPageActivityIntent.putExtra(inputType, inputValue);
			}
			startActivity(registartionPageActivityIntent);
			finish();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private User createUser(JSONObject details) throws JSONException {

		return new User(details.getString(TAGS.KEY_NAME),
				Integer.parseInt(details.getString(TAGS.KEY_ROLLNO)),
				details.getString(TAGS.KEY_EMAIL_ID), Integer.parseInt(details
						.getString(TAGS.KEY_BATCH)),
				details.getString(TAGS.KEY_BRANCH), Long.parseLong(details
						.getString(TAGS.KEY_MOBILE_NO)),
				details.getString(TAGS.KEY_VOLUNTEER_ID),
				details.getString(TAGS.KEY_SECURITY_TOKEN));
	}

	private void startHomePageActivityWithUser() {
		try {
			Intent homePageActivityIntent = new Intent(this,
					Class.forName("org.sankalpnitjamshedpur.HomePage"));
			// Pass this registered user in sharedPrefernces for further usage
			startActivity(homePageActivityIntent);
			finish();
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

		editor.putString(TAGS.KEY_NAME, loggedInUser.getName());
		editor.putString(TAGS.KEY_BATCH,
				String.valueOf(loggedInUser.getBatch()));
		editor.putString(TAGS.KEY_BRANCH, loggedInUser.getBranch());
		editor.putString(TAGS.KEY_EMAIL_ID, loggedInUser.getEmailId());
		editor.putString(TAGS.KEY_ROLLNO,
				String.valueOf(loggedInUser.getRollNo()));
		editor.putString(TAGS.KEY_MOBILE_NO,
				String.valueOf(loggedInUser.getMobileNo()));
		editor.putString(TAGS.KEY_VOLUNTEER_ID, loggedInUser.getVolunteerId());
		editor.putString(TAGS.KEY_SECURITY_TOKEN,
				loggedInUser.getSecurityToken());

		editor.putBoolean(TAGS.KEY_IS_LOGGED_IN, true);

		editor.commit();
	}

	public Context getApplicationContext() {
		return this;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		removeError();
		return false;
	}
}
