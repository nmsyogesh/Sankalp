package org.sankalpnitjamshedpur.tabs;

import org.sankalpnitjamshedpur.R;
import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.entity.ClassRecord;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class ClassRecordsFragment extends Fragment {

	View android;
	Context context;
	DatabaseHandler dbHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		android = inflater.inflate(R.layout.fragment_class_records, container, false);
		context = android.getContext();
		dbHandler = new DatabaseHandler(context);
		LinearLayout mainLayout =  (LinearLayout) android.findViewById(R.id.mainLayout);

		for (ClassRecord record : dbHandler.getAllClassRecords()) {
			mainLayout.addView(getLinearLayout(record));
		}

		return android;
	}

	LinearLayout getLinearLayout(ClassRecord classRecord) {

		LinearLayout mainLinearLayout = new LinearLayout(context);
		mainLinearLayout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams lp = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		LinearLayout headerLinearLayout = new LinearLayout(context);
		headerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		TextView tv = new TextView(context);
		tv.setText("Class StartTime: " + classRecord.getStartTime()
				+ " EndTime: " + classRecord.getEndTime() + " Centre: "
				+ String.valueOf(classRecord.getCentreNo()));
		tv.setGravity(Gravity.LEFT);
		headerLinearLayout.addView(tv, lp);

		LinearLayout footerLinearLayout = new LinearLayout(context);
		footerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

		Button sendMail = new Button(context);
		sendMail.setText("Send Report");
		sendMail.setGravity(Gravity.LEFT);

		Button deleteRecord = new Button(context);
		deleteRecord.setText("Delete Record");
		deleteRecord.setGravity(Gravity.RIGHT);

		footerLinearLayout.addView(sendMail, lp);
		footerLinearLayout.addView(deleteRecord, lp);

		mainLinearLayout.addView(headerLinearLayout);
		mainLinearLayout.addView(footerLinearLayout);
		return mainLinearLayout;
	}
}
