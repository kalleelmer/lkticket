package se.lundakarnevalen.ticket.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class QueryMapper<E extends Entity> {
	private final ResultSet rs;

	public QueryMapper(ResultSet result) {
		this.rs = result;
	}

	public E toEntity(ResultSetFunction<ResultSet, E> constructor) throws SQLException {
		if (!rs.next()) {
			return null;
		}
		return constructor.apply(rs);
	}

	public List<E> toEntityList(ResultSetFunction<ResultSet, E> constructor) throws SQLException {
		List<E> entities = new LinkedList<E>();
		while (rs.next()) {
			entities.add(constructor.apply(rs));
		}
		return entities;
	}

}
