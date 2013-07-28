package net.meisen.general.server.listener.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Helper class which can be used to handle the requested retrieved on a
 * <code>Socket</code>. The <code>Socket</code> to read from is specified when
 * constructing the class.
 * 
 * @author pmeisen
 * 
 */
public abstract class WorkerThread extends Thread {
	private final Socket input;
	private BufferedReader reader = null;
	private PrintWriter writer = null;

	/**
	 * Default constructor of a <code>WorkerThread</code>.
	 * 
	 * @param input
	 *          the <code>Socket</code> to retrieve the data from, cannot be
	 *          <code>null</code>
	 */
	public WorkerThread(final Socket input) {
		super();

		if (input == null) {
			throw new NullPointerException("The input cannot be null.");
		}

		this.input = input;
	}

	/**
	 * Method to retrieve the <code>Socket</code> of the <code>WorkerThread</code>
	 * .
	 * 
	 * @return the <code>Socket</code> of the <code>WorkerThread</code>
	 */
	protected Socket getSocket() {
		return input;
	}

	/**
	 * Creates a <code>BufferedReader</code> on the <code>Socket</code>. There can
	 * be only one <code>BufferedReader</code> on each <code>Socket</code>,
	 * therefore the <code>BufferedReader</code> is created on the first call and
	 * on any other the created instance is returned.
	 * 
	 * @return a <code>BufferedReader</code> to retrieve data from the
	 *         <code>Socket</code>
	 * 
	 * @throws IOException
	 *           if the <code>InputStream</code> on the <code>Socket</code> cannot
	 *           be created
	 */
	protected BufferedReader createSocketReader() throws IOException {
		if (reader == null) {
			final Socket socket = getSocket();
			reader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
		}

		return reader;
	}

	/**
	 * Creates a <code>PrintWriter</code> on the <code>Socket</code>. There can be
	 * only one <code>PrintWriter</code> on each <code>Socket</code>, therefore
	 * the <code>PrintWriter</code> is created on the first call and on any other
	 * the created instance is returned.
	 * 
	 * @return a <code>PrintWriter</code> to write to the <code>Socket</code>
	 * 
	 * @throws IOException
	 *           if the <code>OutputStream</code> on the <code>Socket</code>
	 *           cannot be created
	 */
	protected PrintWriter createSocketWriter() throws IOException {
		if (writer == null) {
			final Socket socket = getSocket();
			writer = new PrintWriter(socket.getOutputStream(), true);
		}

		return writer;
	}

	@Override
	public abstract void run();
}