package se.lundakarnevalen.ticket.api;

import io.swagger.annotations.Api;
import se.lundakarnevalen.ticket.db.Category;
import se.lundakarnevalen.ticket.db.Performance;
import se.lundakarnevalen.ticket.db.Rate;
import se.lundakarnevalen.ticket.db.Show;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Api(value = "Public")
@Path("/public/shows")
@PermitAll
@Produces("application/json; charset=UTF-8")
public class PublicShows extends Request {
	@GET
	public Response getAll() throws SQLException {
		List<Show> shows = Show.getAll();
		return status(200).entity(shows).build();
	}

	@GET
	@Path("/{id}")
	public Response getSingle(@PathParam("id") long id) throws SQLException {
		Show show = Show.getSingle(id);
		assertNotNull(show, 404);
		return status(200).entity(show).build();
	}

	@GET
	@Path("/{id}/performances")
	public Response getPerformances(@PathParam("id") int id) throws SQLException {
		List<Performance> perfs = Performance.getByShow(id);
		return status(200).entity(perfs).build();
	}

	@GET
	@Path("/{id}/categories")
	public Response getCategories(@PathParam("id") int id) throws SQLException {
		List<Category> cats = Category.getByShow(id);
		return status(200).entity(cats).build();
	}

	@GET
	@Path("/{id}/rates")
	public Response getRates(@PathParam("id") int id) throws SQLException {
		List<Rate> rates = Rate.getByShow(id);
		return status(200).entity(rates).build();
	}
}
