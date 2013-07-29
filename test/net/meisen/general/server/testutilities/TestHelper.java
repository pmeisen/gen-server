package net.meisen.general.server.testutilities;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import net.meisen.general.server.Server;
import net.meisen.general.server.api.IServerSettings;
import net.meisen.general.server.control.ServerControllerException;

/**
 * Helper class for tests.
 * 
 * @author pmeisen
 * 
 */
public class TestHelper {

	/**
	 * Helper to get the <code>Server</code> instance created by the specified
	 * <code>extension</code>.
	 * 
	 * @param extension
	 *          the spring configuration to be loaded
	 * 
	 * @return the <code>Server</code> instance created by the specified
	 *         <code>extension</code>
	 */
	public static Server getServer(final String extension) {
		return Server.createServer(extension);
	}

	/**
	 * Get the <code>IServerSettings</code> instance, created by the specified
	 * <code>extension</code>.
	 * 
	 * @param extension
	 *          the spring configuration to be loaded
	 * 
	 * @return the <code>IServerSettings</code> instance created by the specified
	 *         <code>extension</code>
	 */
	public static IServerSettings getSettings(final String extension) {
		final Server server = getServer(extension);
		return server.getServerSettings();
	}

	public static String sendMessage(final String msg, final String host,
			final int port) {

		try {
			final Socket socket = new Socket(host, port);
			final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			final BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			// send the message
			out.println(msg);

			// get the answer - right now we don't care
			final String answer = in.readLine();

			// close the Socket
			socket.close();

			return answer;
		} catch (final UnknownHostException e) {
			fail(e.getMessage());
		} catch (final IOException e) {
			fail(e.getMessage());
		}

		fail("Reached something unreachable...");
		return null;
	}
}
