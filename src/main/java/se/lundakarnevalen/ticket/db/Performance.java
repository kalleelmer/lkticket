package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

public class Performance extends Entity {
	@Column
	public final int id;
	@Column
	protected int show_id;
	@Column
	@Getter
	protected int surcharge;
	@Column
	@Getter
	protected Timestamp start;

	private static final String TABLE = "`performances`";
	private static final String COLS = Entity.getCols(Performance.class);

	private Performance(int id) throws SQLException {
		this.id = id;
	}

	private static Performance create(ResultSet rs) throws SQLException {
		Performance perf = new Performance(rs.getInt("id"));
		populateColumns(perf, rs);
		return perf;
	}

	public static List<Performance> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE;
		return new Mapper<Performance>(getCon(), query).toEntityList(rs -> Performance.create(rs));
	}

	public static List<Performance> getByShow(int show_id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `show_id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setInt(1, show_id);
		return new Mapper<Performance>(stmt).toEntityList(rs -> Performance.create(rs));
	}

	public static Performance getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new Mapper<Performance>(stmt).toEntity(rs -> Performance.create(rs));
	}

	public static Performance create(int show_id, JSONObject input) throws SQLException, JSONException {
		String query = "INSERT INTO " + TABLE + " SET `show_id`=?, `start`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setInt(1, show_id);
		stmt.setTimestamp(2, Timestamp.valueOf(input.getString("start") + ":00"));
		int id = executeInsert(stmt);
		stmt.getConnection().close();
		return getSingle(id);
	}

	public Show getShow() throws SQLException {
		return Show.getSingle(show_id);
	}

	public static JSONObject availability(int id) throws SQLException, JSONException {
		String query = "SELECT `profiles`.`id` as `profile_id`, `seats`.`category_id`, COUNT(*) as `total`, "
				+ "SUM(IF(`active_ticket_id` IS NULL, 1, 0)) as `available` "
				+ "FROM `seats` LEFT JOIN `profiles` ON `seats`.`profile_id`=`profiles`.`id` "
				+ "WHERE `performance_id`=? GROUP BY `profiles`.`id`, `seats`.`category_id`";
		PreparedStatement stmt = prepare(query);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		JSONObject profiles = new JSONObject();
		while (rs.next()) {
			JSONObject category = new JSONObject();
			category.put("total", rs.getInt("total"));
			category.put("available", rs.getInt("available"));
			String profile_id = Integer.toString(rs.getInt("profile_id"));
			JSONObject profile = profiles.has(profile_id) ? profiles.getJSONObject(profile_id) : new JSONObject();
			profile.put(Integer.toString(rs.getInt("category_id")), category);
			profiles.put(profile_id, profile);
		}
		return profiles;
	}
}
