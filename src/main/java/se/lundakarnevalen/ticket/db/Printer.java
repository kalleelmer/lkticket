package se.lundakarnevalen.ticket.db;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import lombok.Getter;
import org.json.JSONException;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

import java.sql.*;
import java.util.List;

public class Printer extends Entity {
	@Column
	public final int id;
	@Column
	@Getter
	protected String name;
	@Column
	@Getter
	protected String url;
	@Column
	@Getter
	protected String sno;
	@Column
	@Getter
	protected Timestamp alive;

	private static final String TABLE = "`printers`";
	private static final String COLS = Entity.getCols(Printer.class);

	private Printer(int id) throws SQLException {
		this.id = id;
	}

	private static Printer create(ResultSet rs) throws SQLException {
		Printer show = new Printer(rs.getInt("id"));
		populateColumns(show, rs);
		return show;
	}

	public static List<Printer> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE;
		return new Mapper<Printer>(getCon(), query).toEntityList(rs -> Printer.create(rs));
	}

	public static Printer getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new Mapper<Printer>(stmt).toEntity(rs -> Printer.create(rs));
	}

	public void addToPrintQueue(Ticket ticket, User user, int location_id) throws JSONException, SQLException {
		String data = ticket.renderPrint();
		System.out.println("Printing ticket: " + data);
		Connection con = transaction();
		try {
			int transaction_id = Transaction.create(con, user.id, ticket.order_id, 0, 0, id, location_id);
			Transaction.addTicket(con, transaction_id, ticket.id, Transaction.TICKET_PRINT_QUEUED);
			AmazonSQS sqs = new AmazonSQSClient();
			SendMessageRequest request = new SendMessageRequest().withQueueUrl(url).withMessageBody(data);
			sqs.sendMessage(request);
			commit(con);
		} finally {
			rollback(con);
		}
	}

	public void setAlive() throws SQLException {
		String query = "UPDATE " + TABLE + " SET `alive`=CURRENT_TIMESTAMP WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		stmt.executeUpdate();
	}

	public static Printer getBySerialNumber(String sno) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `sno`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setString(1, sno);
		return new Mapper<Printer>(stmt).toEntity(rs -> Printer.create(rs));
	}
}
