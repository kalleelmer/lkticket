package se.lundakarnevalen.ticket.api.html;

import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import se.lundakarnevalen.ticket.GoogleAuthenticator;
import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.AuthToken;
import se.lundakarnevalen.ticket.db.User;

@Path("/login/google")
public class GoogleLogin extends Request {
	private final GoogleAuthenticator helper = new GoogleAuthenticator();

	@GET
	@PermitAll
	@Produces("text/html; charset=UTF-8")
	public Response getLoginPage() {
		return status(200).entity("<a href=\"" + helper.buildLoginUrl() + "\">Login</a>").build();
	}

	@GET
	@PermitAll
	@Path("/validate")
	@Produces("text/html; charset=UTF-8")
	public Response validateLogin(@QueryParam("code") String authCode) throws IOException, JSONException, SQLException {
		JSONObject data = helper.getUserInfoJson(authCode);
		if (!data.getBoolean("verified_email")) {
			throw new NotAuthorizedException("Email not verified");
		}
		String email = data.getString("email");
		User user = User.getByEmail(email);
		if (user == null) {
			System.err.println("Didn't find email for login: " + email);
			throw new NotAuthorizedException("Email not found");
		}
		AuthToken token = AuthToken.issue(user);
		JSONObject response = new JSONObject();
		response.put("user", user.toJSON());
		response.put("token", token.getToken());
		return status(200).entity(response.toString()).build();
	}
}
