package cz.zcu.kiv.eeg.lab.reservation;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddRecordActivity extends Activity implements OnClickListener {

	private static final String TAG = AddRecordActivity.class.getSimpleName();

	private int year, month, day, fromHour, fromMinute, toHour, toMinute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Add new record activity loaded");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_record);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Bundle b = getIntent().getExtras();
		year = b.getInt("year");
		month = b.getInt("month") + 1;
		day = b.getInt("day");

		initFields();
		initButtons();
	}

	private void initButtons() {
		Button changeFrom = (Button) findViewById(R.id.fromTimeButton);
		Button changeTo = (Button) findViewById(R.id.toTimeButton);

		changeFrom.setOnClickListener(this);
		changeTo.setOnClickListener(this);
	}

	private void initFields() {
		TextView dateField = (TextView) findViewById(R.id.dateField);
		TextView fromField = (TextView) findViewById(R.id.fromField);
		TextView toField = (TextView) findViewById(R.id.toField);

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
		dateField.setText(String.format("%d.%d.%d", day, month, year));

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

	private OnTimeSetListener fromListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			fromHour = hourOfDay;
			fromMinute = minute;

			TextView fromField = (TextView) findViewById(R.id.fromField);
			fromField.setText(String.format("%02d:%02d", fromHour, fromMinute));

		}
	};

	private OnTimeSetListener toListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			toHour = hourOfDay;
			toMinute = minute;

			TextView toField = (TextView) findViewById(R.id.toField);
			toField.setText(String.format("%02d:%02d", toHour, toMinute));

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fromTimeButton:
			TimePickerDialog fromDialog = new TimePickerDialog(this,
					fromListener, fromHour, fromMinute, true);
			fromDialog.show();
			break;
		case R.id.toTimeButton:
			TimePickerDialog toDialog = new TimePickerDialog(this, toListener,
					toHour, toMinute, true);
			toDialog.show();
			break;
		}

	}
}
