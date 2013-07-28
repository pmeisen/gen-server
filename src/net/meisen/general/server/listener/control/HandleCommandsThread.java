package net.meisen.general.server.listener.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import net.meisen.general.server.api.IControlMessagesManager;
import net.meisen.general.server.listener.utility.WorkerThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle the commands retrieved on the <code>Socket</code>.
 * 
 * @author pmeisen
 * 
 */
public class HandleCommandsThread extends WorkerThread {
	private final static Logger LOG = LoggerFactory
			.getLogger(HandleCommandsThread.class);

	private IControlMessagesManager controlMessagesManager;

	/**
	 * The default constructor which defines the <code>Socket</code> to handle
	 * within <code>this</code> instance.
	 * 
	 * @param input
	 *          the <code>Socket</code> to handle the data from within
	 *          <code>this</code> instance
	 * @param controlMessagesManager
	 *          the <code>ControlMessagesManager</code> to be used
	 */
	public HandleCommandsThread(final Socket input,
			final IControlMessagesManager controlMessagesManager) {
		super(input);

		this.controlMessagesManager = controlMessagesManager;
	}

	@Override
	public void run() {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Starting the connection thread...");
		}

		// get the input
		final Socket input = getSocket();

		// make sure we have an input
		if (input != null) {
			try {
				final BufferedReader in = createSocketReader();
				final PrintWriter out = createSocketWriter();

				while (!out.checkError()) {

					final String msg = in.readLine();
					if (msg == null) {
						out.println("[NLRCVD]");
					} else {
						out.println("[RCVD]");
						System.out.println(msg);
					}
				}
			} catch (final IOException e) {
				if (LOG.isWarnEnabled()) {
					LOG.warn("Read on socket interrupted.", e);
				}
			}
		}

		if (LOG.isTraceEnabled()) {
			LOG.trace("Ending the connection thread...");
		}
	}
}