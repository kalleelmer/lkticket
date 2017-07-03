package se.lundakarnevalen.ticket.api.admin;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.Category;
import se.lundakarnevalen.ticket.db.Performance;
import se.lundakarnevalen.ticket.db.Show;

@Path("/admin/shows")
public class ShowRequest extends Request {
	@GET
	@RolesAllowed("ADMIN")
	@Produces("application/json; charset=UTF-8")
	public Response getAll() throws SQLException, JSONException {
		List<Show> shows = Show.getAll();
		return status(200).entity(shows).build();
	}

	@GET
	@RolesAllowed("ADMIN")
	@Path("/{id}")
	@Produces("application/json; charset=UTF-8")
	public Response getSingle(@PathParam("id") long id) throws SQLException, JSONException {
		Show show = Show.getSingle(id);
		assertNotNull(show, 404);
		return status(200).entity(show).build();
	}

	@GET
	@RolesAllowed("ADMIN")
	@Path("/{id}/performances")
	@Produces("application/json; charset=UTF-8")
	public Response getPerformances(@PathParam("id") int id) throws SQLException, JSONException {
		List<Performance> perfs = Performance.getByShow(id);
		return status(200).entity(perfs).build();
	}

	@POST
	@RolesAllowed("ADMIN")
	@Path("/{id}/performances")
	@Produces("application/json; charset=UTF-8")
	public Response createPerformance(@PathParam("id") int id, String data) throws SQLException, JSONException {
		JSONObject input = new JSONObject(data);
		Performance perf = Performance.create(id, input);
		return status(200).entity(perf.toJSON().toString()).build();
	}

	@GET
	@RolesAllowed("ADMIN")
	@Path("/{id}/categories")
	@Produces("application/json; charset=UTF-8")
	public Response getCategories(@PathParam("id") int id) throws SQLException, JSONException {
		List<Category> cats = Category.getByShow(id);
		return status(200).entity(cats).build();
	}

	@POST
	@RolesAllowed("ADMIN")
	@Path("/{id}/categories")
	@Produces("application/json; charset=UTF-8")
	public Response createCategory(@PathParam("id") int id, String data) throws SQLException, JSONException {
		JSONObject input = new JSONObject(data);
		Category cat = Category.create(id, input);
		return status(200).entity(cat.toJSON().toString()).build();
	}

	@POST
	@RolesAllowed("ADMIN")
	@Produces("application/json; charset=UTF-8")
	public Response createNew(String data) throws JSONException, SQLException {
		JSONObject input = new JSONObject(data);
		Show show = Show.create(input);
		return status(200).entity(show.toJSON().toString()).build();
	}

}
