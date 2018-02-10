package se.lundakarnevalen.ticket.api.admin;

import io.swagger.annotations.Api;
import se.lundakarnevalen.ticket.api.desk.DeskPrinters;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Api
@Path("/admin/printers")
@RolesAllowed("ADMIN")
@Produces("application/json; charset=UTF-8")
public class AdminPrinters extends DeskPrinters {
}
