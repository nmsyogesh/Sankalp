package org.sankalpnitjamshedpur;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.entity.User;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends Activity implements OnClickListener {
	Button loginButton;
	Button registerButton;
	EditText nameBox, rollNoBox, batchBox, emailBox, passwordBox, mobileNoBox;
	DatabaseHandler dbHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_page);
		dbHandler = new DatabaseHandler(this);
		registerButton = (Button) findViewById(R.id.registerButton);
		nameBox = (EditText) findViewById(R.id.name);
		rollNoBox = (EditText) findViewById(R.id.rollNo);
		emailBox = (EditText) findViewById(R.id.email);
		batchBox = (EditText) findViewById(R.id.batch);
		passwordBox = (EditText) findViewById(R.id.password);
		mobileNoBox = (EditText) findViewById(R.id.mobileNo);
		registerButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == registerButton) {
			new RemoteDatabaseHandler().execute(new User(nameBox.getText()
					.toString(), rollNoBox.getText().toString(), emailBox
					.getText().toString(), Integer.parseInt(batchBox.getText()
					.toString()), passwordBox.getText().toString(), Long
					.parseLong(mobileNoBox.getText().toString())));
		}
	}

	void onResult(String result) {
		{
			try {
				JSONObject json = new JSONObject(result);
				if(json.get("status").equals("SUCCESS")) {
					Toast.makeText(getApplicationContext(),
							"Yippppiieeee!!!!!",
							Toast.LENGTH_LONG).show();
					startActivity(new Intent(this,
							Class.forName("org.sankalpnitjamshedpur.HomePage")));
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class RemoteDatabaseHandler extends AsyncTask<User, String, String> {
		private static final String KEY_NAME = "1000158";
		private static final String KEY_ROLLNO = "1000161";
		private static final String KEY_BATCH = "1000162";
		private static final String KEY_MOBILE_NO = "1000163";
		private static final String KEY_EMAIL_ID = "1000159";
		private static final String KEY_PASSWORD = "1000160";
		private static final String KEY_VOLUNTEERID = "1000157";

		private static final String url = "https://api.ragic.com/sankalp/sankalp/1?v=3";
		private static final String USER_AGENT = "Mozilla/5.0";
		private static final String API_KEY = "Basic ZnNzNmt2aUt3Rms0YjRGS3RraExNaDl4SjF4MkxqTXQ4allxR2QrOUdxM3kzT1RRR3dVWEJlbW5Bb3VOdkhEd1ZsSUEyK09SdDNjPQ==";

		StringBuffer result;

		@Override
		protected String doInBackground(User... users) {
			User user = users[0];
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			result = new StringBuffer();
			// add header
			post.setHeader("User-Agent", USER_AGENT);

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			urlParameters.add(new BasicNameValuePair(KEY_NAME, user.getName())); // Contact
																					// Name
			urlParameters.add(new BasicNameValuePair(KEY_ROLLNO, user
					.getRollNo()));
			urlParameters.add(new BasicNameValuePair(KEY_MOBILE_NO, String
					.valueOf(user.getMobileNo())));
			urlParameters.add(new BasicNameValuePair(KEY_BATCH, String
					.valueOf(user.getBatch())));
			urlParameters.add(new BasicNameValuePair(KEY_VOLUNTEERID, user
					.getVolunteerId()));
			urlParameters.add(new BasicNameValuePair(KEY_EMAIL_ID, user
					.getEmailId()));
			urlParameters.add(new BasicNameValuePair(KEY_PASSWORD, user
					.getPassword()));

			try {
				post.setEntity(new UrlEncodedFormEntity(urlParameters));
				post.setHeader("Authorization", API_KEY);

				HttpResponse response = client.execute(post);
				System.out.println("\nSending 'POST' request to URL : " + url);
				System.out.println("Post parameters : " + post.getEntity());
				System.out.println("Response Code : "
						+ response.getStatusLine().getStatusCode());

				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));

				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}

				System.out.println(result.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result.toString();
		}

		protected void onPostExecute(String result) {
			onResult(result);
		}
	}
}
