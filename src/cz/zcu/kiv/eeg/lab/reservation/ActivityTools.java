package cz.zcu.kiv.eeg.lab.reservation;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import cz.zcu.kiv.eeg.lab.reservation.data.Constants;

public class ActivityTools {

	public Context context;

	public ActivityTools(Context context) {
		this.context = context;
	}

	private ProgressDialog wsProgressDialog;

	public Handler messageHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case Constants.MSG_WORKING_START:
				wsProgressDialog = ProgressDialog.show(context, context.getString(R.string.working), (String) msg.obj);
				break;
			case Constants.MSG_WORKING_DONE:
				wsProgressDialog.dismiss();
				break;
			case Constants.MSG_ERROR:
				Toast.makeText(context, (String) msg.obj, Toast.LENGTH_LONG).show();
			}
		}
	};

	public void sendMessage(int messageCode, Object body) {
		Message msg = Message.obtain();
		msg.what = messageCode;
		msg.obj = body;
		messageHandler.sendMessage(msg);
	}
}
