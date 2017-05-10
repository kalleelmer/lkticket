package se.lundakarnevalen.ticket.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.json.JSONException;

import se.lundakarnevalen.ticket.logging.ErrorLogger;

@Provider
public class JSONExceptionMapper implements ExceptionMapper<JSONException> {

	@Override
	public Response toResponse(JSONException e) {
		ErrorLogger.getInstance().put(e);
		return Response.status(400).entity("{\"error\":\"Invalid or incomplete JSON\"}").build();
	}

}
