package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

public class Rate extends Entity {
	@Column
	public final int id;
	@Column
	protected int show_id;
	@Column
	protected String name;

	private static final String TABLE = "`rates`";
	private static final String COLS = Entity.getCols(Rate.class);

	private Rate(int id) throws SQLException {
		this.id = id;
	}

	private static Rate create(ResultSet rs) throws SQLException {
		Rate rate = new Rate(rs.getInt("id"));
		populateColumns(rate, rs);
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
