package cz.zcu.kiv.eeg.lab.reservation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import cz.zcu.kiv.eeg.lab.reservation.data.Constants;
import cz.zcu.kiv.eeg.lab.reservation.data.ProgressState;
import cz.zcu.kiv.eeg.lab.reservation.service.TestCredentials;
import cz.zcu.kiv.eeg.lab.reservation.utils.ValidationUtils;

public class WelcomeActivity extends ProgressActivity {

	private final static String TAG = WelcomeActivity.class.getSimpleName();
	private ProgressDialog wsProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Welcome activity displayed");
		setContentView(R.layout.welcome);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.login_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menuLogin:
			loginClick();
		}
		return super.onOptionsItemSelected(item);
	}

	public void loginClick() {

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

			new TestCredentials(this, true).execute();
		} else {
			showAlert(error.toString());
		}
	}

	@Override
	public void changeProgress(final ProgressState messageType, final Message message) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				switch (messageType) {
				case RUNNING:
					wsProgressDialog = ProgressDialog.show(WelcomeActivity.this, getString(R.string.working),
							(String) message.obj, true, true);
					break;
				case INACTIVE:
				case DONE:
					wsProgressDialog.dismiss();
					break;
				case ERROR:
					showAlert(message.obj.toString());
				default:
					break;
				}
			}
		});
	}
}
