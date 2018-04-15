package se.lundakarnevalen.ticket.api.admin;

import java.sql.SQLException;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import io.swagger.annotations.Api;
import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.FinanceReport;

@Api(value = "Admin")
@Path("/admin/finance")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class AdminFinance extends Request {
	@GET
	@Path("/day/{date}")
	public Response getDay(@PathParam("date") String date) throws SQLException, JSONException {
		JSONObject report = FinanceReport.getDay(date);
		return status(200).entity(report.toString()).build();
	}
}
