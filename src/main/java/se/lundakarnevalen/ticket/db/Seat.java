package se.lundakarnevalen.ticket.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;

import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

public class Seat extends Entity {
	@Column
	public final int id;
	@Column
	protected int performance_id;
	@Column
	protected int category_id;
	@Column
	protected int active_ticket_id;
	@Column
	protected int profile_id;

	private static final String TABLE = "`seats`";
	private static final String COLS = Entity.getCols(Seat.class);

	private Seat(int id) throws SQLException {
		this.id = id;
	}

	private static Seat create(ResultSet rs) throws SQLException {
		Seat perf = new Seat(rs.getInt("id"));
		populateColumns(perf, rs);
		return perf;
	}

	public static List<Seat> getByPerformance(int performance_id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `performance_id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setInt(1, performance_id);
		return new Mapper<Seat>(stmt).toEntityList(rs -> Seat.create(rs));
	}

	public static Seat getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new Mapper<Seat>(stmt).toEntity(rs -> Seat.create(rs));
	}

	public static void create(int performance_id, int category_id, int count) throws SQLException, JSONException {
		String query = "INSERT INTO " + TABLE + " SET `performance_id`=?, `category_id`=?";
		System.out.println(query + " : " + performance_id + " : " + category_id);
		Connection con = getCon();
		try {
			con.setAutoCommit(false);
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, performance_id);
			stmt.setInt(2, category_id);
			for (int i = 0; i < count; i++) {
				stmt.executeUpdate();
			}
			con.commit();
		} finally {
			con.close();
		}
	}
}
