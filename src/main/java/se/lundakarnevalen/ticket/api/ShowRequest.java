package se.lundakarnevalen.ticket.api;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import se.lundakarnevalen.ticket.db.Show;

@Path("/shows")
public class ShowRequest extends Request {
	@GET
	@PermitAll
	@Produces("application/json; charset=UTF-8")
	public Response getAll() throws SQLException, JSONException {
		List<Show> shows = Show.getAll();
		return status(200).entity(shows).build();
	}

	@GET
	@PermitAll
	@Path("/{id}")
	@Produces("application/json; charset=UTF-8")
	public Response getSingle(@PathParam("id") long id) throws SQLException, JSONException {
		Show show = Show.getSingle(id);
		assertNotNull(show, 404);
		return status(200).entity(show).build();
	}
}
