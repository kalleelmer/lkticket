package se.lundakarnevalen.ticket.api.admin;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.TicketTransaction;

@Api(value = "Admin")
@Path("/admin/transactions")
@RolesAllowed("ADMIN")
@Produces("application/json; charset=UTF-8")
public class AdminTransactions extends Request {

	@GET
	@Path("/{transaction_id}/tickets")
	public Response getTickets(@PathParam("transaction_id") int transaction_id) throws SQLException {
		List<TicketTransaction> transactions = TicketTransaction.getByTransaction(transaction_id);
		return status(200).entity(transactions).build();
	}
}
