package se.lundakarnevalen.ticket.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.client.auth.oauth2.TokenResponseException;

import se.lundakarnevalen.ticket.GoogleAuthenticator;
import se.lundakarnevalen.ticket.db.AuthToken;
import se.lundakarnevalen.ticket.db.User;
import se.lundakarnevalen.ticket.logging.ErrorLogger;
import se.lundakarnevalen.ticket.logging.Logger;

@Path("/login")
public class Login extends Request {
	private final GoogleAuthenticator helper = new GoogleAuthenticator();

	@GET
	@PermitAll
	@Path("/google/url")
	@Produces("text/plain; charset=UTF-8")
	public Response getLoginUrl(@QueryParam("redirect") String redirect) {
		assertNotNull(redirect, 400);
		return status(200).entity(helper.buildLoginUrl(redirect)).build();
	}

	@GET
	@PermitAll
	@Path("/google/token")
	@Produces("text/html; charset=UTF-8")
	public Response validateLogin(@QueryParam("code") String authCode, @QueryParam("redirect") String redirect)
			throws IOException, JSONException, SQLException {
		try {
			JSONObject data = helper.getUserInfoJson(authCode, redirect);
			System.out.println(data.toString());
			if (!data.getBoolean("verified_email")) {
				throw new NotAuthorizedException("Email not verified");
			}
			String email = data.getString("email");
			User user = User.getByEmail(email);
			if (user == null) {
				System.err.println("Didn't find email for login: " + email);
				throw new NotAuthorizedException("Email not found");
			}
			String name = data.getString("name");
			if (name != null && !name.equals(user.getName())) {
				Logger.log("Updating name of " + email + " to " + name, Level.INFO);
				user.setName(name);
			}
			AuthToken token = AuthToken.issue(user);
			JSONObject response = new JSONObject();
			response.put("user", user.toJSON());
			response.put("token", token.getToken());
			return status(200).entity(response.toString()).build();
		} catch (TokenResponseException e) {
			ErrorLogger.getInstance().put(e);
			throw new NotAuthorizedException("Invalid token");
		}
	}
}
