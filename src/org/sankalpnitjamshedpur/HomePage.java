package org.sankalpnitjamshedpur;

import org.sankalpnitjamshedpur.helper.SharedPreferencesKey;
import org.sankalpnitjamshedpur.helper.TAGS;
import org.sankalpnitjamshedpur.tabs.TabPagerAdapter;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class HomePage extends FragmentActivity implements
		ActionBar.TabListener {

	private ViewPager viewPager;
	private TabPagerAdapter tabPagerAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Profile", "Take a Class", "Report a Issue",
			"Class records", "Marks Entry" };

	Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homepage);

		viewPager = (ViewPager) findViewById(R.id.pager);
		tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(tabPagerAdapter);

		actionBar = getActionBar();

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
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.logout_action:

			new AlertDialog.Builder(this)
					.setTitle("Logging Out")
					.setMessage("Press OK to Logout!!")
					.setCancelable(true)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent;
									try {
										intent = new Intent(
												getApplicationContext(),
												Class.forName("org.sankalpnitjamshedpur.LoginActivity"));
										SharedPreferencesKey
												.putInSharedPreferences(
														TAGS.KEY_IS_LOGGED_IN,
														false,
														getApplicationContext());
										startActivity(intent);
										finish();
									} catch (ClassNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}).create().show();
			return true;
			
		case R.id.help:

			AlertDialog.Builder aB = new AlertDialog.Builder(this);

			aB.setTitle("Direction to use:");
			aB.setView(getDirectionsView());
			aB.setCancelable(true);

			aB.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			}).create().show();
			
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	ScrollView getDirectionsView() {
		ScrollView sV = new ScrollView(this);
		TextView tv = new TextView(this);
		tv.setPadding(20, 10, 10, 20);
		tv.setTextSize(15);
		tv.setText(R.string.directions);

		sV.addView(tv);
		return sV;
	}
}
