package se.lundakarnevalen.ticket;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public final class GoogleAuthenticator {

	/**
	 * Please provide a value for the CLIENT_ID constant before proceeding, set
	 * this up at https://code.google.com/apis/console/
	 */
	private static final String CLIENT_ID = Environment.getProperty("auth.google.id");
	/**
	 * Please provide a value for the CLIENT_SECRET constant before proceeding,
	 * set this up at https://code.google.com/apis/console/
	 */
	private static final String CLIENT_SECRET = Environment.getProperty("auth.google.secret");

	private static final Collection<String> SCOPE = Arrays
			.asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email"
					.split(";"));
	private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	private String stateToken;

	private final GoogleAuthorizationCodeFlow flow;

	/**
	 * Constructor initializes the Google Authorization Code Flow with CLIENT
	 * ID, SECRET, and SCOPE
	 */
	public GoogleAuthenticator() {
		flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPE)
				.build();
		generateStateToken();
	}

	/**
	 * Builds a login URL based on client ID, secret, callback URI, and scope
	 */
	public String buildLoginUrl(String redirect) {

		final GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();

		return url.setRedirectUri(redirect).setState(stateToken).set("hd", "lundakarnevalen.lu.se").build();
	}

	/**
	 * Generates a secure state token
	 */
	private void generateStateToken() {

		SecureRandom sr1 = new SecureRandom();

		stateToken = "google;" + sr1.nextInt();

	}

	/**
	 * Accessor for state token
	 */
	public String getStateToken() {
		return stateToken;
	}

	/**
	 * Expects an Authentication Code, and makes an authenticated request for
	 * the user's profile information.
	 * 
	 * @return JSON formatted user profile information
	 * @param authCode
	 *            authentication code provided by google
	 * @throws JSONException
	 */
	public JSONObject getUserInfoJson(final String authCode, String redirect) throws IOException, JSONException {

		final GoogleTokenResponse response = flow.newTokenRequest(authCode).setRedirectUri(redirect).execute();
		final Credential credential = flow.createAndStoreCredential(response, null);
		final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
		final GenericUrl url = new GenericUrl(USER_INFO_URL);
		final HttpRequest request = requestFactory.buildGetRequest(url);
		request.getHeaders().setContentType("application/json");
		final String data = request.execute().parseAsString();

		return new JSONObject(data);

	}
}
