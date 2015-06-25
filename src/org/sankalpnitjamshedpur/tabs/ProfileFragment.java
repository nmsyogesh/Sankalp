package org.sankalpnitjamshedpur.tabs;

import org.sankalpnitjamshedpur.R;
import org.sankalpnitjamshedpur.SharedPreferencesKey;

import android.content.Context;
import android.content.SharedPreferences;
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

		
		SharedPreferences sf;
		sf = getActivity().getApplicationContext().getSharedPreferences(
				SharedPreferencesKey.PREFS_NAME, Context.MODE_PRIVATE); // 1

		((TextView) android.findViewById(R.id.Name)).setText(sf
				.getString(SharedPreferencesKey.KEY_NAME, null));
		
		((TextView) android.findViewById(R.id.RollNo)).setText(sf
				.getString(SharedPreferencesKey.KEY_ROLLNO, null));
		
		((TextView) android.findViewById(R.id.Batch)).setText(sf
				.getString(SharedPreferencesKey.KEY_BATCH, null));
		
		((TextView) android.findViewById(R.id.Branch)).setText(sf
				.getString(SharedPreferencesKey.KEY_BRANCH, null));
		
		((TextView) android.findViewById(R.id.EmailId)).setText(sf
				.getString(SharedPreferencesKey.KEY_EMAIL_ID, null));
		
		((TextView) android.findViewById(R.id.MobileNo)).setText(sf
				.getString(SharedPreferencesKey.KEY_MOBILE_NO, null));
		
		((TextView) android.findViewById(R.id.VolunteerId)).setText(sf
				.getString(SharedPreferencesKey.KEY_VOLUNTEERID, null));

		return android;
	}
}
