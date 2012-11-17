package cz.zcu.kiv.eeg.lab.reservation.ui;

import java.util.Calendar;

import android.app.*;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import cz.zcu.kiv.eeg.lab.reservation.R;
import cz.zcu.kiv.eeg.lab.reservation.container.ReservationAdapter;
import cz.zcu.kiv.eeg.lab.reservation.data.Constants;
import cz.zcu.kiv.eeg.lab.reservation.data.Reservation;

public class AgendaFragment extends Fragment implements OnClickListener {

	private final static String TAG = AgendaFragment.class.getSimpleName();

	private static int year, month, day;
	private TextView dateLabel;

	private final OnDateSetListener dateSetListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			AgendaFragment.year = year;
			month = monthOfYear;
			day = dayOfMonth;
			updateDate();
			updateData();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	private void initButtons() {
		Button chooseDateButton = (Button) getActivity().findViewById(R.id.chooseDateButton);
		chooseDateButton.setOnClickListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.reservation_list, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);

		initButtons();
		initData(savedState);
		updateDate();
		if(!((ProgressActivity) getActivity()).progressOn)
			updateData();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("year", year);
		outState.putInt("month", month);
		outState.putInt("day", day);
	}

	private void initData(Bundle savedState) {

		if (savedState != null) {
			year = (Integer) savedState.get("year");
			month = (Integer) savedState.get("month");
			day = (Integer) savedState.get("day");
		} else {
			Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
		}
		dateLabel = (TextView) getActivity().findViewById(R.id.dateLabel);
	}

	protected void updateDate() {
		// Oracle months are counted from zero instead of one
		dateLabel.setText(String.format("%d.%d.%d", day, month + 1, year));
	}

	public void updateData() {
		ReservationListFragment listFragment = (ReservationListFragment) getFragmentManager().findFragmentById(
				R.id.reservation_list);
		listFragment.update(day, month + 1, year);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.reservation_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addRecord:
			Log.d(TAG, "Add new booking time chosen");
			Intent intent = new Intent(getActivity(), AddRecordActivity.class);
			Bundle b = new Bundle();
			b.putInt("year", year);
			b.putInt("month", month);
			b.putInt("day", day);
			intent.putExtras(b);
			startActivityForResult(intent, Constants.ADD_RECORD_FLAG);
			break;
		case R.id.settings:
			Log.d(TAG, "Settings button pressed");
			Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
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
		((ProgressActivity) getActivity()).showAlert(getString(R.string.app_about_description));
	}

	public void chooseDateClick(View v) {
		Log.d(TAG, "Add new booking time chosen");
		DatePickerDialog datePicker = new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
		datePicker.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (Constants.ADD_RECORD_FLAG): {
			if (resultCode == Activity.RESULT_OK) {
				ReservationListFragment listFragment = (ReservationListFragment) getFragmentManager().findFragmentById(
						R.id.reservation_list);
				Reservation record = (Reservation) data.getExtras().get(Constants.ADD_RECORD_KEY);
				((ReservationAdapter) listFragment.getListAdapter()).add(record);
			}
			break;
		}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chooseDateButton:
			chooseDateClick(v);
			break;
		}

	}

}
