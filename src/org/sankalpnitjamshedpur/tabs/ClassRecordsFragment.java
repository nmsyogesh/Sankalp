package org.sankalpnitjamshedpur.tabs;

import java.util.List;

import org.sankalpnitjamshedpur.CreateReportMail;
import org.sankalpnitjamshedpur.R;
import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.entity.ClassRecord;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ClassRecordsFragment extends Fragment {

	View android;
	Context context;
	DatabaseHandler dbHandler;
	ImageView deleteImage;

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
			tv.setText("Sorry No records found");
			lp.gravity = Gravity.CENTER_HORIZONTAL;
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
		tv.setText("Class StartTime: " + classRecord.getStartTime()
				+ " EndTime: " + classRecord.getEndTime() + " Centre: "
				+ String.valueOf(classRecord.getCentreNo()));
		tv.setGravity(Gravity.LEFT);
		headerLinearLayout.addView(tv, extendedLp);

		LinearLayout footerLinearLayout = new LinearLayout(context);
		footerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

		Button sendMail = new Button(context);
		sendMail.setOnClickListener(new CustomSendMailListener(classRecord
				.getStartTime()));
		sendMail.setText("Send Report");
		sendMail.setGravity(Gravity.LEFT);

		deleteImage = new ImageView(context);
		deleteImage.setClickable(true);
		deleteImage.setOnClickListener(new CustomOnClickDeleteListener(
				classRecord.getStartTime()));

		Bitmap bmp;
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cancel);
		bmp = Bitmap.createScaledBitmap(bmp, 50, 50, true);
		deleteImage.setImageBitmap(bmp);
		deleteImage.setScaleType(ScaleType.FIT_CENTER);

		footerLinearLayout.addView(sendMail, wrappedLp);

		extendedLp.setMargins(18, 8, 3, 3);
		wrappedLp.gravity = Gravity.RIGHT;

		footerLinearLayout.addView(deleteImage, extendedLp);

		mainLinearLayout.addView(headerLinearLayout);
		mainLinearLayout.addView(footerLinearLayout, extendedLp);
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
		long startTime;

		public CustomSendMailListener(long startTime) {
			this.startTime = startTime;
		}

		@Override
		public void onClick(View v) {
			CreateReportMail createReportMailHelper = new CreateReportMail(
					startTime, context);
			createReportMailHelper.sendMail();
		}
	}
}
