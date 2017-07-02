package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class User extends Entity {
	@Column(name = "email")
	private String email;

	private static final String TABLE = "`users`";
	private static final String COLS = Entity.getCols(User.class);

	private User(int id) throws SQLException {
		super(id);
	}

	private static User create(ResultSet rs) throws SQLException {
		User user = new User(rs.getInt("id"));
		user.email = rs.getString("email");
		return user;
	}

	public static List<User> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE;
		return new Mapper<User>(getCon(), query).toEntityList(rs -> User.create(rs));
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("email", email);
		return json;
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

	public List<Profile> getProfiles() throws SQLException {
		return Profile.getByUser(this.id);
	}
}
