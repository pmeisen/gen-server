package net.meisen.general.server.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerController {

	private final int port;
	private final String host;

	public ServerController(final String host, final int port) {
		this.port = port;
		this.host = host;
	}

	public void sendShutdown() {
		sendMessage("shutdown");
	}

	protected String sendMessage(final String msg) {
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
			throw new ServerControllerException("Unable to connect to server.", e);
		} catch (final IOException e) {
			throw new ServerControllerException("Unable to write to socket.", e);
		}
	}
}
