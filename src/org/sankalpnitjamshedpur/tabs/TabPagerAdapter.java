package org.sankalpnitjamshedpur.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabPagerAdapter extends FragmentPagerAdapter {
	public TabPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int i) {
		switch (i) {
		case 0:
			return new ProfileFragment();
		case 1:
			return new TakeClassFragment();
		case 2:
			return new ReportIssueFragment();
		case 3:
			return new ClassRecordsFragment();
		case 4:
			return new MarksEntryFragment();
		}
		return null;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 5; // No of Tabs
	}

}