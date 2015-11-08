package org.sankalpnitjamshedpur.tabs;

import org.sankalpnitjamshedpur.R;
import org.sankalpnitjamshedpur.helper.SharedPreferencesKey;
import org.sankalpnitjamshedpur.helper.TAGS;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View android = inflater.inflate(R.layout.fragment_profile, container,
				false);

		((TextView) android.findViewById(R.id.Name))
				.setText(SharedPreferencesKey.getStringFromSharedPreferences(
						TAGS.KEY_NAME, null, getActivity()
								.getApplicationContext()));

		((TextView) android.findViewById(R.id.RollNo))
				.setText(SharedPreferencesKey.getStringFromSharedPreferences(
						TAGS.KEY_ROLLNO, null, getActivity()
								.getApplicationContext()));

		((TextView) android.findViewById(R.id.Batch))
				.setText(SharedPreferencesKey.getStringFromSharedPreferences(
						TAGS.KEY_BATCH, null, getActivity()
								.getApplicationContext()));

		((TextView) android.findViewById(R.id.Branch))
				.setText(SharedPreferencesKey.getStringFromSharedPreferences(
						TAGS.KEY_BRANCH, null, getActivity()
								.getApplicationContext()));

		((TextView) android.findViewById(R.id.EmailId))
				.setText(SharedPreferencesKey.getStringFromSharedPreferences(
						TAGS.KEY_EMAIL_ID, null, getActivity()
								.getApplicationContext()));

		((TextView) android.findViewById(R.id.MobileNo))
				.setText(SharedPreferencesKey.getStringFromSharedPreferences(
						TAGS.KEY_MOBILE_NO, null, getActivity()
								.getApplicationContext()));

		((TextView) android.findViewById(R.id.VolunteerId))
				.setText(SharedPreferencesKey.getStringFromSharedPreferences(
						TAGS.KEY_VOLUNTEER_ID, null,
						getActivity().getApplicationContext()));

		return android;
	}
}
