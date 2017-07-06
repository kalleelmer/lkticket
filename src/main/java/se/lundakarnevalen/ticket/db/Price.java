package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Price extends Entity {
	@Column(name = "category_id")
	private int category_id;
	@Column(name = "rate_id")
	private int rate_id;
	@Column(name = "price")
	private double price;

	private static final String TABLE = "`prices`";
	private static final String COLS = Entity.getCols(Price.class);

	private Price() throws SQLException {
	}

	private static Price create(ResultSet rs) throws SQLException {
		Price rate = new Price();
		rate.category_id = rs.getInt("category_id");
		rate.rate_id = rs.getInt("rate_id");
		rate.price = rs.getDouble("price");
		return rate;
	}

	public static List<Price> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE;
		return new Mapper<Price>(getCon(), query).toEntityList(rs -> Price.create(rs));
	}

	public static List<Price> getByCategory(int category_id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `category_id`=?";
		System.out.println(query);
		PreparedStatement stmt = prepare(query);
		stmt.setInt(1, category_id);
		return new Mapper<Price>(stmt).toEntityList(rs -> Price.create(rs));
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("category_id", category_id);
		json.put("rate_id", rate_id);
		json.put("price", price);
		return json;
	}

	public static Price getSingle(int category_id, int rate_id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `category_id`=? AND `rate_id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setInt(1, category_id);
		stmt.setInt(2, rate_id);
		return new Mapper<Price>(stmt).toEntity(rs -> Price.create(rs));
	}

	public static Price set(int category_id, int rate_id, double price) throws SQLException, JSONException {
		String query = "REPLACE INTO " + TABLE + " SET `category_id`=?, `rate_id`=?, `price`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setInt(1, category_id);
		stmt.setInt(2, rate_id);
		stmt.setDouble(3, price);
		executeInsert(stmt);
		return getSingle(category_id, rate_id);
	}
}
