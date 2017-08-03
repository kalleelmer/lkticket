package se.lundakarnevalen.ticket.api.admin;

import java.sql.SQLException;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import se.lundakarnevalen.ticket.api.PublicShows;
import se.lundakarnevalen.ticket.db.Category;
import se.lundakarnevalen.ticket.db.Performance;
import se.lundakarnevalen.ticket.db.Rate;
import se.lundakarnevalen.ticket.db.Seat;
import se.lundakarnevalen.ticket.db.Show;

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
			for (int i = 0; i < cat.getTicketCount(); i++) {
				System.out.println("Creating seat");
				Seat.create(perf.id, cat.id);
			}
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
		return status(200).entity(show.toJSON().toString()).build();
	}

}
