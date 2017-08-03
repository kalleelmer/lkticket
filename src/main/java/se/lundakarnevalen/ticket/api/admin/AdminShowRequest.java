package se.lundakarnevalen.ticket.api.admin;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.Category;
import se.lundakarnevalen.ticket.db.Performance;
import se.lundakarnevalen.ticket.db.Rate;
import se.lundakarnevalen.ticket.db.Seat;
import se.lundakarnevalen.ticket.db.Show;

@Path("/admin/shows")
@RolesAllowed("ADMIN")
@Produces("application/json; charset=UTF-8")
public class AdminShowRequest extends Request {
	@GET
	public Response getAll() throws SQLException, JSONException {
		List<Show> shows = Show.getAll();
		return status(200).entity(shows).build();
	}

	@GET
	@Path("/{id}")
	public Response getSingle(@PathParam("id") long id) throws SQLException, JSONException {
		Show show = Show.getSingle(id);
		assertNotNull(show, 404);
		return status(200).entity(show).build();
	}

	@GET
	@Path("/{id}/performances")
	public Response getPerformances(@PathParam("id") int id) throws SQLException, JSONException {
		List<Performance> perfs = Performance.getByShow(id);
		return status(200).entity(perfs).build();
	}

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

	@GET
	@Path("/{id}/categories")
	public Response getCategories(@PathParam("id") int id) throws SQLException, JSONException {
		List<Category> cats = Category.getByShow(id);
		return status(200).entity(cats).build();
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

	@GET
	@Path("/{id}/rates")
	public Response getRates(@PathParam("id") int id) throws SQLException, JSONException {
		List<Rate> rates = Rate.getByShow(id);
		return status(200).entity(rates).build();
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
