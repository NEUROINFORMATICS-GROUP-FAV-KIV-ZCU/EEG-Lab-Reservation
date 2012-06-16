package cz.zcu.kiv.eeg.lab.reservation;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class AddRecordActivity extends Activity {

	private static final String TAG = AddRecordActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Add new record activity loaded");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_record);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		initFields();
	}

	private void initFields() {
		TextView dateField = (TextView) findViewById(R.id.dateField);
		TextView fromField = (TextView) findViewById(R.id.fromField);
		TextView toField = (TextView) findViewById(R.id.toField);

		Bundle b = getIntent().getExtras();
		int year = b.getInt("year");
		int month = b.getInt("month");
		int day = b.getInt("day");

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
		dateField.setText(String.format("%d.%d.%d", day, month + 1, year));

		fromField.setText(sf.format(c.getTime()));
		toField.setText(sf.format(c.getTime()));
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
