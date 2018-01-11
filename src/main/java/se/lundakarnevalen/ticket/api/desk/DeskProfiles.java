package se.lundakarnevalen.ticket.api.desk;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.Profile;

@Path("/desk/profiles")
@RolesAllowed("ADMIN")
@Produces("application/json; charset=UTF-8")
public class DeskProfiles extends Request {
	@GET
	@Path("/mine")
	public Response getAll(@Context ContainerRequestContext context) throws SQLException {
		List<Profile> profiles = Profile.getByUser((Integer) context.getProperty("user_id"));
		return status(200).entity(profiles).build();
	}
}
