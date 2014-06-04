package net.meisen.general.server.listener.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import net.meisen.general.genmisc.types.Streams;

/**
 * {@code WorkerThread} used to handle strings from and read strings to the
 * associated {@code Socket}.
 * 
 * @author pmeisen
 * 
 */
public abstract class StringWorkerThread extends WorkerThread {
	private BufferedReader reader = null;
	private PrintWriter writer = null;

	/**
	 * Constructor which binds the specified {@code Socket} to {@code this}.
	 * 
	 * @param input
	 *            the socket which should be handled by {@code this}
	 */
	public StringWorkerThread(final Socket input) {
		super(input);
	}

	/**
	 * Creates a <code>BufferedReader</code> on the <code>Socket</code>. There
	 * can be only one <code>BufferedReader</code> on each <code>Socket</code>,
	 * therefore the <code>BufferedReader</code> is created on the first call
	 * and on any other the created instance is returned.
	 * 
	 * @return a <code>BufferedReader</code> to retrieve data from the
	 *         <code>Socket</code>
	 * 
	 * @throws IOException
	 *             if the <code>InputStream</code> on the <code>Socket</code>
	 *             cannot be created
	 */
	protected BufferedReader createSocketReader() throws IOException {
		if (reader == null) {
			final Socket socket = getSocket();
			reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		}

		return reader;
	}

	/**
	 * Creates a <code>PrintWriter</code> on the <code>Socket</code>. There can
	 * be only one <code>PrintWriter</code> on each <code>Socket</code>,
	 * therefore the <code>PrintWriter</code> is created on the first call and
	 * on any other the created instance is returned.
	 * 
	 * @return a <code>PrintWriter</code> to write to the <code>Socket</code>
	 * 
	 * @throws IOException
	 *             if the <code>OutputStream</code> on the <code>Socket</code>
	 *             cannot be created
	 */
	protected PrintWriter createSocketWriter() throws IOException {
		if (writer == null) {
			final Socket socket = getSocket();
			writer = new PrintWriter(socket.getOutputStream(), true);
		}

		return writer;
	}

	@Override
	public void close() {
		if (reader != null) {
			Streams.closeIO(reader);
		}
		if (writer != null) {
			Streams.closeIO(writer);
		}
		super.close();
	}
}
