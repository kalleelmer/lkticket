package se.lundakarnevalen.ticket.db;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import se.lundakarnevalen.ticket.Environment;
import se.lundakarnevalen.ticket.logging.Logger;

/**
 * The superclass of all database entities. Each subclass implementation should
 * correspond to a database table.
 * 
 * @author Kalle Elmér
 *
 */
public abstract class Entity {
	public final long id;

	public Entity(long id) {
		this.id = id;
	}

	private static Connection getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String dbName = Environment.getProperty("db.name");
			String userName = Environment.getProperty("db.user");
			String password = Environment.getProperty("db.password");
			String hostname = Environment.getProperty("db.host");
			String port = Environment.getProperty("db.port");
			String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password="
					+ password + "&serverTimezone=UTC";
			Logger.trace("Getting remote connection with connection string from environment variables.");
			Connection con = DriverManager.getConnection(jdbcUrl);
			Logger.trace("Remote connection successful.");
			return con;
		} catch (ClassNotFoundException e) {
			throw new SQLException(e);
		}
	}

	protected static ResultSet query(String query) throws SQLException {
		return getConnection().createStatement().executeQuery(query);
	}

	/**
	 * Generates a list of columns for an SQL query string based on @Column
	 * annotations on the fields.
	 * 
	 * @param entity
	 * @return
	 * A String in the format "`id`,`col1`,`col2`" etc.
	 */
	public static String getCols(Class<? extends Entity> entity) {
		StringBuilder cols = new StringBuilder();
		cols.append("`id`");
		Field[] fields = entity.getDeclaredFields();
		for (Field field : fields) {
			Column col = field.getAnnotation(Column.class);
			if (col != null) {
				cols.append(",`" + col.name() + "`");
			}
		}
		return cols.toString();
	}

	public abstract JSONObject toJSON() throws JSONException;
}
