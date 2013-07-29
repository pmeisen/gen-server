package net.meisen.general.server.listener.utility;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper implementation to handle request on an accepting <code>Socket</code>.
 * The implementation creates a new thread (see
 * <code>createWorkerThread(Socket)</code>) for each incoming connection.
 * 
 * @author pmeisen
 * 
 */
public abstract class AcceptListenerThread extends Thread {
	private final static Logger LOG = LoggerFactory
			.getLogger(AcceptListenerThread.class);

	private final ServerSocket serverSocket;

	/**
	 * Default constructor which specifies the <code>port</code> to retrieve the
	 * incoming connections from.
	 * 
	 * @param port
	 *          the port to retrieve the incoming connection from
	 * 
	 * @throws IOException
	 *           if the <code>Socket</code> cannot be created
	 */
	public AcceptListenerThread(final int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

	/**
	 * Get the <code>Socket</code> for the incoming connections.
	 * 
	 * @return the <code>Socket</code> for the incoming connections
	 */
	protected ServerSocket getServerSocket() {
		return serverSocket;
	}

	@Override
	public void run() {

		// determine the status of the first call
		final boolean firstState = !Thread.interrupted() && !isClosed();
		boolean curState = firstState;

		// do while incoming connections can be accepted
		final ServerSocket serverSocket = getServerSocket();
		if (LOG.isInfoEnabled()) {
			LOG.info("Start listening on port " + serverSocket.getLocalPort() + "...");
		}

		while (curState) {
			try {
				// listen to the socket
				final Socket socket = serverSocket.accept();

				// log the incoming connection
				if (LOG.isDebugEnabled()) {
					LOG.debug("Incoming connection from " + socket.getInetAddress());
				}

				// start the thread to handle the connection
				final Thread t = createWorkerThread(socket);
				t.setDaemon(true);
				t.start();

				// set the current state
				curState = !Thread.interrupted() && !isClosed();
			} catch (final InterruptedIOException ex) {
				break;
			} catch (final SocketException e) {
				break;
			} catch (final IOException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error("I/O error initialising connection thread", e);
				}
				break;
			}
		}

		if (firstState) {
			if (LOG.isInfoEnabled()) {
				LOG.info("End listening on port " + serverSocket.getLocalPort() + "...");
			}

			// make sure the socket is closed
			close();
		}
	}

	/**
	 * Creates a new <code>Thread</code> to handle the incoming data on the
	 * specified <code>Socket</code>
	 * 
	 * @param socket
	 *          the <code>Socket</code> on which data is send by the client
	 * 
	 * @return a new <code>Thread</code> which hanldes the data retrieved on the
	 *         specified <code>Socket</code>
	 * 
	 * @see WorkerThread
	 */
	protected abstract Thread createWorkerThread(final Socket socket);

	@Override
	public void interrupt() {
		super.interrupt();

		// when the thread is interrupted we should also close the socket
		close();
	}

	/**
	 * Closes the listener and makes sure that no further connections are handled.
	 */
	public void close() {
		final ServerSocket serverSocket = getServerSocket();

		synchronized (serverSocket) {

			// close the connection
			if (!serverSocket.isClosed()) {
				try {
					serverSocket.close();
				} catch (final IOException e) {
					// ignore it
				}
			}
		}
	}

	/**
	 * Checks if the <code>ServerSocket</code> is closed.
	 * 
	 * @return <code>true</code> if the <code>Socket</code> is closed, otherwise
	 *         <code>false</code>
	 */
	public boolean isClosed() {
		final ServerSocket serverSocket = getServerSocket();

		synchronized (serverSocket) {
			return serverSocket.isClosed();
		}
	}
}
