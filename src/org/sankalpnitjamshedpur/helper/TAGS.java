package org.sankalpnitjamshedpur.helper;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TAGS {
	public static final String KEY_NAME = "name";
	public static final String KEY_ROLLNO = "rollNo";
	public static final String KEY_BATCH = "batch";
	public static final String KEY_MOBILE_NO = "contactNo";
	public static final String KEY_EMAIL_ID = "emailId";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_VOLUNTEER_ID = "volunteerId";
	public static final String KEY_BRANCH = "branch";
	public static final String KEY_SECURITY_TOKEN = "securityToken";

	public static final String KEY_START_TIME = "startTime";
	public static final String KEY_END_TIME = "endTime";
	public static final String KEY_START_LATITUDE = "startLatitude";
	public static final String KEY_START_LONGITUDE = "startLongitude";
	public static final String KEY_END_LATITUDE = "endLatitude";
	public static final String KEY_END_LONGITUDE = "endLongitude";
	public static final String KEY_COMMENTS = "comments";
	public static final String KEY_URI_LIST = "listUri";
	public static final String KEY_SENT_NOTIFICATION = "sentNotification";

	public static final String KEY_IS_LOGGED_IN = "LoggedIn";

	public static final String VOLUNTEERS_RECORD_URL = "http://www.sankalpnitjamshedpur.org/login/createClassRecord.php";
	public static final String VOLUNTEERS_LOGIN_URL = "http://www.sankalpnitjamshedpur.org/login/loginVolunteer.php";
	public static final String VOLUNTEERS_REGISTRATION_URL = "http://www.sankalpnitjamshedpur.org/login/createVolunteer.php";
	public static final String CENTRES_LIST_URL = "http://www.sankalpnitjamshedpur.org/login/centreList.php";
	public static final String CLASS_LIST_URL = "http://www.sankalpnitjamshedpur.org/login/classList.php";
	public static final String STUDENTS_LIST_URL = "http://www.sankalpnitjamshedpur.org/login/studentsList.php";
	public static final String SUBJECTS_LIST_URL = "http://www.sankalpnitjamshedpur.org/login/subjectsList.php";

	// json response params
	public static final String KEY_SUCCESS = "success";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_DETAILS = "details";
	public static final String KEY_PARAMS = "params";

	public static final String KEY_REGISTERATION_TYPE = "registrationType";
	
	//centres table
	public static final String KEY_CENTRES = "centres";
	public static final String KEY_CENTRE_NAME = "centreName";
	public static final String KEY_CENTRE_ID = "centreId";

	// class table
	public static final String KEY_CLASSES = "classes";
	public static final String KEY_CLASS_ID = "classId";
	public static final String KEY_CLASS_NAME = "className";

	// student Table
	public static final String KEY_STUDENTS = "students";
	public static final String KEYS_STUDENT_ID = "studentId";
	public static final String KEYS_STUDENT_NAME = "studentName";
	public static final String KEYS_STUDENT_ROLLNO = "rollNo";
	
	// subjects table
	public static final String KEY_SUBJECTS = "subjects";
	public static final String KEY_SUBJECT_ID = "subjectId";
	public static final String KEY_SUBJECT_NAME = "subjectName";

	// sync targets
	public static final String SYNC_CENTRES = "sync_centres";
	public static final String SYNC_CLASSES = "sync_classes";
	public static final String SYNC_SUBJECTS = "sync_subjects";

	public static String generateHash(String string) {
		byte[] hash = null;

		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Huh, MD5 should be supported?");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Huh, UTF-8 should be supported?");
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);

		for (byte b : hash) {
			int i = (b & 0xFF);
			if (i < 0x10)
				hex.append('0');
			hex.append(Integer.toHexString(i));
		}

		return hex.toString();
	}
}
