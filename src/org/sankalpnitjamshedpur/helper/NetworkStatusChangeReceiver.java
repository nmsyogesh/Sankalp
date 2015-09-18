package org.sankalpnitjamshedpur.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.db.RemoteDatabaseConfiguration;
import org.sankalpnitjamshedpur.entity.ClassRecord;
import org.sankalpnitjamshedpur.tabs.ClassRecordsFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class NetworkStatusChangeReceiver extends BroadcastReceiver {

	public List<ClassRecord> classRecords = new ArrayList<ClassRecord>();
	public Context context;
	public DatabaseHandler dbHandler;
	public ClassRecordsFragment notifyListener;

	public NetworkStatusChangeReceiver(Context context,
			ClassRecordsFragment notifyListener) {
		this.context = context;
		this.notifyListener = notifyListener;
		dbHandler = new DatabaseHandler(context);
	}

	public NetworkStatusChangeReceiver() {
		super();
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void addClassRecord(ClassRecord classRecord) {
		if (!classRecords.contains(classRecord)) {
			classRecords.add(classRecord);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		processRequests();
	}

	public void processRequests() {
		if (isConnected(context)) {
			for (ClassRecord classRecord : classRecords) {
				HttpRecordRequestHandler requestHandler = new HttpRecordRequestHandler(
						classRecord);
				requestHandler.execute(getHttpRecordPostRequest(classRecord));
			}
		}
	}

	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return (netInfo != null && netInfo.isConnected());
	}

	public void setNotifyListener(ClassRecordsFragment notifyListener) {
		this.notifyListener = notifyListener;
	}

	public static HttpUriRequest getHttpRecordPostRequest(
			ClassRecord classRecord) {
		HttpPost postRequest = new HttpPost(
				RemoteDatabaseConfiguration.RECORD_URL);
		postRequest.setHeader("User-Agent",
				RemoteDatabaseConfiguration.USER_AGENT);

		try {
			postRequest.setEntity(new UrlEncodedFormEntity(
					getRecordParameters(classRecord)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		postRequest.setHeader("Authorization",
				RemoteDatabaseConfiguration.getApiKey());

		return postRequest;
	}

	static List<NameValuePair> getRecordParameters(ClassRecord classRecord) {
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_RECORD_CENTRE_NO, String
						.valueOf(classRecord.getCentreNo())));
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_RECORD_VOLUNTEERID, classRecord
						.getVolunteerId()));

		long duration = classRecord.getEndTime() - classRecord.getStartTime();

		Date date = new Date(classRecord.getStartTime());
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_RECORD_START_TIME,
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date)));
		date = new Date(classRecord.getEndTime());
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_RECORD_END_TIME,
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date)));
		date = new Date(duration);
		String durationString = String.format("%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(duration),
				TimeUnit.MILLISECONDS.toMinutes(duration),
				TimeUnit.MILLISECONDS.toSeconds(duration));
		urlParameters
				.add(new BasicNameValuePair(
						RemoteDatabaseConfiguration.KEY_RECORD_DURATION,
						durationString));

		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_RECORD_START_LATITUDE, String
						.valueOf(classRecord.getStartGpsLatitude())));
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_RECORD_START_LONGITUDE, String
						.valueOf(classRecord.getStartGpsLongitude())));
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_RECORD_END_LATITUDE, String
						.valueOf(classRecord.getEndGpsLatitude())));
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_RECORD_END_LONGITUDE, String
						.valueOf(classRecord.getEndGpsLongitude())));
		urlParameters.add(new BasicNameValuePair(
				RemoteDatabaseConfiguration.KEY_RECORD_COMMENTS, String
						.valueOf(classRecord.getComments())));

		return urlParameters;
	}

	private class HttpRecordRequestHandler extends
			AsyncTask<HttpUriRequest, String, HttpResponse> {

		ClassRecord classRecord;
		boolean recordPosted = false;

		public HttpRecordRequestHandler(ClassRecord classRecord) {
			super();
			this.classRecord = classRecord;
		}

		@Override
		protected HttpResponse doInBackground(HttpUriRequest... httprequests) {
			HttpUriRequest httpRequest = httprequests[0];
			HttpClient client = new DefaultHttpClient();

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
						if (mainJsonObj.get("status").equals("SUCCESS")) {
							recordPosted = true;
						}
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

		protected void onPostExecute(HttpResponse response) {
			if(recordPosted) {
				dbHandler.markClassRecordNotification(classRecord
						.getStartTime());
				classRecords.remove(classRecord);
				notifyListener.notifyView();
			}
		}
	}
}