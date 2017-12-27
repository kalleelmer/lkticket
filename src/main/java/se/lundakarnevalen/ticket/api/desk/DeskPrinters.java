package se.lundakarnevalen.ticket.api.desk;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.Printer;
import se.lundakarnevalen.ticket.db.Ticket;

@Path("/desk/printers")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class DeskPrinters extends Request {
	@POST
	@Path("/{id}/print")
	public Response printOrder(@PathParam("id") int id, String data) throws SQLException, JSONException {
		Printer printer = Printer.getSingle(id);
		assertNotNull(printer, 404);
		List<Ticket> tickets = new LinkedList<Ticket>();
		JSONObject input = new JSONObject(data);
		JSONArray inputTickets = input.getJSONArray("tickets");
		for (int i = 0; i < inputTickets.length(); i++) {
			Ticket ticket = Ticket.getSingle(inputTickets.getInt(i));
			if (ticket.getPrinted() != null) {
				throw new ClientErrorException(409);
			}
			tickets.add(ticket);
		}
		for (Ticket t : tickets) {
			printer.print(t);
			t.setPrinted();
		}
		return status(204).build();
	}

	@PUT
	@Path("/{id}/alive")
	public Response setAlive(@PathParam("id") int id, String data) throws SQLException, JSONException {
		Printer printer = Printer.getSingle(id);
		assertNotNull(printer, 404);
		printer.setAlive();
		return status(204).build();
	}
}
