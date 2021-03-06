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
import cz.zcu.kiv.eeg.lab.reservation.service.ProgressService;
import cz.zcu.kiv.eeg.lab.reservation.utils.ConnectionUtils;

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
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		if (header != null)
			getListView().addHeaderView(header);

		View detailsFrame = getActivity().findViewById(R.id.details);
		isDualView = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

		if (savedInstanceState != null) {
			cursorPosition = savedInstanceState.getInt("cursorPos", 0);
		}

		if (isDualView) {
			getListView().setSelector(R.drawable.list_selector);
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			if (cursorPosition >= HEADER_ROW) {
				showDetails(cursorPosition);
				this.setSelection(cursorPosition);
			}
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
		if (pos >= HEADER_ROW && pos <= dataAdapter.getCount()) {
			showDetails(pos);
			this.setSelection(pos);
		} else {
			DetailsFragment details = (DetailsFragment) getFragmentManager().findFragmentById(R.id.details);
			if (details != null && details.isVisible()) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
				ft.remove(details);
				ft.commit();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("cursorPos", cursorPosition);
	}

	/**
	 * Helper function to show the details of a selected item, either by displaying a fragment in-place in the current UI, or starting a whole new
	 * activity in which it is displayed.
	 */
	void showDetails(int index) {
		cursorPosition = index;

		ReservationAdapter dataAdapter = getAdapter();
		if (dataAdapter != null && !dataAdapter.isEmpty())
			if (isDualView) {
				getListView().setItemChecked(index, true);

				DetailsFragment oldDetails = (DetailsFragment) getFragmentManager().findFragmentById(R.id.details);
				DetailsFragment details = new DetailsFragment();
				FragmentTransaction ft = getFragmentManager().beginTransaction();

				if (oldDetails == null) {
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				} else {
					ft.detach(oldDetails);
					ft.remove(oldDetails);
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				}
				Bundle args = new Bundle();
				args.putInt("index", index);
				args.putSerializable("data", dataAdapter.getItem(index - HEADER_ROW));
				details.setArguments(args);

				ft.replace(R.id.details, details, DetailsFragment.TAG);
				ft.commit();

			} else {
				Intent intent = new Intent();
				intent.setClass(getActivity(), DetailsActivity.class);
				intent.putExtra("index", index);
				intent.putExtra("data", dataAdapter.getItem(index - HEADER_ROW));
				startActivity(intent);
			}
	}

	public void update(int day, int month, int year) {

		ProgressActivity activity = (ProgressActivity) getActivity();

		if (ConnectionUtils.isOnline(activity)) {
			activity.service = (ProgressService<?, ?, ?>) new FetchReservationsToDate(activity, getAdapter()).execute(day, month, year);
		} else
			activity.showAlert(activity.getString(R.string.error_offline));
	}

	private ReservationAdapter getAdapter() {
		if (dataAdapter == null)
			dataAdapter = new ReservationAdapter(getActivity(), R.layout.row, new ArrayList<Reservation>());

		return dataAdapter;
	}

}
