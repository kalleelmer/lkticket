package se.lundakarnevalen.ticket.api.admin;

import io.swagger.annotations.Api;
import org.json.JSONException;
import org.json.JSONObject;
import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.Profile;
import se.lundakarnevalen.ticket.db.Seat;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Api(value = "Admin")
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

	@DELETE
	@Path("/{id}/profile")
	public Response deleteProfile(@PathParam("id") int id) throws SQLException, JSONException {
		Seat seat = Seat.getSingle(id);
		if (!seat.isAvailable()) {
			throw new ClientErrorException(409);
		}
		seat.removeProfile();
		return status(200).entity(seat).build();
	}

}
