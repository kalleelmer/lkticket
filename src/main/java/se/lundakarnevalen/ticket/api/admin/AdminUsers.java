package se.lundakarnevalen.ticket.api.admin;

import org.json.JSONException;
import org.json.JSONObject;
import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.User;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/admin/users")
@RolesAllowed("ADMIN")
@Produces("application/json; charset=UTF-8")
public class AdminUsers extends Request {

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

	@POST
	public Response createUser(String data) throws JSONException, SQLException {
		JSONObject input = new JSONObject(data);
		String email = input.getString("email");
		User user = User.createUser(email);
		return status(200).entity(user).build();
	}
}
