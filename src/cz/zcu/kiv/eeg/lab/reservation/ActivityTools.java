package cz.zcu.kiv.eeg.lab.reservation;

import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

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
				wsProgressDialog = ProgressDialog.show(context, context.getString(R.string.working), (String) msg.obj,
						true, true);
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

		if (messageCode == Constants.MSG_ERROR) {
			if (body instanceof Exception) {
				msg.obj = getErrorMessage((Exception) body);
			} else {
				msg.obj = body;
			}
		} else {
			msg.obj = body;
		}
		messageHandler.sendMessage(msg);
	}

	private String getErrorMessage(Exception exception) {

		if (exception instanceof HttpClientErrorException) {
			HttpClientErrorException httpException = (HttpClientErrorException) exception;

			if (HttpStatus.BAD_REQUEST.equals(httpException.getStatusCode())) {
				return context.getString(R.string.http_400);
			} else if (HttpStatus.UNAUTHORIZED.equals(httpException.getStatusCode())) {
				return context.getString(R.string.http_401);
			} else if (HttpStatus.FORBIDDEN.equals(httpException.getStatusCode())) {
				return context.getString(R.string.http_403);
			} else if (HttpStatus.NOT_FOUND.equals(httpException.getStatusCode())) {
				return context.getString(R.string.http_404);
			} else if (HttpStatus.METHOD_NOT_ALLOWED.equals(httpException.getStatusCode())) {
				return context.getString(R.string.http_405);
			} else if (HttpStatus.REQUEST_TIMEOUT.equals(httpException.getStatusCode())) {
				return context.getString(R.string.http_408);
			} else if (HttpStatus.INTERNAL_SERVER_ERROR.equals(R.string.http_500)) {
				return context.getString(R.string.http_500);
			} else if (HttpStatus.SERVICE_UNAVAILABLE.equals(httpException.getStatusCode())) {
				return context.getString(R.string.http_503);
			}
		}

		if (exception.getCause() instanceof UnknownHostException)
			return context.getString(R.string.error_unknown_host);

		return exception.getLocalizedMessage();
	}

	public boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}
}
