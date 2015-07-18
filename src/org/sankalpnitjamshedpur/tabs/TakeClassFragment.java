package org.sankalpnitjamshedpur.tabs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.sankalpnitjamshedpur.R;
import org.sankalpnitjamshedpur.adapter.ImageAdapter;
import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.entity.ClassRecord;
import org.sankalpnitjamshedpur.helper.GPSTracker;
import org.sankalpnitjamshedpur.helper.SharedPreferencesKey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

public class TakeClassFragment extends Fragment implements OnClickListener {
	// Activity request codes
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	// directory name to store captured images and videos
	private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

	DatabaseHandler dbHandler;

	GridView gridView;
	EditText editTextSubject, editTextMessage;
	Button btnCapturePicture, centreOption;
	Button classFunctionButton;
	String email, subject, message, attachmentFile;
	Uri fileUri;
	ArrayList<Uri> URIList = new ArrayList<Uri>();
	private static final int PICK_FROM_GALLERY = 101;
	int columnIndex;
	GPSTracker gpsTracker;
	Dialog gpsDialog;

	Location startLocation;
	Location endLocation;

	boolean classAlreadyStarted = false;

	String volunteerId;
	int centreNo = 0;

	long startTime, endTime;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View android = inflater.inflate(R.layout.fragment_takeclass, container,
				false);

		gpsTracker = new GPSTracker(android.getContext());
		dbHandler = new DatabaseHandler(android.getContext());
		volunteerId = SharedPreferencesKey.getStringFromSharedPreferences(
				SharedPreferencesKey.KEY_VOLUNTEERID, "", android.getContext());

		gridView = (GridView) android.findViewById(R.id.previewPane);

		editTextSubject = (EditText) android.findViewById(R.id.editTextSubject);
		editTextMessage = (EditText) android.findViewById(R.id.editTextMessage);
		centreOption = (Button) android.findViewById(R.id.centreOption);

		classFunctionButton = (Button) android
				.findViewById(R.id.buttonClassFunction);

		btnCapturePicture = (Button) android
				.findViewById(R.id.btnCapturePicture);
		btnCapturePicture.setOnClickListener(this);
		classFunctionButton.setOnClickListener(this);
		centreOption.setOnClickListener(this);

		if (savedInstanceState != null) {
			fileUri = savedInstanceState.getParcelable("file_uri");
		}

		if (!classAlreadyStarted) {
			classFunctionButton.setText("Start the class!!");
			disableFields();
		} else {
			classFunctionButton.setText("Stop the class!!");
			enableFields();
		}
		if (centreNo != 0) {
			centreOption.setText("Centre: " + centreNo);
		}

