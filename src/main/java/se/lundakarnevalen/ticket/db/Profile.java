package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lombok.Getter;

public class Profile extends Entity {
	@Column(name = "id")
	public final int id;
	@Column(name = "name")
	@Getter
	protected String name;

	private static final String TABLE = "`profiles`";
	private static final String COLS = Entity.getCols(Profile.class);

	private Profile(int id) throws SQLException {
		this.id = id;
	}

	private static Profile create(ResultSet rs) throws SQLException {
		Profile profile = new Profile(rs.getInt("id"));
		profile.name = rs.getString("name");
		return profile;
	}

	public static List<Profile> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE;
		return new Mapper<Profile>(getCon(), query).toEntityList(rs -> Profile.create(rs));
	}

	public static Profile getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new Mapper<Profile>(stmt).toEntity(rs -> Profile.create(rs));
	}

	public static List<Profile> getByUser(long user_id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id` IN "
				+ "(SELECT `profile_id` FROM `user_profiles` WHERE `user_id`=?)";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, user_id);
		return new Mapper<Profile>(stmt).toEntityList(rs -> Profile.create(rs));
	}
}
