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
import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.db.HttpRequestHandler;
import org.sankalpnitjamshedpur.db.RegistrationStage;
import org.sankalpnitjamshedpur.entity.User;
import org.sankalpnitjamshedpur.helper.TAGS;
import org.sankalpnitjamshedpur.helper.ValidationException;
import org.sankalpnitjamshedpur.helper.Validator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
	ArrayAdapter<CharSequence> branchAdapter, batchAdapter;
	User userToBeRegistered;

	boolean emailExists = true, mobileNoExists = true,
			volunteerIdExists = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_page);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

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

		String inputType = getIntent().getStringExtra(
				TAGS.KEY_REGISTERATION_TYPE);

		initiateListeners();

		// if user has been redirected from login page for registration
		if (inputType != null) {
			String inputValue = getIntent().getStringExtra(inputType);
			if (inputType.equalsIgnoreCase(TAGS.KEY_EMAIL_ID)) {
				emailBox.setText(inputValue);
			} else if (inputType.equalsIgnoreCase(TAGS.KEY_MOBILE_NO)) {
				mobileNoBox.setText(inputValue);
			} else if (inputType.equalsIgnoreCase(TAGS.KEY_VOLUNTEER_ID)) {
				batch = inputValue.substring(0, 4);
				branch = inputValue.substring(4);
				branch = branch.substring(0, branch.length() - 3);
				int branchPosition = branchAdapter.getPosition(branch);
				branchSpinner.setSelection(branchPosition);
				int batchPosition = batchAdapter.getPosition(batch);
				batchSpinner.setSelection(batchPosition);
				rollNoBox
						.setText(inputValue.substring(inputValue.length() - 3));
			}
		}
	}

	private void initiateListeners() {

		branchAdapter = ArrayAdapter.createFromResource(this,
				R.array.branch_array, android.R.layout.simple_spinner_item);
		branchAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		branchSpinner.setAdapter(branchAdapter);

		batchAdapter = ArrayAdapter.createFromResource(this,
				R.array.batch_array, android.R.layout.simple_spinner_item);
		batchAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		batchSpinner.setAdapter(batchAdapter);

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

			userToBeRegistered = captureUser();
			HttpRequestHandler requestHandler = new HttpRequestHandler(this,
					null);
			requestHandler
					.execute(getHttpRegistrationPostRequest(userToBeRegistered));

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
						.getText().toString(), Integer.parseInt(batch), branch,
				Long.parseLong(mobileNoBox.getText().toString()), null, null);
	}

	private HttpUriRequest getHttpRegistrationPostRequest(
			User userToBeRegistered) {

		HttpPost postRequest = new HttpPost(TAGS.VOLUNTEERS_REGISTRATION_URL);

		try {
			postRequest.setEntity(new UrlEncodedFormEntity(
					getUrlParameters(userToBeRegistered), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return postRequest;
	}

	List<NameValuePair> getUrlParameters(User userToBeRegistered) {
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(TAGS.KEY_NAME,
				userToBeRegistered.getName()));
		urlParameters.add(new BasicNameValuePair(TAGS.KEY_BRANCH,
				userToBeRegistered.getBranch()));
		urlParameters.add(new BasicNameValuePair(TAGS.KEY_ROLLNO, String
				.format("%03d", userToBeRegistered.getRollNo())));
		urlParameters.add(new BasicNameValuePair(TAGS.KEY_MOBILE_NO, String
				.valueOf(userToBeRegistered.getMobileNo())));
		urlParameters.add(new BasicNameValuePair(TAGS.KEY_BATCH, String
				.valueOf(userToBeRegistered.getBatch())));
		urlParameters.add(new BasicNameValuePair(TAGS.KEY_EMAIL_ID,
				userToBeRegistered.getEmailId()));
		urlParameters.add(new BasicNameValuePair(TAGS.KEY_PASSWORD, TAGS
				.generateHash(passwordBox.getText().toString())));
		return urlParameters;
	}

	public void onRequestResult(StringBuffer result,
			RegistrationStage registrationStage) {
		if (progressDialog != null)
			progressDialog.dismiss();
		if (result == null) {
			return;
		}

		JSONObject mainJsonObj;
		final JSONObject details;
		try {
			mainJsonObj = new JSONObject(result.toString());

			if (mainJsonObj.getInt(TAGS.KEY_SUCCESS) == 1) {
				details = mainJsonObj.getJSONObject(TAGS.KEY_DETAILS);
				// registered successfully, emailId returned, prompt user to
				// login now
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
				alertDialog.setTitle("Yippiiieee");
				alertDialog.setMessage("Registration successful!!!");

				alertDialog.setPositiveButton("Login now!!",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									startLoginActivity(details
											.getString(TAGS.KEY_EMAIL_ID),
											TAGS.KEY_EMAIL_ID);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});

				alertDialog.show();

			} else if (mainJsonObj.getInt(TAGS.KEY_SUCCESS) == 0) {
				// some error occurred
				String message = mainJsonObj.getString(TAGS.KEY_MESSAGE);

				if (message.equals("USER_EXISTS")) {
					details = mainJsonObj.getJSONObject(TAGS.KEY_DETAILS);
					// prompt the user to login with returned input parameter
					final String loginType = details.getJSONArray(
							TAGS.KEY_PARAMS).getString(0);
					String inputValue = "";

					if (loginType.equals(TAGS.KEY_EMAIL_ID)) {
						inputValue = userToBeRegistered.getEmailId();
					} else if (loginType.equals(TAGS.KEY_MOBILE_NO)) {
						inputValue = String.valueOf(userToBeRegistered
								.getMobileNo());
					} else if (loginType.equals(TAGS.KEY_VOLUNTEER_ID)) {
						inputValue = userToBeRegistered.getVolunteerId();
					}

					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							this);
					alertDialog.setTitle("User Exists");
					alertDialog.setMessage("A user already exist with "
							+ loginType.toUpperCase()
							+ ". Please try login with this.");

					final String finalInput = inputValue;
					alertDialog.setPositiveButton("Take me to login page!!",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startLoginActivity(finalInput, loginType);
								}
							});

					alertDialog.show();
				} else if (message.equals("INTERNAL_ERROR")) {
					// prompt the user to wait
					Toast.makeText(
							getApplicationContext(),
							"Some internal error occured, please try again later",
							Toast.LENGTH_LONG).show();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void startLoginActivity(String input, String inputType) {
		try {
			Intent loginPageActivityIntent = new Intent(this,
					Class.forName("org.sankalpnitjamshedpur.LoginActivity"));
			if (inputType != null) {
				loginPageActivityIntent.putExtra(TAGS.KEY_REGISTERATION_TYPE,
						inputType);
				loginPageActivityIntent.putExtra(inputType, input);
			}
			startActivity(loginPageActivityIntent);
			finish();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
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
					if (nameBox.getText() != null
							&& nameBox.getText().toString().trim()
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
