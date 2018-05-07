package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

public class TicketTransaction extends Entity {

	private static final String TABLE = "`ticket_transactions`"
			+ " LEFT JOIN `transactions` ON `ticket_transactions`.`transaction_id`=`transactions`.`id`";
	private static final String COLS = Entity.getCols(TicketTransaction.class);

	@Getter
	@Column(table = "ticket_transactions")
	protected int transaction_id;

	@Getter
	@Column(table = "ticket_transactions")
	protected int ticket_id;

	@Getter
	@Column(table = "ticket_transactions")
	protected int activity;

	@Getter
	@Column(table = "transactions")
	protected Timestamp date;

	@Getter
	@Column(table = "transactions")
	protected int user_id;

	@Getter
	@Column(table = "transactions")
	protected int profile_id;

	private TicketTransaction() throws SQLException {
	}

	private static TicketTransaction create(ResultSet rs) throws SQLException {
		TicketTransaction ticket = new TicketTransaction();
		populateColumns(ticket, rs);
		return ticket;
	}

	public static List<TicketTransaction> getByTransaction(int transaction_id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `ticket_transactions`.`transaction_id`=?";
		System.out.println(query);
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, transaction_id);
		return new Mapper<TicketTransaction>(stmt).toEntityList(TicketTransaction::create);
	}

	public static List<TicketTransaction> getByOrder(int order_id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `transactions`.`order_id`=?";
		System.out.println(query);
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, order_id);
		return new Mapper<TicketTransaction>(stmt).toEntityList(TicketTransaction::create);
	}
}
