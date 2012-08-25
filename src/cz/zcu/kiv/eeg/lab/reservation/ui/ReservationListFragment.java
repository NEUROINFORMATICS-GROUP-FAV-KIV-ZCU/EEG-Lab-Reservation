package cz.zcu.kiv.eeg.lab.reservation.ui;

import java.util.ArrayList;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.*;
import android.widget.ListView;
import cz.zcu.kiv.eeg.lab.reservation.R;
import cz.zcu.kiv.eeg.lab.reservation.container.ReservationAdapter;
import cz.zcu.kiv.eeg.lab.reservation.data.Reservation;
import cz.zcu.kiv.eeg.lab.reservation.service.FetchReservationsToDate;

public class ReservationListFragment extends ListFragment {

	// private final String TAG = ReservationListFragment.class.getSimpleName();

	private View header = null;
	private static ReservationAdapter dataAdapter = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setListAdapter(null);
		if (header != null)
			getListView().addHeaderView(header);
		if (dataAdapter == null)
			dataAdapter = new ReservationAdapter(getActivity(), R.layout.row, new ArrayList<Reservation>());

		setListAdapter(dataAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		header = inflater.inflate(R.layout.header_row, null);
		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {
		showDetails(pos);
	}

	/**
	 * Helper function to show the details of a selected item, either by
	 * displaying a fragment in-place in the current UI, or starting a whole new
	 * activity in which it is displayed.
	 */
	void showDetails(int index) {
	}

	public void update(int day, int month, int year) {
		new FetchReservationsToDate((ProgressActivity) getActivity(), dataAdapter).execute(day, month, year);
	}

}
