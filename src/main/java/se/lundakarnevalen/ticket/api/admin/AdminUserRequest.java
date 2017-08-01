package se.lundakarnevalen.ticket.api.admin;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.User;

@Path("/admin/users")
@RolesAllowed("ADMIN")
@Produces("application/json; charset=UTF-8")
public class AdminUserRequest extends Request {

	@GET
	public Response getAll() throws SQLException, JSONException {
		List<User> users = User.getAll();
		return status(200).entity(users).build();
	}

	@GET
	@Path("/{id}")
	public Response getSingle(@PathParam("id") int id) throws SQLException, JSONException {
		User user = User.getSingle(id);
		return status(200).entity(user).build();
	}
}
