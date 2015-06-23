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

public class LoginActivity extends Activity implements OnClickListener {
	Button loginButton;
	EditText emailVolunteerId, password;
	DatabaseHandler dbHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);
		dbHandler = new DatabaseHandler(this);
		emailVolunteerId = (EditText) findViewById(R.id.emailVolunteerId);
		password = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		User dbUser = null;
		if (v == loginButton) {
			String emailVolunteerIdText = emailVolunteerId.getText().toString();
			String passwordText = password.getText().toString();

			if (emailVolunteerIdText.contains("@")) // It is a email ID
			{
				dbUser = dbHandler.getContactByEmailId(emailVolunteerIdText);
			} // It is a VolunteerId
			else {
				dbUser = dbHandler
						.getContactByVolunteerId(emailVolunteerIdText);
			}

			try {
				if (dbUser != null && dbUser.getPassword().equals(passwordText)) {
					Toast.makeText(getApplicationContext(), "LOGIN Success!!!",
							Toast.LENGTH_LONG).show();

					startActivity(new Intent(this,
							Class.forName("org.sankalpnitjamshedpur.HomePage")));

				} else if (dbUser != null) {
					Toast.makeText(getApplicationContext(), "Wrong Password",
							Toast.LENGTH_LONG).show();

					startActivity(new Intent(
							this,
							Class.forName("org.sankalpnitjamshedpur.LoginActivity")));
				} else {
					Toast.makeText(getApplicationContext(), "Please register",
							Toast.LENGTH_LONG).show();
					
					startActivity(new Intent(
							this,
							Class.forName("org.sankalpnitjamshedpur.RegistrationActivity")));
				}

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
