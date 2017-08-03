package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

public class Category extends Entity {
	@Column(name = "id")
	public final int id;

	@Column(name = "show_id")
	protected int show_id;

	@Getter
	@Column(name = "name")
	protected String name;

	@Getter
	@Column(name = "ticketCount")
	protected int ticketCount;

	private static final String TABLE = "`categories`";
	private static final String COLS = Entity.getCols(Category.class);

	private Category(int id) throws SQLException {
		this.id = id;
	}

	private static Category create(ResultSet rs) throws SQLException {
		Category perf = new Category(rs.getInt("id"));
		perf.show_id = rs.getInt("show_id");
		perf.name = rs.getString("name");
		perf.ticketCount = rs.getInt("ticketCount");
		return perf;
	}

	public static List<Category> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE;
		return new Mapper<Category>(getCon(), query).toEntityList(rs -> Category.create(rs));
	}

	public static List<Category> getByShow(int show_id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `show_id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setInt(1, show_id);
		return new Mapper<Category>(stmt).toEntityList(rs -> Category.create(rs));
	}

	public static Category getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new Mapper<Category>(stmt).toEntity(rs -> Category.create(rs));
	}

	public static Category create(int show_id, JSONObject input) throws SQLException, JSONException {
		String query = "INSERT INTO " + TABLE + " SET `show_id`=?, `name`=?, `ticketCount`=0";
		PreparedStatement stmt = prepare(query);
		stmt.setInt(1, show_id);
		stmt.setString(2, input.getString("name"));
		int id = executeInsert(stmt);
		return getSingle(id);
	}

	public void setTicketCount(int count) throws SQLException {
		String query = "UPDATE " + TABLE + " SET `ticketCount`=? WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setInt(1, count);
		stmt.setInt(2, id);
		stmt.executeUpdate();
	}
}
