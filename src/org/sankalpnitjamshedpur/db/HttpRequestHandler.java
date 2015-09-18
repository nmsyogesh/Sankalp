package org.sankalpnitjamshedpur.db;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.sankalpnitjamshedpur.UserAuthenticationActivity;

import android.os.AsyncTask;

public class HttpRequestHandler extends
		AsyncTask<HttpUriRequest, String, HttpResponse> {

	UserAuthenticationActivity userAuthenticationActivity;
	RegistrationStage registrationStage;

	public HttpRequestHandler(
			UserAuthenticationActivity userAuthenticationActivity,
			RegistrationStage registrationStage) {
		super();
		this.registrationStage = registrationStage;
		this.userAuthenticationActivity = userAuthenticationActivity;
	}

	@Override
	protected HttpResponse doInBackground(HttpUriRequest... httprequests) {
		HttpUriRequest httpRequest = httprequests[0];
		HttpClient client = new DefaultHttpClient();

		try {
			HttpResponse response = client.execute(httpRequest);
			if (response.getEntity() != null)
				userAuthenticationActivity.onRequestResult(response, registrationStage);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(HttpResponse response) {
	}

}
