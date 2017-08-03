package se.lundakarnevalen.ticket.api.admin;

import java.sql.SQLException;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import se.lundakarnevalen.ticket.api.CategoryRequest;
import se.lundakarnevalen.ticket.db.Category;
import se.lundakarnevalen.ticket.db.Price;

@Path("/admin/categories")
@RolesAllowed("ADMIN")
@Produces("application/json; charset=UTF-8")
public class AdminCategoryRequest extends CategoryRequest {

	@PUT
	@Produces("text/plain;charset=UTF-8")
	@Path("/{id}/ticketCount")
	public Response getCategory(@PathParam("id") int cid, String data) throws SQLException, JSONException {
		Category cat = Category.getSingle(cid);
		try {
			cat.setTicketCount(Integer.parseInt(data));
		} catch (NumberFormatException e) {
			throw new BadRequestException("Value must be integer");
		}
		return status(200).entity(data).build();
	}

	@PUT
	@Path("/{id}/prices/{rate_id}")
	public Response createCategoryPrice(@PathParam("id") int cid, @PathParam("rate_id") int rid, String data)
			throws SQLException, JSONException {
		JSONObject input = new JSONObject(data);
		Price price = Price.set(cid, rid, input.getDouble("price"));
		return status(200).entity(price).build();
	}

	@DELETE
	@Path("/{id}/prices/{rate_id}")
	public Response deleteCategoryPrice(@PathParam("id") int cid, @PathParam("rate_id") int rid, String data)
			throws SQLException, JSONException {
		Price.delete(cid, rid);
		return status(200).build();
	}
}
