package se.lundakarnevalen.ticket.db;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.mysql.cj.api.jdbc.Statement;

import se.lundakarnevalen.ticket.Environment;
import se.lundakarnevalen.ticket.db.framework.Column;
import se.lundakarnevalen.ticket.db.framework.Table;
import se.lundakarnevalen.ticket.logging.Logger;

/**
 * The superclass of all database entities. Each subclass implementation should
 * correspond to a database table.
 * 
 * @author Kalle Elmér
 *
 */
public abstract class Entity {

	protected static Connection getCon() throws SQLException {
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
		return getCon().createStatement().executeQuery(query);
	}

	protected static PreparedStatement prepare(String query) throws SQLException {
		return getCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
	}

	/**
	 * Generates a list of columns for an SQL query string based on @Column
	 * annotations on the fields.
	 * 
	 * @param entity
	 * @return A String in the format "`id`,`col1`,`col2`" etc.
	 */
	public static String getCols(Class<? extends Entity> entity) {
		StringBuilder cols = new StringBuilder();
		Field[] fields = entity.getDeclaredFields();
		int index = 0;
		String classPrefix = entity.isAnnotationPresent(Table.class)
				? "`" + entity.getAnnotation(Table.class).name() + "`." : "";

		for (Field field : fields) {
			if (field.isAnnotationPresent(Column.class)) {
				Column column = field.getAnnotation(Column.class);
				String fieldTable = column.table();
				String prefix = fieldTable.isEmpty() ? classPrefix : "`" + fieldTable + "`.";
				String fieldColumn = column.column().isEmpty() ? field.getName() : column.column();
				cols.append((index == 0 ? "" : ",") + prefix + "`" + fieldColumn + "` as `" + field.getName() + "`");
			}
			index++;
		}
		System.out.println(cols.toString());
		return cols.toString();
	}

	public static void populateColumns(Entity object, ResultSet rs) throws SQLException {
		Class<? extends Entity> entity = object.getClass();
		Field[] fields = entity.getDeclaredFields();
		try {
			for (Field field : fields) {
				if (field.isAnnotationPresent(Column.class) && !Modifier.isFinal(field.getModifiers())) {
					if (field.getType().equals(int.class)) {
						field.setInt(object, rs.getInt(field.getName()));
					} else if (field.getType().equals(String.class)) {
						field.set(object, rs.getString(field.getName()));
					} else if (field.getType().equals(Timestamp.class)) {
						field.set(object, rs.getTimestamp(field.getName()));
					} else if (field.getType().equals(double.class)) {
						field.setDouble(object, rs.getDouble(field.getName()));
					} else {
						throw new SQLException("Unknown column type '" + field.getType().toString() + "'");
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new SQLException(e);
		}
	}

	/**
	 * Executes an INSERT and returns the generated key.
	 * 
	 * @throws SQLException
	 */
	protected static int executeInsert(PreparedStatement stmt) throws SQLException {
		stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		if (!rs.next()) {
			throw new SQLException("No AUTO_INCREMENT ID generated");
		}
		int id = rs.getInt(1);
		return id;
	}
}
