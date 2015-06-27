package org.sankalpnitjamshedpur;

import org.sankalpnitjamshedpur.tabs.TabPagerAdapter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

@SuppressWarnings("deprecation")
public class HomePage extends ActionBarActivity implements
		ActionBar.TabListener {

	private ViewPager viewPager;
	private TabPagerAdapter tabPagerAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Profile", "Take a Class", "Report a Issue" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homepage);

		viewPager = (ViewPager) findViewById(R.id.pager);
		tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(tabPagerAdapter);
		
		actionBar = getSupportActionBar();

		// Enable Tabs on Action Bar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			/**
			 * on swipe select the respective tab
			 * */
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onTabReselected(android.support.v7.app.ActionBar.Tab arg0,
			android.support.v4.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(android.support.v7.app.ActionBar.Tab tab,
			android.support.v4.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(android.support.v7.app.ActionBar.Tab arg0,
			android.support.v4.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
}
