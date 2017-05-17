package se.lundakarnevalen.ticket.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Simple error logger for now. Integrations with error reporting services may
 * be added in the future.
 */

public class ErrorLogger {

	private static ErrorLogger logger = null;

	private ErrorLogger() {

	}

	public static synchronized ErrorLogger getInstance() {
		if (logger == null) {
			logger = new ErrorLogger();
		}
		return logger;
	}

	public void put(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		Logger.error(sw.toString());
	}

}
