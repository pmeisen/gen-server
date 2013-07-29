package net.meisen.general.server.api.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.Socket;

import net.meisen.general.genmisc.exceptions.registry.IExceptionRegistry;
import net.meisen.general.server.api.IListener;
import net.meisen.general.server.api.impl.exceptions.BaseListenerException;
import net.meisen.general.server.listener.utility.AcceptListenerThread;
import net.meisen.general.server.listener.utility.WorkerThread;
import net.meisen.general.server.settings.pojos.Connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class BaseListener implements IListener {
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Autowired(required = false)
	@Qualifier("exceptionRegistry")
	private IExceptionRegistry exceptionRegistry;

	private int port = -1;
	private AcceptListenerThread listenerThread;

	@Override
	public void initialize(final Connector c) {

		// get the specified port
		final int specPort = c.getPort();
		if (getExceptionRegistry() == null) {
			throw new NullPointerException("The exceptionRegitry cannot be null.");
		} else if (specPort < 1 || specPort > 65535) {
			getExceptionRegistry().throwException(BaseListenerException.class, 1000,
					specPort);
		}

		// set the values
		this.port = specPort;
	}

	@Override
	public void open() {

		// check if we have a running thread
		if (listenerThread != null) {
			getExceptionRegistry().throwException(BaseListenerException.class, 1003,
					toString());
		}

		// log it
		if (LOG.isTraceEnabled()) {
			LOG.trace("Opening '" + toString() + "...");
		}

		// start the new thread
		try {
			listenerThread = new AcceptListenerThread(getPort()) {

				@Override
				protected Thread createWorkerThread(final Socket socket) {
					return BaseListener.this.createWorkerThread(socket);
				}
			};
		} catch (final BindException e) {
			getExceptionRegistry().throwException(BaseListenerException.class, 1002,
					e, getPort());
		} catch (final IOException e) {
			getExceptionRegistry().throwException(BaseListenerException.class, 1001,
					e, getPort());
		}

		// run the thread
		listenerThread.setDaemon(false);
		listenerThread.start();
	}

	protected Thread createWorkerThread(Socket socket) {
		return new WorkerThread(socket) {

			@Override
			public void run() {

				try {
					final BufferedReader in = createSocketReader();
					final PrintWriter out = createSocketWriter();

					while (!out.checkError()) {
						final String input = in.readLine();
						final String output = BaseListener.this.handleInput(input);

						// write the answer
						if (output == null) {
							out.println("");
						} else {
							out.println(output);
						}
					}
				} catch (final IOException e) {
					getExceptionRegistry().throwException(BaseListenerException.class,
							1004, e, getPort());
				}
			}
		};
	}

	protected String handleInput(final String input) {
		return "";
	}

	protected IExceptionRegistry getExceptionRegistry() {
		return exceptionRegistry;
	}

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
}
