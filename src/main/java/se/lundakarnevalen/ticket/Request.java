package se.lundakarnevalen.ticket;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import se.lundakarnevalen.ticket.logging.Logger;
import se.lundakarnevalen.ticket.logging.Logger.LogLevel;

public abstract class Request {

	static {
		System.out.println("The following encodings are being used. Unless this is all UTF-8, you may be in trouble.");
		System.out.println("file.encoding=" + System.getProperty("file.encoding"));
		System.out.println("Default Charset=" + Charset.defaultCharset());
		OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
		String enc = writer.getEncoding();
		System.out.println("Default Charset in Use=" + enc);
	}

	public static ResponseBuilder status(int status) {
		return Response.status(status);
	}

	/**
	 * Checks that an object isn't null or else sends the client a 404.
	 * 
	 * @param object
	 * @throws NotFoundException
	 *             if the object is null.
	 */
	protected static void assertNotNull(Object object) throws WebApplicationException {
		assertNotNull(object, 404);
	}

	/** Checks that an object isn't null, with a custom response code. */
	protected static void assertNotNull(Object object, int status) throws WebApplicationException {
		if (object == null) {
			Logger.log("Object was null", LogLevel.WARNING);
			throw new WebApplicationException(status);
		}
	}

}