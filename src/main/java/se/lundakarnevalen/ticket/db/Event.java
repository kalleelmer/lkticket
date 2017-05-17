package se.lundakarnevalen.ticket.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Event extends Entity {
	@Column(name = "name")
	private String name;

	private static final String TABLE = "`events`";
	private static final String COLS = Entity.getCols(Event.class);

	private Event(long id) throws SQLException {
		super(id);
	}

	private static Event create(ResultSet rs) throws SQLException {
		Event event = new Event(rs.getLong("id"));
		event.name = rs.getString("name");
		return event;
	}

	public static List<Event> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE;
		return new QueryMapper<Event>(query(query)).toEntityList(rs -> Event.create(rs));
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("name", name);
		return json;
	}
}
