package cz.zcu.kiv.eeg.lab.reservation.container;

import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cz.zcu.kiv.eeg.lab.reservation.R;
import cz.zcu.kiv.eeg.lab.reservation.data.Reservation;
import cz.zcu.kiv.eeg.lab.reservation.service.RemoveReservation;
import cz.zcu.kiv.eeg.lab.reservation.service.data.ReservationData;
import cz.zcu.kiv.eeg.lab.reservation.ui.ProgressActivity;

/**
 * Custom class of ArrayAdapter. Used for viewing ReservationData records in ListView.
 * 
 * @author Petr Miko - miko.petr (at) gmail.com
 * 
 */
@SuppressLint("SimpleDateFormat")
public class ReservationAdapter extends ArrayAdapter<Reservation> implements OnClickListener {

	private final static String TAG = ReservationAdapter.class.getSimpleName();

	private final Context context;
	private final int resourceId;

	public ReservationAdapter(Context context, int resourceId, List<Reservation> items) {
		super(context, resourceId, items);
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
		Reservation record = getItem(position);
		if (record != null) {
			SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
			TextView topText = (TextView) row.findViewById(R.id.toptext);
			TextView additionalText = (TextView) row.findViewById(R.id.bottomtext);
			ImageView removeButton = (ImageView) row.findViewById(R.id.removeButton);
			if (topText != null) {
				topText.setText(sf.format(record.getFromTime()) + " – " + sf.format(record.getToTime()));
			}
			if (additionalText != null) {
				additionalText.setText(record.getResearchGroup());
			}

			if (!record.getCanRemove()) {
				removeButton.setEnabled(false);
				removeButton.setVisibility(View.INVISIBLE);
			} else {
				removeButton.setEnabled(true);
				removeButton.setVisibility(View.VISIBLE);
				removeButton.setTag(record);
				removeButton.setOnClickListener(this);
			}
		}
		return row;
	}

	@Override
	public void onClick(View v) {

		if (v.getTag() instanceof Reservation) {
			final Reservation reservation = (Reservation) v.getTag();

			Log.d(TAG, "Clicked on remove record: " + reservation.getFromTime().toString() + " | " + reservation.getToTime().toString());

			new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert).setTitle(context.getString(R.string.dialog_remove_header))
					.setMessage(context.getString(R.string.dialog_remove_body))
					.setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ReservationData data = new ReservationData(reservation.getReservationId(), reservation.getResearchGroupId(), reservation.getResearchGroup().toString(),
									reservation.getStringFromTime(), reservation.getStringToTime(), reservation.getCanRemove());
							new RemoveReservation((ProgressActivity) context).execute(data);
						}

					}).setNegativeButton(context.getString(android.R.string.cancel), null).show();
		}

	}
}
