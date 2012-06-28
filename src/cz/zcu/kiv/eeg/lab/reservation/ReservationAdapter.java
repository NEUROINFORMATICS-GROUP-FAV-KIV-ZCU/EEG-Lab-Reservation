package cz.zcu.kiv.eeg.lab.reservation;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cz.zcu.kiv.eeg.lab.reservation.data.ReservationData;

/**
 * Custom class of ArrayAdapter. Used for viewing ReservationData records in
 * ListView.
 * 
 * @author Petr Miko - miko.petr (at) gmail.com
 * 
 */
public class ReservationAdapter extends ArrayAdapter<ReservationData> {
	// private final List<ReservationData> items;
	private final Context context;
	private final int resourceId;

	public ReservationAdapter(Context context, int resourceId, List<ReservationData> items) {
		super(context, resourceId, items);
		// this.items = items;
		this.context = context;
		this.resourceId = resourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(resourceId, parent, false);
		}
		ReservationData record = getItem(position);
		if (record != null) {
			SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
			TextView topText = (TextView) row.findViewById(R.id.toptext);
			TextView additionalText = (TextView) row.findViewById(R.id.bottomtext);
			if (topText != null) {
				topText.setText(sf.format(record.getFromTime()) + " – " + sf.format(record.getToTime()));
			}
			if (additionalText != null) {
				additionalText.setText(record.getResearchGroup());
			}
		}
		return row;
	}
}
