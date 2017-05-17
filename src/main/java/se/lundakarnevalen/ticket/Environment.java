package se.lundakarnevalen.ticket;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Environment {
	public static String getProperty(String key) {
		try {
			InitialContext initialContext = new javax.naming.InitialContext();
			String value = (String) initialContext.lookup("java:comp/env/" + key);
			if (value != null) {
				return value;
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return System.getProperty(key);
	}
}
