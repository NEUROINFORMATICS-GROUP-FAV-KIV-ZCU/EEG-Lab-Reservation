package cz.zcu.kiv.eeg.lab.reservation.service;

import static cz.zcu.kiv.eeg.lab.reservation.data.ProgressState.*;

import java.util.Collections;

import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import cz.zcu.kiv.eeg.lab.reservation.*;
import cz.zcu.kiv.eeg.lab.reservation.service.data.ResearchGroupDataList;
import cz.zcu.kiv.eeg.lab.reservation.service.ssl.HttpsClient;
import cz.zcu.kiv.eeg.lab.reservation.ui.AgendaActivity;
import cz.zcu.kiv.eeg.lab.reservation.ui.ProgressActivity;

public class TestCredentials extends ProgressService<Void, Void, Boolean> {

	private final static String TAG = TestCredentials.class.getSimpleName();

	private boolean startupTest;

	public TestCredentials(ProgressActivity activity, boolean startupTest) {
		super(activity);
		this.startupTest = startupTest;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		SharedPreferences credentials = getCredentials();
		String username = credentials.getString("tmp_username", null);
		String password = credentials.getString("tmp_password", null);
		String url = credentials.getString("tmp_url", null) + "groups";

		changeProgress(RUNNING, R.string.working_ws_login);
		HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAuthorization(authHeader);
		requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
		HttpEntity<Object> entity = new HttpEntity<Object>(requestHeaders);

		// Create a new RestTemplate instance
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpsClient.getNewHttpClient()));
		restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

		try {
			// Make the network request
			Log.d(TAG, url);
			ResponseEntity<ResearchGroupDataList> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					ResearchGroupDataList.class);
			response.getBody();

			return true;

		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
			changeProgress(ERROR, e);
			return false;
		} finally {
			changeProgress(DONE, null);
		}
	}

	@Override
	protected void onPostExecute(Boolean verified) {
		SharedPreferences credentials = getCredentials();
		if (verified) {
			String username = credentials.getString("tmp_username", null);
			String password = credentials.getString("tmp_password", null);
			String url = credentials.getString("tmp_url", null);

			SharedPreferences.Editor editor = credentials.edit();
			editor.putString("username", username);
			editor.putString("password", password);
			editor.putString("url", url);
			editor.commit();

			Toast.makeText(activity, R.string.settings_saved, Toast.LENGTH_SHORT).show();

			if (startupTest)
				activity.startActivity(new Intent(activity, AgendaActivity.class));
			else
				activity.finish();
		}
	}

}
