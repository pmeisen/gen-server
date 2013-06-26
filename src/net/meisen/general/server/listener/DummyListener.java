package net.meisen.general.server.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.meisen.general.server.api.IListener;

public class DummyListener implements IListener {
	public static final String NAME = "DUMMY";
	
	private final static Logger LOG = LoggerFactory
			.getLogger(DummyListener.class);

	@Override
	public void initialize(final int port) {
		if (LOG.isWarnEnabled()) {
			LOG.warn("Trying to initialize the dummy listener, this listener is just for demonstration purposes and should never be used in production.");
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
}
