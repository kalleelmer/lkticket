package se.lundakarnevalen.ticket;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Environment {
	public static String getProperty(String key) {
		String env = System.getProperty(key);
		if (env != null) {
			return env;
		}
		try {
			InitialContext initialContext = new javax.naming.InitialContext();
			String value = (String) initialContext.lookup("java:comp/env/" + key);
			return value;
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
