package se.lundakarnevalen.ticket.api.desk;

import io.swagger.annotations.Api;
import org.json.JSONException;
import org.json.JSONObject;
import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.*;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Api
@Path("/desk/orders")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class DeskOrders extends Request {
	@GET
	public Response getOrders() throws SQLException {
		List<Order> orders = Order.getAll();
		return status(200).entity(orders).build();
	}

	@GET
	@Path("/unpaid")
	public Response getUnpaid() throws SQLException {
		List<Order> orders = Order.getUnpaid();
		return status(200).entity(orders).build();
	}

	@GET
	@Path("/paid")
	public Response getPaid() throws SQLException {
		List<Order> orders = Order.getPaid();
		return status(200).entity(orders).build();
	}

	@GET
	@Path("/create")
	public Response createOrder() throws SQLException {
		Order order = Order.create();
		return status(200).entity(order).build();
	}

	@GET
	@Path("/identifier/{id}")
	public Response getOrderByIdentifier(@PathParam("id") String id) throws SQLException {
		Order order = Order.getByIdentifier(id);
		assertNotNull(order, 404);
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

	@PUT
	@Path("/{id}/customer")
	public Response setCustomer(@PathParam("id") int id, int data) throws SQLException {
		Order order = Order.getSingle(id);
		assertNotNull(order, 404);
		Customer customer = Customer.getSingle(data);
		assertNotNull(customer, 400);
		order.setCustomer(customer.id);
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
	public Response addTickets(@PathParam("id") int id, @Context ContainerRequestContext context, String data)
			throws SQLException, JSONException {
		JSONObject input = new JSONObject(data);
		Order order = Order.getSingle(id);
		Payment payment = Payment.getSingle(order.getPayment_id());
		assertNull(payment, 404);
		assertNotNull(order, 404);
		Performance perf = Performance.getSingle(input.getInt("performance_id"));
		assertNotNull(perf, 404);
		Rate rate = Rate.getSingle(input.getInt("rate_id"));
		assertNotNull(rate, 404);
		Category cat = Category.getSingle(input.getInt("category_id"));
		assertNotNull(cat, 404);
		int profile_id = input.has("profile_id") ? input.getInt("profile_id") : 0;
		if (profile_id > 0) {
			Profile profile = Profile.getSingle(profile_id);
			assertNotNull(profile, 400);
			User user = User.getSingle((Integer) context.getProperty("user_id"));
			profile.assertAccess(user);
		}
		Show show = perf.getShow();
		if (!rate.showIs(show) || !cat.showIs(show)) {
			throw new BadRequestException();
		}
		int ticketCount = input.getInt("count");
		List<Ticket> tickets = order.addTickets(perf, cat.id, rate.id, profile_id, ticketCount);
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

	@POST
	@Path("/{id}/payments")
	public Response pay(@PathParam("id") int id, @Context ContainerRequestContext context, String data)
			throws JSONException, SQLException {
		User user = User.getSingle((Integer) context.getProperty("user_id"));
		JSONObject input = new JSONObject(data);
		Order order = Order.getSingle(id);
		assertNotNull(order, 404);
		if (order.getPayment_id() > 0) {
			throw new ClientErrorException(409);
		}
		int amount = input.getInt("amount");
		String method = input.getString("method");
		String reference = input.has("reference") ? input.getString("reference") : null;
		List<Ticket> tickets = Ticket.getByOrder(order.id);
		int sum = 0;
		for (Ticket t : tickets) {
			sum += t.getPrice();
		}
		if (sum != amount) {
			throw new ClientErrorException(409);
		}
		Profile profile = Profile.getSingle(input.getInt("profile_id"));
		profile.assertAccess(user);
		Payment payment = order.pay(user.id, profile.id, amount, tickets, method, reference);
		return Response.status(200).entity(payment).build();
	}
}
