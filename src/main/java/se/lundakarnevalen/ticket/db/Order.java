package se.lundakarnevalen.ticket.db;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

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

	private static final String TABLE = "`orders`";
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

	public static Order getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new Mapper<Order>(stmt).toEntity(rs -> Order.create(rs));
	}

	public static List<Order> getByCustomer(Customer customer) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `customer_id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, customer.id);
		return new Mapper<Order>(stmt).toEntityList(rs -> Order.create(rs));
	}

	public static Order create() throws SQLException {
		String query = "INSERT INTO " + TABLE + " SET `expires`=?, `identifier`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis() + 30 * 60)); // 30min
		stmt.setString(2, new BigInteger(48, random).toString(32).substring(0, 8).toUpperCase());
		int id = executeInsert(stmt);
		stmt.getConnection().close();
		return getSingle(id);
	}

	public List<Ticket> addTickets(int performance_id, int category_id, int rate_id, int ticketCount)
			throws SQLException {
		System.out.println("Reserving " + ticketCount + " tickets for perf=" + performance_id + ", cat=" + category_id
				+ " and rate=" + rate_id);
		Connection con = getCon();
		try {
			con.setAutoCommit(false);
			String query = "SELECT `id` FROM `seats` WHERE `active_ticket_id` IS NULL AND `category_id`=? AND `performance_id`=? LIMIT ? FOR UPDATE";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, category_id);
			stmt.setInt(2, performance_id);
			stmt.setInt(3, ticketCount);
			ResultSet rs = stmt.executeQuery();

			Price price = Price.getSingle(category_id, rate_id);

			List<Ticket> tickets = new LinkedList<Ticket>();
			int ticketsAvailable = 0;
			while (rs.next()) {
				int seat_id = rs.getInt("id");
				Ticket ticket = Ticket.create(con, id, seat_id, rate_id, price.price);
				stmt = con.prepareStatement("UPDATE `seats` SET `active_ticket_id`=? WHERE `id`=?");
				stmt.setInt(1, ticket.id);
				stmt.setInt(2, seat_id);
				stmt.executeUpdate();
				tickets.add(ticket);
				ticketsAvailable++;
			}
			if (ticketsAvailable < ticketCount) {
				con.rollback();
				return null;
			}
			con.commit();
			con.close();
			return tickets;
		} catch (SQLException e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}
}
