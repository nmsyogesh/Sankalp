package org.sankalpnitjamshedpur.tabs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sankalpnitjamshedpur.R;
import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.entity.Centre;
import org.sankalpnitjamshedpur.entity.Student;
import org.sankalpnitjamshedpur.entity.StudentClass;
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
	Button centreSelect, classSelect, marksSubmit;
	int centreId, classId;
	ScrollView studentsList;
	ProgressDialog progressDialog;
	HashMap<String, Integer> marksMap = new HashMap<String, Integer>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		android = inflater.inflate(R.layout.fragment_marks_entry, container,
				false);
		context = android.getContext();
		centreSelect = (Button) android.findViewById(R.id.centreSelect);
		classSelect = (Button) android.findViewById(R.id.classSelect);
		studentsList = (ScrollView) android.findViewById(R.id.studentsList);
		marksSubmit = (Button) android.findViewById(R.id.submitMarks);
		studentsList.removeAllViews();
		centreSelect.setOnClickListener(this);
		centreSelect.setText("Select a centre");
		classSelect.setText("Select a class");
		classSelect.setOnClickListener(this);
		marksSubmit.setOnClickListener(this);
		marksSubmit.setVisibility(View.GONE);
		classSelect.setVisibility(View.GONE);
		dbHandler = new DatabaseHandler(context);

		return android;
	}

	public void populateView(ArrayList<Student> students) {
		studentsList.removeAllViews();

		if (students == null || students.isEmpty()) {
			TextView tv = new TextView(context);
			tv.setText("Sorry No Students found \n Consider choosing another combination of class and centre!!");
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			studentsList.addView(tv);
			marksSubmit.setVisibility(View.GONE);
			return;
		}
		TableLayout tableLayout = new TableLayout(context);
		tableLayout.addView(insertHeaderRow(), new TableLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		for (Student student : students) {
			tableLayout.addView(getTableRow(student),
					new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
		}
		studentsList.addView(tableLayout);
	}

	private View insertHeaderRow() {
		TableRow tr_head = new TableRow(context);
		tr_head.setBackgroundResource(R.drawable.rectangle);
		tr_head.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		TextView rollNo = new TextView(context);
		rollNo.setText(String.valueOf("Roll No"));
		rollNo.setPadding(5, 5, 5, 5);
		tr_head.addView(rollNo, new TableRow.LayoutParams(0,
				TableRow.LayoutParams.WRAP_CONTENT, 1f));

		rollNo = new TextView(context);
		rollNo.setText(String.valueOf("Name"));
		tr_head.addView(rollNo, new TableRow.LayoutParams(0,
				TableRow.LayoutParams.WRAP_CONTENT, 1f));

		rollNo = new TextView(context);
		rollNo.setText(String.valueOf("Marks"));
		tr_head.addView(rollNo, new TableRow.LayoutParams(0,
				TableRow.LayoutParams.WRAP_CONTENT, 1f));

		return tr_head;
	}

	TableRow getTableRow(Student student) {
		TableRow tr_head = new TableRow(context);
		tr_head.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		TextView rollNo = new TextView(context);
		rollNo.setText(String.valueOf(student.getRollNo()));
		rollNo.setPadding(5, 5, 5, 5);
		tr_head.addView(rollNo, new TableRow.LayoutParams(0,
				TableRow.LayoutParams.WRAP_CONTENT, 1f));

		TextView name = new TextView(context);
		name.setText(student.getStudentname());
		name.setPadding(5, 5, 5, 5);
		tr_head.addView(name, new TableRow.LayoutParams(0,
				TableRow.LayoutParams.WRAP_CONTENT, 1f));

		EditText marks = new EditText(context);
		marks.addTextChangedListener(new CustomTextChangeListener(student
				.getStudentId()));
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(5, 5, 5, 5);
		marks.setLayoutParams(layoutParams);
		marks.setInputType(InputType.TYPE_CLASS_NUMBER);
		tr_head.addView(marks, new TableRow.LayoutParams(0,
				TableRow.LayoutParams.WRAP_CONTENT, 1f));
		return tr_head;
	}

	@Override
	public void onClick(View v) {
		if (v == centreSelect) {
			centreSelect.setError(null);
			classSelect.setVisibility(View.GONE);
			classSelect.setText("Select a class");
			studentsList.removeAllViews();
			marksSubmit.setVisibility(View.GONE);
			final ArrayList<Centre> listOfCentres = dbHandler
					.getListOfCentres();
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());

			// Setting Dialog Title
			alertDialogBuilder.setTitle("Choose Centre");

			alertDialogBuilder.setSingleChoiceItems(
					getCentreList(listOfCentres), 0, null).setPositiveButton(
					"OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							if (listOfCentres != null
									&& !listOfCentres.isEmpty()) {
								Centre centre = listOfCentres
										.get(((AlertDialog) dialog)
												.getListView()
												.getCheckedItemPosition());
								centreId = centre.getCentreId();
								classSelect.setVisibility(View.VISIBLE);
								centreSelect.setText("Centre: "
										+ centre.getCentreName());
							}
						}
					});

			alertDialogBuilder.create().show();
		} else if (v == classSelect) {
			marksMap.clear();
			classSelect.setError(null);
			studentsList.removeAllViews();
			marksSubmit.setVisibility(View.GONE);
			final ArrayList<StudentClass> listOfClasses = dbHandler
					.getListOfClasses();
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());

			// Setting Dialog Title
			alertDialogBuilder.setTitle("Choose Class");

			alertDialogBuilder.setSingleChoiceItems(
					getClassList(listOfClasses), 0, null).setPositiveButton(
					"OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							if (listOfClasses != null
									&& !listOfClasses.isEmpty()) {
								StudentClass studentClass = listOfClasses
										.get(((AlertDialog) dialog)
												.getListView()
												.getCheckedItemPosition());
								classId = studentClass.getClassId();
								classSelect.setText(studentClass.getClassName());
								marksSubmit.setVisibility(View.VISIBLE);
								HttpRequestHandler httpRequestHandler = new HttpRequestHandler(
										"getStudents");
								httpRequestHandler.execute(new HttpGet(
										TAGS.STUDENTS_LIST_URL + "?centreId="
												+ centreId + "&classId="
												+ classId));
								progressDialog = ProgressDialog.show(context,
										"Please Wait",
										"Fetching list of students");
								progressDialog.setCancelable(true);
							}
						}
					});

			alertDialogBuilder.create().show();
		} else if(v==marksSubmit) {
			String display="";
			for(String key: marksMap.keySet()) {
				display += key + " " + marksMap.get(key) + "\n";
			}
			Toast.makeText(context,display, Toast.LENGTH_LONG).show();
		}
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

	private class CustomTextChangeListener implements TextWatcher {
		private String studentId;

		public CustomTextChangeListener(String studentId) {
			this.studentId = studentId;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			try {
				if (s.toString() != null) {
					marksMap.put(studentId, Integer.parseInt(s.toString()));
				}
			} catch (NumberFormatException e) {
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
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
				if (mainJsonObj.getInt(TAGS.KEY_SUCCESS) == 1) {
					if (type.equals("getStudents")) {
						progressDialog.dismiss();
						JSONArray students;

						students = mainJsonObj.getJSONObject(TAGS.KEY_DETAILS)
								.getJSONArray(TAGS.KEY_STUDENTS);
						ArrayList<Student> studentsList = new ArrayList<Student>();
						for (int i = 0; i < students.length(); i++) {
							JSONObject student = students.getJSONObject(i);
							studentsList.add(new Student(student
									.getString(TAGS.KEYS_STUDENT_ID), student
									.getString(TAGS.KEYS_STUDENT_NAME), student
									.getInt(TAGS.KEYS_STUDENT_ROLLNO)));
						}
						populateView(studentsList);
					}
				} else {
					progressDialog.dismiss();
					populateView(null);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
