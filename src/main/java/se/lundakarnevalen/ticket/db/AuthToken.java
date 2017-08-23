package se.lundakarnevalen.ticket.db;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lombok.Getter;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Mapper;

public class AuthToken extends Entity {

	@Column
	public final int id;

	@Column
	@Getter
	protected String token;

	@Column
	protected int user_id;

	private static final String TABLE = "`user_tokens`";
	private static final String COLS = Entity.getCols(AuthToken.class);

	private static SecureRandom random = new SecureRandom();

	private AuthToken(int id) throws SQLException {
		this.id = id;
	}

	private static AuthToken create(ResultSet rs) throws SQLException {
		AuthToken token = new AuthToken(rs.getInt("id"));
		populateColumns(token, rs);
		return token;
	}

	public static List<AuthToken> getAll() throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE;
		return new Mapper<AuthToken>(getCon(), query).toEntityList(rs -> AuthToken.create(rs));
	}

	public static AuthToken getSingle(long id) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `id`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setLong(1, id);
		return new Mapper<AuthToken>(stmt).toEntity(rs -> AuthToken.create(rs));
	}

	public static AuthToken getSingle(String token) throws SQLException {
		String query = "SELECT " + COLS + " FROM " + TABLE + " WHERE `token`=?";
		PreparedStatement stmt = prepare(query);
		stmt.setString(1, token);
		return new Mapper<AuthToken>(stmt).toEntity(rs -> AuthToken.create(rs));
	}

	public static AuthToken issue(User user) throws SQLException {
		String tokenValue = new BigInteger(130, random).toString(32);
		String query = "INSERT INTO " + TABLE + " SET `token`=?, user_id=?";
		PreparedStatement stmt = prepare(query);
		stmt.setString(1, tokenValue);
		stmt.setInt(2, user.id);
		int id = executeInsert(stmt);
		stmt.getConnection().close();
		AuthToken token = new AuthToken(id);
		token.token = tokenValue;
		token.user_id = user.id;
		return token;
	}

	public User getUser() throws SQLException {
		return User.getSingle(this.user_id);
	}
}
