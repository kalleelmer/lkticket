package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.ForbiddenException;

import lombok.Getter;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

public class Profile extends Entity {
	@Column
	public final int id;
	@Column
	@Getter
	public String name;

	private static final String TABLE = "`profiles`";
	private static final String COLS = Entity.getCols(Profile.class);

	private Profile(int id) throws SQLException {
		this.id = id;
	}

	private static Profile create(ResultSet rs) throws SQLException {
		Profile profile = new Profile(rs.getInt("id"));
		populateColumns(profile, rs);
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

	public void assertAccess(User user) throws SQLException {
		if (!hasUser(user.id)) {
			throw new ForbiddenException();
		}
	}

	public boolean hasUser(int user_id) throws SQLException {
		String query = "SELECT `user_id` FROM `user_profiles` WHERE `user_id`=? AND `profile_id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, user_id);
		stmt.setLong(2, id);
		ResultSet rs = stmt.executeQuery();
		boolean result = rs.next();
		stmt.getConnection().close();
		return result;
	}

	public void addCustomer(int customer_id) throws SQLException {
		String query = "INSERT INTO `customer_profiles` (`customer_id`, `profile_id`) VALUES (?,?)";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, customer_id);
		stmt.setLong(2, id);
		stmt.executeUpdate();
	}
}
