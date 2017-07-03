package se.lundakarnevalen.ticket.api.exception;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import se.lundakarnevalen.ticket.logging.ErrorLogger;

@Provider
public class ClientErrorExceptionMapper implements ExceptionMapper<ClientErrorException> {

	@Override
	public Response toResponse(ClientErrorException e) {
		ErrorLogger.getInstance().put(e);
		Response response = Response.fromResponse(e.getResponse()).header("Content-Type", "text/plain")
				.entity(e.getMessage()).build();
		return response;
	}

}
