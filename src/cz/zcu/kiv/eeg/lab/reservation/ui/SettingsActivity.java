package cz.zcu.kiv.eeg.lab.reservation.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.*;
import android.util.Log;
import android.widget.TextView;
import cz.zcu.kiv.eeg.lab.reservation.R;
import cz.zcu.kiv.eeg.lab.reservation.data.Constants;
import cz.zcu.kiv.eeg.lab.reservation.service.ProgressService;
import cz.zcu.kiv.eeg.lab.reservation.service.TestCredentials;
import cz.zcu.kiv.eeg.lab.reservation.utils.ConnectionUtils;
import cz.zcu.kiv.eeg.lab.reservation.utils.ValidationUtils;

public class SettingsActivity extends SaveDiscardActivity {

	private static final String TAG = SettingsActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Settings screen");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		SharedPreferences credentials = getSharedPreferences(Constants.PREFS_CREDENTIALS, Context.MODE_PRIVATE);
		CharSequence username = credentials.getString("username", null);
		CharSequence password = credentials.getString("password", null);
		CharSequence url = credentials.getString("url", "https://");
		TextView usernameField = (TextView) findViewById(R.id.settings_username_field);
		TextView passwordField = (TextView) findViewById(R.id.settings_password_field);
		TextView urlField = (TextView) findViewById(R.id.settings_url_field);
		usernameField.setText(username);
		passwordField.setText(password);
		urlField.setText(url);
	}

	public void save() {

		TextView usernameField = (TextView) findViewById(R.id.settings_username_field);
		TextView passwordField = (TextView) findViewById(R.id.settings_password_field);
		TextView urlField = (TextView) findViewById(R.id.settings_url_field);

		testCredentials(usernameField.getText().toString(), passwordField.getText().toString(), urlField.getText().toString());
	}
	

	@Override
	protected void discard() {
		finish();
	}

	private void testCredentials(String username, String password, String url) {

		if (!ConnectionUtils.isOnline(this)) {
			showAlert(getString(R.string.error_offline));
			return;
		}

		SharedPreferences credentials = getSharedPreferences(Constants.PREFS_CREDENTIALS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = credentials.edit();

		StringBuilder error = new StringBuilder();

		if (ValidationUtils.isUsernameFormatInvalid(username))
			error.append(getString(R.string.error_invalid_username)).append('\n');
		if (ValidationUtils.isPasswordFormatInvalid(password))
			error.append(getString(R.string.error_invalid_password)).append('\n');
		if (ValidationUtils.isUrlFormatInvalid(url))
			error.append(getString(R.string.error_invalid_url)).append('\n');

		if (error.toString().isEmpty()) {

			if (url != null && !url.endsWith("/"))
				url += "/";

			editor.putString("tmp_username", username.toString());
			editor.putString("tmp_password", password.toString());
			editor.putString("tmp_url", url);
			editor.commit();

			service = (ProgressService<?, ?, ?>) new TestCredentials(this, true).execute();
		} else {
			showAlert(error.toString());
		}
	}
}
