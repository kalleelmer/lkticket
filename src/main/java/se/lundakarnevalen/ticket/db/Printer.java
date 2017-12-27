package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import lombok.Getter;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

public class Printer extends Entity {
	@Column
	public final int id;
	@Column
	@Getter
	protected String name;
	@Column
	@Getter
	protected String url;

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

	public void print(Ticket ticket) throws JSONException {
		String data = ticket.renderPrint();
		System.out.println("Printing ticket: " + data);
		AmazonSQS sqs = new AmazonSQSClient();
		SendMessageRequest request = new SendMessageRequest().withQueueUrl(url).withMessageBody(data);
		sqs.sendMessage(request);
	}

	public void setAlive() throws SQLException {
		String query = "UPDATE " + TABLE + " SET `alive`=CURRENT_TIMESTAMP WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		stmt.executeUpdate();
	}
}
