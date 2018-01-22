package se.lundakarnevalen.ticket.api.desk;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import se.lundakarnevalen.ticket.api.PublicPerformances;

@Path("/desk/performances")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class DeskPerformances extends PublicPerformances {
}
