package org.sankalpnitjamshedpur.tabs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.sankalpnitjamshedpur.CreateReportMail;
import org.sankalpnitjamshedpur.R;
import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.db.RemoteDatabaseConfiguration;
import org.sankalpnitjamshedpur.entity.ClassRecord;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ClassRecordsFragment extends Fragment {

	View android;
	Context context;
	DatabaseHandler dbHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		android = inflater.inflate(R.layout.fragment_class_records, container,
				false);
		context = android.getContext();
		dbHandler = new DatabaseHandler(context);
		populateView();
		return android;
	}

	void populateView() {
		ScrollView mainScrollView = (ScrollView) android
				.findViewById(R.id.mainLayout);
		mainScrollView.removeAllViews();

		LinearLayout mainLayout = new LinearLayout(context);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lp = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		List<ClassRecord> classRecords = dbHandler.getAllClassRecords();
		if (classRecords == null || classRecords.isEmpty()) {
			TextView tv = new TextView(context);
			tv.setText("Sorry No records found \n Consider taking a class today!!!");
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			lp.gravity = Gravity.CENTER_VERTICAL;
			mainScrollView.addView(tv, lp);
			return;
		}

		for (ClassRecord record : classRecords) {
			mainLayout.addView(getLinearLayout(record));
		}
		mainScrollView.addView(mainLayout, lp);
	}

	LinearLayout getLinearLayout(ClassRecord classRecord) {

		LinearLayout mainLinearLayout = new LinearLayout(context);
		mainLinearLayout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams extendedLp = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams wrappedLp = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		LinearLayout headerLinearLayout = new LinearLayout(context);
		headerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		TextView tv = new TextView(context);
		Calendar classCalendar = Calendar.getInstance();
		classCalendar.setTimeInMillis(classRecord.getStartTime());
		tv.setText("Class Record"
				+ "\n"
				+ String.format("%02d",
						classCalendar.get(Calendar.DAY_OF_MONTH))
				+ "-"
				+ String.format("%02d", classCalendar.get(Calendar.MONTH))
				+ "-"
				+ String.format("%02d", classCalendar.get(Calendar.YEAR))
				+ " ("
				+ String.format("%02d", classCalendar.get(Calendar.HOUR_OF_DAY))
				+ ":"
				+ String.format("%02d", classCalendar.get(Calendar.MINUTE))
				+ ":"
				+ String.format("%02d", classCalendar.get(Calendar.SECOND))
				+ ")" + "\n" + "Centre: "
				+ String.valueOf(classRecord.getCentreNo())
				);

		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setPadding(13, 10, 13, 10);
		tv.setBackgroundResource(R.drawable.rectangle);

		LinearLayout footerLinearLayout = new LinearLayout(context);
		footerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

		/*
		 * Button sendMail = new Button(context);
		 * sendMail.setOnClickListener(new CustomSendMailListener(classRecord
		 * .getStartTime())); sendMail.setText("Send Report");
		 * sendMail.setGravity(Gravity.LEFT);
		 */

		ImageView deleteImage = new ImageView(context);
		deleteImage.setClickable(true);
		deleteImage.setOnClickListener(new CustomOnClickDeleteListener(
				classRecord.getStartTime()));
		deleteImage.setBackgroundResource(R.drawable.rectangle);
		deleteImage.setImageResource(R.drawable.cancel);
		deleteImage.setPadding(13, 10, 13, 10);

		ImageView sendMail = new ImageView(context);
		sendMail.setClickable(true);
		sendMail.setOnClickListener(new CustomSendMailListener(classRecord));
		sendMail.setBackgroundResource(R.drawable.rectangle);
		sendMail.setImageResource(R.drawable.sendmail);
		sendMail.setPadding(13, 10, 13, 10);

		wrappedLp.setMargins(10, 10, 10, 10);

		headerLinearLayout.addView(deleteImage, wrappedLp);
		headerLinearLayout.addView(tv, wrappedLp);
		headerLinearLayout.addView(sendMail, wrappedLp);
		headerLinearLayout.setGravity(Gravity.CENTER);

		/*
		 * Bitmap bmp; bmp = BitmapFactory.decodeResource(getResources(),
		 * R.drawable.cancel); bmp = Bitmap.createScaledBitmap(bmp, 100, 100,
		 * true); deleteImage.setImageBitmap(bmp);
		 * deleteImage.setScaleType(ScaleType.FIT_CENTER);
		 */
		// footerLinearLayout.addView(sendMail, wrappedLp);

		// extendedLp.setMargins(18, 8, 3, 3);
		// wrappedLp.gravity = Gravity.RIGHT;

		// footerLinearLayout.addView(deleteImage, extendedLp);

		mainLinearLayout.addView(headerLinearLayout, wrappedLp);
		// mainLinearLayout.addView(footerLinearLayout, extendedLp);
		mainLinearLayout.setPadding(5, 5, 5, 5);
		mainLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		return mainLinearLayout;
	}

	private class CustomOnClickDeleteListener implements OnClickListener {

		long startTime;
		DatabaseHandler db = new DatabaseHandler(context);

		public CustomOnClickDeleteListener(long startTime) {
			this.startTime = startTime;
		}

		@Override
		public void onClick(View v) {

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					getActivity());

			// Setting Dialog Title
			alertDialog.setTitle("Confirm Delete...");

			alertDialog.setMessage("Are you sure to delete!!");

			alertDialog.setPositiveButton("Delete",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							db.deleteClassRecord(startTime);
							populateView();
						}
					});

			alertDialog.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});

			alertDialog.show();
		}
	}

	private class CustomSendMailListener implements OnClickListener {

		ClassRecord classRecord;

		public CustomSendMailListener(ClassRecord classRecord) {
			this.classRecord = classRecord;
		}

		@Override
		public void onClick(View v) {
			CreateReportMail createReportMailHelper = new CreateReportMail(
					classRecord.getStartTime(), context);
			if(!classRecord.isSentNotification()) {
				HttpRecordRequestHandler requestHandler = new HttpRecordRequestHandler(
						classRecord);
				requestHandler.execute(getHttpRecordPostRequest(classRecord));				
			}
			createReportMailHelper.sendMail();
		}

		private HttpUriRequest getHttpRecordPostRequest(ClassRecord classRecord) {
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

		List<NameValuePair> getRecordParameters(ClassRecord classRecord) {
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair(
					RemoteDatabaseConfiguration.KEY_RECORD_CENTRE_NO, String
							.valueOf(classRecord.getCentreNo())));
			urlParameters.add(new BasicNameValuePair(
					RemoteDatabaseConfiguration.KEY_RECORD_VOLUNTEERID,
					classRecord.getVolunteerId()));

			long duration = classRecord.getEndTime()
					- classRecord.getStartTime();

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
			urlParameters.add(new BasicNameValuePair(
					RemoteDatabaseConfiguration.KEY_RECORD_DURATION,
					durationString));

			urlParameters.add(new BasicNameValuePair(
					RemoteDatabaseConfiguration.KEY_RECORD_START_LATITUDE,
					String.valueOf(classRecord.getStartGpsLatitude())));
			urlParameters.add(new BasicNameValuePair(
					RemoteDatabaseConfiguration.KEY_RECORD_START_LONGITUDE,
					String.valueOf(classRecord.getStartGpsLongitude())));
			urlParameters.add(new BasicNameValuePair(
					RemoteDatabaseConfiguration.KEY_RECORD_END_LATITUDE, String
							.valueOf(classRecord.getEndGpsLatitude())));
			urlParameters.add(new BasicNameValuePair(
					RemoteDatabaseConfiguration.KEY_RECORD_END_LONGITUDE,
					String.valueOf(classRecord.getEndGpsLongitude())));

			return urlParameters;
		}
	}

	private class HttpRecordRequestHandler extends
			AsyncTask<HttpUriRequest, String, HttpResponse> {

		ClassRecord classRecord;

		public HttpRecordRequestHandler(ClassRecord classRecord) {
			super();
			this.classRecord = classRecord;
		}

		@Override
		protected HttpResponse doInBackground(HttpUriRequest... httprequests) {
			HttpUriRequest httpRequest = httprequests[0];
			HttpClient client = new DefaultHttpClient();

			try {
				HttpResponse response = client.execute(httpRequest);
				if (response.getEntity() != null)
					return response;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(HttpResponse response) {
			// Mark the report sent in the db by record
			if (response != null
					&& response.getStatusLine().getStatusCode() == 200) {
				Toast.makeText(context, "Posted success", Toast.LENGTH_SHORT)
						.show();
				StringBuffer result = new StringBuffer();
				BufferedReader rd;
				try {
					rd = new BufferedReader(new InputStreamReader(response
								.getEntity().getContent()));
					String line = "";
					while ((line = rd.readLine()) != null) {
						result.append(line);
					}
					JSONObject mainJsonObj = new JSONObject(result.toString());
					if (mainJsonObj.get("status").equals("SUCCESS")) {
						dbHandler.markClassRecordNotification(classRecord.getStartTime());
						populateView();
					}
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}
