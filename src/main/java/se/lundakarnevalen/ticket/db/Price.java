package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;

import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

public class Price extends Entity {
	@Column(name = "category_id")
	protected int category_id;
	@Column(name = "rate_id")
	protected int rate_id;
	@Column(name = "price")
	protected double price;

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
		stmt.executeUpdate();
		stmt.getConnection().close();
		return getSingle(category_id, rate_id);
	}

	public static void delete(int category_id, int rate_id) throws SQLException {
		String query = "DELETE FROM " + TABLE + " WHERE `category_id`=? AND `rate_id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setInt(1, category_id);
		stmt.setInt(2, rate_id);
		stmt.executeUpdate();
		stmt.getConnection().close();
	}
}
