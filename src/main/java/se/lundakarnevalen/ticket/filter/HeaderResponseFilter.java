package se.lundakarnevalen.ticket.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class HeaderResponseFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		// Headers to allow CORS
		responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
		responseContext.getHeaders().add("Access-Control-Allow-Headers",
				"Content-Type, X-User-ID, X-Auth-Token");
		responseContext.getHeaders().add("Access-Control-Expose-Headers",
				"Content-Type, X-User-ID, X-Auth-Token");
		responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");

		// Custom server
		responseContext.getHeaders().add("Server", "Lundakarnevalen Ticket System");
	}
}