		if (!doesDeviceSupportCamera()) {
			Toast.makeText(getActivity().getApplicationContext(),
					"Sorry! Your device doesn't support camera",
					Toast.LENGTH_LONG).show();
			Log.e(attachmentFile, "Sorry! Your device doesn't support camera");
		}
		Log.e(attachmentFile, "Device supports camera");
		return android;
	}

	/**
	 * Checking device has camera hardware or not
	 * */
	private boolean doesDeviceSupportCamera() {
		if (getActivity().getApplicationContext().getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_FROM_GALLERY
				&& resultCode == android.app.Activity.RESULT_OK && data != null
				&& data.getData() != null) {
			/**
			 * Get Path
			 */
			Uri selectedImage = data.getData();
			URIList.add(selectedImage);
			previewCapturedImage();
		}
		if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == android.app.Activity.RESULT_OK) {
				// successfully captured the image
				// display it in image view
				previewCapturedImage();
			} else if (resultCode == android.app.Activity.RESULT_CANCELED) {
				// user cancelled Image capture
				Toast.makeText(getActivity().getApplicationContext(),
						"User cancelled image capture", Toast.LENGTH_SHORT)
						.show();
			} else {
				// failed to capture image
				Toast.makeText(getActivity().getApplicationContext(),
						"Sorry! Failed to capture image", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	void disableFields() {
		btnCapturePicture.setVisibility(View.INVISIBLE);
		centreOption.setVisibility(View.INVISIBLE);
		editTextMessage.setVisibility(View.INVISIBLE);
		editTextSubject.setVisibility(View.INVISIBLE);

	}

	void enableFields() {
		btnCapturePicture.setVisibility(View.VISIBLE);
		centreOption.setVisibility(View.VISIBLE);
		editTextMessage.setVisibility(View.VISIBLE);
		editTextSubject.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		if (v == btnCapturePicture) {
			Log.i(attachmentFile, "taking pic");
			captureImage();
		}

		if (v == classFunctionButton && !classAlreadyStarted) {
			if (!gpsTracker.canGetLocation()) {
				gpsTracker.showSettingsAlert();
				return;
			}
			startTime = Calendar.getInstance().getTimeInMillis();
			classFunctionButton.setText("Stop the class!!");
			classAlreadyStarted = true;
			startLocation = gpsTracker.getLocation();
			if (startLocation != null) {
				Toast.makeText(
						getActivity().getApplicationContext(),
						"Location determined" + "\n"
								+ startLocation.getLatitude() + "\n"
								+ startLocation.getLongitude(),
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						"Location Null", Toast.LENGTH_SHORT).show();
			}
			centreOption.setText("Select Centre");
			enableFields();
		} else if (v == classFunctionButton && classAlreadyStarted) {
			if (centreNo == 0) {
				centreOption.setError("Please select Centre");
				return;
			}
			if (!gpsTracker.canGetLocation()) {
				gpsTracker.showSettingsAlert();
				return;
			}
			endTime = Calendar.getInstance().getTimeInMillis();
			classFunctionButton.setText("Start the class!!");
			classAlreadyStarted = false;
			endLocation = gpsTracker.getLocation();
			if (endLocation != null) {
				Toast.makeText(
						getActivity().getApplicationContext(),
						"Location determined" + "\n"
								+ endLocation.getLatitude() + "\n"
								+ endLocation.getLongitude(),
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						"Location Null", Toast.LENGTH_SHORT).show();
			}

			dbHandler.addClassRecord(new ClassRecord(URIList, startTime,
					endTime, volunteerId, centreNo,
					startLocation.getLatitude(), startLocation.getLongitude(),
					endLocation.getLatitude(), endLocation.getLongitude()));
			URIList.clear();
			disableFields();
			startLocation = null;
			endLocation = null;
			centreNo = 0;
		}

		if (v == centreOption) {
			centreOption.setError(null);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());

			// Setting Dialog Title
			alertDialogBuilder.setTitle("Choose Centre");

			alertDialogBuilder.setSingleChoiceItems(R.array.centre_array, 0,
					null).setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							centreNo = 1 + ((AlertDialog) dialog).getListView()
									.getCheckedItemPosition();
							centreOption.setText("Centre: " + centreNo);
						}
					});

			alertDialogBuilder.create().show();
		}

	}

	ArrayList<Uri> getCompressedUri(List<Uri> uriList) {
		File outputFileH = null;
		ArrayList<Uri> list = new ArrayList<Uri>();

		try {
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
					Locale.getDefault()).format(new Date());
			String outputFileName = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
					+ File.separator
					+ "VolunteerSubmission"
					+ timeStamp
					+ ".zip";
			outputFileH = new File(outputFileName);
			ZipFile zipFile = new ZipFile(outputFileName);
			ArrayList<File> filesToAdd = new ArrayList<File>();
			ZipParameters parameters = new ZipParameters();

			// COMP_DEFLATE is for compression
			// COMp_STORE no compression
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			// DEFLATE_LEVEL_ULTRA = maximum compression
			// DEFLATE_LEVEL_MAXIMUM
			// DEFLATE_LEVEL_NORMAL = normal compression
			// DEFLATE_LEVEL_FAST
			// DEFLATE_LEVEL_FASTEST = fastest compression
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);

			parameters.setEncryptFiles(true);
			parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
			parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
			parameters.setPassword("password");

			for (Uri uri : uriList) {
				File inputFileH = new File(uri.getPath());
				filesToAdd.add(inputFileH);
			}

			zipFile.createZipFile(filesToAdd, parameters);

			// outputFileH = new File(outputFileName);

		} catch (Exception e) {
			e.printStackTrace();
		}

		list.add(Uri.fromFile(outputFileH));

		return (outputFileH != null) ? list : null;
	}

	/**
	 * Capturing Camera Image will lauch camera app requrest image capture
	 */
	private void captureImage() {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		URIList.add(fileUri);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// start the image capture Intent
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	}

	/**
	 * Here we store the file url as it will be null after returning from camera
	 * app
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// save file url in bundle as it will be null on screen orientation
		// changes
		outState.putParcelable("file_uri", fileUri);
	}

	/**
	 * Display image from a path to ImageView
	 */
	private void previewCapturedImage() {
		gridView.setVerticalScrollBarEnabled(false);
		gridView.setAdapter(new ImageAdapter(getActivity()
				.getApplicationContext(), URIList));
	}

	public void openGallery() {

		Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
		getIntent.setType("image/*");

		Intent pickIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		pickIntent.setType("image/*");

		Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
				new Intent[] { pickIntent });

		chooserIntent.putExtra("return-data", true);
		startActivityForResult(chooserIntent, PICK_FROM_GALLERY);

	}

	/**
	 * ------------ Helper Methods ----------------------
	 * */

	/**
	 * Creating file uri to store image/video
	 */
	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/**
	 * returning image / video
	 */
	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (gpsDialog != null)
			gpsDialog.cancel();

		if (!gpsTracker.canGetLocation()) {
			gpsDialog = gpsTracker.showSettingsAlert();
		}
	}
}
