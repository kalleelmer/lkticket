package se.lundakarnevalen.ticket.api;

import java.sql.SQLException;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import se.lundakarnevalen.ticket.db.User;

@Path("/users")
public class UserRequest extends Request {

	@GET
	@Path("/current")
	@RolesAllowed("USER")
	@Produces("application/json; charset=UTF-8")
	public Response getCurrent(@Context ContainerRequestContext context) throws SQLException, JSONException {
		System.out.println("Requesting current user");
		int userID = (Integer) context.getProperty("user_id");
		User user = User.getSingle(userID);
		return status(200).entity(user.toJSON().toString()).build();
	}
}
