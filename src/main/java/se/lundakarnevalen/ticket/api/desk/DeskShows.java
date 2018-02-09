package se.lundakarnevalen.ticket.api.desk;

import io.swagger.annotations.Api;
import se.lundakarnevalen.ticket.api.PublicShows;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Api
@Path("/desk/shows")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class DeskShows extends PublicShows {

}
