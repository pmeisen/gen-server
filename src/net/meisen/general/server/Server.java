package net.meisen.general.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.meisen.general.genmisc.exceptions.registry.IExceptionRegistry;
import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.helper.SpringHelper;
import net.meisen.general.server.api.IListener;
import net.meisen.general.server.api.IServerSettings;
import net.meisen.general.server.exceptions.ServerInitializeException;
import net.meisen.general.server.settings.listener.ListenerFactory;
import net.meisen.general.server.settings.pojos.Connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The <code>Server</code> instance which starts the server and all the defined
 * listeners.
 * 
 * @author pmeisen
 * 
 * @see IListener
 * 
 */
public class Server {
	private final static Logger LOG = LoggerFactory.getLogger(Server.class);

	private final static String extendedConfiguratorCoreConfig = "sbconfigurator-core.xml";

	@Autowired
	@Qualifier("finalServerSettings")
	private IServerSettings finalServerSettings;

	@Autowired
	@Qualifier("listenerFactory")
	private ListenerFactory listenerFactory;

	@Autowired
	@Qualifier("exceptionRegistry")
	private IExceptionRegistry exceptionRegistry;

	private List<IListener> listeners;

	private Thread serverThread;

	/**
	 * Hide the default constructor, please use {@link Server#createServer()} to
	 * create a <code>Server</code>.
	 * 
	 * @see Server#createServer()
	 * @see Server#createServer(String)
	 */
	private Server() {
		// nothing to do
	}

	/**
	 * Starts the <code>Server</code> in a new <code>Thread</code>.
	 */
	public synchronized void startAsync() {

		serverThread = new Thread() {

			@Override
			public void run() {
				Server.this.start();
			}
		};
		serverThread.setName("ServerMainThread");
		serverThread.setDaemon(false);
		serverThread.start();
	}

	/**
	 * Starts the <code>Server</code> in the current <code>Thread</code> if no
	 * other <code>serverThread</code> is specified.
	 */
	public synchronized void start() {

		// check some pre-conditions
		if (exceptionRegistry == null) {
			throw new ServerInitializeException(
					"The Server was not created correctly, did you use Server.createServer().");
		} else if (finalServerSettings == null) {
			exceptionRegistry.throwException(ServerInitializeException.class, 1000);
		} else if (this.listeners != null) {
			exceptionRegistry.throwException(ServerInitializeException.class, 1003);
		}

		// define the current thread if none is used so far
		if (serverThread == null) {
			serverThread = Thread.currentThread();
		}

		// initialize each listener
		final List<IListener> listeners = new ArrayList<IListener>();
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

		// register the shutdown hook to finish the whole thing gracefully
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				if (LOG.isInfoEnabled()) {
					LOG.info("The server will be shut down because of a ShutdownHook.");
				}

				Server.this.shutdown();
			}
		});

		// keep the opened listeners
		this.listeners = Collections.synchronizedList(listeners);

		synchronized (serverThread) {
			try {
				serverThread.wait();
			} catch (final InterruptedException e) {
				if (LOG.isTraceEnabled()) {
					LOG.info("The server thread was interrupted.");
				}
			}
		}
	}

	/**
	 * Used to shutdown the server correctly, i.e. inform all listeners to
	 * shutdown and let them shutdown successfully.
	 */
	public synchronized void shutdown() {

		if (listeners == null) {
			// nothing to do
		} else {

			for (final IListener listener : listeners) {

				// log
				if (LOG.isDebugEnabled()) {
					LOG.debug("Closing listener '" + listener.toString() + "'...");
				}

				// if a listener cannot shutdown we still should try to shutdown the
				// others correctly
				try {
					listener.close();
				} catch (final RuntimeException e) {
					if (LOG.isErrorEnabled()) {
						LOG.error(
								"Error while closing the listener '" + listener.toString()
										+ "'", e);
					}
				}
			}

			// reset the listeners
			listeners = null;
		}

		// notify the thread to be awaken again
		synchronized (serverThread) {
			serverThread.notifyAll();
		}
	}

	/**
	 * Gets the loaded <code>ServerSettings</code> of <code>this</code> instance.
	 * 
	 * @return the <code>ServerSettings</code> used by <code>this</code>
	 */
	public IServerSettings getServerSettings() {
		return finalServerSettings;
	}

	/**
	 * Creates a <code>Server</code> instance using the default configuration file
	 * found on class-path.
	 * 
	 * @return the created <code>Server</code> instance
	 */
	public static Server createServer() {
		return createServer(extendedConfiguratorCoreConfig);
	}

	/**
	 * Creates a <code>Server</code> instance using the defined
	 * <code>coreConfig</code> file, which is searched for on the class-path.
	 * 
	 * @param coreConfig
	 *          the file to load the server from
	 * 
	 * @return the created <code>Server</code> instance
	 */
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

	/**
	 * Main entry point, which creates and starts the default server. The server
	 * registers a shut-down hook to be shutdown gracefully.
	 * 
	 * @param args
	 *          the arguments passed to the main-method
	 */
	public static void main(final String[] args) {

		try {

			// create the server
			final Server server = createServer();

			// now start the server
			server.start();
		} catch (final Throwable t) {
			if (LOG.isErrorEnabled()) {
				LOG.error(t.getMessage(), t);
			}
		}
	}
}
