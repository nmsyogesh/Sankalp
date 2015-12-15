package org.sankalpnitjamshedpur.tabs;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.sankalpnitjamshedpur.CreateReportMail;
import org.sankalpnitjamshedpur.R;
import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.entity.Centre;
import org.sankalpnitjamshedpur.entity.ClassRecord;
import org.sankalpnitjamshedpur.helper.NetworkStatusChangeReceiver;
import org.sankalpnitjamshedpur.helper.SharedPreferencesKey;
import org.sankalpnitjamshedpur.helper.TAGS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
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
	public NetworkStatusChangeReceiver networkStatusChangeReceiver;
	ArrayList<Centre> centreList = new ArrayList<Centre>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		android = inflater.inflate(R.layout.fragment_class_records, container,
				false);
		context = android.getContext();
		networkStatusChangeReceiver = new NetworkStatusChangeReceiver(context,
				this);
		dbHandler = new DatabaseHandler(context);
		centreList = dbHandler.getListOfCentres();
		populateView();
		return android;
	}
	
	String getCentrename(int id) {
		for(Centre c: centreList) {
			if(c.getCentreId()== id) 
				return c.getCentreName();
		}
		return "";
	}

	public void populateView() {
		ScrollView mainScrollView = (ScrollView) android
				.findViewById(R.id.mainLayout);
		mainScrollView.removeAllViews();

		LinearLayout mainLayout = new LinearLayout(context);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lp = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		List<ClassRecord> classRecords = dbHandler
				.getAllClassRecords(SharedPreferencesKey
						.getStringFromSharedPreferences(TAGS.KEY_VOLUNTEER_ID,
								null, context));
		if (classRecords == null || classRecords.isEmpty()) {
			TextView tv = new TextView(context);
			tv.setText("Sorry No records found \n Consider taking a class today!!!");
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			lp.gravity = Gravity.CENTER_VERTICAL;
			mainScrollView.addView(tv, lp);
			return;
		}

		for (ClassRecord record : classRecords) {
			LinearLayout.LayoutParams wrappedLp = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			wrappedLp.setMargins(0, 10, 0, 10);
			mainLayout.addView(getLinearLayout(record), wrappedLp);
		}
		mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		mainScrollView.addView(mainLayout, lp);
	}

	LinearLayout getLinearLayout(ClassRecord classRecord) {

		LinearLayout.LayoutParams wrappedLp = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		LinearLayout headerLinearLayout = new LinearLayout(context);
		headerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		TextView tv = new TextView(context);
		Calendar classCalendar = Calendar.getInstance();
		classCalendar.setTimeInMillis(classRecord.getStartTime());
		tv.setText(String.format("%02d",
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
				+ getCentrename(classRecord.getCentreNo()));

		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setPadding(13, 10, 13, 10);
		tv.setBackgroundResource(R.drawable.rectangle);

		ImageView deleteImage = new ImageView(context);
		deleteImage.setClickable(true);
		deleteImage.setOnClickListener(new CustomOnClickDeleteListener(
				classRecord));
		deleteImage.setImageResource(R.drawable.cancel);
		deleteImage.setBackgroundResource(R.drawable.rectangle);
		deleteImage.setPadding(13, 10, 13, 10);

		ImageView sendMail = new ImageView(context);
		sendMail.setClickable(true);
		sendMail.setOnClickListener(new CustomSendMailListener(classRecord));
		sendMail.setImageResource(R.drawable.sendmail);
		sendMail.setBackgroundResource(R.drawable.rectangle);
		sendMail.setPadding(13, 10, 13, 10);

		ImageView notification = new ImageView(context);
		notification.setClickable(false);
		notification.setImageResource(R.drawable.done);
		if (!classRecord.isSentNotification()) {
			notification.setVisibility(View.INVISIBLE);
		}

		headerLinearLayout.addView(deleteImage);

		wrappedLp.setMargins(10, 10, 10, 10);
		headerLinearLayout.addView(tv, wrappedLp);

		wrappedLp.setMargins(10, 10, 0, 10);
		headerLinearLayout.addView(sendMail, wrappedLp);

		headerLinearLayout.addView(notification);

		headerLinearLayout.setGravity(Gravity.CENTER);
		return headerLinearLayout;
	}

	public void notifyView() {
		populateView();
	}

	private class CustomOnClickDeleteListener implements OnClickListener {

		ClassRecord classRecord;
		DatabaseHandler db = new DatabaseHandler(context);

		public CustomOnClickDeleteListener(ClassRecord classRecord) {
			this.classRecord = classRecord;
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
							if (classRecord.getUriList() != null
									&& classRecord.getUriList().size() != 0) {
								for (Uri uri : classRecord.getUriList()) {
									File f = new File(uri.getPath());
									if (f != null) {
										f.delete();
									}
								}

								String timeStamp = String.valueOf(classRecord
										.getStartTime());
								String zipFilePath = Environment
										.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
										+ File.separator
										+ "VolunteerSubmission"
										+ timeStamp
										+ ".zip";
								File f = new File(zipFilePath);
								if (f != null) {
									f.delete();
								}
							}
							db.deleteClassRecord(classRecord.getStartTime());
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

	public class CustomSendMailListener implements OnClickListener {

		ClassRecord classRecord;
		String comment = "";

		public CustomSendMailListener(ClassRecord classRecord) {
			this.classRecord = classRecord;
		}

		@Override
		public void onClick(View v) {

			if (!classRecord.isSentNotification()) {
				AlertDialog.Builder alert = new AlertDialog.Builder(context);

				alert.setTitle("Generating Report");
				alert.setMessage("Do you want to edit the comment before Sending");

				// Set an EditText view to get user input
				final EditText input = new EditText(context);
				input.setText(classRecord.getComments());
				alert.setView(input);

				alert.setPositiveButton("Done",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								comment = input.getText().toString();
								classRecord.setComments(comment);

								networkStatusChangeReceiver
										.addClassRecord(classRecord);
								networkStatusChangeReceiver.processRequests();
								Toast.makeText(context,
										"Added post request to queue",
										Toast.LENGTH_SHORT).show();

								if (classRecord.getUriList() != null
										&& classRecord.getUriList().size() != 0) {
									processEmail();
								}
							}
						});
				alert.show();
			} else if (classRecord.getUriList() != null
					&& classRecord.getUriList().size() != 0) {
				processEmail();
			} else {
				Toast.makeText(context, "Record already posted!!",
						Toast.LENGTH_SHORT).show();
			}
		}

		public void processEmail() {
			CreateReportMail createReportMailHelper = new CreateReportMail(
					classRecord, context);
			createReportMailHelper.sendMail();
		}
	}
}
