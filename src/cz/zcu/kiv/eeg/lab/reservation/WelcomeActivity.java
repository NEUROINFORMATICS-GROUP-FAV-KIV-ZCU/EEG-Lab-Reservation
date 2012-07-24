package cz.zcu.kiv.eeg.lab.reservation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.TextView;
import cz.zcu.kiv.eeg.lab.reservation.data.Constants;
import cz.zcu.kiv.eeg.lab.reservation.service.TestCredentials;

public class WelcomeActivity extends Activity {

	private final static String TAG = WelcomeActivity.class.getSimpleName();

	private ActivityTools activityTools = new ActivityTools(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Welcome activity displayed");
		setContentView(R.layout.welcome);

		TextView urlField = (TextView) findViewById(R.id.settings_url_field);
		urlField.setText("https://");
	}

	public void loginClick(View v) {

		TextView usernameField = (TextView) findViewById(R.id.settings_username_field);
		TextView passwordField = (TextView) findViewById(R.id.settings_password_field);
		TextView urlField = (TextView) findViewById(R.id.settings_url_field);

		testCredentials(usernameField.getText().toString(), passwordField.getText().toString(), urlField.getText()
				.toString());
	}

	private void testCredentials(String username, String password, String url) {

		SharedPreferences credentials = getSharedPreferences(Constants.PREFS_CREDENTIALS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = credentials.edit();

		StringBuilder error = new StringBuilder();

		if (username == null || username.isEmpty())
			error.append(getString(R.string.error_empty_username)).append('\n');
		else if (!activityTools.isEmailValid(username))
			error.append(getString(R.string.error_invalid_username)).append('\n');
		if (password == null || password.isEmpty())
			error.append(getString(R.string.error_empty_password)).append('\n');
		if (url == null || url.isEmpty())
			error.append(getString(R.string.error_empty_url)).append('\n');
		else if (!URLUtil.isValidUrl(url) || "http://".equals(url) || "https://".equals(url))
			error.append(getString(R.string.error_invalid_url)).append('\n');

		if (error.toString().isEmpty()) {

			if (url != null && !url.endsWith("/"))
				url += "/";

			editor.putString("tmp_username", username.toString());
			editor.putString("tmp_password", password.toString());
			editor.putString("tmp_url", url);
			editor.commit();

			new TestCredentials(activityTools, true).execute();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(error.toString()).setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			builder.create().show();
		}
	}

}
