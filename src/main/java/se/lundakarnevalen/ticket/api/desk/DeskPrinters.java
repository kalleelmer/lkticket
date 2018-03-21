package se.lundakarnevalen.ticket.api.desk;

import io.swagger.annotations.Api;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.Printer;
import se.lundakarnevalen.ticket.db.Ticket;
import se.lundakarnevalen.ticket.db.User;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Api
@Path("/desk/printers")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class DeskPrinters extends Request {
	@GET
	public Response getPrinters() throws SQLException, JSONException {
		List<Printer> printers = Printer.getAll();
		return status(200).entity(printers).build();
	}

	@GET
	@Path("/{id}")
	public Response getPrinter(@PathParam("id") int id) throws SQLException, JSONException {
		Printer printer = Printer.getSingle(id);
		assertNotNull(printer, 404);
		return status(200).entity(printer).build();
	}

	@GET
	@Path("/sno/{sno}")
	public Response getPrinterBySerialNumber(@PathParam("sno") String sno) throws SQLException, JSONException {
		Printer printer = Printer.getBySerialNumber(sno);
		assertNotNull(printer, 404);
		return status(200).entity(printer).build();
	}

	@POST
	@Path("/{id}/print")
	public Response printOrder(@PathParam("id") int id, @Context ContainerRequestContext context, String data)
			throws SQLException, JSONException {
		User user = User.getCurrent(context);
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
			printer.print(t, user);
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
