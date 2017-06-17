package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Show extends Entity {
	@Column(name = "name")
	private String name;

	private static final String TABLE = "`shows`";
	private static final String COLS = Entity.getCols(Show.class);

	private Show(int id) throws SQLException {
		super(id);
	}

	private static Show create(ResultSet rs) throws SQLException {
		Show event = new Show(rs.getInt("id"));
		event.name = rs.getString("name");
		return event;
	}

	public static List<Show> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE;
		return new QueryMapper<Show>(query(query)).toEntityList(rs -> Show.create(rs));
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("name", name);
		return json;
	}

	public static Show getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new QueryMapper<Show>(stmt.executeQuery()).toEntity(rs -> Show.create(rs));
	}
}
