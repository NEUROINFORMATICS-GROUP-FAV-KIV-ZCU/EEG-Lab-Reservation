package cz.zcu.kiv.eeg.lab.reservation;

import java.util.*;

import android.app.*;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import cz.zcu.kiv.eeg.lab.reservation.container.ReservationAdapter;
import cz.zcu.kiv.eeg.lab.reservation.data.*;
import cz.zcu.kiv.eeg.lab.reservation.service.FetchReservationsToDate;

/**
 * 
 * @author Petr Miko
 * 
 */
public class CalendarActivity extends ProgressActivity {

	private static final String TAG = CalendarActivity.class.getSimpleName();
	private int year, month, day;
	private TextView dateLabel;
	private ReservationAdapter reservationAdapter;
	private ProgressDialog wsProgressDialog;

	private final OnDateSetListener dateSetListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			CalendarActivity.this.year = year;
			month = monthOfYear;
			day = dayOfMonth;
			updateDate();
			updateData();
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "App started");
		setTitle(R.string.app_overview);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initData();
		updateDate();
	}

	private void initData() {

		Map<String, Object> lastState = (Map<String, Object>) getLastNonConfigurationInstance();
		if (lastState != null) {
			year = (Integer) lastState.get("year");
			month = (Integer) lastState.get("month");
			day = (Integer) lastState.get("day");
			reservationAdapter = (ReservationAdapter) lastState.get("reservations");
		} else {
			Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
			reservationAdapter = new ReservationAdapter(this, R.layout.row, new ArrayList<Reservation>());
			updateData();
		}
		dateLabel = (TextView) findViewById(R.id.dateLabel);

		View header = getLayoutInflater().inflate(R.layout.header_row, null);
		ListView listView = (ListView) findViewById(R.id.list);
		listView.addHeaderView(header);
		listView.setAdapter(reservationAdapter);
	}

	protected void updateDate() {
		// Oracle months are counted from zero instead of one
		dateLabel.setText(String.format("%d.%d.%d", day, month + 1, year));
	}

	private void updateData() {
		new FetchReservationsToDate(this, reservationAdapter).execute(day, month + 1, year);
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
		case R.id.refresh:
			updateData();
			Log.d(TAG, "Refresh data button pressed");
		}
		return super.onOptionsItemSelected(item);
	}

	private void showAbout() {
		showAlert(getString(R.string.app_about_description));
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
				Reservation record = (Reservation) data.getExtras().get(Constants.ADD_RECORD_KEY);
				reservationAdapter.add(record);
			}
			break;
		}
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		Map<String, Object> lastState = new HashMap<String, Object>();
		lastState.put("reservations", reservationAdapter);
		lastState.put("year", year);
		lastState.put("month", month);
		lastState.put("day", day);
		return lastState;
	}

	@Override
	public void changeProgress(final ProgressState messageType, final Message message) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				switch (messageType) {
				case RUNNING:
					wsProgressDialog = ProgressDialog.show(CalendarActivity.this, getString(R.string.working),
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