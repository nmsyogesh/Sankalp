package org.sankalpnitjamshedpur.tabs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sankalpnitjamshedpur.R;
import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.entity.Centre;
import org.sankalpnitjamshedpur.entity.Exam;
import org.sankalpnitjamshedpur.entity.Student;
import org.sankalpnitjamshedpur.entity.StudentClass;
import org.sankalpnitjamshedpur.entity.Subject;
import org.sankalpnitjamshedpur.helper.NetworkStatusChangeReceiver;
import org.sankalpnitjamshedpur.helper.SharedPreferencesKey;
import org.sankalpnitjamshedpur.helper.TAGS;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MarksEntryFragment extends Fragment implements OnClickListener {

	View android;
	Context context;
	DatabaseHandler dbHandler;
	Button centreSelect, classSelect, subjectSelect, maxMarksSelect,
			examSelect, marksSubmit;
	int centreId, classId, subjectId, examId, maxMarks;
	ScrollView studentsListView;

	ArrayList<Student> studentsList;
	ArrayList<Exam> listOfExams;
	ArrayList<Centre> listOfCentres;
	ArrayList<StudentClass> listOfClasses;

	ProgressDialog progressDialog;
	HashMap<String, Integer> marksMap = new HashMap<String, Integer>();

	Toast toast;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		android = inflater.inflate(R.layout.fragment_marks_entry, container,
				false);
		context = android.getContext();

		centreSelect = (Button) android.findViewById(R.id.centreSelect);
		classSelect = (Button) android.findViewById(R.id.classSelect);
		subjectSelect = (Button) android.findViewById(R.id.subjectSelect);
		maxMarksSelect = (Button) android.findViewById(R.id.maxMarksSelect);
		examSelect = (Button) android.findViewById(R.id.examSelect);
		studentsListView = (ScrollView) android.findViewById(R.id.studentsList);
		marksSubmit = (Button) android.findViewById(R.id.submitMarks);

		centreSelect.setOnClickListener(this);
		subjectSelect.setOnClickListener(this);
		maxMarksSelect.setOnClickListener(this);
		classSelect.setOnClickListener(this);
		examSelect.setOnClickListener(this);
		marksSubmit.setOnClickListener(this);

		examSelect.setText("Select Exam");
		marksSubmit.setVisibility(View.GONE);
		classSelect.setVisibility(View.GONE);
		centreSelect.setVisibility(View.GONE);
		maxMarksSelect.setVisibility(View.GONE);
		subjectSelect.setVisibility(View.GONE);

		dbHandler = new DatabaseHandler(context);

		return android;
	}

	public void populateView(ArrayList<Student> students) {
		studentsListView.removeAllViews();

		if (students == null || students.isEmpty()) {
			TextView tv = new TextView(context);
			tv.setText("Sorry No Students found \n Consider choosing another combination of class and centre!!");
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			studentsListView.addView(tv);
			marksSubmit.setVisibility(View.GONE);
			return;
		}
		TableLayout tableLayout = new TableLayout(context);
		tableLayout.addView(insertHeaderRow(), new TableLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		tableLayout.setPadding(10, 0, 10, 0);
		for (Student student : students) {
			tableLayout.addView(getTableRow(student),
					new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
		}
		studentsListView.addView(tableLayout);
	}

	private View insertHeaderRow() {
		TableRow tr_head = new TableRow(context);
		tr_head.setBackgroundColor(Color.parseColor("#16a085"));
		tr_head.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		TextView rollNo = new TextView(context);
		rollNo.setText(String.valueOf("Roll No"));
		rollNo.setPadding(5,2, 5,2);
		tr_head.addView(rollNo, new TableRow.LayoutParams(0,
				TableRow.LayoutParams.WRAP_CONTENT, 0.20f));

		rollNo = new TextView(context);
		rollNo.setText(String.valueOf("Name"));
		tr_head.addView(rollNo, new TableRow.LayoutParams(0,
				TableRow.LayoutParams.WRAP_CONTENT, 0.50f));

		rollNo = new TextView(context);
		rollNo.setText(String.valueOf("Marks"));
		tr_head.addView(rollNo, new TableRow.LayoutParams(0,
				TableRow.LayoutParams.WRAP_CONTENT, 0.30f));

		return tr_head;
	}

	TableRow getTableRow(Student student) {
		TableRow tr_head = new TableRow(context);
		tr_head.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		TextView rollNo = new TextView(context);
		rollNo.setText(String.valueOf(student.getRollNo()));
		rollNo.setPadding(5, 2, 5, 2);
		tr_head.addView(rollNo, new TableRow.LayoutParams(0,
				TableRow.LayoutParams.WRAP_CONTENT, 0.20f));

		TextView name = new TextView(context);
		name.setText(student.getStudentname());
		name.setPadding(5, 2, 5, 2);
		tr_head.addView(name, new TableRow.LayoutParams(0,
				TableRow.LayoutParams.WRAP_CONTENT, 0.50f));

		EditText marks = new EditText(context);
		marks.addTextChangedListener(new CustomTextChangeListener(student
				.getStudentId()));
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(5, 2, 5, 2);
		marks.setLayoutParams(layoutParams);
		marks.setInputType(InputType.TYPE_CLASS_NUMBER);
		tr_head.addView(marks, new TableRow.LayoutParams(0,
				TableRow.LayoutParams.WRAP_CONTENT, 0.30f));
		return tr_head;
	}

	@Override
	public void onClick(View v) {
		if (v == examSelect) {
			listOfExams = dbHandler.getListOfExams();

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());

			// Setting Dialog Title
			alertDialogBuilder.setTitle("Choose Exam");

			if (listOfExams != null && !listOfExams.isEmpty()) {
				alertDialogBuilder.setSingleChoiceItems(
						getExamsList(listOfExams), 0, null).setPositiveButton(
						"OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								Exam exam = listOfExams
										.get(((AlertDialog) dialog)
												.getListView()
												.getCheckedItemPosition());
								examId = exam.getExamId();
								examSelect.setText(exam.getExamType() + " "
										+ exam.getExamDate());
								centreSelect.setVisibility(View.VISIBLE);
								if (centreId == 0)
									centreSelect.setError("");
							}
						});
			} else {
				alertDialogBuilder
						.setMessage("No active exams are available, try synching the App.");
			}
			alertDialogBuilder.create().show();
		} else if (v == centreSelect) {

			listOfCentres = dbHandler.getListOfCentres();
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());

			alertDialogBuilder.setTitle("Choose Centre");
			if (listOfCentres != null && !listOfCentres.isEmpty()) {
				alertDialogBuilder.setSingleChoiceItems(
						getCentreList(listOfCentres), 0, null)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {										
										classSelect.setVisibility(View.GONE);
										subjectSelect.setVisibility(View.GONE);
										maxMarksSelect.setVisibility(View.GONE);
										marksSubmit.setVisibility(View.GONE);

										marksMap.clear();

										studentsListView.removeAllViews();

										Centre centre = listOfCentres
												.get(((AlertDialog) dialog)
														.getListView()
														.getCheckedItemPosition());
										centreId = centre.getCentreId();
										classSelect.setVisibility(View.VISIBLE);
										classSelect.setText("Select Class");
										centreSelect.setError(null);
										classSelect.setError("");
										centreSelect.setText("Centre: "
												+ centre.getCentreName());
									}
								});
			} else {
				alertDialogBuilder
						.setMessage("No centres found, try synching the App");
			}
			alertDialogBuilder.create().show();

		} else if (v == classSelect) {

			listOfClasses = dbHandler.getListOfClasses();
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());

			// Setting Dialog Title
			alertDialogBuilder.setTitle("Choose Class");

			if (listOfClasses != null && !listOfClasses.isEmpty()) {
				alertDialogBuilder.setSingleChoiceItems(
						getClassList(listOfClasses), 0, null)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										subjectSelect.setVisibility(View.GONE);
										maxMarksSelect.setVisibility(View.GONE);
										marksSubmit.setVisibility(View.GONE);

										marksMap.clear();

										studentsListView.removeAllViews();
										
										StudentClass studentClass = listOfClasses
												.get(((AlertDialog) dialog)
														.getListView()
														.getCheckedItemPosition());
										classId = studentClass.getClassId();
										classSelect.setText(studentClass
												.getClassName());
										subjectSelect
												.setVisibility(View.VISIBLE);
										classSelect.setError(null);
										subjectSelect.setText("Select Subject");
										subjectSelect.setError("");
										if (!NetworkStatusChangeReceiver
												.isConnected(context)) {
											Toast.makeText(
													context,
													"No Internet Connectivity. Please Check.",
													Toast.LENGTH_SHORT).show();
											return;
										}

										HttpRequestHandler httpRequestHandler = new HttpRequestHandler(
												"getStudents");
										httpRequestHandler
												.execute(new HttpGet(
														TAGS.STUDENTS_LIST_URL
																+ "?centreId="
																+ centreId
																+ "&classId="
																+ classId));
										progressDialog = ProgressDialog.show(
												context, "Please Wait",
												"Fetching list of students");
										progressDialog.setCancelable(true);
									}
								});
			} else {
				alertDialogBuilder
						.setMessage("No classes found, try synching the App");
			}
			alertDialogBuilder.create().show();
		} else if (v == subjectSelect) {

			final ArrayList<Subject> listOfSubjects = dbHandler
					.getListOfSubjects(classId);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder.setTitle("Choose Subject");

			if (listOfSubjects != null && !listOfSubjects.isEmpty()) {
				alertDialogBuilder.setSingleChoiceItems(
						getSubjectsList(listOfSubjects), 0, null)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										maxMarksSelect.setVisibility(View.GONE);
										marksSubmit.setVisibility(View.GONE);
										
										Subject subject = listOfSubjects
												.get(((AlertDialog) dialog)
														.getListView()
														.getCheckedItemPosition());
										subjectId = subject.getSubjectId();
										subjectSelect.setText(subject
												.getSubjectName());
										subjectSelect.setError(null);
										maxMarksSelect
												.setVisibility(View.VISIBLE);
										maxMarksSelect.setText("Max Marks");
										maxMarksSelect.setError("");
									}
								});
			} else {
				alertDialogBuilder
						.setMessage("No subjects are available for this class, try synching the App");
			}
			alertDialogBuilder.create().show();
		} else if (v == maxMarksSelect) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());

			// Setting Dialog Title
			alertDialogBuilder.setTitle("Enter Max marks");

			final EditText input = new EditText(context);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			alertDialogBuilder.setView(input);

			alertDialogBuilder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								maxMarks = Integer.parseInt(input.getText()
										.toString());
							} catch (NumberFormatException e) {
							}
							marksSubmit.setVisibility(View.GONE);

							if (maxMarks > 0) {
								maxMarksSelect.setError(null);
								marksSubmit.setVisibility(View.VISIBLE);
								studentsListView.setVisibility(View.VISIBLE);
								maxMarksSelect.setText("MM:" + maxMarks);
								dialog.dismiss();
							} else {
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
										getActivity());
								alertDialogBuilder.setTitle("Error");
								alertDialogBuilder
										.setMessage("Invalid Max Marks.");
								alertDialogBuilder.create().show();
							}
						}
					});
			alertDialogBuilder.create().show();
		} else if (v == marksSubmit) {
			if (marksMap.isEmpty()) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						getActivity());
				alertDialogBuilder.setTitle("Error");
				alertDialogBuilder.setMessage("Nothing to submit");
				alertDialogBuilder.create().show();
				return;
			}
			HttpPost request = getMarksPostRequest();
			HttpRequestHandler httpRequestHandler = new HttpRequestHandler(
					"submitMarks");
			httpRequestHandler.execute(request);
			progressDialog = ProgressDialog.show(context, "Please Wait",
					"Submitting marks to server");
			progressDialog.setCancelable(false);
		}
	}

	private HttpPost getMarksPostRequest() {
		HttpPost postRequest = new HttpPost(TAGS.MARKS_SUBMIT_URL);
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(TAGS.KEY_MAX_MARKS, String
				.valueOf(maxMarks)));
		urlParameters.add(new BasicNameValuePair(TAGS.KEY_SUBJECT_ID, String
				.valueOf(subjectId)));
		urlParameters.add(new BasicNameValuePair(TAGS.KEY_EXAM_ID, String
				.valueOf(examId)));
		urlParameters.add(new BasicNameValuePair(TAGS.KEY_MARKS_JSON,
				getMarksJson()));
		urlParameters.add(new BasicNameValuePair(TAGS.KEY_SECURITY_TOKEN,
				SharedPreferencesKey.getStringFromSharedPreferences(
						TAGS.KEY_SECURITY_TOKEN, "", context)));
		urlParameters.add(new BasicNameValuePair(TAGS.KEY_VOLUNTEER_ID,
				SharedPreferencesKey.getStringFromSharedPreferences(
						TAGS.KEY_VOLUNTEER_ID, "", context)));

		try {
			postRequest.setEntity(new UrlEncodedFormEntity(urlParameters,
					"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return postRequest;
	}

	private String getMarksJson() {
		JSONArray marksArray = new JSONArray();
		for (String studentId : marksMap.keySet()) {
			JSONObject marks = new JSONObject();
			try {
				marks.put(TAGS.KEYS_STUDENT_ID, studentId);
				marks.put(TAGS.KEY_MARKS_OBTAINED, marksMap.get(studentId));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			marksArray.put(marks);
		}
		return marksArray.toString();
	}

	private String[] getExamsList(ArrayList<Exam> exams) {
		String[] examsList = new String[listOfExams.size()];
		int i = 0;
		for (Exam exam : exams) {
			examsList[i] = exam.getExamType() + " " + exam.getExamDate();
			i++;
		}
		return examsList;
	}

	public String[] getCentreList(ArrayList<Centre> centres) {
		String[] centreList = new String[centres.size()];
		int i = 0;
		for (Centre centre : centres) {
			centreList[i] = centre.getCentreName();
			i++;
		}
		return centreList;
	}

	public String[] getClassList(ArrayList<StudentClass> classes) {
		String[] classList = new String[classes.size()];
		int i = 0;
		for (StudentClass studentClass : classes) {
			classList[i] = studentClass.getClassName();
			i++;
		}
		return classList;
	}

	public String[] getSubjectsList(ArrayList<Subject> subjects) {
		String[] subjectList = new String[subjects.size()];
		int i = 0;
		for (Subject subject : subjects) {
			subjectList[i] = subject.getSubjectName();
			i++;
		}
		return subjectList;
	}

	private class CustomTextChangeListener implements TextWatcher {
		private String studentId;

		public CustomTextChangeListener(String studentId) {
			this.studentId = studentId;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			marksMap.remove(studentId);
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			try {
				if (s.toString() != null) {
					int marks = Integer.parseInt(s.toString());
					if (marks > maxMarks) {
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								getActivity());
						alertDialogBuilder.setTitle("Error");
						alertDialogBuilder
								.setMessage("Marks can't be more than max marks");
						s.replace(0, s.length(), "0");
						alertDialogBuilder.create().show();					
					} else {
						marksMap.put(studentId, marks);
					}
				}
			} catch (NumberFormatException e) {
			}
		}
	}

	private class HttpRequestHandler extends
			AsyncTask<HttpUriRequest, String, JSONObject> {
		String type;

		public HttpRequestHandler(String string) {
			super();
			type = string;
		}

		@Override
		protected JSONObject doInBackground(HttpUriRequest... httprequests) {
			HttpUriRequest httpRequest = httprequests[0];
			HttpClient client = new DefaultHttpClient();
			client.getParams()
					.setParameter(
							CoreProtocolPNames.USER_AGENT,
							"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			httpRequest.setHeader(HTTP.CONTENT_TYPE,
					"application/x-www-form-urlencoded;charset=UTF-8");
			HttpResponse response = null;
			try {
				response = client.execute(httpRequest);
			} catch (ClientProtocolException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			if (response != null && response.getEntity() != null) {
				// Mark the report sent in the db by record
				if (response.getStatusLine().getStatusCode() == 200) {
					StringBuffer result = new StringBuffer();
					BufferedReader rd;
					try {
						rd = new BufferedReader(new InputStreamReader(response
								.getEntity().getContent()));
						String line = "";
						while ((line = rd.readLine()) != null) {
							result.append(line);
						}
						JSONObject mainJsonObj = new JSONObject(
								result.toString());
						return mainJsonObj;
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		protected void onPostExecute(JSONObject mainJsonObj) {
			try {
				progressDialog.dismiss();
				if (mainJsonObj != null
						&& mainJsonObj.getInt(TAGS.KEY_SUCCESS) == 1) {
					if (type.equals("getStudents")) {
						JSONArray students;

						students = mainJsonObj.getJSONObject(TAGS.KEY_DETAILS)
								.getJSONArray(TAGS.KEY_STUDENTS);
						studentsList = new ArrayList<Student>();
						for (int i = 0; i < students.length(); i++) {
							JSONObject student = students.getJSONObject(i);
							studentsList.add(new Student(student
									.getString(TAGS.KEYS_STUDENT_ID), student
									.getString(TAGS.KEYS_STUDENT_NAME), student
									.getInt(TAGS.KEYS_STUDENT_ROLLNO)));
						}
					} else if (type.equals("submitMarks")) {
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								getActivity());
						alertDialogBuilder.setTitle("Congrats!!");
						alertDialogBuilder
								.setMessage("Marks submitted successfully");
						alertDialogBuilder.create().show();
					}
				} else {
					if (type.equals("getStudents")) {
						studentsList = null;

						classSelect.setText("Select Class");
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								getActivity());
						alertDialogBuilder.setTitle("Error");
						alertDialogBuilder
								.setMessage("No students available at this centre for the mentioned class");
						alertDialogBuilder.create().show();

						subjectSelect.setVisibility(View.GONE);
						maxMarksSelect.setVisibility(View.GONE);
						marksSubmit.setVisibility(View.GONE);
					} else if (type.equals("submitMarks")
							&& mainJsonObj != null) {
						JSONArray failedStudentIds = mainJsonObj.getJSONObject(
								TAGS.KEY_DETAILS).getJSONArray(
								TAGS.KEY_FAILED_STUDENT_IDS);

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								getActivity());
						alertDialogBuilder.setTitle("Error");
						alertDialogBuilder
								.setMessage("Marks submission failed for some students with studentIds: "
										+ failedStudentIds.join(" :: "));
						alertDialogBuilder.create().show();
					}

				}
				populateView(studentsList);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
