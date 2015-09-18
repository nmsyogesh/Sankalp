package org.sankalpnitjamshedpur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
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
import org.sankalpnitjamshedpur.helper.LoginConstants;
import org.sankalpnitjamshedpur.helper.SharedPreferencesKey;
import org.sankalpnitjamshedpur.helper.ValidationException;
import org.sankalpnitjamshedpur.helper.Validator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

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
	ProgressDialog progressDialog;
	TextView errorView;

	private ImageView fb_login_initiator;

	private CallbackManager callbackManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(this.getApplicationContext());
		setContentView(R.layout.login_page);

		if (SharedPreferencesKey.getBooleanFromSharedPreferences(
				SharedPreferencesKey.KEY_IS_LOGGED_IN, false,
				getApplicationContext())) {
			startHomePageActivityWithUser();
			finish();
			return;
		}

		String registeredType = getIntent().getStringExtra(
				LoginConstants.KEY_REGISTERED_TYPE);

		errorView = (TextView) findViewById(R.id.errorView);
		errorView.setVisibility(View.VISIBLE);
		inputParam = (EditText) findViewById(R.id.inputParam);
		password = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.loginButton);
		fb_login_initiator = (ImageView) findViewById(R.id.fb_login);
		fb_login_initiator.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LoginManager.getInstance().logInWithReadPermissions(
						LoginActivity.this, Arrays.asList("email"));
			}
		});

		loginOptionSpinner = (Spinner) findViewById(R.id.login_option);
		volunteerIdHelp = (TextView) findViewById(R.id.volunteerIdHelp);
		initiateErrorListeners();

		callbackManager = CallbackManager.Factory.create();

		LoginManager.getInstance().registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {

					@Override
					public void onSuccess(LoginResult loginResult) {
						GraphRequest request = GraphRequest.newMeRequest(
								loginResult.getAccessToken(),
								new GraphRequest.GraphJSONObjectCallback() {
									@Override
									public void onCompleted(JSONObject user,
											GraphResponse response) {
										if (user.optString("email").isEmpty()) {
											errorView
													.setError("No Email found for facebook user");
											return;
										}
										HttpRequestHandler requestHandler = new HttpRequestHandler(
												LoginActivity.this,
												RegistrationStage.FACEBOOK_LOGIN);
										progressDialog = ProgressDialog.show(
												getApplicationContext(),
												"Please Wait",
												"We are logging you in!!");
										progressDialog.setCancelable(true);
										Log.v("user details",
												response.toString());
										Log.i("Login",
												"user "
														+ user.optString("email")
														+ " has logged in through facebook");
										requestHandler
												.execute(getHttpRegistrationGetRequest(
														user.optString("email")
																.toLowerCase(),
														RemoteDatabaseConfiguration.KEY_USER_EMAIL_ID));
										Log.i("LOGIN",
												"generated login request for "
														+ user.optString("email"));

										LoginManager.getInstance().logOut();
									}
								});
						Bundle parameters = new Bundle();
						parameters.putString("fields", "email");
						request.setParameters(parameters);
						request.executeAsync();
					}

					@Override
					public void onCancel() {
						errorView.setError("User canceleed facebook login!!");
					}

					@Override
					public void onError(FacebookException error) {
						errorView.setError("Facebook login error!!");
					}
				});

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
						// inputParam.setText("");
						String input;
						if (option.equalsIgnoreCase("Volunteer Id")) {
							if ((input = SharedPreferencesKey
									.getStringFromSharedPreferences(
											SharedPreferencesKey.KEY_VOLUNTEERID,
											null, getApplicationContext())) != null) {
								inputParam.setText(input);
							}
							volunteerIdHelp.setVisibility(View.VISIBLE);
							inputParam.setHint("Enter your Volunteer Id");
						} else if (option.equalsIgnoreCase("Email Id")) {
							if ((input = SharedPreferencesKey
									.getStringFromSharedPreferences(
											SharedPreferencesKey.KEY_EMAIL_ID,
											null, getApplicationContext())) != null) {
								inputParam.setText(input);
							}
							volunteerIdHelp.setVisibility(View.GONE);
							inputParam.setHint("Enter your Email Id");
						} else if (option.equalsIgnoreCase("Mobile No")) {
							if ((input = SharedPreferencesKey
									.getStringFromSharedPreferences(
											SharedPreferencesKey.KEY_MOBILE_NO,
											null, getApplicationContext())) != null) {
								inputParam.setText(input);
							}
							volunteerIdHelp.setVisibility(View.GONE);
							inputParam.setHint("Enter your Mobile No");
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		if (registeredType != null) {
			if (registeredType.equalsIgnoreCase(LoginConstants.KEY_EMAILID)) {
				loginOptionSpinner.setSelection(1);
			} else if (registeredType
					.equalsIgnoreCase(LoginConstants.KEY_MOBILENO)) {
				loginOptionSpinner.setSelection(2);
			} else if (registeredType
					.equalsIgnoreCase(LoginConstants.KEY_VOLUNTEERID)) {
				loginOptionSpinner.setSelection(0);
			}
			String text = getIntent().getStringExtra(registeredType);
			inputParam.setText(text);
		}
		showHashKey(this);
	}

	public void showHashKey(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					"org.sankalpnitjamshedpur", PackageManager.GET_SIGNATURES); // Your
																				// package
																				// name
																				// here
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.i("KeyHash:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {
		}
	}

	private void initiateErrorListeners() {

		inputParam.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
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
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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
		errorView.setError(null);
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
						RemoteDatabaseConfiguration.KEY_USER_VOLUNTEERID));
			} else if (option.equalsIgnoreCase("Email Id")) {
				requestHandler.execute(getHttpRegistrationGetRequest(
						inputString.toLowerCase(),
						RemoteDatabaseConfiguration.KEY_USER_EMAIL_ID));
			} else if (option.equalsIgnoreCase("Mobile No")) {
				requestHandler.execute(getHttpRegistrationGetRequest(
						inputString,
						RemoteDatabaseConfiguration.KEY_USER_MOBILE_NO));
			}
			progressDialog = ProgressDialog.show(this, "Please Wait",
					"We are logging you in!!");
			progressDialog.setCancelable(true);
		} 
	}

	private void checkfields() {
		// Setting text boxes with their values so that their onTextChanged
		// Method is triggered
		inputParam.setText(inputParam.getText().toString());
		password.setText(password.getText().toString());
	}

	private HttpUriRequest getHttpRegistrationGetRequest(String value,
			String fieldId) {

		HttpGet getRequest = new HttpGet(RemoteDatabaseConfiguration.USER_URL
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
		if (progressDialog != null)
			progressDialog.dismiss();
		if (httpResponse == null) {
			Toast.makeText(getApplicationContext(),
					"Login failed. Please check Internet connectivity.",
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
			if (RegistrationStage.LOGIN == registrationStage) {
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
					return;
				}
			} else if (registrationStage == RegistrationStage.GOOGLE_LOGIN
					|| registrationStage == RegistrationStage.FACEBOOK_LOGIN) {
				if (mainJsonObj.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"No user Exists with this Email",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}

			if (registrationStage == RegistrationStage.GOOGLE_LOGIN) {
				loggedInUser = checkPasswordAndCreateUser(mainJsonObj, false);
			} else if (registrationStage == RegistrationStage.FACEBOOK_LOGIN) {
				loggedInUser = checkPasswordAndCreateUser(mainJsonObj, false);
			} else if (registrationStage == RegistrationStage.LOGIN) {
				loggedInUser = checkPasswordAndCreateUser(mainJsonObj, true);
			}

			if (loggedInUser != null) {
				Log.i("LOGIN",
						"login successful for " + loggedInUser.getEmailId());
				setPrefernces();
				startHomePageActivityWithUser();
			} else {
				Toast.makeText(getApplicationContext(),
						"Wrong password!! try again", Toast.LENGTH_LONG).show();
			}

		} catch (IllegalStateException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}

	private User checkPasswordAndCreateUser(JSONObject mainJsonObj,
			boolean checkPassword) throws JSONException {
		Iterator<String> keysIterator = mainJsonObj.keys();
		if (keysIterator.hasNext()) {
			JSONObject dataObject = mainJsonObj.getJSONObject(keysIterator
					.next());

			if (checkPassword
					&& !passwordText.equals(dataObject.getString("Password"))) {
				Log.e("LOGIN",
						"wrong password for " + dataObject.getString("EmailId"));
				return null;
			}

			return new User(dataObject.getString("Name"),
					Integer.parseInt(dataObject.getString("RollNo")),
					dataObject.getString("EmailId"),
					Integer.parseInt(dataObject.getString("Batch")),
					dataObject.getString("Branch"),
					dataObject.getString("Password"), Long.parseLong(dataObject
							.getString("MobileNo")));
		}
		return null;
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

		editor.putBoolean(SharedPreferencesKey.KEY_IS_LOGGED_IN, true);

		editor.commit();
	}

	public Context getApplicationContext() {
		return this;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		super.onActivityResult(requestCode, responseCode, intent);
		callbackManager.onActivityResult(requestCode, responseCode, intent);
	}

	@Override
	public void finish() {
		LoginManager.getInstance().logOut();
		super.finish();
	}
}
