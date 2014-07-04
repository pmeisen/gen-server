package net.meisen.general.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.meisen.general.genmisc.exceptions.registry.IExceptionRegistry;
import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.helper.SpringHelper;
import net.meisen.general.server.api.IListener;
import net.meisen.general.server.api.IServerSettings;
import net.meisen.general.server.api.IServerSettingsManager;
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
	@Qualifier("serverSettingsManager")
	private IServerSettingsManager serverSettingsManager;

	@Autowired
	@Qualifier("listenerFactory")
	private ListenerFactory listenerFactory;

	@Autowired
	@Qualifier(IConfiguration.coreExceptionRegistryId)
	private IExceptionRegistry exceptionRegistry;

	private List<IListener> listeners;

	private Thread serverThread;

	private boolean started = false;
	private Throwable startException = null;

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
	public void startAsync() {

		if (serverThread == null) {
			serverThread = new Thread() {

				@Override
				public void run() {
					try {
						Server.this.start();
					} catch (final Throwable e) {
						startException = e;
					}
				}
			};
			serverThread.setName("ServerMainThread");
			serverThread.setDaemon(false);
			serverThread.start();
		} else {
			if (LOG.isWarnEnabled()) {
				LOG.warn("The server was already started when startAsync was called.");
			}
		}
	}

	/**
	 * Starts the <code>Server</code> in the current <code>Thread</code> if no
	 * other <code>serverThread</code> is specified.
	 */
	public void start() {
		final IServerSettings finalServerSettings = getServerSettings();

		// check some pre-conditions
		if (exceptionRegistry == null) {
			throw new ServerInitializeException(
					"The Server was not created correctly, did you use Server.createServer().");
		} else if (finalServerSettings == null) {
			exceptionRegistry.throwException(ServerInitializeException.class,
					1000);
		} else if (this.listeners != null) {
			exceptionRegistry.throwException(ServerInitializeException.class,
					1003);
		}

		// define the current thread if none is used so far
		if (serverThread == null) {
			serverThread = Thread.currentThread();
		}

		// initialize each listener
		synchronized (serverThread) {
			final List<IListener> listeners = new ArrayList<IListener>();
			for (final Connector c : finalServerSettings.getConnectorSettings()) {
				final IListener listener = listenerFactory.createListener(c
						.getListener());

				// check if we have a listener, otherwise invalid configuration
				if (listener == null) {
					// exception
				}

				// log
				if (LOG.isDebugEnabled()) {
					LOG.debug("Start to initialize listener '"
							+ listener.toString() + "' on port '" + c.getPort()
							+ "'...");
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
					LOG.debug("Opening listener '" + listener.toString()
							+ "'...");
				}

				listener.open();
			}

			// register the shutdown hook to finish the whole thing gracefully
			Runtime.getRuntime().addShutdownHook(new Thread() {

				@Override
				public void run() {

					// check if the server is running and shut it down if so
					if (Server.this.isRunning()) {
						if (LOG.isInfoEnabled()) {
							LOG.info("The server will be shut down because of a ShutdownHook.");
						}

						Server.this.shutdown();
					}
				}
			});

			// keep the opened listeners
			this.listeners = Collections.synchronizedList(listeners);

			// set the started flag
			this.started = true;

			// let's wait for the thread
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
	 * Check if the server is still starting.
	 * 
	 * @return {@code true} if the server is still starting, otherwise
	 *         {@code false}
	 */
	public boolean isStarting() {
		return !isFailed() && !isRunning();
	}

	/**
	 * Checks if the asynchronous start failed.
	 * 
	 * @return {@code true} if the start failed, otherwise {@code false}
	 */
	public boolean isFailed() {
		return startException != null;
	}

	/**
	 * Gets the exception thrown when starting, if one occurred, otherwise
	 * {@code false}.
	 * 
	 * @return the exception thrown when starting
	 */
	public Throwable getFailedException() {
		return startException;
	}

	/**
	 * Checks if the server is currently running.
	 * 
	 * @return <code>true</code> if it's running, otherwise <code>false</code>
	 */
	public boolean isRunning() {
		return this.started;
	}

	/**
	 * Used to shutdown the server correctly, i.e. inform all listeners to
	 * shutdown and let them shutdown successfully.
	 */
	public void shutdown() {

		if (!this.started) {
			return;
		} else if (listeners == null) {
			// do nothing
		} else {

			synchronized (serverThread) {
				for (final IListener listener : listeners) {

					// log
					if (LOG.isDebugEnabled()) {
						LOG.debug("Closing listener '" + listener.toString()
								+ "'...");
					}

					// if a listener cannot shutdown we still should try to
					// shutdown the others correctly
					try {
						listener.close();
					} catch (final RuntimeException e) {
						if (LOG.isErrorEnabled()) {
							LOG.error("Error while closing the listener '"
									+ listener.toString() + "'", e);
						}
					}
				}

				// reset the listeners
				listeners = null;
			}
		}

		// notify the thread to be awaken again
		synchronized (serverThread) {
			serverThread.notifyAll();
		}

		// release thread
		serverThread = null;
		this.started = false;
	}

	/**
	 * Gets the loaded <code>ServerSettings</code> of <code>this</code>
	 * instance.
	 * 
	 * @return the <code>ServerSettings</code> used by <code>this</code>
	 */
	public IServerSettings getServerSettings() {
		return serverSettingsManager.getServerSettings();
	}

	/**
	 * Creates a <code>Server</code> instance using the default configuration
	 * file found on class-path.
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
	 *            the file to load the server from
	 * 
	 * @return the created <code>Server</code> instance
	 */
	public static Server createServer(final String coreConfig) {
		return createServer(coreConfig, Server.class);
	}

	/**
	 * Creates a <code>Server</code> instance using the defined
	 * <code>coreConfig</code> file, which is searched for on the class-path.
	 * 
	 * @param coreConfig
	 *            the file to load the server from
	 * @param context
	 *            the context to look for the defined <code>coreConfig</code> at
	 *            (i.e. the class)
	 * 
	 * @return the created <code>Server</code> instance
	 */
	public static Server createServer(final String coreConfig,
			final Class<?> context) {
		// load the coreSettings
		try {
			final ConfigurationCoreSettings settings = ConfigurationCoreSettings
					.loadCoreSettings(coreConfig, context);

			return settings.getConfiguration().getModule("server");
		} catch (final RuntimeException e) {
			// spring errors are hard to understand, let's get back to the
			// normal
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
	 *            the arguments passed to the main-method
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
