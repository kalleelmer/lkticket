package se.lundakarnevalen.ticket.api.admin;

import io.swagger.annotations.Api;
import org.json.JSONException;
import org.json.JSONObject;
import se.lundakarnevalen.ticket.api.PublicShows;
import se.lundakarnevalen.ticket.db.*;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Api(value = "Admin")
@Path("/admin/shows")
@RolesAllowed("ADMIN")
@Produces("application/json; charset=UTF-8")
public class AdminShows extends PublicShows {
	@POST
	@Path("/{id}/performances")
	public Response createPerformance(@PathParam("id") int id, String data) throws SQLException, JSONException {
		JSONObject input = new JSONObject(data);
		Show show = Show.getSingle(id);
		assertNotNull(show);
		Performance perf = Performance.create(show.id, input);
		for (Category cat : Category.getByShow(show.id)) {
			System.out.println(
					"Add for category " + cat.id + ", " + cat.getName() + " with ticketCount " + cat.getTicketCount());
			Seat.create(perf.id, cat.id, cat.getTicketCount());
		}
		return status(200).entity(perf).build();
	}

	@POST
	@Path("/{id}/categories")
	public Response createCategory(@PathParam("id") int id, String data) throws SQLException, JSONException {
		JSONObject input = new JSONObject(data);
		Category cat = Category.create(id, input);
		return status(200).entity(cat).build();
	}

	@PUT
	@Path("/{id}/name")
	@Produces("text/plain;charset=UTF-8")
	public Response changeName(@PathParam("id") int id, String data) throws SQLException, JSONException {
		Show show = Show.getSingle(id);
		assertNotNull(show, 404);
		show.setName(data);
		return status(200).entity(data).build();
	}

	@POST
	@Path("/{id}/rates")
	public Response createRate(@PathParam("id") int id, String data) throws SQLException, JSONException {
		JSONObject input = new JSONObject(data);
		Rate rate = Rate.create(id, input);
		return status(200).entity(rate).build();
	}

	@POST
	public Response createNew(String data) throws JSONException, SQLException {
		JSONObject input = new JSONObject(data);
		Show show = Show.create(input);
		return status(200).entity(show).build();
	}

}
