package se.lundakarnevalen.ticket.api.admin;

import io.swagger.annotations.Api;
import se.lundakarnevalen.ticket.api.desk.DeskOrders;
import se.lundakarnevalen.ticket.db.Order;

import java.sql.SQLException;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
	public Response cleanup(@QueryParam("profile_id") int profile_id) throws SQLException {
		if (profile_id == 0) {
			throw new ClientErrorException(400);
		}
		Order.cleanup(profile_id);
		return status(204).build();
	}
}
