package cz.zcu.kiv.eeg.lab.reservation.container;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import cz.zcu.kiv.eeg.lab.reservation.R;
import cz.zcu.kiv.eeg.lab.reservation.data.Reservation;

/**
 * Custom class of ArrayAdapter. Used for viewing ReservationData records in
 * ListView.
 * 
 * @author Petr Miko - miko.petr (at) gmail.com
 * 
 */
public class ReservationAdapter extends ArrayAdapter<Reservation> implements OnClickListener{
	
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
			Button removeButton = (Button) row.findViewById(R.id.removeButton);
			if (topText != null) {
				topText.setText(sf.format(record.getFromTime()) + " – " + sf.format(record.getToTime()));
			}
			if (additionalText != null) {
				additionalText.setText(record.getResearchGroup());
			}
			
//			if(!record.getCanRemove()){
//				removeButton.setEnabled(false);
//				removeButton.setActivated(false);
//			}else{
//				removeButton.setTag(record);
//				removeButton.setOnClickListener(this);
//			}
		}
		return row;
	}

	@Override
	public void onClick(View v) {

		if(v.getTag() instanceof Reservation){
			Reservation reservation = (Reservation) v.getTag();
			
			Log.d(TAG, "Clicked on remove record: " + reservation.getFromTime().toString() + " | " + reservation.getToTime().toString());
		}
		
	}
}
