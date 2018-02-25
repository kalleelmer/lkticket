package se.lundakarnevalen.ticket.api.desk;

import io.swagger.annotations.Api;
import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.Customer;
import se.lundakarnevalen.ticket.db.Profile;
import se.lundakarnevalen.ticket.db.Show;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Api
@Path("/desk/profiles")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class DeskProfiles extends Request {
	@GET
	@Path("/mine")
	public Response getAll(@Context ContainerRequestContext context) throws SQLException {
		List<Profile> profiles = Profile.getByUser((Integer) context.getProperty("user_id"));
		return status(200).entity(profiles).build();
	}

	@GET
	@Path("/{id}/customers")
	public Response getCustomers(@Context ContainerRequestContext context, @PathParam("id") int id)
			throws SQLException {
		List<Customer> customers = Customer.getByProfile(id);
		return status(200).entity(customers).build();
	}

	@GET
	@Path("/{id}/shows")
	public Response getShows(@Context ContainerRequestContext context, @PathParam("id") int id) throws SQLException {
		List<Show> shows = Show.getByProfile(id);
		return status(200).entity(shows).build();
	}
}
