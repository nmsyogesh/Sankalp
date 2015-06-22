package com.example.apppp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StartActivity extends Activity implements OnClickListener{
	Button loginButton;
	Button registrationButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_activity);
		loginButton = (Button) findViewById(R.id.login);
		registrationButton = (Button) findViewById(R.id.register);
		loginButton.setOnClickListener(this);
		registrationButton.setOnClickListener(this);		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == loginButton) {
			try {
				startActivity(new Intent(this, Class.forName("com.example.apppp.LoginActivity")));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if(v == registrationButton) {
			try {
				startActivity(new Intent(this, Class.forName("com.example.apppp.RegistrationActivity")));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
