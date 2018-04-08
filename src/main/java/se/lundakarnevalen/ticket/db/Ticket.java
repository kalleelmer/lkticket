package se.lundakarnevalen.ticket.db;

import com.mysql.cj.api.jdbc.Statement;
import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;
import se.lundakarnevalen.ticket.db.framework.Table;

import javax.ws.rs.ClientErrorException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

@Table(name = "tickets")
public class Ticket extends Entity {
	@Column
	public final int id;
	@Column
	@Getter
	protected String identifier;

	@Column
	@Getter
	protected int seat_id;
	@Column
	@Getter
	protected int order_id;
	@Column
	@Getter
	protected int rate_id;

	@Column(table = "seats")
	@Getter
	protected int performance_id;
	@Column(table = "seats")
	@Getter
	protected int category_id;

	@Column(table = "categories", column = "name")
	@Getter
	protected String category_name;

	@Column(table = "rates", column = "name")
	@Getter
	protected String rate_name;

	@Column(table = "performances", column = "start")
	@Getter
	protected Timestamp performance_start;

	@Column(table = "shows", column = "name")
	@Getter
	protected String show_name;

	@Column
	@Getter
	protected int price;

	@Column
	@Getter
	protected int paid;
	@Column
	@Getter
	protected int printed;
	@Column
	@Getter
	protected int scanned;
	@Column
	@Getter
	protected int confirmed;
	@Column
	@Getter
	protected int cancelled;

	private static final String TABLE = "`tickets` " + "LEFT JOIN `rates` ON `tickets`.`rate_id`=`rates`.`id` "
			+ "LEFT JOIN `seats` ON `tickets`.`seat_id`=`seats`.`id` "
			+ "LEFT JOIN `categories` ON `seats`.`category_id`=`categories`.`id`"
			+ "LEFT JOIN `performances` ON `seats`.`performance_id`=`performances`.`id`"
			+ "LEFT JOIN `shows` ON `performances`.`show_id`=`shows`.`id`";
	private static final String COLS = Entity.getCols(Ticket.class);

	private Ticket(int id) throws SQLException {
		this.id = id;
	}

	private static Ticket create(ResultSet rs) throws SQLException {
		Ticket ticket = new Ticket(rs.getInt("id"));
		populateColumns(ticket, rs);
		return ticket;
	}

	public static List<Ticket> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE;
		return new Mapper<Ticket>(getCon(), query).toEntityList(rs -> Ticket.create(rs));
	}

	public static List<Ticket> getByOrder(int show_id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `tickets`.`order_id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, show_id);
		return new Mapper<Ticket>(stmt).toEntityList(rs -> Ticket.create(rs));
	}

	public static Ticket getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `tickets`.`id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new Mapper<Ticket>(stmt).toEntity(rs -> Ticket.create(rs));
	}

	public static Ticket getSingle(Connection con, long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `tickets`.`id`=?";
		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setLong(1, id);
		ResultSet rs = stmt.executeQuery();
		return rs.next() ? Ticket.create(rs) : null;
	}

	public static Ticket create(Connection con, int order_id, int seat_id, int rate_id, int price) throws SQLException {
		String query = "INSERT INTO `tickets` SET `order_id`=?, `seat_id`=?, `rate_id`=?, `price`=?";
		PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		stmt.setInt(1, order_id);
		stmt.setInt(2, seat_id);
		stmt.setInt(3, rate_id);
		stmt.setInt(4, price);
		int id = executeInsert(stmt);
		return getSingle(con, id);
	}

	public void remove(User user) throws SQLException {
		if (isPaid()) {
			throw new ClientErrorException(409);
		}
		Connection con = getCon();
		try {
			con.setAutoCommit(false);
			String query = "UPDATE `tickets` SET `order_id`=NULL WHERE `id`=?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setLong(1, id);
			stmt.executeUpdate();
			String query2 = "UPDATE `seats` SET `active_ticket_id`=NULL WHERE `active_ticket_id`=?";
			PreparedStatement stmt2 = con.prepareStatement(query2);
			stmt2.setLong(1, id);
			stmt2.executeUpdate();
			int transaction_id = Transaction.create(con, user.id, order_id, 0, 0, 0, 0);
			Transaction.addTicket(con, transaction_id, id, Transaction.TICKET_REMOVED);
			con.commit();
		} catch (SQLException e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	public String renderPrint() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("show_name", show_name);
		json.put("rate_name", rate_name);
		json.put("category_name", category_name);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		format.setTimeZone(TimeZone.getTimeZone("CET"));
		json.put("performance_start", format.format(performance_start));
		return json.toString();
	}

	public void setPrinted(Connection con) throws SQLException {
		String query = "UPDATE `tickets` SET `tickets`.`printed`=1 WHERE `tickets`.`id`=?";
		PreparedStatement stmt = prepare(con, query);
		stmt.setInt(1, id);
		stmt.executeUpdate();
	}

	public void setPaid(Connection con) throws SQLException {
		String query = "UPDATE `tickets` SET `tickets`.`paid`=1 WHERE `tickets`.`id`=?";
		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setInt(1, id);
		stmt.executeUpdate();
	}

	public boolean isPaid() {
		return paid != 0;
	}

	public boolean isPrinted() {
		return printed != 0;
	}

	public void refund(User user) {
		this.cancelled = 1;
	}
}
