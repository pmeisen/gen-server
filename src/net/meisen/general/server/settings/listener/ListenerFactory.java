package net.meisen.general.server.settings.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.meisen.general.genmisc.types.Classes;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.server.api.IListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Factory to create the different <code>Listener</code> instances. A
 * <code>Listener</code> can be registered and creatable by name or by it's
 * class.
 * 
 * @author pmeisen
 * 
 * @see IListener
 * 
 */
public class ListenerFactory {
	private final static Logger LOG = LoggerFactory
			.getLogger(ListenerFactory.class);

	private Map<String, Class<? extends IListener>> listeners = new HashMap<String, Class<? extends IListener>>();
	private String defaultListener;

	@Autowired
	@Qualifier(IConfiguration.coreConfigurationId)
	private IConfiguration configuration;

	/**
	 * Registers a <code>listenerClazz</code> with an alias, i.e. a name which can
	 * be used instead of the name to create an instance of the
	 * <code>Listener</code>.
	 * 
	 * @param name
	 *          the name to be the alias for the <code>listenerClazz</code>
	 * @param listenerClazz
	 *          the class to be associated to the specified <code>name</code>
	 */
	public void registerNamedListener(final String name,
			final Class<? extends IListener> listenerClazz) {
		final Class<? extends IListener> oldListener = listeners.put(
				name.toUpperCase(), listenerClazz);

		// do some logging if we override another one with the same name
		if (oldListener != null && !oldListener.equals(listenerClazz)) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("The listener '" + name
						+ "' was defined multiple times. The implementation '"
						+ listenerClazz + "' overrides the '" + oldListener + "'");
			}
		}
	}

	/**
	 * Registers the <code>Map</code> of <code>Listener</code> classes with the
	 * specified name (i.e. the key of the passed map).
	 * 
	 * @param listeners
	 *          the <code>Listener</code> instances to be registered
	 */
	public void registerNamedListeners(
			final Map<String, Class<? extends IListener>> listeners) {
		for (final Entry<String, Class<? extends IListener>> entry : listeners
				.entrySet()) {
			final String name = entry.getKey();
			final Class<? extends IListener> clazz = entry.getValue();

			registerNamedListener(name, clazz);
		}
	}

	/**
	 * Gets the default <code>Listener</code>, i.e. the one used when the name or
	 * class of the <code>Listener</code> to be resolved is <code>null</code>.
	 * 
	 * @return the default <code>Listener</code> to be used if none is specified
	 * 
	 * @see #resolve(String)
	 */
	public String getDefaultListener() {
		return defaultListener;
	}

	/**
	 * Sets the default <code>Listener</code>.
	 * 
	 * @param defaultListener
	 *          the default <code>Listener</code> to be used
	 */
	public void setDefaultListener(final String defaultListener) {
		this.defaultListener = defaultListener;
	}

	/**
	 * Resolves the passed <code>Listener</code> to the correct <code>Class</code>
	 * , i.e. the implementation to be used to create the <code>Listener</code>
	 * instance.
	 * 
	 * @param listener
	 *          the name or the class (as string) of the listener to be resolved
	 * 
	 * @return the <code>Class</code> of the <code>Listener</code> specified by
	 *         the passed <code>listener</code>
	 */
	public Class<? extends IListener> resolve(final String listener) {

		// get the listener's class
		if (listener == null && defaultListener != null) {
			return resolve(defaultListener);
		} else if (listener == null) {
			throw new IllegalStateException(
					"Please specify a defaultListener for the ListenerFactory '"
							+ getClass().getName() + "'.");
		} else if (listeners.containsKey(listener)) {
			return listeners.get(listener.toUpperCase());
		} else {
			final Class<?> clazz = Classes.getClass(listener);
			if (clazz == null) {
				return null;
			} else if (IListener.class.isAssignableFrom(clazz)) {

				@SuppressWarnings("unchecked")
				final Class<? extends IListener> listenerClazz = (Class<? extends IListener>) clazz;
				return listenerClazz;
			} else {
				return null;
			}
		}
	}

	/**
	 * Creates the instance of the <code>Listener</code> which is specified by the
	 * name or the it's class. The created instance will be wired, i.e. annotation
	 * based wiring is performed.
	 * 
	 * @param listener
	 *          the name or the class of the <code>Listener</code> instance to be
	 *          created
	 * 
	 * @return the created and wired instance
	 */
	public IListener createListener(final String listener) {
		return createListener(resolve(listener));
	}

	/**
	 * Creates the instance of the <code>Listener</code> which is specified by the
	 * <code>listenerClazz</code>. The created instance will be wired, i.e.
	 * annotation based wiring is performed.
	 * 
	 * @param listenerClazz
	 *          the class of the <code>Listener</code> instance to be created
	 * 
	 * @return the created and wired instance
	 */
	public IListener createListener(final Class<? extends IListener> listenerClazz) {
		if (listenerClazz == null) {
			return null;
		} else {
			return configuration.createInstance(listenerClazz);
		}
	}
}
