package org.sankalpnitjamshedpur.tabs;

import org.sankalpnitjamshedpur.R;
import org.sankalpnitjamshedpur.helper.SharedPreferencesKey;

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
						SharedPreferencesKey.KEY_NAME, null, getActivity()
								.getApplicationContext()));

		((TextView) android.findViewById(R.id.RollNo))
				.setText(SharedPreferencesKey.getStringFromSharedPreferences(
						SharedPreferencesKey.KEY_ROLLNO, null, getActivity()
								.getApplicationContext()));

		((TextView) android.findViewById(R.id.Batch))
				.setText(SharedPreferencesKey.getStringFromSharedPreferences(
						SharedPreferencesKey.KEY_BATCH, null, getActivity()
								.getApplicationContext()));

		((TextView) android.findViewById(R.id.Branch))
				.setText(SharedPreferencesKey.getStringFromSharedPreferences(
						SharedPreferencesKey.KEY_BRANCH, null, getActivity()
								.getApplicationContext()));

		((TextView) android.findViewById(R.id.EmailId))
				.setText(SharedPreferencesKey.getStringFromSharedPreferences(
						SharedPreferencesKey.KEY_EMAIL_ID, null, getActivity()
								.getApplicationContext()));

		((TextView) android.findViewById(R.id.MobileNo))
				.setText(SharedPreferencesKey.getStringFromSharedPreferences(
						SharedPreferencesKey.KEY_MOBILE_NO, null, getActivity()
								.getApplicationContext()));

		((TextView) android.findViewById(R.id.VolunteerId))
				.setText(SharedPreferencesKey.getStringFromSharedPreferences(
						SharedPreferencesKey.KEY_VOLUNTEERID, null,
						getActivity().getApplicationContext()));

		return android;
	}
}
