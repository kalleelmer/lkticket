package se.lundakarnevalen.ticket.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

public class Payment extends Entity {
	@Column
	public final int id;
	@Column
	protected int transaction_id;
	@Column
	protected int order_id;
	@Column
	public int amount;
	@Column
	public String method;
	@Column
	public String reference;

	private static final String TABLE = "`payments`";
	private static final String COLS = Entity.getCols(Payment.class);

	private Payment(int id) throws SQLException {
		this.id = id;
	}

	private static Payment create(ResultSet rs) throws SQLException {
		Payment perf = new Payment(rs.getInt("id"));
		populateColumns(perf, rs);
		return perf;
	}

	public static Payment getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new Mapper<Payment>(stmt).toEntity(rs -> Payment.create(rs));
	}

	public static int create(Connection con, int transaction_id, int order_id, int amount, String method,
			String reference) throws SQLException, JSONException {
		String query = "INSERT INTO `payments` SET `transaction_id`=?, `order_id`=?, `amount`=?, `method`=?, `reference`=?";
		PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, transaction_id);
		stmt.setInt(2, order_id);
		stmt.setInt(3, amount);
		stmt.setString(4, method);
		stmt.setString(5, reference);
		int id = executeInsert(stmt);
		return id;
	}
}
