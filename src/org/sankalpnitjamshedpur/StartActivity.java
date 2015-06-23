package org.sankalpnitjamshedpur;

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
		loginButton = (Button) findViewById(R.id.loginActivity);
		registrationButton = (Button) findViewById(R.id.registerActivity);
		loginButton.setOnClickListener(this);
		registrationButton.setOnClickListener(this);		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == loginButton) {
			try {
				startActivity(new Intent(this, Class.forName("org.sankalpnitjamshedpur.LoginActivity")));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if(v == registrationButton) {
			try {
				startActivity(new Intent(this, Class.forName("org.sankalpnitjamshedpur.RegistrationActivity")));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
