package se.lundakarnevalen.ticket.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Mapper<E extends Entity> {
	private final ResultSet rs;
	private final Connection con;

	// public QueryMapper(ResultSet result) {
	// this.rs = result;
	// this.stmt = null;
	// }

	public Mapper(Connection con, String query) throws SQLException {
		this.con = con;
		this.rs = con.createStatement().executeQuery(query);
	}

	public Mapper(PreparedStatement stmt) throws SQLException {
		this.con = stmt.getConnection();
		this.rs = stmt.executeQuery();
	}

	public E toEntity(ResultSetFunction<ResultSet, E> constructor) throws SQLException {
		if (!rs.next()) {
			return null;
		}
		E e = constructor.apply(rs);
		if (this.con != null) {
			con.close();
		}
		return e;
	}

	public List<E> toEntityList(ResultSetFunction<ResultSet, E> constructor) throws SQLException {
		List<E> entities = new LinkedList<E>();
		while (rs.next()) {
			entities.add(constructor.apply(rs));
		}
		if (this.con != null) {
			con.close();
		}
		return entities;
	}

}
