package com.example.apppp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends Activity implements OnClickListener{
	Button loginButton;
	Button registerButton;
	EditText name, rollNo, batch, email, password;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_page);
		registerButton = (Button) findViewById(R.id.registerButton);
		name = (EditText) findViewById(R.id.name);
		rollNo = (EditText) findViewById(R.id.rollNo);
		email = (EditText) findViewById(R.id.email);
		batch = (EditText) findViewById(R.id.batch);
		password = (EditText) findViewById(R.id.password);
		registerButton.setOnClickListener(this);		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == registerButton) {
			String username = name.getText().toString();
			
			Toast.makeText(getApplicationContext(), "Hi " + username + "!! Registration Success!!!",
					Toast.LENGTH_LONG).show();
			
			try {
				startActivity(new Intent(this, Class.forName("com.example.apppp.HomePage")));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
