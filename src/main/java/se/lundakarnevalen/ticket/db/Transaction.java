package se.lundakarnevalen.ticket.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.json.JSONException;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

public class Transaction extends Entity {
	public static final int TICKET_PAID = 1;

	@Column
	public final int id;
	@Column
	protected int user_id;
	@Column
	protected Timestamp date;
	@Column
	protected int order_id;
	@Column
	protected int profile_id;

	private static final String TABLE = "`transactions`";
	private static final String COLS = Entity.getCols(Transaction.class);

	private Transaction(int id) throws SQLException {
		this.id = id;
	}

	private static Transaction create(ResultSet rs) throws SQLException {
		Transaction transaction = new Transaction(rs.getInt("id"));
		populateColumns(transaction, rs);
		return transaction;
	}

	public static Transaction getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new Mapper<Transaction>(stmt).toEntity(rs -> Transaction.create(rs));
	}

	public static int create(Connection con, int user_id, int order_id, int profile_id)
			throws SQLException, JSONException {
		String query = "INSERT INTO `transactions` SET `user_id`=?, `order_id`=?, `profile_id`=?";
		PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, user_id);
		stmt.setInt(2, order_id);
		stmt.setInt(3, profile_id);
		int id = executeInsert(stmt);
		return id;
	}

	public static void addTicket(Connection con, int transaction_id, int ticket_id, int activity) throws SQLException {
		String query = "INSERT INTO `ticket_transactions` SET `ticket_id`=?, `transaction_id`=?, `activity`=?";
		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setInt(1, ticket_id);
		stmt.setInt(2, transaction_id);
		stmt.setInt(3, activity);
		stmt.executeUpdate();
	}
}
