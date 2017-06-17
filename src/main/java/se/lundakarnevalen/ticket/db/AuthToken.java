package se.lundakarnevalen.ticket.db;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;

public class AuthToken extends Entity {

	@Column(name = "token")
	@Getter
	private String token;

	@Column(name = "user_id")
	private int user_id;

	private static final String TABLE = "`user_tokens`";
	private static final String COLS = Entity.getCols(AuthToken.class);

	private static SecureRandom random = new SecureRandom();

	private AuthToken(int id) throws SQLException {
		super(id);
	}

	private static AuthToken create(ResultSet rs) throws SQLException {
		AuthToken token = new AuthToken(rs.getInt("id"));
		token.token = rs.getString("token");
		token.user_id = rs.getInt("user_id");
		return token;
	}

	public static List<AuthToken> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE;
		return new QueryMapper<AuthToken>(query(query)).toEntityList(rs -> AuthToken.create(rs));
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		return null;
	}

	public static AuthToken getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new QueryMapper<AuthToken>(stmt.executeQuery()).toEntity(rs -> AuthToken.create(rs));
	}

	public static AuthToken issue(User user) throws SQLException {
		String tokenValue = new BigInteger(130, random).toString(32);
		String query = "INSERT INTO " + TABLE + " SET `token`=?, user_id=?";
		PreparedStatement stmt = prepare(query);
		stmt.setString(1, tokenValue);
		stmt.setInt(2, user.id);
		stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		if (!rs.next()) {
			throw new SQLException("Token ID not generated");
		}
		AuthToken token = new AuthToken(rs.getInt(1));
		token.token = tokenValue;
		token.user_id = user.id;
		return token;
	}
}
