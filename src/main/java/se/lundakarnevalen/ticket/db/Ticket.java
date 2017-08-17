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

public class Ticket extends Entity {
	@Column
	public final int id;
	@Column
	protected String identifier;

	@Column
	protected int seat_id;
	@Column
	protected int order_id;
	@Column
	protected int rate_id;

	@Column
	protected int price;

	@Column
	protected Timestamp paid;
	@Column
	protected Timestamp printed;
	@Column
	protected Timestamp scanned;
	@Column
	protected Timestamp confirmed;

	private static final String TABLE = "`tickets`";
	private static final String COLS = Entity.getCols(Ticket.class);

	private static SecureRandom random = new SecureRandom();

	private Ticket(int id) throws SQLException {
		this.id = id;
	}

	private static Ticket create(ResultSet rs) throws SQLException {
		Ticket ticket = new Ticket(rs.getInt("id"));
		ticket.identifier = rs.getString("identifier");
		ticket.seat_id = rs.getInt("seat_id");
		ticket.order_id = rs.getInt("order_id");
		ticket.rate_id = rs.getInt("rate_id");
		ticket.price = rs.getInt("price");
		ticket.paid = rs.getTimestamp("paid");
		ticket.printed = rs.getTimestamp("printed");
		ticket.scanned = rs.getTimestamp("scanned");
		ticket.confirmed = rs.getTimestamp("confirmed");
		return ticket;
	}

	public static List<Ticket> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE;
		return new Mapper<Ticket>(getCon(), query).toEntityList(rs -> Ticket.create(rs));
	}

	public static List<Ticket> getByOrder(int show_id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `order_id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, show_id);
		return new Mapper<Ticket>(stmt).toEntityList(rs -> Ticket.create(rs));
	}

	public static Ticket getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new Mapper<Ticket>(stmt).toEntity(rs -> Ticket.create(rs));
	}

	public static Ticket create() throws SQLException {
		String query = "INSERT INTO " + TABLE + " SET `expires`=?, `identifier`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis() + 30 * 60)); // 30min
		stmt.setString(2, new BigInteger(48, random).toString(32).substring(0, 8));
		int id = executeInsert(stmt);
		return getSingle(id);
	}
}
