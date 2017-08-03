package se.lundakarnevalen.ticket.api;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/test")
public class Test extends Request {
	@GET
	@PermitAll
	public Response getTest(@QueryParam("echo") String echo) {
		return Response.status(200).entity("Hello World: " + echo).build();
	}

	@GET
	@RolesAllowed("USER")
	@Path("/login")
	public Response getLogin(@QueryParam("echo") String echo) {
		return Response.status(200).entity("You are logged in.").build();
	}

	@GET
	@RolesAllowed("ADMIN")
	@Path("/admin")
	public Response getAdmin(@QueryParam("echo") String echo) {
		return Response.status(200).entity("You are admin.").build();
	}
}