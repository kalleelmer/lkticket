package se.lundakarnevalen.ticket.api.desk;

import io.swagger.annotations.Api;
import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.Location;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Api
@Path("/desk/locations")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class DeskLocations extends Request {
	@GET
	public Response getAll(@Context ContainerRequestContext context) throws SQLException {
		List<Location> locations = Location.getAll();
		return status(200).entity(locations).build();
	}
}
