package cz.zcu.kiv.eeg.lab.reservation.ui;

import java.util.ArrayList;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ListView;
import cz.zcu.kiv.eeg.lab.reservation.R;
import cz.zcu.kiv.eeg.lab.reservation.container.ReservationAdapter;
import cz.zcu.kiv.eeg.lab.reservation.data.Reservation;
import cz.zcu.kiv.eeg.lab.reservation.service.FetchReservationsToDate;

public class ReservationListFragment extends ListFragment {

	// private final String TAG = ReservationListFragment.class.getSimpleName();

	private boolean isDualView;
	private int cursorPosition;

	private View header = null;
	private static ReservationAdapter dataAdapter = null;

	private final static int HEADER_ROW = 1;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setListAdapter(null);
		if (header != null)
			getListView().addHeaderView(header);

		View detailsFrame = getActivity().findViewById(R.id.details);
		isDualView = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

		if (savedInstanceState != null) {
			cursorPosition = savedInstanceState.getInt("cursorPos", 0);
		}

		if (isDualView) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			if (cursorPosition >= HEADER_ROW)
				showDetails(cursorPosition);
		}

		setListAdapter(getAdapter());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		header = inflater.inflate(R.layout.header_row, null);
		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {
		if (pos >= HEADER_ROW)
			showDetails(pos);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("cursorPos", cursorPosition);
	}

	/**
	 * Helper function to show the details of a selected item, either by
	 * displaying a fragment in-place in the current UI, or starting a whole new
	 * activity in which it is displayed.
	 */
	void showDetails(int index) {
		cursorPosition = index;

		ReservationAdapter dataAdapter = getAdapter();
		if (dataAdapter != null && !dataAdapter.isEmpty())
			if (isDualView) {
				getListView().setItemChecked(index, true);

				DetailsFragment details = (DetailsFragment) getFragmentManager().findFragmentById(R.id.details);
				if (details == null || details.getShownIndex() != index) {
					details = new DetailsFragment();

					Bundle args = new Bundle();
					args.putInt("index", index);
					args.putSerializable("data", dataAdapter.getItem(index - HEADER_ROW));
					details.setArguments(args);

					FragmentTransaction ft = getFragmentManager().beginTransaction();
					ft.replace(R.id.details, details, DetailsFragment.TAG);
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					ft.commit();
				}

			} else {
				Intent intent = new Intent();
				intent.setClass(getActivity(), DetailsActivity.class);
				intent.putExtra("index", index);
				intent.putExtra("data", dataAdapter.getItem(index - HEADER_ROW));
				startActivity(intent);
			}
	}

	public void update(int day, int month, int year) {
		new FetchReservationsToDate((ProgressActivity) getActivity(), getAdapter()).execute(day, month, year);
	}

	private ReservationAdapter getAdapter() {
		if (dataAdapter == null)
			dataAdapter = new ReservationAdapter(getActivity(), R.layout.row, new ArrayList<Reservation>());

		return dataAdapter;
	}

}
