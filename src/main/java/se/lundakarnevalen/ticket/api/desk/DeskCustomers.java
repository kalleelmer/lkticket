package se.lundakarnevalen.ticket.api.desk;

import io.swagger.annotations.Api;
import org.json.JSONException;
import org.json.JSONObject;
import se.lundakarnevalen.ticket.api.Request;
import se.lundakarnevalen.ticket.db.Customer;
import se.lundakarnevalen.ticket.db.Order;
import se.lundakarnevalen.ticket.db.Profile;
import se.lundakarnevalen.ticket.db.User;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Api
@Path("/desk/customers")
@RolesAllowed("USER")
@Produces("application/json; charset=UTF-8")
public class DeskCustomers extends Request {
	@POST
	public Response createCustomer(@Context ContainerRequestContext context, String data)
			throws SQLException, JSONException {
		User user = User.getSingle((Integer) context.getProperty("user_id"));
		JSONObject input = new JSONObject(data);
		String name = input.getString("name");
		String phone = input.getString("phone");
		String email = input.getString("email");
		Profile profile = Profile.getSingle(input.getInt("profile_id"));
		profile.assertAccess(user);
		Customer customer = Customer.create(name, email, phone);
		profile.addCustomer(customer.id);
		return status(200).entity(customer).build();
	}

	@GET
	@Path("/{id}")
	public Response getCustomer(@PathParam("id") int id) throws SQLException {
		Customer customer = Customer.getSingle(id);
		assertNotNull(customer, 404);
		return status(200).entity(customer).build();
	}

	@GET
	@Path("/{id}/orders")
	public Response getOrders(@PathParam("id") int id) throws SQLException {
		Customer customer = Customer.getSingle(id);
		assertNotNull(customer, 404);
		List<Order> orders = Order.getByCustomer(customer);
		return status(200).entity(orders).build();
	}

	@PUT
	@Path("/{id}")
	public Response changeCustomer(@PathParam("id") int id, String data) throws JSONException, SQLException {
		Customer customer = Customer.getSingle(id);
		assertNotNull(customer, 404);
		JSONObject input = new JSONObject(data);
		String name = input.getString("name");
		String email = input.getString("email");
		String phone = input.getString("phone");
		customer = Customer.update(id, name, email, phone);
		return status(200).entity(customer).build();
	}
}
