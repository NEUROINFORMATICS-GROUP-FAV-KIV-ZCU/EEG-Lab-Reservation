package cz.zcu.kiv.eeg.lab.reservation;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Petr Miko
 * 
 */
public class CalendarActivity extends Activity implements OnClickListener {

	private static final String TAG = "CalendarActivity";
	private int year, month, day;
	private TextView dateLabel;

	private final OnDateSetListener dateSetListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			CalendarActivity.this.year = year;
			month = monthOfYear;
			day = dayOfMonth;

			updateDate();

		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "App started");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		dateLabel = (TextView) findViewById(R.id.dateLabel);
		updateDate();

		initButtons();
	}

	protected void updateDate() {
		// Oracle months are counted from zero instead of one
		dateLabel.setText(String.format("%d.%d.%d", day, month + 1, year));
	}

	private void initButtons() {
		Button addRecord = (Button) findViewById(R.id.addBookTime);
		Button chooseDate = (Button) findViewById(R.id.chooseDate);
		addRecord.setOnClickListener(this);
		chooseDate.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			Log.d(TAG, "Settings button pressed");
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			break;
		case R.id.about:
			showAbout();
			Log.d(TAG, "About button pressed");
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showAbout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.app_about_description).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addBookTime:
			Log.d(TAG, "Add new booking time chosen");
			Toast.makeText(this, R.string.main_add_time, Toast.LENGTH_SHORT).show();
			break;
		case R.id.chooseDate:
			Log.d(TAG, "Add new booking time chosen");
			DatePickerDialog datePicker = new DatePickerDialog(this, dateSetListener, year, month, day);
			datePicker.show();
			break;
		}

	}
}