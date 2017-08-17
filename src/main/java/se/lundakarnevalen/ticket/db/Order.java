package se.lundakarnevalen.ticket.db;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

public class Order extends Entity {
	@Column
	public final int id;
	@Column
	protected Timestamp created;
	@Column
	protected Timestamp expires;
	@Column
	protected String identifier;
	@Column
	protected int customer_id;

	private static final String TABLE = "`orders`";
	private static final String COLS = Entity.getCols(Order.class);

	private static SecureRandom random = new SecureRandom();

	private Order(int id) throws SQLException {
		this.id = id;
	}

	private static Order create(ResultSet rs) throws SQLException {
		Order order = new Order(rs.getInt("id"));
		order.created = rs.getTimestamp("created");
		order.expires = rs.getTimestamp("expires");
		order.identifier = rs.getString("identifier");
		order.customer_id = rs.getInt("customer_id");
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

	public static Order create() throws SQLException {
		String query = "INSERT INTO " + TABLE + " SET `expires`=?, `identifier`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis() + 30 * 60)); // 30min
		stmt.setString(2, new BigInteger(48, random).toString(32).substring(0, 8));
		int id = executeInsert(stmt);
		return getSingle(id);
	}
}
