package se.lundakarnevalen.ticket.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONException;
import org.json.JSONObject;

public class FinanceReport extends Entity {
	public static JSONObject getDay(String day) throws SQLException, JSONException {
		JSONObject output = new JSONObject();
		String query = "SELECT SUM(`amount`) as `net`" + ", SUM(IF(amount > 0, amount, 0)) as `purchases`"
				+ ", SUM(IF(amount < 0, -amount, 0)) as `returns`" + " FROM `payments`"
				+ " LEFT JOIN `transactions` ON `payments`.`transaction_id` = `transactions`.`id`"
				+ " WHERE DATE(`transactions`.`date`) = ?";
		System.out.println(query);
		System.out.println(day);
		PreparedStatement stmt = prepare(query);
		stmt.setString(1, day);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			output.put("net", rs.getInt("net"));
			output.put("purchases", rs.getInt("purchases"));
			output.put("returns", rs.getInt("returns"));
		}
		return output;
	}
}
