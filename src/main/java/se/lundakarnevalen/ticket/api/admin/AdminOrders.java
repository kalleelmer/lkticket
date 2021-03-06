package se.lundakarnevalen.ticket.api.admin;

import io.swagger.annotations.Api;
import se.lundakarnevalen.ticket.api.desk.DeskOrders;
import se.lundakarnevalen.ticket.db.Order;
import se.lundakarnevalen.ticket.db.TicketTransaction;
import se.lundakarnevalen.ticket.db.Transaction;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Api(value = "Admin")
@Path("/admin/orders")
@RolesAllowed("ADMIN")
@Produces("application/json; charset=UTF-8")
public class AdminOrders extends DeskOrders {
	@POST
	@Path("/cleanup")
	public Response cleanup(@QueryParam("profile_id") int profile_id, @QueryParam("customer_mode") String customer_mode)
			throws SQLException {
		if (profile_id == 0) {
			throw new ClientErrorException(400);
		}
		Order.cleanup(profile_id, "any".equals(customer_mode));
		return status(204).build();
	}

	@GET
	@Path("/{order_id}/transactions")
	public Response getTransactions(@PathParam("order_id") int order_id) throws SQLException {
		List<Transaction> transactions = Transaction.getByOrder(order_id);
		return status(200).entity(transactions).build();
	}

	@GET
	@Path("/{order_id}/ticket_transactions")
	public Response getTicketTransactions(@PathParam("order_id") int order_id) throws SQLException {
		List<TicketTransaction> transactions = TicketTransaction.getByOrder(order_id);
		return status(200).entity(transactions).build();
	}
}
