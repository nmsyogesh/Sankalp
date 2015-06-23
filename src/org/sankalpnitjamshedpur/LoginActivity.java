package org.sankalpnitjamshedpur;

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
	EditText email, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);
		email = (EditText) findViewById(R.id.emailVolunteerId);
		password = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == loginButton) {
			try {
				Toast.makeText(getApplicationContext(), "LOGIN Success!!!",
						Toast.LENGTH_LONG).show();

				startActivity(new Intent(this,
						Class.forName("org.sankalpnitjamshedpur.HomePage")));

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
