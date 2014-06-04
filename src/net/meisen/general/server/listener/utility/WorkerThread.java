package net.meisen.general.server.listener.utility;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

/**
 * Helper class which can be used to handle the requested retrieved on a
 * <code>Socket</code>. The <code>Socket</code> to read from is specified when
 * constructing the class.
 * 
 * @author pmeisen
 * 
 */
public abstract class WorkerThread extends Thread implements Closeable {
	private final Socket input;

	/**
	 * Default constructor of a <code>WorkerThread</code>.
	 * 
	 * @param input
	 *            the <code>Socket</code> to retrieve the data from, cannot be
	 *            <code>null</code>
	 */
	public WorkerThread(final Socket input) {
		super();

		if (input == null) {
			throw new NullPointerException("The input cannot be null.");
		}

		this.input = input;
	}

	/**
	 * Method to retrieve the <code>Socket</code> of the
	 * <code>WorkerThread</code> .
	 * 
	 * @return the <code>Socket</code> of the <code>WorkerThread</code>
	 */
	protected Socket getSocket() {
		return input;
	}

	/**
	 * Closes the socket bound to the thread.
	 */
	@Override
	public void close() {
		if (input != null) {
			if (!input.isClosed()) {
				try {
					input.close();
				} catch (final IOException e) {
					// we cannot do anything
				}
			}
		}
	}

	@Override
	public abstract void run();
}