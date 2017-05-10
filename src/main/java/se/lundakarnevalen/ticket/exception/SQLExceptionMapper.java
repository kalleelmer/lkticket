package se.lundakarnevalen.ticket.exception;

import java.sql.SQLException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import se.lundakarnevalen.ticket.logging.ErrorLogger;

@Provider
public class SQLExceptionMapper implements ExceptionMapper<SQLException> {

	@Override
	public Response toResponse(SQLException e) {
		ErrorLogger.getInstance().put(e);
		return Response.status(500).build();
	}

}
