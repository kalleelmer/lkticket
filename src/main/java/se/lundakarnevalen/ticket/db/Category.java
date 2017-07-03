package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Category extends Entity {
	@Column(name = "show_id")
	private int show_id;
	@Column(name = "name")
	private String name;
	@Column(name = "ticketCount")
	private int ticketCount;

	private static final String TABLE = "`categories`";
	private static final String COLS = Entity.getCols(Category.class);

	private Category(int id) throws SQLException {
		super(id);
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

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("show_id", show_id);
		json.put("name", name);
		json.put("ticketCount", ticketCount);
		return json;
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
}
