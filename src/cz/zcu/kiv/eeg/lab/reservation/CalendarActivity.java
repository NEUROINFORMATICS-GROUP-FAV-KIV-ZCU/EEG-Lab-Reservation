package cz.zcu.kiv.eeg.lab.reservation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import cz.zcu.kiv.eeg.lab.reservation.data.Constants;
import cz.zcu.kiv.eeg.lab.reservation.data.ReservationData;

/**
 * 
 * @author Petr Miko
 * 
 */
public class CalendarActivity extends Activity {

	private static final String TAG = CalendarActivity.class.getSimpleName();
	private int year, month, day;
	private TextView dateLabel;
	private ReservationAdapter reservationAdapter;

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

		initList();
		updateDate();
	}

	private void initList() {

		reservationAdapter = (ReservationAdapter) getLastNonConfigurationInstance();
		if (reservationAdapter == null)
			reservationAdapter = new ReservationAdapter(this, R.layout.row, new ArrayList<ReservationData>());

		View header = getLayoutInflater().inflate(R.layout.header_row, null);
		ListView listView = (ListView) findViewById(R.id.list);
		listView.addHeaderView(header);
		listView.setAdapter(reservationAdapter);
	}

	private void updateReservations(final List<ReservationData> data) {

		Runnable updateData = new Runnable() {
			@Override
			public void run() {
				reservationAdapter.clear();

				for (ReservationData record : data) {
					reservationAdapter.add(record);
				}
				reservationAdapter.notifyDataSetChanged();
			}
		};

		runOnUiThread(updateData);
	}

	protected void updateDate() {
		// Oracle months are counted from zero instead of one
		dateLabel.setText(String.format("%d.%d.%d", day, month + 1, year));
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

	public void addRecordClick(View v) {
		Log.d(TAG, "Add new booking time chosen");
		Intent intent = new Intent(this, AddRecordActivity.class);
		Bundle b = new Bundle();
		b.putInt("year", year);
		b.putInt("month", month);
		b.putInt("day", day);
		intent.putExtras(b);
		startActivityForResult(intent, Constants.ADD_RECORD_FLAG);
	}

	public void chooseDateClick(View v) {
		Log.d(TAG, "Add new booking time chosen");
		DatePickerDialog datePicker = new DatePickerDialog(this, dateSetListener, year, month, day);
		datePicker.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (Constants.ADD_RECORD_FLAG): {
			if (resultCode == Activity.RESULT_OK) {
				// HACK just for testing of additions from AddRecordActivity
				// will be replaced with loading from REST web service, when
				// adding was OK.
				ReservationData record = (ReservationData) data.getExtras().get(Constants.ADD_RECORD_KEY);
				List<ReservationData> a = new ArrayList<ReservationData>();
				a.add(record);
				updateReservations(a);
			}
			break;
		}
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return reservationAdapter;
	}
}