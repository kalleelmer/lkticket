package se.lundakarnevalen.ticket.api;

import io.swagger.annotations.Api;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Api
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

	public static class JsonTest {
		public String foo = "bar";
	}

	public static class JsonTestFull extends JsonTest {
		public String hello = "world";
	}

	@GET
	@PermitAll
	@Produces("application/json")
	@Path("/json")
	public Response getJson() {
		JsonTest test = new JsonTest();
		return Response.status(200).entity(test).build();
	}

	@GET
	@PermitAll
	@Produces("application/json")
	@Path("/json/full")
	public Response getJsonFull() {
		JsonTestFull test = new JsonTestFull();
		return Response.status(200).entity(test).build();
	}
}