package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lombok.Getter;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

public class User extends Entity {
	@Column(name = "id")
	public final int id;
	@Column(name = "email")
	protected String email;

	@Getter
	@Column(name = "name")
	protected String name;

	private static final String TABLE = "`users`";
	private static final String COLS = Entity.getCols(User.class);

	private User(int id) throws SQLException {
		this.id = id;
	}

	private static User create(ResultSet rs) throws SQLException {
		User user = new User(rs.getInt("id"));
		user.email = rs.getString("email");
		user.name = rs.getString("name");
		return user;
	}

	public static List<User> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE;
		return new Mapper<User>(getCon(), query).toEntityList(rs -> User.create(rs));
	}

	public static User getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new Mapper<User>(stmt).toEntity(rs -> User.create(rs));
	}

	public static User getByEmail(String email) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `email`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setString(1, email);
		return new Mapper<User>(stmt).toEntity(rs -> User.create(rs));
	}

	public void setName(String name) throws SQLException {
		String query = "UPDATE " + TABLE + " SET `name`=? WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setString(1, name);
		stmt.setInt(2, id);
		stmt.executeUpdate();
		stmt.getConnection().close();
		this.name = name;
	}

	public List<Profile> getProfiles() throws SQLException {
		return Profile.getByUser(this.id);
	}
}
