package cz.zcu.kiv.eeg.lab.reservation.service;

import java.util.Collections;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import cz.zcu.kiv.eeg.lab.reservation.ActivityTools;
import cz.zcu.kiv.eeg.lab.reservation.R;
import cz.zcu.kiv.eeg.lab.reservation.data.Constants;
import cz.zcu.kiv.eeg.lab.reservation.service.data.ResearchGroupDataList;
import cz.zcu.kiv.eeg.lab.reservation.service.ssl.HttpsClient;

public class TestCredentials extends AsyncTask<Void, Void, Boolean> {

	private final static String TAG = TestCredentials.class.getSimpleName();

	private ActivityTools tools;

	public TestCredentials(ActivityTools tools) {
		this.tools = tools;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		SharedPreferences credentials = tools.context.getSharedPreferences(Constants.PREFS_CREDENTIALS,
				Context.MODE_PRIVATE);
		String username = credentials.getString("tmp_username", null);
		String password = credentials.getString("tmp_password", null);
		String url = credentials.getString("tmp_url", null) + "groups";

		tools.sendMessage(Constants.MSG_WORKING_START, tools.context.getString(R.string.working_ws_login));
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
			tools.sendMessage(Constants.MSG_ERROR, e);
			return false;
		} finally {
			tools.sendMessage(Constants.MSG_WORKING_DONE, null);
		}
	}

	@Override
	protected void onPostExecute(Boolean resultList) {
		SharedPreferences credentials = tools.context.getSharedPreferences(Constants.PREFS_CREDENTIALS,
				Context.MODE_PRIVATE);
		if (resultList) {
			String username = credentials.getString("tmp_username", null);
			String password = credentials.getString("tmp_password", null);
			String url = credentials.getString("tmp_url", null);

			SharedPreferences.Editor editor = credentials.edit();
			editor.putString("username", username);
			editor.putString("password", password);
			editor.putString("url", url);
			editor.commit();

			Toast.makeText(tools.context, R.string.settings_saved, Toast.LENGTH_SHORT).show();
			((Activity) tools.context).finish();
		}
	}

}
