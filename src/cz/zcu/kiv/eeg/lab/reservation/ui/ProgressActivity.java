package cz.zcu.kiv.eeg.lab.reservation.ui;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cz.zcu.kiv.eeg.lab.reservation.R;
import cz.zcu.kiv.eeg.lab.reservation.data.ProgressState;

public abstract class ProgressActivity extends Activity {

	private volatile ProgressDialog wsProgressDialog;
	private boolean progressOn = false;
	private String progressTitle;
	private String progressMessage;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			progressOn = savedInstanceState.getBoolean("progressOn", false);

			if (progressOn) {
				progressTitle = savedInstanceState.getString("progressTitle");
				progressMessage = savedInstanceState.getString("progressMessage");
				wsProgressDialog = ProgressDialog.show(ProgressActivity.this, progressTitle, progressMessage, true, false);
			}
		}
	}

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

	@Override
	protected void onPause() {
		if (wsProgressDialog != null && wsProgressDialog.isShowing()) {
			wsProgressDialog.dismiss();
		}
		super.onPause();
	}
	
	public void changeProgress(final ProgressState messageType, final Message message) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				switch (messageType) {
				case RUNNING:
					progressOn = true;
					if (wsProgressDialog != null && !wsProgressDialog.isShowing())
						wsProgressDialog.show();
					else {
						progressTitle = getString(R.string.working);
						progressMessage = (String) message.obj;
						wsProgressDialog = ProgressDialog.show(ProgressActivity.this, progressTitle, progressMessage, true, false);
					}
					break;
				case INACTIVE:
				case DONE:
					if (wsProgressDialog != null && wsProgressDialog.isShowing()){
						wsProgressDialog.dismiss();
						wsProgressDialog = null;
					}
					progressOn = false;
					break;
				case ERROR:
					showAlert(message.obj.toString());
				default:
					break;
				}
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("progressOn", progressOn);
		outState.putString("progressTitle", progressTitle);
		outState.putString("progressMessage", progressMessage);
	}

}
