package cz.zcu.kiv.eeg.lab.reservation.service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import cz.zcu.kiv.eeg.lab.reservation.container.ReservationAdapter;
import cz.zcu.kiv.eeg.lab.reservation.data.Reservation;
import cz.zcu.kiv.eeg.lab.reservation.service.data.ReservationData;
import cz.zcu.kiv.eeg.lab.reservation.service.data.ReservationDataList;
import cz.zcu.kiv.eeg.lab.reservation.service.ssl.HttpsClient;

public class FetchReservationsToDate extends AsyncTask<Integer, Void, List<ReservationData>> {

	private static final String TAG = FetchReservationsToDate.class.getSimpleName();

	private String username = "username";
	private String password = "password";

	private Context context;
	private ReservationAdapter reservationAdapter;

	public FetchReservationsToDate(Context context, ReservationAdapter reservationAdapter) {
		this.context = context;
		this.reservationAdapter = reservationAdapter;
	}

	@Override
	protected List<ReservationData> doInBackground(Integer... params) {
		String url;
		//HACK URL is hardcoded for now, will be changed in upcomming updates
		if (params.length == 3) {
			url = "https://147.228.64.172:8443/EEGDatabase/webservice/reservation/" + params[0] + "-" + params[1] + "-"
					+ params[2];
		} else {
			Log.e(TAG, "Invalid params count! Must be 3 in order of year, month, day");
			Toast.makeText(context, "Invalid params count! Max. is 2", Toast.LENGTH_SHORT).show();
			return Collections.emptyList();
		}

		// Populate the HTTP Basic Authentication header with the username and
		// password
		HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAuthorization(authHeader);
		requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));

		// Create a new RestTemplate instance
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpsClient.getNewHttpClient()));
		restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

		try {
			// Make the network request
			Log.d(TAG, url);
			ResponseEntity<ReservationDataList> response = restTemplate.exchange(url, HttpMethod.GET,
					new HttpEntity<Object>(requestHeaders), ReservationDataList.class);
			ReservationDataList body = response.getBody();

			if (body != null) {
				return body.getReservations();
			}

		} catch (HttpClientErrorException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
			Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		}
		return Collections.emptyList();
	}

	@Override
	protected void onPostExecute(List<ReservationData> resultList) {
		reservationAdapter.clear();
		if (resultList != null && !resultList.isEmpty())
			for (ReservationData res : resultList) {
				try {
					SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy hh:mm");
					Date fromTime = sf.parse(res.getFromTime());
					Date toTime = sf.parse(res.getToTime());
					Reservation reservation = new Reservation(res.getResearchGroup(), fromTime, toTime);
					reservationAdapter.add(reservation);
				} catch (Exception e) {
					Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
					Log.e(TAG, e.getLocalizedMessage(), e);
				}
			}
	}
}
