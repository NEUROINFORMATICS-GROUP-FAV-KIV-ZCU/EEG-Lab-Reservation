package cz.zcu.kiv.eeg.lab.reservation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import cz.zcu.kiv.eeg.lab.reservation.data.Constants;

public class StartUpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences credentials = getSharedPreferences(Constants.PREFS_CREDENTIALS, Context.MODE_PRIVATE);
		CharSequence username = credentials.getString("username", null);
		CharSequence password = credentials.getString("password", null);

		if (username == null || password == null) {
			Intent welcomeIntent = new Intent(this, WelcomeActivity.class);
			startActivity(welcomeIntent);
		} else {
			Intent calendarIntent = new Intent(this, CalendarActivity.class);
			startActivity(calendarIntent);
		}

		finish();
	}

}
