package cz.zcu.kiv.eeg.lab.reservation;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class SettingsActivity extends Activity {

	private static final String TAG = "SettingsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Settings screen");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
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
