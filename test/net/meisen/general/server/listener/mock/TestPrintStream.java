package net.meisen.general.server.listener.mock;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import net.meisen.general.genmisc.types.Streams;

/**
 * Just a mock for a <code>PrintStream</code> with a default constructor.
 * 
 * @author pmeisen
 * 
 */
public class TestPrintStream extends PrintStream {
	private static ByteArrayOutputStream baos;

	/**
	 * The default constructor
	 */
	public TestPrintStream() {
		super(createBaos());
	}

	/**
	 * Resets the <code>baos</code> to a new <code>ByteArrayOutputStream</code>.
	 * 
	 * @return the new <code>ByteArrayOutputStream</code>, which is set as
	 *         <code>baos</code>
	 */
	public static OutputStream createBaos() {
		cleanUp();

		// create a new one and return it
		baos = new ByteArrayOutputStream();
		return baos;
	}

	/**
	 * Closes the current <code>baos</code> if one is created.
	 */
	public static void cleanUp() {
		if (baos != null) {

			// reset any current one
			Streams.closeIO(baos);
			baos = null;
		}
	}

	/**
	 * Get the content of the current <code>baos</code>, or <code>null</code> if
	 * none exists.
	 * 
	 * @return the current content (i.e. everything written so far) to the
	 *         <code>baos</code>
	 */
	public static String getBaosContent() {
		return baos == null ? null : baos.toString().trim();
	}
}
