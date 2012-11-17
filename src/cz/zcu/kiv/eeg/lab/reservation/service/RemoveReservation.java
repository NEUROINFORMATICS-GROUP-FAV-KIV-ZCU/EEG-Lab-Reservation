package cz.zcu.kiv.eeg.lab.reservation.service;

import static cz.zcu.kiv.eeg.lab.reservation.data.ProgressState.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;

import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import cz.zcu.kiv.eeg.lab.reservation.R;
import cz.zcu.kiv.eeg.lab.reservation.data.Constants;
import cz.zcu.kiv.eeg.lab.reservation.data.Reservation;
import cz.zcu.kiv.eeg.lab.reservation.service.data.ReservationData;
import cz.zcu.kiv.eeg.lab.reservation.service.ssl.HttpsClient;
import cz.zcu.kiv.eeg.lab.reservation.ui.ProgressActivity;

public class RemoveReservation extends ProgressService<ReservationData, Void, Boolean> {

	private static final String TAG = RemoveReservation.class.getSimpleName();
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	private ReservationData data;

	public RemoveReservation(ProgressActivity context) {
		super(context);
	}

	@Override
	protected Boolean doInBackground(ReservationData... params) {

		data = params[0];

		// will be fixed properly in future
		if (data == null)
			return false;

		try {

			changeProgress(RUNNING, R.string.working_ws_remove);

			SharedPreferences credentials = getCredentials();
			String username = credentials.getString("username", null);
			String password = credentials.getString("password", null);
			String url = credentials.getString("url", null) + "reservation/";

			HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setAuthorization(authHeader);
			requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
			requestHeaders.setContentType(MediaType.APPLICATION_XML);
			HttpEntity<ReservationData> entity = new HttpEntity<ReservationData>(data, requestHeaders);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpsClient.getNewHttpClient()));
			restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

			Log.d(TAG, url);
			restTemplate.delete(url,entity);
			return true;
		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage());
			changeProgress(ERROR, e);
		} finally {
			changeProgress(DONE, null);
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		if (success) 
			Toast.makeText(activity, activity.getString(R.string.reservation_removed), Toast.LENGTH_SHORT).show();
	}
}
