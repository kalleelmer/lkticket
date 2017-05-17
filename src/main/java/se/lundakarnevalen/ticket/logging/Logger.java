package se.lundakarnevalen.ticket.logging;

import java.util.logging.Level;

public class Logger {

	private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("DEFAULT");

	public static void log(String message, Level level) {
		logger.log(Level.INFO, message);
	}

	public static void trace(String message) {
		log(message, Level.FINE);
	}

	public static void info(String message) {
		log(message, Level.INFO);
	}

	public static void warn(String message) {
		log(message, Level.WARNING);
	}

	public static void error(String message) {
		log(message, Level.SEVERE);
	}
}
