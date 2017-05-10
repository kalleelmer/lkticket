package se.lundakarnevalen.ticket;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/test")
public class TestRequest extends Request {
	@GET
	public Response getTest(@QueryParam("echo") String echo) {
		return Response.status(200).entity("Hello World: " + echo).build();
	}
}