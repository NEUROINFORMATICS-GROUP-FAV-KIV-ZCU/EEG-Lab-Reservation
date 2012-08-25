package cz.zcu.kiv.eeg.lab.reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import android.app.*;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.res.Resources.NotFoundException;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import cz.zcu.kiv.eeg.lab.reservation.container.ResearchGroupAdapter;
import cz.zcu.kiv.eeg.lab.reservation.data.ProgressState;
import cz.zcu.kiv.eeg.lab.reservation.data.ResearchGroup;
import cz.zcu.kiv.eeg.lab.reservation.service.CreateReservation;
import cz.zcu.kiv.eeg.lab.reservation.service.FetchResearchGroups;
import cz.zcu.kiv.eeg.lab.reservation.service.data.ReservationData;

public class AddRecordActivity extends ProgressActivity {

	private static final String TAG = AddRecordActivity.class.getSimpleName();

	private int year, month, day, fromHour, fromMinute, toHour, toMinute;
	private ResearchGroupAdapter researchGroupAdapter;
	private ProgressDialog wsProgressDialog;

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
		new FetchResearchGroups(this, researchGroupAdapter).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.save_discard_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
		case R.id.menuDiscard:
			finish();
			break;
		case R.id.menuSave:
			addRecord();
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

	public void addRecord() {

		SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		try {
			Date fromDate = sf.parse(String.format("%02d.%02d.%04d %02d:%02d", day, month, year, fromHour, fromMinute));
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

			new CreateReservation(this).execute(record);
		} catch (NotFoundException e) {
			Log.d(TAG, e.getLocalizedMessage(), e);
			Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		} catch (ParseException e) {
			Log.d(TAG, e.getLocalizedMessage(), e);
			Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void changeProgress(final ProgressState messageType, final Message message) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				switch (messageType) {
				case RUNNING:
					wsProgressDialog = ProgressDialog.show(AddRecordActivity.this, getString(R.string.working),
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
