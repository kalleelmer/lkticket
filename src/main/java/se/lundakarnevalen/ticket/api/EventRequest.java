package se.lundakarnevalen.ticket.api;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import se.lundakarnevalen.ticket.db.Event;

@Path("/events")
public class EventRequest extends Request {
	@GET
	@Produces("application/json; charset=UTF-8")
	public Response responseMsg() throws SQLException, JSONException {
		List<Event> events = Event.getAll();
		return status(200).entity(events).build();
	}
}
