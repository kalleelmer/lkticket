package se.lundakarnevalen.ticket.api.admin;

import io.swagger.annotations.Api;
import se.lundakarnevalen.ticket.api.desk.DeskProfiles;
import se.lundakarnevalen.ticket.db.Profile;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Api(value = "Admin")
@Path("/admin/profiles")
@RolesAllowed("ADMIN")
@Produces("application/json; charset=UTF-8")
public class AdminProfiles extends DeskProfiles {
	@GET
	public Response getAll() throws SQLException {
		List<Profile> profiles = Profile.getAll();
		return status(200).entity(profiles).build();
	}
}
