package org.sankalpnitjamshedpur;

import org.sankalpnitjamshedpur.db.DatabaseHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StartActivity extends Activity implements OnClickListener {
	Button loginButton;
	Button registrationButton;
	DatabaseHandler dbHandler;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_activity);
		context = this;
		dbHandler = new DatabaseHandler(this);
		loginButton = (Button) findViewById(R.id.loginActivity);
		registrationButton = (Button) findViewById(R.id.registerActivity);
		loginButton.setOnClickListener(this);
		registrationButton.setOnClickListener(this);		
	}

	@Override
	public void onClick(View v) {
		if (v == loginButton) {
			try {
				startActivity(new Intent(this,
						Class.forName("org.sankalpnitjamshedpur.LoginActivity")));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		} else if (v == registrationButton) {
			try {
				startActivity(new Intent(
						this,
						Class.forName("org.sankalpnitjamshedpur.RegistrationActivity")));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
