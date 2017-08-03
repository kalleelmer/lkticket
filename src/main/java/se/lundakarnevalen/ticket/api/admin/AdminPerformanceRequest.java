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
import se.lundakarnevalen.ticket.db.Performance;
import se.lundakarnevalen.ticket.db.Seat;

@Path("/admin/performances")
@RolesAllowed("ADMIN")
@Produces("application/json; charset=UTF-8")
public class AdminPerformanceRequest extends Request {

	@GET
	@Path("/{id}")
	public Response getSingle(@PathParam("id") long id) throws SQLException, JSONException {
		Performance perf = Performance.getSingle(id);
		assertNotNull(perf, 404);
		return status(200).entity(perf).build();
	}

	@GET
	@Path("/{id}/seats")
	public Response getSeats(@PathParam("id") long id) throws SQLException, JSONException {
		Performance perf = Performance.getSingle(id);
		assertNotNull(perf, 404);
		List<Seat> seats = Seat.getByPerformance(perf.id);
		return status(200).entity(seats).build();
	}
}
