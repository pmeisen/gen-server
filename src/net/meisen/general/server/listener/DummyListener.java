package net.meisen.general.server.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.meisen.general.server.api.IConnectorValidator;
import net.meisen.general.server.api.IListener;
import net.meisen.general.server.exceptions.ServerSettingsException;
import net.meisen.general.server.settings.pojos.Connector;

/**
 * Really, really simple implementation of a <code>IListener</code> and a
 * <code>IConnectorValidator</code>.
 * 
 * @author pmeisen
 * 
 */
public class DummyListener implements IListener, IConnectorValidator {
	private final static Logger LOG = LoggerFactory
			.getLogger(DummyListener.class);

	/**
	 * The name of this <code>Listener</code> used when defined it, instead of
	 * the class-name
	 */
	public static final String NAME = "DUMMY";

	private Thread dummyThread = null;

	@Override
	public void initialize(final Connector connector) {
		if (!connector.isEnable()) {
			// do nothing
		} else if (LOG.isWarnEnabled()) {
			LOG.warn("Trying to initialize the dummy listener, this listener is just a dummy for the basic implementation of the server.");
		}
	}

	@Override
	public void open() {

		// create a dummy thread
		if (dummyThread == null) {
			dummyThread = new Thread() {
				public void run() {
					try {
						// set the thread into waiting mode
						synchronized (this) {
							dummyThread.wait();
						}
					} catch (final InterruptedException e) {
						interrupt();
					}
				}
			};

			dummyThread.setName("DummyListenerThread");
		}

		dummyThread.start();
	}

	@Override
	public String toString() {
		return "Dummy";
	}

	@Override
	public boolean validate(final Connector connector)
			throws ServerSettingsException {
		return true;
	}

	@Override
	public void close() {

		if (dummyThread != null) {

			// close the thread
			synchronized (dummyThread) {
				dummyThread.notifyAll();
			}

			dummyThread = null;
		}
	}
}
