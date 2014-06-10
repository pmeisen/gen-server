package net.meisen.general.server.listener.utility;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Set<Thread> workingThreads = new HashSet<Thread>();

	/**
	 * Constructor which specifies the <code>port</code> to retrieve the
	 * incoming connections from.
	 * 
	 * @param port
	 *            the port to retrieve the incoming connection from
	 * 
	 * @throws IOException
	 *             if the <code>Socket</code> cannot be created
	 */
	public AcceptListenerThread(final int port) throws IOException {
		this(port, 0);
	}

	/**
	 * Constructor which specifies the <code>port</code> to retrieve the
	 * incoming connections from and the timeout, i.e. if no accepting is
	 * retrieved, the socket will close.
	 * 
	 * @param port
	 *            the port to retrieve the incoming connection from
	 * @param timeout
	 *            the timeout after which the socket should be closed, i.e. if
	 *            no accept was called within this time; a value of {@code 0}
	 *            means no timeout
	 * 
	 * @throws IOException
	 *             if the <code>Socket</code> cannot be created
	 */
	public AcceptListenerThread(final int port, final int timeout)
			throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(timeout);
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
			LOG.info("Start listening on port " + serverSocket.getLocalPort()
					+ "...");
		}

		while (curState) {
			try {
				// listen to the socket
				final Socket socket = serverSocket.accept();

				// log the incoming connection
				if (LOG.isDebugEnabled()) {
					LOG.debug("Incoming connection from "
							+ socket.getInetAddress());
				}

				// start the thread to handle the connection
				final Thread t = createWorkerThread(socket);

				// add the thread
				cleanUpAndAdd(t);

				// start the thread
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
				LOG.info("End listening on port " + serverSocket.getLocalPort()
						+ "...");
			}

			// make sure the socket is closed
			close();
		}
	}

	/**
	 * Cleans up the currently running threads and adds the one specified
	 * 
	 * @param newThread
	 *            the thread to be added, can be {@code null} if just a cleanup
	 *            should be performed
	 */
	protected void cleanUpAndAdd(final Thread newThread) {

		lock.readLock().lock();
		final List<Thread> toBeRemoved;
		try {
			toBeRemoved = new ArrayList<Thread>();
			for (final Thread t : workingThreads) {
				if (!t.isAlive() || t.isInterrupted()) {
					toBeRemoved.add(t);
				}
			}
		} finally {
			lock.readLock().unlock();
		}

		// remove the finished once and add the new one
		lock.writeLock().lock();
		try {
			workingThreads.removeAll(toBeRemoved);

			if (newThread != null) {
				workingThreads.add(newThread);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Creates a new <code>Thread</code> to handle the incoming data on the
	 * specified <code>Socket</code>
	 * 
	 * @param socket
	 *            the <code>Socket</code> on which data is send by the client
	 * 
	 * @return a new <code>Thread</code> which hanldes the data retrieved on the
	 *         specified <code>Socket</code>
	 * 
	 * @throws IOException
	 *             if the <code>Socket</code> cannot be bind or used
	 * 
	 * @see WorkerThread
	 */
	protected abstract Thread createWorkerThread(final Socket socket)
			throws IOException;

	@Override
	public void interrupt() {
		super.interrupt();

		// when the thread is interrupted we should also close the socket
		close();
	}

	/**
	 * Closes the listener and makes sure that no further connections are
	 * handled.
	 */
	public void close() {
		final ServerSocket serverSocket = getServerSocket();

		synchronized (serverSocket) {

			lock.writeLock().lock();
			try {
				for (final Thread t : workingThreads) {
					t.interrupt();

					// close the instance if it's a WorkerThread
					if (t instanceof WorkerThread) {
						((WorkerThread) t).close();
					}
				}

				// validate the threads
				for (final Thread t : workingThreads) {

					// check if the thread is still alive
					if (t.isAlive()) {
						try {
							t.join(500);
						} catch (final InterruptedException e) {
							// do nothing
						}
					} else {
						continue;
					}

					// check if it's still alive
					if (t.isAlive()) {
						if (LOG.isErrorEnabled()) {
							LOG.error("The thread working-thread '"
									+ t.getName()
									+ "' is still running, even after flagged interrupted and closed (only if WorkingThread). This might lead to a memory-leak.");
						}
					}
				}

				// remove all threads
				workingThreads.clear();

				// close the socket while write-lock is enabled
				if (!serverSocket.isClosed()) {
					try {
						serverSocket.close();
					} catch (final IOException e) {
						// ignore it
					}
				}
			} finally {
				lock.writeLock().unlock();
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
