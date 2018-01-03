package se.lundakarnevalen.ticket.api.admin;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import se.lundakarnevalen.ticket.api.desk.DeskPrinters;

@Path("/admin/printers")
@RolesAllowed("ADMIN")
@Produces("application/json; charset=UTF-8")
public class AdminPrinters extends DeskPrinters {
}
