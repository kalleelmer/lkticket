package se.lundakarnevalen.ticket.api.desk;

import io.swagger.annotations.Api;
import se.lundakarnevalen.ticket.api.PublicCategories;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Api(value = "Des" +
		"k")
@Path("/desk/categories")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class DeskCategories extends PublicCategories {

}
