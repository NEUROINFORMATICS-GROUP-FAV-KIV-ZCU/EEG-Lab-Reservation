package cz.zcu.kiv.eeg.lab.reservation.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import android.annotation.SuppressLint;
import android.app.*;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.res.Resources.NotFoundException;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import cz.zcu.kiv.eeg.lab.reservation.R;
import cz.zcu.kiv.eeg.lab.reservation.container.ResearchGroupAdapter;
import cz.zcu.kiv.eeg.lab.reservation.data.ResearchGroup;
import cz.zcu.kiv.eeg.lab.reservation.service.CreateReservation;
import cz.zcu.kiv.eeg.lab.reservation.service.FetchResearchGroups;
import cz.zcu.kiv.eeg.lab.reservation.service.ProgressService;
import cz.zcu.kiv.eeg.lab.reservation.service.data.ReservationData;
import cz.zcu.kiv.eeg.lab.reservation.utils.ConnectionUtils;

@SuppressLint("SimpleDateFormat")
public class AddRecordActivity extends SaveDiscardActivity{

	private static final String TAG = AddRecordActivity.class.getSimpleName();

	private int year, month, day, fromHour, fromMinute, toHour, toMinute;
	private ResearchGroupAdapter researchGroupAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Add new record activity loaded");
		super.onCreate(savedInstanceState);
		
		Bundle b = getIntent().getExtras();
		year = b.getInt("year");
		month = b.getInt("month") + 1;
		day = b.getInt("day");
		setTitle(String.format("%d.%d.%d - %s", day, month, year, getString(R.string.app_add_record)));

		initFields();
		updateData();
	}

	private void initFields() {
		TextView fromField = (TextView) findViewById(R.id.fromField);
		TextView toField = (TextView) findViewById(R.id.toField);

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm");

		fromHour = toHour = c.get(Calendar.HOUR_OF_DAY);
		fromMinute = toMinute = c.get(Calendar.MINUTE);

		fromField.setText(sf.format(c.getTime()));
		toField.setText(sf.format(c.getTime()));

		researchGroupAdapter = new ResearchGroupAdapter(this, R.layout.group_row, new ArrayList<ResearchGroup>());
		Spinner groupList = (Spinner) findViewById(R.id.groupList);
		groupList.setAdapter(researchGroupAdapter);
	}

	private void updateData() {
		if (ConnectionUtils.isOnline(this)) {
			service = (ProgressService<?, ?, ?>) new FetchResearchGroups(this, researchGroupAdapter).execute();
		} else
			showAlert(getString(R.string.error_offline));
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

	public void save() {

		if (ConnectionUtils.isOnline(this)) {

			SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			try {
				Date fromDate = sf.parse(String.format("%02d.%02d.%04d %02d:%02d", day, month, year, fromHour,
						fromMinute));
				Date toDate = sf.parse(String.format("%02d.%02d.%04d %02d:%02d", day, month, year, toHour, toMinute));

				if (fromDate.getTime() >= toDate.getTime()) {
					Toast.makeText(this, R.string.error_date_comparison, Toast.LENGTH_SHORT).show();
					return;
				}

				ResearchGroup group = (ResearchGroup) ((Spinner) findViewById(R.id.groupList)).getSelectedItem();
				ReservationData record = new ReservationData();

				record.setResearchGroupId(group.getResearchGroupId());
				record.setResearchGroup(group.getResearchGroupName());
				record.setFromTime(sf.format(fromDate));
				record.setToTime(sf.format(toDate));
				record.setCanRemove(true);

				new CreateReservation(this).execute(record);
			} catch (NotFoundException e) {
				Log.d(TAG, e.getLocalizedMessage(), e);
				Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			} catch (ParseException e) {
				Log.d(TAG, e.getLocalizedMessage(), e);
				Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			}
		} else {
			showAlert(getString(R.string.error_offline));
		}
	}
	

	@Override
	protected void discard() {
		finish();
	}
}
