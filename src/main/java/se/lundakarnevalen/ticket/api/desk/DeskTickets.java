package se.lundakarnevalen.ticket.api.desk;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import io.swagger.annotations.Api;
import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.Ticket;
import se.lundakarnevalen.ticket.db.User;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Api(value = "Desk")
@Path("/desk/tickets")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class DeskTickets extends Request {

	@GET
	@Path("/{id}")
	public Response getTicket(@PathParam("id") int id) throws SQLException {
		Ticket ticket = Ticket.getSingle(id);
		assertNotNull(ticket, 404);
		return status(200).entity(ticket).build();
	}

	@POST
	@Path("/{id}/refund")
	public Response refundTicket(@PathParam("id") int id, @Context ContainerRequestContext context, String data)
			throws SQLException, JSONException {
		JSONObject json = new JSONObject(data);
		Ticket ticket = Ticket.getSingle(id);
		assertNotNull(ticket, 404);
		if (!ticket.isPaid() || ticket.isCancelled()) {
			throw new ClientErrorException(409);
		}
		User user = User.getCurrent(context);
		ticket.refund(user, json.getString("method"), json.getString("reference"), json.getInt("location_id"));
		return status(200).entity(ticket).build();
	}
}
