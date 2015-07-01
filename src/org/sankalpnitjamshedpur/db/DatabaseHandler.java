package org.sankalpnitjamshedpur.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.sankalpnitjamshedpur.entity.ClassRecord;
import org.sankalpnitjamshedpur.entity.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "sankalp";

	// Contacts table name
	private static final String TABLE_CONTACTS = "contactInfo";
	// Records table name
	private static final String TABLE_CLASS_RECORDS = "classRecords";

	// Contacts Table Columns names
	private static final String KEY_NAME = "name";
	private static final String KEY_ROLLNO = "roll_number";
	private static final String KEY_BATCH = "batch";
	private static final String KEY_MOBILE_NO = "mobile_number";
	private static final String KEY_EMAIL_ID = "email_id";
	private static final String KEY_BRANCH = "branch";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_VOLUNTEERID = "volunteer_id";

	private static final String KEY_URI_LIST = "listUri";
	private static final String KEY_START_TIME = "startTime";
	private static final String KEY_END_TIME = "endTime";
	private static final String KEY_CENTRE = "centre";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
				+ KEY_VOLUNTEERID + " TEXT PRIMARY KEY," + KEY_NAME + " TEXT,"
				+ KEY_ROLLNO + " TEXT," + KEY_BRANCH + " TEXT," + KEY_BATCH
				+ " INTEGER ," + KEY_EMAIL_ID + " TEXT unique," + KEY_PASSWORD
				+ " TEXT," + KEY_MOBILE_NO + " INTEGER unique" + ")";

		String CREATE_RECORDS_TABLE = "CREATE TABLE " + TABLE_CLASS_RECORDS
				+ "(" + KEY_START_TIME + " INTEGER PRIMARY KEY," + KEY_URI_LIST
				+ " BLOB," + KEY_VOLUNTEERID + " TEXT ," + KEY_END_TIME
				+ " INTEGER ," + KEY_CENTRE + " INTEGER" + ")";

		db.execSQL(CREATE_CONTACTS_TABLE);
		db.execSQL(CREATE_RECORDS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_RECORDS);

		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new contact
	public void addContact(User user) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, user.getName()); // Contact Name
		values.put(KEY_ROLLNO, user.getRollNo());
		values.put(KEY_MOBILE_NO, user.getMobileNo());
		values.put(KEY_BATCH, user.getBatch());
		values.put(KEY_VOLUNTEERID, user.getVolunteerId());
		values.put(KEY_EMAIL_ID, user.getEmailId());
		values.put(KEY_BRANCH, user.getBranch());
		values.put(KEY_PASSWORD, user.getPassword());

		// Inserting Row
		db.insert(TABLE_CONTACTS, null, values);
		db.close(); // Closing database connection
	}

	// Adding new contact
	public void addClassRecord(ClassRecord classRecord) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_URI_LIST, searialiseList(classRecord.getUriList()));
		values.put(KEY_VOLUNTEERID, classRecord.getVolunteerId());
		values.put(KEY_START_TIME, String.valueOf(classRecord.getStartTime()));
		values.put(KEY_END_TIME, String.valueOf(classRecord.getEndTime()));
		values.put(KEY_CENTRE, String.valueOf(classRecord.getCentreNo()));

		// Inserting Row
		db.insert(TABLE_CLASS_RECORDS, null, values);
		db.close(); // Closing database connection
	}

	public ArrayList<ClassRecord> getAllClassRecords() {
		ArrayList<ClassRecord> classRecords = new ArrayList<ClassRecord>();
		SQLiteDatabase db = this.getWritableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_CLASS_RECORDS;

		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				ClassRecord record = new ClassRecord();
				record.setVolunteerId(cursor.getString(2));
				record.setUriList(desearialiseList(cursor.getBlob(1)));
				record.setStartTime(cursor.getLong(0));
				record.setEndTime(cursor.getLong(3));
				record.setCentreNo(cursor.getInt(4));

				classRecords.add(record);
			} while (cursor.moveToNext());
		}

		db.close();
		return classRecords;
	}

	byte[] searialiseList(ArrayList<Uri> uriList) {
		ObjectOutputStream outStream = null;

		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			outStream = new ObjectOutputStream(byteOut);
			outStream.writeObject(uriList);
			outStream.close();
			return byteOut.toByteArray();
		} catch (Exception e) {

		}
		return null;
	}

	ArrayList<Uri> desearialiseList(byte[] uriList) {
		ObjectInputStream inStream = null;
		ArrayList<Uri> listTobeReturned = null;
		try {
			ByteArrayInputStream bytein = new ByteArrayInputStream(uriList);
			inStream = new ObjectInputStream(bytein);
			listTobeReturned = (ArrayList<Uri>) inStream.readObject();
			inStream.close();
		} catch (Exception e) {

		}
		return listTobeReturned;
	}

	// Getting single contact
	public User getContactByVolunteerId(String volunteerId) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_NAME,
				KEY_ROLLNO, KEY_EMAIL_ID, KEY_BATCH, KEY_BRANCH, KEY_PASSWORD,
				KEY_MOBILE_NO, KEY_VOLUNTEERID }, KEY_VOLUNTEERID + "=?",
				new String[] { String.valueOf(volunteerId) }, null, null, null,
				null);
		if (cursor != null && cursor.getCount() > 0)
			cursor.moveToFirst();
		else
			return null;

		User contact = new User(cursor.getString(0), Integer.parseInt(cursor
				.getString(1)), cursor.getString(2), Integer.parseInt(cursor
				.getString(3)), cursor.getString(4), cursor.getString(5),
				Long.parseLong(cursor.getString(6)));
		contact.setVolunteerId(cursor.getString(7));
		// return contact
		return contact;
	}

	public User getContactByEmailId(String emailId) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_NAME,
				KEY_ROLLNO, KEY_EMAIL_ID, KEY_BATCH, KEY_BRANCH, KEY_PASSWORD,
				KEY_MOBILE_NO, KEY_VOLUNTEERID }, KEY_EMAIL_ID + "=?",
				new String[] { String.valueOf(emailId) }, null, null, null,
				null);
		if (cursor != null && cursor.getCount() > 0)
			cursor.moveToFirst();
		else
			return null;

		User contact = new User(cursor.getString(0), Integer.parseInt(cursor
				.getString(1)), cursor.getString(2), Integer.parseInt(cursor
				.getString(3)), cursor.getString(4), cursor.getString(5),
				Long.parseLong(cursor.getString(6)));
		contact.setVolunteerId(cursor.getString(7));
		// return contact
		return contact;
	}

	public User getContactByMobileNo(long mobileNo) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_NAME,
				KEY_ROLLNO, KEY_EMAIL_ID, KEY_BATCH, KEY_BRANCH, KEY_PASSWORD,
				KEY_MOBILE_NO, KEY_VOLUNTEERID }, KEY_MOBILE_NO + "=?",
				new String[] { String.valueOf(mobileNo) }, null, null, null,
				null);
		if (cursor != null && cursor.getCount() > 0)
			cursor.moveToFirst();
		else
			return null;

		User contact = new User(cursor.getString(0), Integer.parseInt(cursor
				.getString(1)), cursor.getString(2), Integer.parseInt(cursor
				.getString(3)), cursor.getString(4), cursor.getString(5),
				Long.parseLong(cursor.getString(6)));
		contact.setVolunteerId(cursor.getString(7));
		// return contact
		return contact;
	}

	public User doesUserExists(User user) {
		User returnedUser;
		if ((returnedUser = getContactByMobileNo(user.getMobileNo())) != null)
			return returnedUser;
		if ((returnedUser = getContactByEmailId(user.getEmailId())) != null)
			return returnedUser;
		if ((returnedUser = getContactByVolunteerId(user.getVolunteerId())) != null)
			return returnedUser;

		return null;
	}

	// Updating single contact
	public int updateContact(User user) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, user.getName());
		values.put(KEY_MOBILE_NO, user.getMobileNo());
		values.put(KEY_EMAIL_ID, user.getEmailId());
		values.put(KEY_PASSWORD, user.getPassword());

		// updating row
		return db.update(TABLE_CONTACTS, values, KEY_VOLUNTEERID + " = ?",
				new String[] { String.valueOf(user.getVolunteerId()) });
	}

	// Deleting single contact
	public void deleteContact(User user) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONTACTS, KEY_VOLUNTEERID + " = ?",
				new String[] { String.valueOf(user.getVolunteerId()) });
		db.close();
	}

	// Deleting single contact
	public void deleteClassRecord(ClassRecord classRecord) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CLASS_RECORDS, KEY_START_TIME + " = ?",
				new String[] { String.valueOf(classRecord.getStartTime()) });
		db.close();
	}

	// Getting contacts Count
	public int getUsersCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}

}
