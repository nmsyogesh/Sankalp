package org.sankalpnitjamshedpur;

import org.sankalpnitjamshedpur.db.DatabaseHandler;
import org.sankalpnitjamshedpur.entity.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends Activity implements OnClickListener {
	Button loginButton;
	Button registerButton;
	EditText nameBox, rollNoBox, batchBox, emailBox, passwordBox, mobileNoBox;
	DatabaseHandler dbHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_page);
		dbHandler = new DatabaseHandler(this);
		registerButton = (Button) findViewById(R.id.registerButton);
		nameBox = (EditText) findViewById(R.id.name);
		rollNoBox = (EditText) findViewById(R.id.rollNo);
		emailBox = (EditText) findViewById(R.id.email);
		batchBox = (EditText) findViewById(R.id.batch);
		passwordBox = (EditText) findViewById(R.id.password);
		mobileNoBox = (EditText) findViewById(R.id.mobileNo);
		registerButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == registerButton) {
			User dbUser = dbHandler.doesUserExists(new User(nameBox.getText()
					.toString(), rollNoBox.getText().toString(), emailBox
					.getText().toString(), Integer.parseInt(batchBox.getText()
					.toString()), passwordBox.getText().toString(), Long
					.parseLong(mobileNoBox.getText().toString())));

			if (dbUser != null) {
				Toast.makeText(getApplicationContext(), "User exists!!!",
						Toast.LENGTH_LONG).show();
			} else {
				dbHandler.addContact(new User(nameBox.getText().toString(),
						rollNoBox.getText().toString(), emailBox.getText()
								.toString(), Integer.parseInt(batchBox
								.getText().toString()), passwordBox.getText()
								.toString(), Long.parseLong(mobileNoBox
								.getText().toString())));
				Toast.makeText(getApplicationContext(),
						"Hi " + nameBox.getText().toString() + "!! Registration Success!!!",
						Toast.LENGTH_LONG).show();
			}

			try {
				startActivity(new Intent(this,
						Class.forName("org.sankalpnitjamshedpur.HomePage")));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
