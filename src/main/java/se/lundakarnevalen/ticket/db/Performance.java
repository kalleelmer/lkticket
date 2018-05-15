package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.json.JSONArray;
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

	// Replaces start on ticket if present
	@Column
	@Getter
	protected String title;

	@Column
	@Getter
	protected String note;

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
		System.out.println(query);
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

	public static JSONObject availability(int id, int profile_id) throws SQLException, JSONException {
		String query = "SELECT `seats`.`category_id`, `categories`.`name`, COUNT(*) as `total`, "
				+ "SUM(IF(`active_ticket_id` IS NULL, 1, 0)) as `available` "
				+ "FROM `seats` LEFT JOIN `categories` ON `seats`.`category_id` = `categories`.`id` "
				+ "WHERE `performance_id`=? AND `profile_id`=? GROUP BY `seats`.`category_id`";
		PreparedStatement stmt = prepare(query);
		stmt.setInt(1, id);
		stmt.setInt(2, profile_id);
		ResultSet rs = stmt.executeQuery();
		JSONObject profile = new JSONObject();
		while (rs.next()) {
			JSONObject category = new JSONObject();
			category.put("name", rs.getString("name"));
			category.put("total", rs.getInt("total"));
			category.put("available", rs.getInt("available"));
			profile.put(Integer.toString(rs.getInt("category_id")), category);
		}
		stmt.getConnection().close();
		return profile;
	}

	public static JSONObject getReportByShow() throws JSONException, SQLException {
		String query = "SELECT `performances`.`start` as `performance_start`" + ", `shows`.`name` as `show_name`"
				+ ", `categories`.`name` as `category_name`" + ", COUNT(*) as `total`" //
				+ ", SUM(IF(`seats`.`active_ticket_id` IS NULL, 1, 0)) as `available`" //
				+ ", SUM(IF(`tickets`.`paid`=1, 1, 0)) as `paid`" //
				+ ", SUM(IF(`tickets`.`paid`=1 AND `tickets`.`printed`=1, 1, 0)) as `printed`" //
				+ ", SUM(IF(`tickets`.`paid`=1 AND `tickets`.`printed` IS NULL, 1, 0)) as `nonprinted`" //
				+ " FROM `seats`" //
				+ " LEFT JOIN `performances` ON `seats`.`performance_id`=`performances`.`id`"
				+ " LEFT JOIN `categories` ON `seats`.`category_id` = `categories`.`id`"
				+ " LEFT JOIN `tickets` ON `seats`.`active_ticket_id`=`tickets`.`id`"
				+ " LEFT JOIN `shows` ON `performances`.`show_id` = `shows`.`id`"
				+ " GROUP BY `show_name`, `performance_start`, `category_name`"
				+ " ORDER BY `show_name`, `performance_start`, `category_name`;";
		PreparedStatement stmt = prepare(query);
		ResultSet rs = stmt.executeQuery();
		JSONArray entries = new JSONArray();
		while (rs.next()) {
			JSONObject entry = new JSONObject();
			entry.put("performance_start", rs.getString("performance_start"));
			entry.put("show_name", rs.getString("show_name"));
			entry.put("category_name", rs.getString("category_name"));
			entry.put("available", rs.getInt("available"));
			entry.put("total", rs.getInt("total"));
			entry.put("paid", rs.getInt("paid"));
			entry.put("printed", rs.getInt("printed"));
			entry.put("nonprinted", rs.getInt("nonprinted"));
			entries.put(entry);
		}
		JSONObject report = new JSONObject();
		report.put("entries", entries);
		return report;
	}
}
