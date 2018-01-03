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
import org.json.JSONObject;

import se.lundakarnevalen.ticket.api.PublicPerformances;
import se.lundakarnevalen.ticket.db.Performance;
import se.lundakarnevalen.ticket.db.Seat;

@Path("/admin/performances")
@RolesAllowed("ADMIN")
@Produces("application/json; charset=UTF-8")
public class AdminPerformances extends PublicPerformances {
	@GET
	@Path("/{id}/seats")
	public Response getSeats(@PathParam("id") int id) throws SQLException {
		List<Seat> seats = Seat.getByPerformance(id);
		return status(200).entity(seats).build();
	}

	@GET
	@Path("/{id}/availability")
	public Response getAvailability(@PathParam("id") int id) throws SQLException, JSONException {
		JSONObject availability = Performance.availability(id);
		return status(200).entity(availability.toString()).build();
	}
}
