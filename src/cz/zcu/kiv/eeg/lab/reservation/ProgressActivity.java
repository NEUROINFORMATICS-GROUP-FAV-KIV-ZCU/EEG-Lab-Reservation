package cz.zcu.kiv.eeg.lab.reservation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Message;
import cz.zcu.kiv.eeg.lab.reservation.data.ProgressState;

public abstract class ProgressActivity extends Activity {

	public abstract void changeProgress(ProgressState messageType, Message message);

	public void showAlert(String alert) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(alert).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}
}