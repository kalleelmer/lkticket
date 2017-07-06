package se.lundakarnevalen.ticket.api.admin;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.Category;
import se.lundakarnevalen.ticket.db.Performance;
import se.lundakarnevalen.ticket.db.Price;
import se.lundakarnevalen.ticket.db.Rate;
import se.lundakarnevalen.ticket.db.Show;

@Path("/admin/shows")
public class AdminShowRequest extends Request {
	@GET
	@RolesAllowed("ADMIN")
	@Produces("application/json; charset=UTF-8")
	public Response getAll() throws SQLException, JSONException {
		List<Show> shows = Show.getAll();
		return status(200).entity(shows).build();
	}

	@GET
	@RolesAllowed("ADMIN")
	@Path("/{id}")
	@Produces("application/json; charset=UTF-8")
	public Response getSingle(@PathParam("id") long id) throws SQLException, JSONException {
		Show show = Show.getSingle(id);
		assertNotNull(show, 404);
		return status(200).entity(show).build();
	}

	@GET
	@RolesAllowed("ADMIN")
	@Path("/{id}/performances")
	@Produces("application/json; charset=UTF-8")
	public Response getPerformances(@PathParam("id") int id) throws SQLException, JSONException {
		List<Performance> perfs = Performance.getByShow(id);
		return status(200).entity(perfs).build();
	}

	@POST
	@RolesAllowed("ADMIN")
	@Path("/{id}/performances")
	@Produces("application/json; charset=UTF-8")
	public Response createPerformance(@PathParam("id") int id, String data) throws SQLException, JSONException {
		JSONObject input = new JSONObject(data);
		Performance perf = Performance.create(id, input);
		return status(200).entity(perf).build();
	}

	@GET
	@RolesAllowed("ADMIN")
	@Path("/{id}/categories")
	@Produces("application/json; charset=UTF-8")
	public Response getCategories(@PathParam("id") int id) throws SQLException, JSONException {
		List<Category> cats = Category.getByShow(id);
		return status(200).entity(cats).build();
	}

	@POST
	@RolesAllowed("ADMIN")
	@Path("/{id}/categories")
	@Produces("application/json; charset=UTF-8")
	public Response createCategory(@PathParam("id") int id, String data) throws SQLException, JSONException {
		JSONObject input = new JSONObject(data);
		Category cat = Category.create(id, input);
		return status(200).entity(cat).build();
	}

	@GET
	@RolesAllowed("ADMIN")
	@Path("/{id}/categories/{cid}")
	@Produces("application/json; charset=UTF-8")
	public Response getCategory(@PathParam("id") int id, @PathParam("cid") int cid) throws SQLException, JSONException {
		Category cat = Category.getSingle(id);
		return status(200).entity(cat).build();
	}

	@GET
	@RolesAllowed("ADMIN")
	@Path("/{id}/categories/{cid}/prices")
	@Produces("application/json; charset=UTF-8")
	public Response getCategoryPrices(@PathParam("id") int id, @PathParam("cid") int cid)
			throws SQLException, JSONException {
		List<Price> prices = Price.getByCategory(cid);
		return status(200).entity(prices).build();
	}

	@PUT
	@RolesAllowed("ADMIN")
	@Path("/{id}/categories/{cid}/prices")
	@Produces("application/json; charset=UTF-8")
	public Response createCategoryPrice(@PathParam("id") int id, @PathParam("cid") int cid, String data)
			throws SQLException, JSONException {
		JSONObject input = new JSONObject(data);
		Price price = Price.set(cid, input.getInt("rate_id"), input.getDouble("price"));
		return status(200).entity(price).build();
	}

	@GET
	@RolesAllowed("ADMIN")
	@Path("/{id}/rates")
	@Produces("application/json; charset=UTF-8")
	public Response getRates(@PathParam("id") int id) throws SQLException, JSONException {
		List<Rate> rates = Rate.getByShow(id);
		return status(200).entity(rates).build();
	}

	@POST
	@RolesAllowed("ADMIN")
	@Path("/{id}/rates")
	@Produces("application/json; charset=UTF-8")
	public Response createRate(@PathParam("id") int id, String data) throws SQLException, JSONException {
		JSONObject input = new JSONObject(data);
		Rate rate = Rate.create(id, input);
		return status(200).entity(rate).build();
	}

	@POST
	@RolesAllowed("ADMIN")
	@Produces("application/json; charset=UTF-8")
	public Response createNew(String data) throws JSONException, SQLException {
		JSONObject input = new JSONObject(data);
		Show show = Show.create(input);
		return status(200).entity(show.toJSON().toString()).build();
	}

}
