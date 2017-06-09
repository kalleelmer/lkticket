package se.lundakarnevalen.ticket.api.html;

import java.io.IOException;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import se.lundakarnevalen.ticket.GoogleAuthenticator;
import se.lundakarnevalen.ticket.api.Request;

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
	public Response validateLogin(@QueryParam("code") String authCode) throws IOException, JSONException {
		// TODO Issue token
		return status(200).entity(helper.getUserInfoJson(authCode).toString()).build();
	}
}
