package net.meisen.general.server.api.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.Socket;
import java.net.SocketException;

import net.meisen.general.genmisc.exceptions.registry.IExceptionRegistry;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.server.api.IListener;
import net.meisen.general.server.api.impl.exceptions.BaseListenerException;
import net.meisen.general.server.listener.utility.AcceptListenerThread;
import net.meisen.general.server.listener.utility.StringWorkerThread;
import net.meisen.general.server.listener.utility.WorkerThread;
import net.meisen.general.server.settings.pojos.Connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The base-implementation of a <code>Listener</code>.
 * 
 * @see AcceptListenerThread
 * @see WorkerThread
 * 
 * @author pmeisen
 * 
 */
public abstract class BaseListener implements IListener {
	private final static Logger LOG = LoggerFactory
			.getLogger(BaseListener.class);

	@Autowired(required = false)
	@Qualifier(IConfiguration.coreExceptionRegistryId)
	private IExceptionRegistry exceptionRegistry;

	private int port = -1;
	private AcceptListenerThread listenerThread;

	@Override
	public void initialize(final Connector c) {

		// get the specified port
		final int specPort = c.getPort();
		if (getExceptionRegistry() == null) {
			throw new NullPointerException(
					"The exceptionRegistry cannot be null.");
		} else if (specPort < 1 || specPort > 65535) {
			getExceptionRegistry().throwException(BaseListenerException.class,
					1000, specPort);
		}

		// set the values
		this.port = specPort;
	}

	@Override
	public void open() {

		// check if we have a running thread
		if (listenerThread != null) {
			getExceptionRegistry().throwException(BaseListenerException.class,
					1003, toString());
		}

		// log it
		if (LOG.isTraceEnabled()) {
			LOG.trace("Opening '" + toString() + "...");
		}

		// start the new thread
		try {
			listenerThread = createAcceptListenerThread();
		} catch (final BindException e) {
			getExceptionRegistry().throwException(BaseListenerException.class,
					1002, e, getPort());
		} catch (final IOException e) {
			getExceptionRegistry().throwException(BaseListenerException.class,
					1001, e, getPort());
		}

		// run the thread
		listenerThread.setDaemon(false);
		listenerThread.start();
	}

	/**
	 * Creates the instance of the <code>AcceptListenerThread</code>, i.e. the
	 * one which is used to accept request on a <code>Socket</code>.
	 * 
	 * @return the <code>AcceptListenerThread</code> used to accept requests
	 * 
	 * @throws IOException
	 *             if the <code>AcceptListenerThread</code> cannot be created,
	 *             e.g. opened
	 */
	protected AcceptListenerThread createAcceptListenerThread()
			throws IOException {
		return new AcceptListenerThread(getPort()) {

			@Override
			protected Thread createWorkerThread(final Socket socket) {
				return BaseListener.this.createWorkerThread(socket);
			}
		};
	}

	/**
	 * Creates a <code>WorkerThread</code> which handles requests. This method
	 * is only called, if the<code>{@link #createAcceptListenerThread()}</code>
	 * supports it. The default implementation calls this method to create the
	 * <code>WorkerThread</code>.
	 * 
	 * @param socket
	 *            the <code>Socket</code> to handle the request on
	 * 
	 * @return the <code>Thread</code> which will handle the request on the
	 *         <code>Socket</code> when started
	 */
	protected Thread createWorkerThread(final Socket socket) {
		return new StringWorkerThread(socket) {

			@Override
			public void run() {
				try {
					final BufferedReader in = createSocketReader();
					final PrintWriter out = createSocketWriter();

					while (!out.checkError()) {
						final String input = in.readLine();
						final String output = BaseListener.this
								.handleInput(input);

						// write the answer
						if (output == null) {
							out.println("");
						} else {
							out.println(output);
						}
					}
				} catch (final SocketException e) {
					// generally ignore the socket is just closed
					if (LOG.isTraceEnabled()) {
						LOG.trace("Caught SocketException and ignored it.", e);
					}
				} catch (final IOException e) {
					getExceptionRegistry().throwException(
							BaseListenerException.class, 1004, e, getPort());
				} finally {
					close();
				}
			}
		};
	}

	/**
	 * Method used to handle a specific input. This method is only called if the
	 * default <code>WorkerThread</code> is used. Otherwise the call might not
	 * be done.
	 * 
	 * @param input
	 *            the input retrieved on the <code>Socket</code>
	 * 
	 * @return the message to reply
	 */
	protected String handleInput(final String input) {
		return "";
	}

	/**
	 * Gets the <code>ExceptionRegistry</code> used by the
	 * <code>BaseListener</code>.
	 * 
	 * @return the <code>ExceptionRegistry</code>
	 */
	protected IExceptionRegistry getExceptionRegistry() {
		return exceptionRegistry;
	}

	/**
	 * Gets the port used by the <code>Listener</code>.
	 * 
	 * @return the port used by the <code>Listener</code>
	 */
	public int getPort() {
		return port;
	}

	@Override
	public void close() {
		if (listenerThread != null && !listenerThread.isClosed()) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Closing '" + toString() + "'...");
			}

			// mark it to be interrupted and then forget about it
			listenerThread.close();
			listenerThread = null;
		}
	}

	/**
	 * Checks if the <code>listenerThread</code>, i.e. the thread connections
	 * are accepted with, is closed.
	 * 
	 * @return <code>true</code> if no request are handled anymore, otherwise
	 *         <code>false</code>
	 */
	public boolean isClosed() {
		return listenerThread == null || listenerThread.isClosed();
	}
}
