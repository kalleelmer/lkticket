package se.lundakarnevalen.ticket.api.admin;

import io.swagger.annotations.Api;
import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.Transaction;

import java.sql.SQLException;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.json.JSONException;

@Api(value = "Admin")
@Path("/admin/reports")
@RolesAllowed("REPORTS")
@Produces("application/json; charset=UTF-8")
public class AdminReports extends Request {
	@GET
	@Path("/sales")
	public Response getSales() throws SQLException, JSONException {
		JSONObject report = Transaction.getSales();
		return status(200).entity(report.toString()).build();
	}
}
