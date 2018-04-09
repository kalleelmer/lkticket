package se.lundakarnevalen.ticket.api.admin;

import io.swagger.annotations.Api;
import org.json.JSONException;
import org.json.JSONObject;
import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.Profile;
import se.lundakarnevalen.ticket.db.User;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Api(value = "Admin")
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

	@PUT
	@Path("/{id}/profiles/{profile_id}")
	public Response addProfile(@PathParam("id") int id, @PathParam("profile_id") int profile_id)
			throws SQLException, JSONException {
		User user = User.getSingle(id);
		assertNotNull(user, 404);
		Profile profile = Profile.getSingle(profile_id);
		assertNotNull(profile, 400);
		user.addProfile(profile.id);
		return status(200).entity(profile).build();
	}

	@DELETE
	@Path("/{id}/profiles/{pid}")
	public Response removeProfile(@PathParam("id") int id, @PathParam("pid") int profile_id)
			throws SQLException, JSONException {
		User user = User.getSingle(id);
		assertNotNull(user, 404);
		Profile profile = Profile.getSingle(profile_id);
		assertNotNull(profile, 400);
		user.removeProfile(profile.id);
		return status(200).entity(profile).build();
	}
}
