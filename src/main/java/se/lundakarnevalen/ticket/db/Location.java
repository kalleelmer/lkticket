package se.lundakarnevalen.ticket.db;

import lombok.Getter;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Jonathan Schurmann on 3/27/18.
 */
public class Location extends Entity {
	@Column
	public final int id;
	@Column
	@Getter
	public String name;

	private static final String TABLE = "`locations`";
	private static final String COLS = Entity.getCols(Location.class);

	private Location(int id) throws SQLException {
		this.id = id;
	}

	private static Location create(ResultSet rs) throws SQLException {
		Location location = new Location(rs.getInt("id"));
		populateColumns(location, rs);
		return location;
	}

	public static Location getSingle(int location_id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setInt(1, location_id);
		return new Mapper<Location>(stmt).toEntity(Location::create);
	}

	public static List<Location> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + "";
		PreparedStatement stmt = prepare(query);
		return new Mapper<Location>(stmt).toEntityList(Location::create);
	}
}
