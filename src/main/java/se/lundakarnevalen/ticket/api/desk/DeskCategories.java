package se.lundakarnevalen.ticket.api.desk;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import se.lundakarnevalen.ticket.api.PublicCategories;

@Path("/desk/categories")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class DeskCategories extends PublicCategories {

}
