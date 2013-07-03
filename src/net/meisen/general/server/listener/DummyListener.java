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
	 * The name of this <code>Listener</code> used when defined it, instead of the
	 * class-name
	 */
	public static final String NAME = "DUMMY";

	@Override
	public void initialize(final Connector connector) {
		if (LOG.isWarnEnabled()) {
			LOG.warn("Trying to initialize the dummy listener, this listener is just a dummy for the basic implementation of the server.");
		}
	}

	@Override
	public void open() {
		// nothing to do
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
		// nothing to do
	}
}
