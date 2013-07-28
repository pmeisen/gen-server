package net.meisen.general.server.listener.control;

import java.io.IOException;
import java.net.BindException;
import java.net.Socket;

import net.meisen.general.genmisc.exceptions.registry.IExceptionRegistry;
import net.meisen.general.server.api.IControlMessagesManager;
import net.meisen.general.server.api.IListener;
import net.meisen.general.server.exceptions.ControlListenerException;
import net.meisen.general.server.listener.utility.AcceptListenerThread;
import net.meisen.general.server.settings.pojos.Connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Listener to control the server, e.g. shutdown.
 * 
 * @author pmeisen
 * 
 */
public class ControlListener implements IListener {
	private final static Logger LOG = LoggerFactory
			.getLogger(ControlListener.class);

	/**
	 * The name of this <code>Listener</code> used when defined it, instead of the
	 * class-name
	 */
	public static final String NAME = "CONTROL";

	@Autowired
	@Qualifier("exceptionRegistry")
	private IExceptionRegistry exceptionRegistry;

	@Autowired
	@Qualifier("controlMessagesManager")
	private IControlMessagesManager controlMessagesManager;

	private int port = -1;
	private AcceptListenerThread listenerThread;

	@Override
	public void initialize(final Connector c) {

		// get the specified port
		final int specPort = c.getPort();
		if (specPort < 1 || specPort > 65535) {
			exceptionRegistry.throwException(ControlListenerException.class, 1000,
					specPort);
		}

		// set the values
		this.port = specPort;

	}

	@Override
	public void open() {

		// check if we have a running thread
		if (listenerThread != null) {
			exceptionRegistry.throwException(ControlListenerException.class, 1003,
					toString());
		}

		// log it
		if (LOG.isTraceEnabled()) {
			LOG.trace("Opening '" + toString() + "...");
		}

		// start the new thread
		try {
			listenerThread = new AcceptListenerThread(port) {

				@Override
				protected Thread createWorkerThread(Socket socket) {
					return new HandleCommandsThread(socket, controlMessagesManager);
				}
			};
		} catch (final BindException e) {
			exceptionRegistry.throwException(ControlListenerException.class, 1002, e,
					port);
		} catch (final IOException e) {
			exceptionRegistry.throwException(ControlListenerException.class, 1001, e,
					port);
		}

		// run the thread
		listenerThread.setDaemon(false);
		listenerThread.start();
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

	@Override
	public String toString() {
		return NAME + (port == -1 ? "" : " (" + port + ")");
	}
}
