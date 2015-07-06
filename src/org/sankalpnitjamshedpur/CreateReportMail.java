package org.sankalpnitjamshedpur;

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

import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.entity.ClassRecord;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

public class CreateReportMail {

	long startTime;
	ClassRecord classRecord;
	DatabaseHandler dBHandler;
	Context context;

	public CreateReportMail(long startTime, Context context) {
		super();
		this.startTime = startTime;
		this.context = context;
	}

	public void sendMail() {
		dBHandler = new DatabaseHandler(context);
		classRecord = dBHandler.getClassRecordByStartTime(startTime);
		try {
			String address = "ugesh.ebay@gmaill.com";

			final Intent emailIntent = new Intent(
					android.content.Intent.ACTION_SEND_MULTIPLE);
			Calendar classCalender = Calendar.getInstance();
			classCalender.setTimeInMillis(classRecord.getStartTime());
			emailIntent.setType("text/plain");

			// Here string array is passes for more than one email
			// addresses.
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					new String[] { address });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Class Report VolunteerId: " + classRecord.getVolunteerId()
							+ " at " + classCalender.get(Calendar.DAY_OF_MONTH)
							+ "-" + classCalender.get(Calendar.MONTH) + "-"
							+ classCalender.get(Calendar.YEAR));

			ArrayList<String> extra_text = new ArrayList<String>();
			extra_text.add("No content in Message");
			emailIntent.putStringArrayListExtra(
					android.content.Intent.EXTRA_TEXT, extra_text);

			/*
			 * // Attaching more than one files. if (URIList.size() != 0)
			 * emailIntent.putParcelableArrayListExtra( Intent.EXTRA_STREAM,
			 * URIList);
			 */

			if(classRecord.getUriList() != null && classRecord.getUriList().size()!=0)
				emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
					getCompressedUri(classRecord.getUriList()));

			context.startActivity(Intent.createChooser(emailIntent,
					"Sending email.."));
			Toast.makeText(context, "Sending....", Toast.LENGTH_LONG).show();

		} catch (Throwable t) {
			Toast.makeText(context, "Request failed try again: " + t.toString(),
					Toast.LENGTH_LONG).show();
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
				if(inputFileH!= null)
					filesToAdd.add(inputFileH);
			}

			zipFile.createZipFile(filesToAdd, parameters);

		} catch (Exception e) {
			e.printStackTrace();
		}

		list.add(Uri.fromFile(outputFileH));

		return (outputFileH != null) ? list : null;
	}
}
