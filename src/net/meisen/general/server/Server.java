package net.meisen.general.server;

import java.util.ArrayList;
import java.util.List;

import net.meisen.general.genmisc.exceptions.registry.IExceptionRegistry;
import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.helper.SpringHelper;
import net.meisen.general.server.api.IListener;
import net.meisen.general.server.api.IServerSettings;
import net.meisen.general.server.settings.listener.ListenerFactory;
import net.meisen.general.server.settings.pojos.Connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class Server {
	private final static Logger LOG = LoggerFactory.getLogger(Server.class);

	private final static String extendedConfiguratorCoreConfig = "sbconfigurator-core.xml";

	@Autowired
	@Qualifier("exceptionRegistry")
	private IExceptionRegistry exceptionRegistry;

	@Autowired
	@Qualifier("finalServerSettings")
	private IServerSettings finalServerSettings;

	@Autowired
	@Qualifier("listenerFactory")
	private ListenerFactory listenerFactory;

	public void start() {
		final List<IListener> listeners = new ArrayList<IListener>();

		// initialize each listener
		for (final Connector c : finalServerSettings.getConnectorSettings()) {
			final IListener listener = listenerFactory
					.createListener(c.getListener());

			// check if we have a listener, otherwise invalid configuration
			if (listener == null) {
				// exception
			}

			// log
			if (LOG.isDebugEnabled()) {
				LOG.debug("Start to initialize listener '" + listener.toString()
						+ "' on port '" + c.getPort() + "'...");
			}

			// open the port to listen for connections
			listener.initialize(c);

			// add the listener because it initialized successfully
			listeners.add(listener);
		}

		// if initialization worked we can open each listener
		for (final IListener listener : listeners) {

			// log
			if (LOG.isDebugEnabled()) {
				LOG.debug("Opening listener '" + listener.toString() + "'...");
			}

			listener.open();
		}
	}

	public IServerSettings getServerSettings() {
		return finalServerSettings;
	}

	public static Server createServer() {
		return createServer(extendedConfiguratorCoreConfig);
	}

	public static Server createServer(final String coreConfig) {

		// load the coreSettings
		try {
			final ConfigurationCoreSettings settings = ConfigurationCoreSettings
					.loadCoreSettings(coreConfig, Server.class);
			
			return settings.getConfiguration().getModule("server");
		} catch (final RuntimeException e) {
			// spring errors are hard to understand, let's get back to the normal
			// exception and forget the spring exceptions
			final RuntimeException noneSpringException = SpringHelper
					.getNoneSpringBeanException(e, RuntimeException.class);

			if (noneSpringException == null) {
				throw (RuntimeException) e;
			} else {
				if (LOG.isWarnEnabled()) {
					LOG.warn("Removed SpringExceptions", e);
				}

				throw noneSpringException;
			}
		}
	}

	public static void main(final String[] args) {
		final Server server = createServer();

		// now start the server
		server.start();
	}
}
