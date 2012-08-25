package cz.zcu.kiv.eeg.lab.reservation;

import android.app.ProgressDialog;
import android.os.*;
import android.util.Log;
import cz.zcu.kiv.eeg.lab.reservation.data.ProgressState;

/**
 * 
 * @author Petr Miko
 * 
 */
public class AgendaActivity extends ProgressActivity {

	private static final String TAG = AgendaActivity.class.getSimpleName();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "App started");
		setTitle(R.string.app_overview);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	public void changeProgress(final ProgressState messageType, final Message message) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				switch (messageType) {
				case RUNNING:
					wsProgressDialog = ProgressDialog.show(AgendaActivity.this, getString(R.string.working),
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