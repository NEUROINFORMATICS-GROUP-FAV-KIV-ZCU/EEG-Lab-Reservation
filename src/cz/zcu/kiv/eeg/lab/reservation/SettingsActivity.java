package cz.zcu.kiv.eeg.lab.reservation;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import cz.zcu.kiv.eeg.lab.reservation.data.Constants;
import cz.zcu.kiv.eeg.lab.reservation.service.TestCredentials;

public class SettingsActivity extends Activity {

	private static final String TAG = SettingsActivity.class.getSimpleName();

	private ActivityTools activityTools = new ActivityTools(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Settings screen");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		SharedPreferences credentials = getSharedPreferences(Constants.PREFS_CREDENTIALS, Context.MODE_PRIVATE);
		CharSequence username = credentials.getString("username", null);
		CharSequence password = credentials.getString("password", null);
		CharSequence url = credentials.getString("url", "http://");
		TextView usernameField = (TextView) findViewById(R.id.settings_username_field);
		TextView passwordField = (TextView) findViewById(R.id.settings_password_field);
		TextView urlField = (TextView) findViewById(R.id.settings_url_field);
		usernameField.setText(username);
		passwordField.setText(password);
		urlField.setText(url);
	}

	public void loginClick(View v) {
		SharedPreferences credentials = getSharedPreferences(Constants.PREFS_CREDENTIALS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = credentials.edit();

		TextView usernameField = (TextView) findViewById(R.id.settings_username_field);
		TextView passwordField = (TextView) findViewById(R.id.settings_password_field);
		TextView urlField = (TextView) findViewById(R.id.settings_url_field);

		String url = urlField.getText().toString();

		if (url != null && !url.endsWith("/"))
			url += "/";

		editor.putString("tmp_username", usernameField.getText().toString());
		editor.putString("tmp_password", passwordField.getText().toString());
		editor.putString("tmp_url", url);
		editor.commit();

		testCredentials();
	}

	private void testCredentials() {
		new TestCredentials(activityTools).execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
