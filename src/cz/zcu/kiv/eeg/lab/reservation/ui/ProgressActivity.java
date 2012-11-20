package cz.zcu.kiv.eeg.lab.reservation.ui;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cz.zcu.kiv.eeg.lab.reservation.R;
import cz.zcu.kiv.eeg.lab.reservation.data.ProgressState;
import cz.zcu.kiv.eeg.lab.reservation.service.ProgressService;

public abstract class ProgressActivity extends Activity {

	private volatile ProgressDialog wsProgressDialog;
	protected volatile boolean progressOn = false;
	private String progressTitle;
	private String progressMessage;
	protected ProgressService<?,?,?> service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {

			synchronized (ProgressActivity.class) {
				progressOn = savedInstanceState.getBoolean("progressOn", false);
			}

			if (progressOn) {
				service = (ProgressService<?,?,?>) savedInstanceState.get("service");
				if (service.isActive()) {
					progressTitle = savedInstanceState.getString("progressTitle");
					progressMessage = savedInstanceState.getString("progressMessage");
					wsProgressDialog = ProgressDialog.show(ProgressActivity.this, progressTitle, progressMessage, true, false);
				}
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
				synchronized (ProgressActivity.class) {
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
						if (wsProgressDialog != null && wsProgressDialog.isShowing()) {
							wsProgressDialog.dismiss();
							wsProgressDialog = null;
						}
						progressOn = false;
						service = null;
						break;
					case ERROR:
						showAlert(message.obj.toString());
						service = null;
					default:
						break;
					}
				}
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		synchronized (ProgressActivity.class) {
			super.onSaveInstanceState(outState);
			if (service != null && service.isActive()) {
				outState.putSerializable("service", service);
				outState.putBoolean("progressOn", progressOn);
				outState.putString("progressTitle", progressTitle);
				outState.putString("progressMessage", progressMessage);
			}
		}
	}

}
