package se.lundakarnevalen.ticket.db;

import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

import java.sql.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Transaction extends Entity {
	public static final int TICKET_PAID = 1;
	public static final int CUSTOMER_SET = 2;
	public static final int TICKET_ADDED = 3;
	public static final int TICKET_REMOVED = 4;
	public static final int TICKET_PRINTED = 5;
	public static final int TICKET_REFUNDED = 6;
	public static final int TICKET_CANCELLED = 7;
	public static final int TICKET_PRINT_QUEUED = 8;
	public static final int TICKET_UNPRINTED = 9;

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

	public static int create(Connection con, int user_id, int order_id, int profile_id, int customer_id, int printer_id,
			int location_id) throws SQLException {
		String query = "INSERT INTO `transactions` SET `user_id`=?, `order_id`=?, `profile_id`=?, `customer_id`=?, `printer_id`=?, `location_id`=?";
		PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, user_id);
		stmt.setInt(2, order_id);
		setIntNullable(stmt, 3, profile_id);
		setIntNullable(stmt, 4, customer_id);
		setIntNullable(stmt, 5, printer_id);
		setIntNullable(stmt, 6, location_id);
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

	public static JSONObject getSales() throws SQLException, JSONException {
		String query = "SELECT DATE(`transactions`.`date`) as `day`" + ", `shows`.`name` as `show_name`"
				+ ", SUM(IF(`ticket_transactions`.`activity`=1, `tickets`.`price`, 0)) as `sales`"
				+ ", SUM(IF(`ticket_transactions`.`activity`=6, `tickets`.`price`, 0)) as `refunds`"
				+ " FROM `ticket_transactions`"
				+ " LEFT JOIN `transactions` ON `ticket_transactions`.`transaction_id`=`transactions`.`id`"
				+ " LEFT JOIN `tickets` ON `ticket_transactions`.`ticket_id`=`tickets`.`id`"
				+ " LEFT JOIN `seats` ON `tickets`.`seat_id`=`seats`.`id`"
				+ " LEFT JOIN `performances` ON `seats`.`performance_id` = `performances`.`id`"
				+ " LEFT JOIN `shows` ON `performances`.`show_id` = `shows`.`id`"
				+ " WHERE `ticket_transactions`.`activity` IN (1, 6)" + " AND `tickets`.`price` != 0"
				+ " GROUP BY `day`, `show_name` ORDER BY `day`, `show_name`;";
		PreparedStatement stmt = prepare(query);
		ResultSet rs = stmt.executeQuery();
		JSONArray days = new JSONArray();
		int totalSales = 0;
		int totalRefunds = 0;
		while (rs.next()) {
			JSONObject entry = new JSONObject();
			int sales = rs.getInt("sales");
			int refunds = rs.getInt("refunds");
			entry.put("day", rs.getString("day"));
			entry.put("show_name", rs.getString("show_name"));
			entry.put("sales", sales);
			entry.put("refunds", refunds);
			entry.put("net", sales - refunds);
			days.put(entry);
			totalSales += sales;
			totalRefunds += refunds;
		}
		JSONObject report = new JSONObject();
		report.put("entries", days);
		report.put("sales", totalSales);
		report.put("refunds", totalRefunds);
		report.put("net", totalSales - totalRefunds);
		return report;
	}
}
