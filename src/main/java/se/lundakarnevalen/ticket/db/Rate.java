package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Rate extends Entity {
	@Column(name = "show_id")
	private int show_id;
	@Column(name = "name")
	private String name;

	private static final String TABLE = "`rates`";
	private static final String COLS = Entity.getCols(Rate.class);

	private Rate(int id) throws SQLException {
		super(id);
	}

	private static Rate create(ResultSet rs) throws SQLException {
		Rate rate = new Rate(rs.getInt("id"));
		rate.show_id = rs.getInt("show_id");
		rate.name = rs.getString("name");
		return rate;
	}

	public static List<Rate> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE;
		return new Mapper<Rate>(getCon(), query).toEntityList(rs -> Rate.create(rs));
	}

	public static List<Rate> getByShow(int show_id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `show_id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setInt(1, show_id);
		return new Mapper<Rate>(stmt).toEntityList(rs -> Rate.create(rs));
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("show_id", show_id);
		json.put("name", name);
		return json;
	}

	public static Rate getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new Mapper<Rate>(stmt).toEntity(rs -> Rate.create(rs));
	}

	public static Rate create(int show_id, JSONObject input) throws SQLException, JSONException {
		String query = "INSERT INTO " + TABLE + " SET `show_id`=?, `name`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setInt(1, show_id);
		stmt.setString(2, input.getString("name"));
		int id = executeInsert(stmt);
		return getSingle(id);
	}
}
