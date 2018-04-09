package se.lundakarnevalen.ticket.api.admin;

import io.swagger.annotations.Api;
import se.lundakarnevalen.ticket.api.desk.DeskOrders;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Api(value = "Admin")
@Path("/admin/orders")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class AdminOrders extends DeskOrders {
}
