package org.sankalpnitjamshedpur;

import org.apache.http.HttpResponse;
import org.sankalpnitjamshedpur.db.RegistrationStage;

public interface UserAuthenticationActivity {
	
	public void onRequestResult(HttpResponse httpResponse, RegistrationStage registrationStage);

}
