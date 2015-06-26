package org.sankalpnitjamshedpur;

import org.apache.http.HttpResponse;
import org.sankalpnitjamshedpur.db.RegistrationStage;

import android.content.Context;

public interface UserAuthenticationActivity {
	
	public void onRequestResult(HttpResponse httpResponse, RegistrationStage registrationStage);
	
	public Context getApplicationContext();

}
