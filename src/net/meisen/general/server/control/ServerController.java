package net.meisen.general.server.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import net.meisen.general.server.control.messages.ShutdownMessage;

/**
 * The implementation to control a <code>Server</code> from an application. The
 * <code>ServerController</code> can e.g. send a shut-down message to the
 * server.
 * 
 * @author pmeisen
 * 
 */
public class ServerController {
	private final static String SHUTDOWN_MESSAGE = ShutdownMessage.MESSAGE_ID;

	/**
	 * Default host used if not defined when using arguments
	 */
	protected final static String DEF_HOST = "localhost";
	/**
	 * Default port used if no port is specified (should never happen)
	 */
	protected final static int DEF_PORT = -1;
	/**
	 * Default messages fired if none is specified using arguments
	 */
	protected final static String DEF_MESSAGE = SHUTDOWN_MESSAGE;

	private final int port;
	private final String host;

	/**
	 * The default constructor which specifies which host and port should be
	 * controlled.
	 * 
	 * @param host
	 *            the host of the server to be controlled
	 * @param port
	 *            the port of the server to be controlled
	 */
	public ServerController(final String host, final int port) {
		this.port = port;
		this.host = host;
	}

	/**
	 * Sends a shut-down message to the server.
	 */
	public void sendShutdown() {
		sendMessage(SHUTDOWN_MESSAGE);
	}

	/**
	 * Implementation to send any message to the server.
	 * 
	 * @param msg
	 *            the message to be send
	 * 
	 * @return the reply of the server to the sent message
	 */
	protected String sendMessage(final String msg) {
		try {
			final Socket socket = new Socket(host, port);
			final PrintWriter out = new PrintWriter(socket.getOutputStream(),
					true);
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
			throw new ServerControllerException("Unable to connect to server.",
					e);
		} catch (final IOException e) {
			throw new ServerControllerException("Unable to write to socket.", e);
		}
	}

	/**
	 * Parses the arguments passed to the main-method.
	 * 
	 * @param args
	 *            arguments passed
	 * @param output
	 *            {@code true} to write to the {@code System.err} or
	 *            {@code System.out}, otherwise {@code false}
	 * @return the parsed arguments or an empty array (i.e. length == 0) if no
	 *         arguments could be parsed
	 */
	protected static Object[] parseArguments(final String[] args,
			final boolean output) {
		final int size = args == null ? -1 : args.length;
		boolean invalidArgs = false;

		// get the arguments
		Integer port = DEF_PORT;
		String host = DEF_HOST;
		String smsg = DEF_MESSAGE;
		if (size < 1) {
			invalidArgs = true;
		} else if (size > 3 || (size == 1 && "-h".equalsIgnoreCase(args[0]))) {

			if (output) {
				// @formatter:off
				System.out.println("Use the following arguments:");
				System.out.println("- generally         : [host] [control-port] [message]");
				System.out.println("- shutdown          : [host] [control-port]");
				System.out.println("- localhost         : [control-port] [message]");
				System.out.println("- shutdown localhost: [control-port]");
				// @formatter:on
			}

			return new Object[] {};
		} else {
			int portPosition = -1;

			// get the position of the port
			for (int i = 0; i < size; i++) {
				try {
					Integer.parseInt(args[i]);
				} catch (final NumberFormatException e) {
					continue;
				}

				portPosition = i;
				break;
			}

			if (portPosition == 0 && size == 1) {
				port = Integer.parseInt(args[0]);
			} else if (portPosition == 0 && size == 2) {
				port = Integer.parseInt(args[0]);
				smsg = args[1];
			} else if (portPosition == 1 && size == 2) {
				host = args[0];
				port = Integer.parseInt(args[1]);
			} else if (portPosition == 1 && size == 3) {
				host = args[0];
				port = Integer.parseInt(args[1]);
				smsg = args[2];
			} else {
				invalidArgs = true;
			}
		}

		if (invalidArgs) {
			if (output) {
				System.err.println("Use -h argument to see valid arguments.");
			}
			return new Object[] {};
		} else {
			return new Object[] { host, port, smsg };
		}
	}

	/**
	 * Main method to control the server using arguments.
	 * 
	 * @param args
	 *            method to control the server
	 */
	public static void main(final String[] args) {

		final Object[] arguments = parseArguments(args, true);
		if (arguments.length != 3) {
			return;
		} else {

			// send the message
			final ServerController cnt = new ServerController(
					(String) arguments[0], (Integer) arguments[1]);
			cnt.sendMessage((String) arguments[2]);
		}
	}
}
