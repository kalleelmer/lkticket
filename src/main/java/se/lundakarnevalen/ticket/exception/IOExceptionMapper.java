package se.lundakarnevalen.ticket.exception;

import java.io.IOException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import se.lundakarnevalen.ticket.logging.ErrorLogger;

@Provider
public class IOExceptionMapper implements ExceptionMapper<IOException> {

	@Override
	public Response toResponse(IOException e) {
		ErrorLogger.getInstance().put(e);
		return Response.status(500).build();
	}

}
