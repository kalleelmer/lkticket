package se.lundakarnevalen.ticket.db;

import lombok.Getter;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;
import se.lundakarnevalen.ticket.db.framework.Table;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.ClientErrorException;

@Table(name = "orders")
public class Order extends Entity {
	@Column
	public final int id;
	@Column
	@Getter
	protected Timestamp created;
	@Column
	@Getter
	protected Timestamp expires;
	@Column
	@Getter
	protected String identifier;
	@Column
	@Getter
	protected int customer_id;
	@Column(table = "payments", column = "id", specifier="MIN(`payments`.`id`)")
	@Getter
	protected int payment_id;

	private static final String TABLE = "`orders` LEFT JOIN `payments` ON `orders`.`id`=`payments`.`order_id`";
	private static final String COLS = Entity.getCols(Order.class);

	private static SecureRandom random = new SecureRandom();

	private Order(int id) throws SQLException {
		this.id = id;
	}

	private static Order create(ResultSet rs) throws SQLException {
		Order order = new Order(rs.getInt("id"));
		populateColumns(order, rs);
		return order;
	}

	public static List<Order> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " GROUP BY `orders`.`id`";
		return new Mapper<Order>(getCon(), query).toEntityList(rs -> Order.create(rs));
	}

	public static List<Order> getUnpaid() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `payments`.`id` IS NULL"
				+ " GROUP BY `orders`.`id`";
		return new Mapper<Order>(getCon(), query).toEntityList(rs -> Order.create(rs));
	}

	public static List<Order> getPaid() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `payments`.`id` IS NOT NULL"
				+ " GROUP BY `orders`.`id`";
		return new Mapper<Order>(getCon(), query).toEntityList(rs -> Order.create(rs));
	}

	public static Order getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `orders`.`id`=?" + " GROUP BY `orders`.`id`";
		System.err.println(query);
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new Mapper<Order>(stmt).toEntity(rs -> Order.create(rs));
	}

	public static Order getByIdentifier(String id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `orders`.`identifier`=?"
				+ " GROUP BY `orders`.`id`";
		PreparedStatement stmt = prepare(query);
		stmt.setString(1, id);
		return new Mapper<Order>(stmt).toEntity(Order::create);
	}

	public static List<Order> getByCustomer(Customer customer) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `orders`.`customer_id`=?"
				+ " GROUP BY `orders`.`id`";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, customer.id);
		return new Mapper<Order>(stmt).toEntityList(rs -> Order.create(rs));
	}

	public static Order create(User user, int location_id) throws SQLException {
		Connection con = transaction();
		try {
			String query = "INSERT INTO `orders` SET `identifier`=?, `expires`=(CURRENT_TIMESTAMP + INTERVAL 30 MINUTE)";
			PreparedStatement stmt = prepare(con, query);
			stmt.setString(1, generateIdentifier());
			int id = executeInsert(stmt);
			Transaction.create(con, user.id, id, 0, 0, 0, location_id);
			commit(con);
			return getSingle(id);
		} finally {
			rollback(con);
		}
	}

	private static String generateIdentifier() {
		String identifier = new BigInteger(48, random).toString(32).substring(0, 8);
		identifier = identifier.toUpperCase();
		identifier = identifier.replace('O', '0');
		identifier = identifier.replace('I', '1');
		return identifier;
	}

	public List<Ticket> addTickets(Performance performance, int category_id, int rate_id, int profile_id,
			int ticketCount, User user, Location location, boolean block_free) throws SQLException {
		System.out.println("Reserving " + ticketCount + " tickets for perf=" + performance.id + ", cat=" + category_id
				+ ", rate=" + rate_id + " and profile=" + profile_id);
		Connection con = getCon();
		try {
			con.setAutoCommit(false);
			String query = "SELECT `id` FROM `seats` WHERE `active_ticket_id` IS NULL AND `category_id`=? AND `performance_id`=? AND `profile_id`=? LIMIT ? FOR UPDATE";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, category_id);
			stmt.setInt(2, performance.id);
			stmt.setInt(3, profile_id);
			stmt.setInt(4, ticketCount);
			ResultSet rs = stmt.executeQuery();

			Price price = Price.getSingle(category_id, rate_id);

			int transaction_id = Transaction.create(con, user.id, id, profile_id, 0, 0, location.id);

			List<Ticket> tickets = new LinkedList<Ticket>();
			int ticketsAvailable = 0;
			while (rs.next()) {
				int seat_id = rs.getInt("id");

				int ticketPrice = Math.max(0, price.price + performance.surcharge);
				if (price.price == 0) {
					if (block_free) {
						throw new ClientErrorException("Free tickets denied", 403);
					}
					// Complimentary tickets are not subject to surcharge
					ticketPrice = 0;
				}

				Ticket ticket = Ticket.create(con, id, seat_id, rate_id, ticketPrice);
				stmt = con.prepareStatement("UPDATE `seats` SET `active_ticket_id`=? WHERE `id`=?");
				stmt.setInt(1, ticket.id);
				stmt.setInt(2, seat_id);
				stmt.executeUpdate();
				Transaction.addTicket(con, transaction_id, ticket.id, Transaction.TICKET_ADDED);
				tickets.add(ticket);
				ticketsAvailable++;
			}
			if (ticketsAvailable < ticketCount) {
				con.rollback();
				return null;
			}
			con.commit();
			return tickets;
		} catch (SQLException e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	public void setCustomer(int new_customer, User user, int location_id) throws SQLException {
		String query = "UPDATE `orders` SET `customer_id`=? WHERE `orders`.`id`=?";
		Connection con = getCon();
		try {
			con.setAutoCommit(false);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setLong(2, id);
			setIntNullable(stmt, 1, new_customer);
			stmt.executeUpdate();
			this.customer_id = new_customer;
			int transaction_id = Transaction.create(con, user.id, id, 0, new_customer, 0, location_id);
			for (Ticket t : Ticket.getByOrder(id)) {
				Transaction.addTicket(con, transaction_id, t.id, Transaction.CUSTOMER_SET);
			}
			con.commit();
		} catch (SQLException e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	public Payment pay(int user_id, int profile_id, int amount, List<Ticket> tickets, String method, String reference,
			int location_id) throws SQLException {
		Connection con = getCon();
		try {
			con.setAutoCommit(false);
			int transaction_id = Transaction.create(con, user_id, id, profile_id, 0, 0, location_id);
			int payment_id = Payment.create(con, transaction_id, id, amount, method, reference);
			for (Ticket t : tickets) {
				Transaction.addTicket(con, transaction_id, t.id, Transaction.TICKET_PAID);
				t.setPaid(con);
			}
			con.commit();
			return Payment.getSingle(payment_id);
		} catch (SQLException e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	public boolean isPaid() throws SQLException {
		List<Payment> payments = Payment.getByOrder(id);
		return !payments.isEmpty();
	}

	public boolean hasTickets() throws SQLException {
		List<Ticket> tickets = Ticket.getByOrder(id);
		return !tickets.isEmpty();
	}

	public static void cleanup(int profile_id, boolean all_customers) throws SQLException {
		System.out.println("Starting abandoned order cleanup");
		Connection con = transaction();
		try {
			String query = "SELECT `seats`.`id` as `seat_id`, `tickets`.`id` as `ticket_id`"
					+ ", `orders`.`id` as `order_id`, `orders`.`identifier`" + ", `orders`.`customer_id`"
					+ " FROM `seats` LEFT JOIN `tickets` ON `seats`.`active_ticket_id` = `tickets`.`id`"
					+ " LEFT JOIN `orders` ON `tickets`.`order_id` = `orders`.`id`"
					+ " WHERE `seats`.`profile_id`=? AND `orders`.`expires` < (NOW() - INTERVAL 10 MINUTE)"
					+ " AND `tickets`.`paid` IS NULL AND `tickets`.`printed` IS NULL";
			if (!all_customers) {
				query += " AND (`orders`.`customer_id` IS NULL OR `orders`.`customer_id`"
						+ " IN (SELECT `customer_id` FROM `customer_profiles` WHERE `profile_id`=?))";
			}
			PreparedStatement stmt = prepare(con, query);
			stmt.setInt(1, profile_id);
			if (!all_customers) {
				stmt.setInt(2, profile_id);
			}
			ResultSet rs = stmt.executeQuery();

			String removeTicketQuery = "UPDATE `tickets` SET `order_id`=NULL WHERE `id`=?";
			PreparedStatement removeTicket = prepare(con, removeTicketQuery);
			String releaseSeatQuery = "UPDATE `seats` SET `active_ticket_id` = NULL WHERE `active_ticket_id`=?";
			PreparedStatement releaseSeat = prepare(con, releaseSeatQuery);

			while (rs.next()) {
				int seat_id = rs.getInt("seat_id");
				int ticket_id = rs.getInt("ticket_id");
				int order_id = rs.getInt("order_id");
				String identifier = rs.getString("identifier");
				System.out.println("Releasing seat " + seat_id + ", ticket " + ticket_id + ", order " + order_id + " "
						+ identifier);
				removeTicket.setInt(1, ticket_id);
				removeTicket.executeUpdate();
				releaseSeat.setInt(1, ticket_id);
				releaseSeat.executeUpdate();
				int transaction_id = Transaction.create(con, 1, order_id, profile_id, 0, 0, 0);
				Transaction.addTicket(con, transaction_id, ticket_id, Transaction.TICKET_REMOVED);
			}
			commit(con);
		} finally {
			rollback(con);
		}
	}
}
