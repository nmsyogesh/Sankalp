package org.sankalpnitjamshedpur.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.sankalpnitjamshedpur.UserAuthenticationActivity;

import android.os.AsyncTask;
import android.util.Log;

public class HttpRequestHandler extends
		AsyncTask<HttpUriRequest, String, StringBuffer> {

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
	protected StringBuffer doInBackground(HttpUriRequest... httprequests) {
		HttpUriRequest httpRequest = httprequests[0];
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
		HttpConnectionParams.setSoTimeout(httpParameters, 8000);
		HttpClient client = new DefaultHttpClient(httpParameters);
		try {
			HttpResponse response = client.execute(httpRequest);
			StringBuffer result = new StringBuffer();
			BufferedReader rd;
			rd = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
					result.append(line);
			}
			return result;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(StringBuffer response) {
		userAuthenticationActivity.onRequestResult(response, registrationStage);
	}

}
