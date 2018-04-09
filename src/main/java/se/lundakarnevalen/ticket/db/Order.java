package se.lundakarnevalen.ticket.db;

import lombok.Getter;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;
import se.lundakarnevalen.ticket.db.framework.Table;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;

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
	@Column(table = "payments", column = "id")
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
		String query = "SELECT " + COLS + " FROM " + TABLE;
		return new Mapper<Order>(getCon(), query).toEntityList(rs -> Order.create(rs));
	}

	public static List<Order> getUnpaid() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `payments`.`id` IS NULL";
		return new Mapper<Order>(getCon(), query).toEntityList(rs -> Order.create(rs));
	}

	public static List<Order> getPaid() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `payments`.`id` IS NOT NULL";
		return new Mapper<Order>(getCon(), query).toEntityList(rs -> Order.create(rs));
	}

	public static Order getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `orders`.`id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new Mapper<Order>(stmt).toEntity(rs -> Order.create(rs));
	}

	public static Order getByIdentifier(String id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `orders`.`identifier`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setString(1, id);
		return new Mapper<Order>(stmt).toEntity(Order::create);
	}

	public static List<Order> getByCustomer(Customer customer) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `orders`.`customer_id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, customer.id);
		return new Mapper<Order>(stmt).toEntityList(rs -> Order.create(rs));
	}

	public static Order create(User user) throws SQLException {
		Connection con = transaction();
		try {
			String query = "INSERT INTO `orders` SET `expires`=?, `identifier`=?";
			PreparedStatement stmt = prepare(con, query);
			long expires = LocalDateTime.now().atZone(ZoneOffset.UTC).plusMinutes(30).toInstant().toEpochMilli(); //30 min
			stmt.setTimestamp(1, new Timestamp(expires));
			stmt.setString(2, new BigInteger(48, random).toString(32).substring(0, 8).toUpperCase());
			int id = executeInsert(stmt);
			Transaction.create(con, user.id, id, 0, 0, 0, 0);
			commit(con);
			return getSingle(id);
		} finally {
			rollback(con);
		}
	}

	public List<Ticket> addTickets(Performance performance, int category_id, int rate_id, int profile_id,
	                               int ticketCount, User user, String location) throws SQLException {
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

//			int location_id = Location.getByLocation(location).id;
			int location_id = 0;
			int transaction_id = Transaction.create(con, user.id, id, profile_id, 0, 0, location_id);

			List<Ticket> tickets = new LinkedList<Ticket>();
			int ticketsAvailable = 0;
			while (rs.next()) {
				int seat_id = rs.getInt("id");

				int ticketPrice = Math.max(0, price.price + performance.surcharge);
				if (price.price == 0) {
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

	public void setCustomer(int new_customer, User user) throws SQLException {
		String query = "UPDATE `orders` SET `customer_id`=? WHERE `orders`.`id`=?";
		Connection con = getCon();
		try {
			con.setAutoCommit(false);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setLong(2, id);
			setIntNullable(stmt, 1, new_customer);
			stmt.executeUpdate();
			this.customer_id = new_customer;
			int transaction_id = Transaction.create(con, user.id, id, 0, new_customer, 0, 0);
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

	public Payment pay(int user_id, int profile_id, int amount, List<Ticket> tickets, String method, String reference)
			throws SQLException {
		Connection con = getCon();
		try {
			con.setAutoCommit(false);
			int transaction_id = Transaction.create(con, user_id, id, profile_id, 0, 0, 0);
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
}
