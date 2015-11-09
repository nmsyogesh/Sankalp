package org.sankalpnitjamshedpur.db;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sankalpnitjamshedpur.entity.Centre;
import org.sankalpnitjamshedpur.entity.ClassRecord;
import org.sankalpnitjamshedpur.entity.StudentClass;
import org.sankalpnitjamshedpur.entity.Subject;
import org.sankalpnitjamshedpur.entity.User;
import org.sankalpnitjamshedpur.helper.TAGS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 8;

	// Database Name
	private static final String DATABASE_NAME = "sankalp";
	// Contacts table name
	private static final String TABLE_CONTACTS = "contactInfo";
	// Records table name
	private static final String TABLE_CLASS_RECORDS = "classRecords";
	// Centres table name
	private static final String TABLE_CENTRES = "centres";
	// Classes table name
	private static final String TABLE_CLASSES = "classes";
	// Subjects table name
	private static final String TABLE_SUBJECTS = "subjects";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
				+ TAGS.KEY_VOLUNTEER_ID + " TEXT PRIMARY KEY," + TAGS.KEY_NAME
				+ " TEXT," + TAGS.KEY_ROLLNO + " TEXT," + TAGS.KEY_BRANCH
				+ " TEXT," + TAGS.KEY_BATCH + " INTEGER ," + TAGS.KEY_EMAIL_ID
				+ " TEXT unique," + TAGS.KEY_MOBILE_NO + " INTEGER unique, "
				+ TAGS.KEY_SECURITY_TOKEN + " TEXT" + ")";

		String CREATE_RECORDS_TABLE = "CREATE TABLE " + TABLE_CLASS_RECORDS
				+ "(" + TAGS.KEY_START_TIME + " INTEGER PRIMARY KEY,"
				+ TAGS.KEY_URI_LIST + " TEXT ," + TAGS.KEY_VOLUNTEER_ID
				+ " TEXT ," + TAGS.KEY_END_TIME + " INTEGER ,"
				+ TAGS.KEY_CENTRE_ID + " INTEGER ," + TAGS.KEY_START_LATITUDE
				+ " REAL ," + TAGS.KEY_START_LONGITUDE + " REAL ,"
				+ TAGS.KEY_END_LATITUDE + " REAL ," + TAGS.KEY_END_LONGITUDE
				+ " REAL ," + TAGS.KEY_SENT_NOTIFICATION + " INTEGER ,"
				+ TAGS.KEY_COMMENTS + " TEXT " + ")";

		String CREATE_CENTRE_TABLE = "CREATE TABLE " + TABLE_CENTRES + "("
				+ TAGS.KEY_CENTRE_ID + " INTEGER PRIMARY KEY,"
				+ TAGS.KEY_CENTRE_NAME + " TEXT " + ")";

		String CREATE_CLASS_TABLE = "CREATE TABLE " + TABLE_CLASSES + "("
				+ TAGS.KEY_CLASS_ID + " INTEGER PRIMARY KEY,"
				+ TAGS.KEY_CLASS_NAME + " TEXT " + ")";

		String CREATE_SUBJECTS_TABLE = "CREATE TABLE " + TABLE_SUBJECTS + "("
				+ TAGS.KEY_SUBJECT_ID + " INTEGER PRIMARY KEY,"
				+ TAGS.KEY_SUBJECT_NAME + " TEXT, " + TAGS.KEY_CLASS_ID
				+ " INTEGER" + ")";

		db.execSQL(CREATE_CONTACTS_TABLE);
		db.execSQL(CREATE_RECORDS_TABLE);
		db.execSQL(CREATE_CENTRE_TABLE);
		db.execSQL(CREATE_CLASS_TABLE);
		db.execSQL(CREATE_SUBJECTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_RECORDS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CENTRES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);

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
		values.put(TAGS.KEY_NAME, user.getName()); // Contact Name
		values.put(TAGS.KEY_ROLLNO, user.getRollNo());
		values.put(TAGS.KEY_MOBILE_NO, user.getMobileNo());
		values.put(TAGS.KEY_BATCH, user.getBatch());
		values.put(TAGS.KEY_VOLUNTEER_ID, user.getVolunteerId());
		values.put(TAGS.KEY_EMAIL_ID, user.getEmailId());
		values.put(TAGS.KEY_BRANCH, user.getBranch());

		// Inserting Row
		db.insert(TABLE_CONTACTS, null, values);
		db.close(); // Closing database connection
	}

	// Adding new centre
	public void addCentre(Centre centre) {
		SQLiteDatabase db = this.getWritableDatabase();

		// if centre already exists
		Cursor cursor = db.query(TABLE_CENTRES, new String[] {
				TAGS.KEY_CENTRE_ID, TAGS.KEY_CENTRE_NAME }, TAGS.KEY_CENTRE_ID
				+ "=? and " + TAGS.KEY_CENTRE_NAME + "=?", new String[] {
				String.valueOf(centre.getCentreId()), centre.getCentreName() },
				null, null, null, null);
		if (cursor != null && cursor.getCount() > 0)
			return;

		ContentValues values = new ContentValues();
		values.put(TAGS.KEY_CENTRE_ID, centre.getCentreId()); // Contact Name
		values.put(TAGS.KEY_CENTRE_NAME, centre.getCentreName());

		// Inserting Row
		db.insert(TABLE_CENTRES, null, values);
		db.close(); // Closing database connection
	}

	// Adding new class
	public void addClass(StudentClass studentClass) {
		SQLiteDatabase db = this.getWritableDatabase();

		// if class already exists
		Cursor cursor = db.query(TABLE_CLASSES,
				new String[] { TAGS.KEY_CLASS_ID }, TAGS.KEY_CLASS_ID + "=?",
				new String[] { String.valueOf(studentClass.getClassId()) },
				null, null, null, null);
		if (cursor != null && cursor.getCount() > 0)
			return;

		ContentValues values = new ContentValues();
		values.put(TAGS.KEY_CLASS_ID, studentClass.getClassId()); // Contact
																	// Name
		values.put(TAGS.KEY_CLASS_NAME, studentClass.getClassName());

		// Inserting Row
		db.insert(TABLE_CLASSES, null, values);
		db.close(); // Closing database connection
	}

	public void addSubject(Subject subject) {
		SQLiteDatabase db = this.getWritableDatabase();

		// if class already exists
		Cursor cursor = db.query(TABLE_SUBJECTS,
				new String[] { TAGS.KEY_SUBJECT_ID }, TAGS.KEY_SUBJECT_ID
						+ "=?",
				new String[] { String.valueOf(subject.getSubjectId()) }, null,
				null, null, null);
		if (cursor != null && cursor.getCount() > 0)
			return;

		ContentValues values = new ContentValues();
		values.put(TAGS.KEY_SUBJECT_ID, subject.getSubjectId());
		values.put(TAGS.KEY_CLASS_ID, subject.getClassId());
		values.put(TAGS.KEY_SUBJECT_NAME, subject.getSubjectName());

		// Inserting Row
		db.insert(TABLE_SUBJECTS, null, values);
		db.close(); // Closing database connection
	}

	// Adding new contact
	public void addClassRecord(ClassRecord classRecord) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(TAGS.KEY_URI_LIST,
				getUriStringFromUriList(classRecord.getUriList()));
		values.put(TAGS.KEY_VOLUNTEER_ID, classRecord.getVolunteerId());
		values.put(TAGS.KEY_START_TIME,
				String.valueOf(classRecord.getStartTime()));
		values.put(TAGS.KEY_END_TIME, String.valueOf(classRecord.getEndTime()));
		values.put(TAGS.KEY_CENTRE_ID,
				String.valueOf(classRecord.getCentreNo()));
		values.put(TAGS.KEY_START_LATITUDE,
				String.valueOf(classRecord.getStartGpsLatitude()));
		values.put(TAGS.KEY_START_LONGITUDE,
				String.valueOf(classRecord.getStartGpsLongitude()));
		values.put(TAGS.KEY_END_LATITUDE,
				String.valueOf(classRecord.getEndGpsLatitude()));
		values.put(TAGS.KEY_END_LONGITUDE,
				String.valueOf(classRecord.getEndGpsLongitude()));
		values.put(TAGS.KEY_SENT_NOTIFICATION,
				classRecord.isSentNotification() ? "1" : "0");
		values.put(TAGS.KEY_COMMENTS, classRecord.getComments());

		// Inserting Row
		db.insert(TABLE_CLASS_RECORDS, null, values);
		db.close(); // Closing database connection
	}

	private String getUriStringFromUriList(ArrayList<Uri> uriList) {
		if (uriList == null || uriList.size() == 0)
			return null;

		ArrayList<String> uriStrings = new ArrayList<String>();
		for (Uri uri : uriList) {
			uriStrings.add(uri.toString());
		}
		JSONObject json = new JSONObject();
		try {
			json.put("uriList", new JSONArray(uriStrings));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	private ArrayList<Uri> getUriListFromUriString(String uriString) {
		if (uriString == null || uriString.trim().isEmpty())
			return null;
		ArrayList<Uri> uriList = new ArrayList<Uri>();
		ArrayList<String> uriStrings = new ArrayList<String>();
		JSONObject json;
		try {
			json = new JSONObject(uriString);

			JSONArray jsonArray = json.optJSONArray("uriList");
			if (jsonArray == null) {
				return null;
			}
			for (int i = 0; i < jsonArray.length(); i++) {
				if (!jsonArray.getString(i).equals("null"))
					uriStrings.add(jsonArray.getString(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		for (String uriPath : uriStrings) {
			uriList.add(Uri.parse(uriPath));
		}
		return uriList;
	}

	public ArrayList<ClassRecord> getAllClassRecords(String volunteerId) {
		ArrayList<ClassRecord> classRecords = new ArrayList<ClassRecord>();
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(TABLE_CLASS_RECORDS, new String[] {
				TAGS.KEY_START_TIME, TAGS.KEY_URI_LIST, TAGS.KEY_VOLUNTEER_ID,
				TAGS.KEY_END_TIME, TAGS.KEY_CENTRE_ID, TAGS.KEY_START_LATITUDE,
				TAGS.KEY_START_LONGITUDE, TAGS.KEY_END_LATITUDE,
				TAGS.KEY_END_LONGITUDE, TAGS.KEY_SENT_NOTIFICATION,
				TAGS.KEY_COMMENTS }, TAGS.KEY_VOLUNTEER_ID + "=?",
				new String[] { String.valueOf(volunteerId) }, null, null, null,
				null);

		if (cursor.moveToFirst()) {
			do {
				ClassRecord record = new ClassRecord();
				record.setVolunteerId(cursor.getString(2));
				record.setUriList(getUriListFromUriString(cursor.getString(1)));
				record.setStartTime(cursor.getLong(0));
				record.setEndTime(cursor.getLong(3));
				record.setCentreNo(cursor.getInt(4));
				record.setStartGpsLatitude(cursor.getDouble(5));
				record.setStartGpsLongitude(cursor.getDouble(6));
				record.setEndGpsLatitude(cursor.getDouble(7));
				record.setEndGpsLongitude(cursor.getDouble(8));
				record.setSentNotification(cursor.getInt(9) == 1 ? true : false);
				record.setComments(cursor.getString(10));
				classRecords.add(record);
			} while (cursor.moveToNext());
		}

		return classRecords;
	}

	public ArrayList<Centre> getListOfCentres() {
		ArrayList<Centre> centres = new ArrayList<Centre>();
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(TABLE_CENTRES, new String[] {
				TAGS.KEY_CENTRE_ID, TAGS.KEY_CENTRE_NAME }, null, null, null,
				null, null, null);

		if (cursor.moveToFirst()) {
			do {
				Centre centre = new Centre(cursor.getInt(0),
						cursor.getString(1));
				centres.add(centre);
			} while (cursor.moveToNext());
		}
		return centres;
	}

	public ArrayList<StudentClass> getListOfClasses() {
		ArrayList<StudentClass> classes = new ArrayList<StudentClass>();
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(TABLE_CLASSES, new String[] {
				TAGS.KEY_CLASS_ID, TAGS.KEY_CLASS_NAME }, null, null, null,
				null, null, null);

		if (cursor.moveToFirst()) {
			do {
				StudentClass studentClass = new StudentClass(cursor.getInt(0),
						cursor.getString(1));
				classes.add(studentClass);
			} while (cursor.moveToNext());
		}
		return classes;
	}

	public ArrayList<Subject> getListOfSubjects(int classId) {
		ArrayList<Subject> subjects = new ArrayList<Subject>();
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db
				.query(TABLE_SUBJECTS, new String[] { TAGS.KEY_SUBJECT_ID,
						TAGS.KEY_SUBJECT_NAME, TAGS.KEY_CLASS_ID }, TAGS.KEY_CLASS_ID + "=?",
						new String[] { String.valueOf(classId) },
						null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				Subject subject = new Subject(cursor.getInt(0),
						cursor.getString(1), cursor.getInt(2));
				subjects.add(subject);
			} while (cursor.moveToNext());
		}
		return subjects;
	}

	// Getting single contact
	public User getContactByVolunteerId(String volunteerId) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { TAGS.KEY_NAME,
				TAGS.KEY_ROLLNO, TAGS.KEY_EMAIL_ID, TAGS.KEY_BATCH,
				TAGS.KEY_BRANCH, TAGS.KEY_MOBILE_NO, TAGS.KEY_VOLUNTEER_ID,
				TAGS.KEY_SECURITY_TOKEN }, TAGS.KEY_VOLUNTEER_ID + "=?",
				new String[] { String.valueOf(volunteerId) }, null, null, null,
				null);
		if (cursor != null && cursor.getCount() > 0)
			cursor.moveToFirst();
		else
			return null;

		User contact = new User(cursor.getString(0), Integer.parseInt(cursor
				.getString(1)), cursor.getString(2), Integer.parseInt(cursor
				.getString(3)), cursor.getString(4), Long.parseLong(cursor
				.getString(5)), cursor.getString(6), cursor.getString(7));
		db.close();

		// return contact
		return contact;
	}

	public User getContactByEmailId(String emailId) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { TAGS.KEY_NAME,
				TAGS.KEY_ROLLNO, TAGS.KEY_EMAIL_ID, TAGS.KEY_BATCH,
				TAGS.KEY_BRANCH, TAGS.KEY_MOBILE_NO, TAGS.KEY_VOLUNTEER_ID,
				TAGS.KEY_SECURITY_TOKEN }, TAGS.KEY_EMAIL_ID + "=?",
				new String[] { String.valueOf(emailId) }, null, null, null,
				null);
		if (cursor != null && cursor.getCount() > 0)
			cursor.moveToFirst();
		else
			return null;

		User contact = new User(cursor.getString(0), Integer.parseInt(cursor
				.getString(1)), cursor.getString(2), Integer.parseInt(cursor
				.getString(3)), cursor.getString(4), Long.parseLong(cursor
				.getString(5)), cursor.getString(6), cursor.getString(7));

		db.close();
		// return contact
		return contact;
	}

	public User getContactByMobileNo(long mobileNo) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { TAGS.KEY_NAME,
				TAGS.KEY_ROLLNO, TAGS.KEY_EMAIL_ID, TAGS.KEY_BATCH,
				TAGS.KEY_BRANCH, TAGS.KEY_MOBILE_NO, TAGS.KEY_VOLUNTEER_ID,
				TAGS.KEY_SECURITY_TOKEN }, TAGS.KEY_MOBILE_NO + "=?",
				new String[] { String.valueOf(mobileNo) }, null, null, null,
				null);
		if (cursor != null && cursor.getCount() > 0)
			cursor.moveToFirst();
		else
			return null;

		User contact = new User(cursor.getString(0), Integer.parseInt(cursor
				.getString(1)), cursor.getString(2), Integer.parseInt(cursor
				.getString(3)), cursor.getString(4), Long.parseLong(cursor
				.getString(5)), cursor.getString(6), cursor.getString(7));

		db.close();
		// return contact
		return contact;
	}

	public ClassRecord getClassRecordByStartTime(long startTime) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CLASS_RECORDS, new String[] {
				TAGS.KEY_START_TIME, TAGS.KEY_URI_LIST, TAGS.KEY_VOLUNTEER_ID,
				TAGS.KEY_END_TIME, TAGS.KEY_CENTRE_ID, TAGS.KEY_START_LATITUDE,
				TAGS.KEY_START_LONGITUDE, TAGS.KEY_END_LATITUDE,
				TAGS.KEY_END_LONGITUDE, TAGS.KEY_SENT_NOTIFICATION,
				TAGS.KEY_COMMENTS }, TAGS.KEY_START_TIME + "=?",
				new String[] { String.valueOf(startTime) }, null, null, null,
				null);

		if (cursor != null && cursor.getCount() > 0)
			cursor.moveToFirst();
		else
			return null;

		ClassRecord record = new ClassRecord();
		record.setVolunteerId(cursor.getString(2));
		record.setUriList(getUriListFromUriString(cursor.getString(1)));
		record.setStartTime(cursor.getLong(0));
		record.setEndTime(cursor.getLong(3));
		record.setCentreNo(cursor.getInt(4));
		record.setStartGpsLatitude(cursor.getDouble(5));
		record.setStartGpsLongitude(cursor.getDouble(6));
		record.setEndGpsLatitude(cursor.getDouble(7));
		record.setEndGpsLongitude(cursor.getDouble(8));
		record.setSentNotification(cursor.getInt(9) == 1 ? true : false);
		record.setComments(cursor.getString(10));

		db.close();

		return record;
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
		values.put(TAGS.KEY_NAME, user.getName());
		values.put(TAGS.KEY_MOBILE_NO, user.getMobileNo());
		values.put(TAGS.KEY_EMAIL_ID, user.getEmailId());

		// updating row
		return db.update(TABLE_CONTACTS, values,
				TAGS.KEY_VOLUNTEER_ID + " = ?",
				new String[] { String.valueOf(user.getVolunteerId()) });
	}

	// Deleting single contact
	public void deleteContact(User user) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONTACTS, TAGS.KEY_VOLUNTEER_ID + " = ?",
				new String[] { String.valueOf(user.getVolunteerId()) });
		db.close();
	}

	// Deleting single centre
	public void deleteCentre(int centreId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CENTRES, TAGS.KEY_CENTRE_ID + " = ?",
				new String[] { String.valueOf(centreId) });
		db.close();
	}

	// Deleting single class
	public void deleteClass(int classId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CLASSES, TAGS.KEY_CLASS_ID + " = ?",
				new String[] { String.valueOf(classId) });
		db.close();
	}

	// Deleting single subject
	public void deleteSubject(int subjectId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SUBJECTS, TAGS.KEY_SUBJECT_ID+ " = ?",
				new String[] { String.valueOf(subjectId) });
		db.close();
	}

	// Deleting single contact
	public void deleteClassRecord(long startTime) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CLASS_RECORDS, TAGS.KEY_START_TIME + " = ?",
				new String[] { String.valueOf(startTime) });
		db.close();
	}

	public int markClassRecordNotification(long startTime) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(TAGS.KEY_SENT_NOTIFICATION, "1");
		// updating row
		return db.update(TABLE_CLASS_RECORDS, values, TAGS.KEY_START_TIME
				+ " = ?", new String[] { String.valueOf(startTime) });
	}

	// Getting contacts Count
	public int getUsersCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		db.close();
		// return count
		return cursor.getCount();
	}

}
