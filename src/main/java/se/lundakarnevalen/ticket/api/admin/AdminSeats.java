package se.lundakarnevalen.ticket.api.admin;

import java.sql.SQLException;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.Profile;
import se.lundakarnevalen.ticket.db.Seat;

@Path("/admin/seats")
@RolesAllowed("ADMIN")
@Produces("application/json; charset=UTF-8")
public class AdminSeats extends Request {
	@GET
	@Path("/{id}")
	public Response getSeat(@PathParam("id") int id) throws SQLException {
		Seat seat = Seat.getSingle(id);
		return status(200).entity(seat).build();
	}

	@PUT
	@Path("/{id}/profile")
	public Response setProfile(@PathParam("id") int id, String data) throws SQLException, JSONException {
		JSONObject input = new JSONObject(data);
		Seat seat = Seat.getSingle(id);
		if (!seat.isAvailable()) {
			throw new ClientErrorException(409);
		}
		Profile profile = Profile.getSingle(input.getInt("id"));
		seat.setProfile(profile.id);
		return status(200).entity(seat).build();
	}
}
