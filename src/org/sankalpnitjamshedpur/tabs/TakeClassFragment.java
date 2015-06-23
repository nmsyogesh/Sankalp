package org.sankalpnitjamshedpur.tabs;

import org.sankalpnitjamshedpur.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TakeClassFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View android = inflater.inflate(R.layout.fragment_takeclass, container,
				false);
		return android;
	}
}
