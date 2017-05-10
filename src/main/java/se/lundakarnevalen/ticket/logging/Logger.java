package se.lundakarnevalen.ticket.logging;

import java.util.logging.Level;

public class Logger {

	public static enum LogLevel {
		DEBUG, INFO, WARNING, ERROR, MINIMAL
	};

	private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("DEFAULT");

	public static void log(String message, LogLevel logLevel) {
		logger.log(Level.INFO, message);
	}

	/**
	 * A shorthand for sending log(String, LogLevel.INFO)
	 * 
	 * @param message
	 *            the message
	 */
	public static void log(String message) {
		log(message, LogLevel.INFO);
	}
}
