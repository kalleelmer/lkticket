package se.lundakarnevalen.ticket.api;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.Category;
import se.lundakarnevalen.ticket.db.Price;

@Path("/public/categories")
@PermitAll
@Produces("application/json; charset=UTF-8")
public class PublicCategories extends Request {

	@GET
	@Path("/{id}")
	public Response getCategory(@PathParam("id") int cid) throws SQLException, JSONException {
		Category cat = Category.getSingle(cid);
		return status(200).entity(cat).build();
	}

	@GET
	@Path("/{id}/prices")
	public Response getCategoryPrices(@PathParam("id") int cid) throws SQLException, JSONException {
		List<Price> prices = Price.getByCategory(cid);
		return status(200).entity(prices).build();
	}
}
