package se.lundakarnevalen.ticket.api.desk;

import io.swagger.annotations.Api;
import se.lundakarnevalen.ticket.api.PublicPerformances;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Api
@Path("/desk/performances")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class DeskPerformances extends PublicPerformances {
}
