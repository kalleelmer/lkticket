package se.lundakarnevalen.ticket.api.desk;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.DELETE;
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
import se.lundakarnevalen.ticket.db.Customer;
import se.lundakarnevalen.ticket.db.Order;
import se.lundakarnevalen.ticket.db.Performance;
import se.lundakarnevalen.ticket.db.Rate;
import se.lundakarnevalen.ticket.db.Show;
import se.lundakarnevalen.ticket.db.Ticket;

@Path("/desk/orders")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class DeskOrders extends Request {
	@GET
	@Path("/create")
	public Response createOrder() throws SQLException {
		Order order = Order.create();
		return status(200).entity(order).build();
	}

	@GET
	@Path("/{id}")
	public Response getOrder(@PathParam("id") int id) throws SQLException {
		Order order = Order.getSingle(id);
		assertNotNull(order, 404);
		return status(200).entity(order).build();
	}

	@GET
	@Path("/{id}/customer")
	public Response getCustomer(@PathParam("id") int id) throws SQLException {
		Order order = Order.getSingle(id);
		assertNotNull(order, 404);
		Customer customer = Customer.getSingle(order.getCustomer_id());
		return status(200).entity(customer).build();
	}

	@GET
	@Path("/{id}/tickets")
	public Response getTickets(@PathParam("id") int id) throws SQLException {
		Order order = Order.getSingle(id);
		assertNotNull(order, 404);
		List<Ticket> tickets = Ticket.getByOrder(order.id);
		return status(200).entity(tickets).build();
	}

	@POST
	@Path("/{id}/tickets")
	public Response addTickets(@PathParam("id") int id, String data) throws SQLException, JSONException {
		JSONObject input = new JSONObject(data);
		Order order = Order.getSingle(id);
		assertNotNull(order, 404);
		Performance perf = Performance.getSingle(input.getInt("performance_id"));
		assertNotNull(perf, 404);
		Rate rate = Rate.getSingle(input.getInt("rate_id"));
		assertNotNull(rate, 404);
		Category cat = Category.getSingle(input.getInt("category_id"));
		assertNotNull(cat, 404);
		Show show = perf.getShow();
		if (!rate.showIs(show) || !cat.showIs(show)) {
			throw new BadRequestException();
		}
		int ticketCount = input.getInt("count");
		List<Ticket> tickets = order.addTickets(perf.id, cat.id, rate.id, ticketCount);
		assertNotNull(tickets, 409);
		return status(200).entity(tickets).build();
	}

	@DELETE
	@Path("/{id}/tickets/{ticketID}")
	public Response deleteTicket(@PathParam("id") int id, @PathParam("ticketID") int ticketID) throws SQLException {
		Order order = Order.getSingle(id);
		assertNotNull(order, 404);
		Ticket ticket = Ticket.getSingle(ticketID);
		assertNotNull(ticket);
		if (ticket.getOrder_id() != order.id) {
			throw new ClientErrorException(409);
		}
		ticket.remove();
		return status(204).build();
	}
}
