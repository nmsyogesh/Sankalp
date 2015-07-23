package org.sankalpnitjamshedpur.helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

public class GPSTracker extends Service implements LocationListener {

	private final Context mContext;
	// flag for GPS status
	public boolean isGPSEnabled = false;

	Location location; // location

	AlertDialog gpsdialog;

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPSTracker(Context context) {
		this.mContext = context;
		locationManager = (LocationManager) mContext
				.getSystemService(LOCATION_SERVICE);
		getLocation();
	}

	public static boolean doesDeviceHasGpsSensor(Context context) {
		PackageManager packMan = context.getPackageManager();
		return packMan.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
	}

	public Location getLocation() {
		try {
			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if (!isGPSEnabled) {
				// no network provider is enabled
				showSettingsAlert();
			} else {
				if (location == null) {
					locationManager = (LocationManager) mContext
							.getSystemService(LOCATION_SERVICE);
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, 10, 0, this);
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					}

					if (location == null) {
						if (locationManager
								.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
							locationManager.requestLocationUpdates(
									LocationManager.NETWORK_PROVIDER, 10, 0,
									this);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		return isGPSEnabled;
	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * */
	public Dialog showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle("GPS Settings");

		// Setting Dialog Message
		alertDialog.setMessage("Please Enable GPS !!!");

		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						mContext.startActivity(intent);

					}
				});

		// Showing Alert Message
		gpsdialog = alertDialog.create();

		gpsdialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (!canGetLocation()) {
					showSettingsAlert();
					return;
				}
				if (getLocation() == null) {
					dialog.dismiss();
					new ShowDialog().execute();
				}
			}
		});
		gpsdialog.show();
		return gpsdialog;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			this.location = location;
		}
		if (gpsdialog != null) {
			gpsdialog.dismiss();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		if (provider == LocationManager.GPS_PROVIDER) {
			isGPSEnabled = false;
			showSettingsAlert();
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		if (provider == LocationManager.GPS_PROVIDER) {
			isGPSEnabled = true;
			if (getLocation() != null && gpsdialog != null) {
				gpsdialog.cancel();
			}
			if (getLocation() == null) {
				if (gpsdialog != null) {
					gpsdialog.dismiss();
				}
				new ShowDialog().execute();
			}
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public class ShowDialog extends AsyncTask<String, Integer, String> {
		ProgressDialog progDailog = null;

		public LocationManager mLocationManager;

		@Override
		protected void onPreExecute() {
			progDailog = new ProgressDialog(mContext);
			progDailog.setCancelable(false);
			progDailog.setTitle("Please Wait");
			progDailog.setMessage("Getting Location through GPS !!\nIt may take some time.");
			progDailog.setIndeterminate(true);
			progDailog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			progDailog.dismiss();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			while (getLocation() == null) {
			}
			return null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}