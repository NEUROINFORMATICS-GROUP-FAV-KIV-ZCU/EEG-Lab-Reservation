package cz.zcu.kiv.eeg.lab.reservation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import cz.zcu.kiv.eeg.lab.reservation.data.Constants;
import cz.zcu.kiv.eeg.lab.reservation.data.ReservationData;

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
		Button save = (Button) findViewById(R.id.addRecordButton);

		changeFrom.setOnClickListener(this);
		changeTo.setOnClickListener(this);
		save.setOnClickListener(this);
	}

	private void initFields() {
		TextView dateField = (TextView) findViewById(R.id.dateField);
		TextView fromField = (TextView) findViewById(R.id.fromField);
		TextView toField = (TextView) findViewById(R.id.toField);

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
		dateField.setText(String.format("%d.%d.%d", day, month, year));

		fromHour = toHour = c.get(Calendar.HOUR_OF_DAY);
		fromMinute = toMinute = c.get(Calendar.MINUTE);

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

		case R.id.addRecordButton:

			Intent resultIntent = new Intent();

			try {
				SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
				Date fromDate = sf.parse(String.format(
						"%02d.%02d.%04d %02d:%02d", day, month, year, fromHour,
						fromMinute));
				Date toDate = sf.parse(String.format(
						"%02d.%02d.%04d %02d:%02d", day, month, year, toHour,
						toMinute));

				if (fromDate.getTime() >= toDate.getTime()) {
					throw new Exception(
							getString(R.string.error_date_comparison));
				}
				// HACK username is now hard coded, will be filled in accordance
				// to login into REST WS
				ReservationData record = new ReservationData("petrmiko",
						fromDate, toDate);
				// TODO REST server validation
				resultIntent.putExtra(Constants.ADD_RECORD_KEY, record);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				Toast errorMsg = Toast.makeText(this, e.getMessage(),
						Toast.LENGTH_SHORT);
				errorMsg.show();
			}
			break;
		}
	}
}
