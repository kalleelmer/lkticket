package se.lundakarnevalen.ticket.api.desk;

import io.swagger.annotations.Api;
import org.json.JSONException;
import org.json.JSONObject;
import se.lundakarnevalen.ticket.api.PublicPerformances;
import se.lundakarnevalen.ticket.db.Performance;
import se.lundakarnevalen.ticket.db.Profile;
import se.lundakarnevalen.ticket.db.User;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Api(value = "Desk")
@Path("/desk/performances")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class DeskPerformances extends PublicPerformances {
	@GET
	@Path("/{id}/profiles/{profile_id}/availability")
	public Response getAvailability(@PathParam("id") int id, @PathParam("profile_id") int profile_id,
			@Context ContainerRequestContext context) throws SQLException, JSONException {
		User user = User.getSingle((Integer) context.getProperty("user_id"));
		Profile profile = Profile.getSingle(profile_id);
		profile.assertAccess(user);
		JSONObject availability = Performance.availability(id, profile_id);
		return status(200).entity(availability.toString()).build();
	}
}
