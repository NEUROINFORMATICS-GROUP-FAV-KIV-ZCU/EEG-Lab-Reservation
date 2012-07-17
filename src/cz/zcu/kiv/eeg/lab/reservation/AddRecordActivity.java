package cz.zcu.kiv.eeg.lab.reservation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import cz.zcu.kiv.eeg.lab.reservation.container.ResearchGroupAdapter;
import cz.zcu.kiv.eeg.lab.reservation.data.Constants;
import cz.zcu.kiv.eeg.lab.reservation.data.ResearchGroup;
import cz.zcu.kiv.eeg.lab.reservation.data.Reservation;

public class AddRecordActivity extends Activity {

	private static final String TAG = AddRecordActivity.class.getSimpleName();

	private int year, month, day, fromHour, fromMinute, toHour, toMinute;
	private ArrayAdapter<ResearchGroup> researchGroupAdapter;

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

		researchGroupAdapter = new ResearchGroupAdapter(this, R.layout.group_row, new ArrayList<ResearchGroup>());
		Spinner groupList = (Spinner) findViewById(R.id.groupList);
		groupList.setAdapter(researchGroupAdapter);

		researchGroupAdapter.add(new ResearchGroup(1, "Test group"));
		researchGroupAdapter.add(new ResearchGroup(2, "Test group 2"));
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

	private final OnTimeSetListener fromListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			fromHour = hourOfDay;
			fromMinute = minute;

			TextView fromField = (TextView) findViewById(R.id.fromField);
			fromField.setText(String.format("%02d:%02d", fromHour, fromMinute));

		}
	};

	private final OnTimeSetListener toListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			toHour = hourOfDay;
			toMinute = minute;

			TextView toField = (TextView) findViewById(R.id.toField);
			toField.setText(String.format("%02d:%02d", toHour, toMinute));

		}
	};

	public void fromTimeClick(View v) {
		TimePickerDialog fromDialog = new TimePickerDialog(this, fromListener, fromHour, fromMinute, true);
		fromDialog.show();
	}

	public void toTimeClick(View v) {
		TimePickerDialog toDialog = new TimePickerDialog(this, toListener, toHour, toMinute, true);
		toDialog.show();
	}

	public void addRecordClick(View v) {
		Intent resultIntent = new Intent();

		try {
			SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			Date fromDate = sf.parse(String.format("%02d.%02d.%04d %02d:%02d", day, month, year, fromHour, fromMinute));
			Date toDate = sf.parse(String.format("%02d.%02d.%04d %02d:%02d", day, month, year, toHour, toMinute));

			if (fromDate.getTime() >= toDate.getTime()) {
				throw new Exception(getString(R.string.error_date_comparison));
			}
			ResearchGroup group = (ResearchGroup) ((Spinner) findViewById(R.id.groupList)).getSelectedItem();

			// HACK group adding hard coded, will be filled in accordance
			// to login into REST WS
			Reservation record = new Reservation(group.getResearchGroupName(), fromDate, toDate);
			// TODO REST server validation
			resultIntent.putExtra(Constants.ADD_RECORD_KEY, record);
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			Toast errorMsg = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
			errorMsg.show();
		}
	}
}
