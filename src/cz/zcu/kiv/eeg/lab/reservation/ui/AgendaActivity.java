package cz.zcu.kiv.eeg.lab.reservation.ui;

import android.os.*;
import android.util.Log;
import cz.zcu.kiv.eeg.lab.reservation.R;

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
}