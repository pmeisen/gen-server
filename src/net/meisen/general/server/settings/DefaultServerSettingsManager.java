package net.meisen.general.server.settings;

import java.util.HashSet;
import java.util.Set;

import net.meisen.general.genmisc.exceptions.registry.IExceptionRegistry;
import net.meisen.general.server.api.IListener;
import net.meisen.general.server.api.IServerSettings;
import net.meisen.general.server.api.IServerSettingsManager;
import net.meisen.general.server.exceptions.ServerInitializeException;
import net.meisen.general.server.settings.listener.ListenerFactory;
import net.meisen.general.server.settings.pojos.Connector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The default implementation of the <code>ServerSettingsManager</code>.
 * 
 * @author pmeisen
 * 
 * @see IServerSettingsManager
 * 
 */
public class DefaultServerSettingsManager implements IServerSettingsManager {

	@Autowired
	@Qualifier("defaultServerSettings")
	private IServerSettings defaultSettings;

	@Autowired(required = false)
	@Qualifier("serverSettings")
	private IServerSettings userSettings;

	@Autowired
	@Qualifier("exceptionRegistry")
	private IExceptionRegistry exceptionRegistry;

	@Autowired
	@Qualifier("listenerFactory")
	private ListenerFactory listenerFactory;

	// the merged settings are available after initializing the module
	private IServerSettings mergedSettings = null;

	@Override
	public void initialize() {

		if (defaultSettings == null) {
			exceptionRegistry.throwException(ServerInitializeException.class, 1000);
		} else if (!defaultSettings.isDefaultSettings()) {
			exceptionRegistry.throwException(ServerInitializeException.class, 1001,
					defaultSettings.getClass().getName());
		} else if (!defaultSettings.validate()) {
			// nothing to do the validation can throw the exception, if it's not
			// validated and comes here we should just give a general message, to be
			// sure
		} else if (userSettings == null) {
			// if we don't have any user-settings we use the default
			mergedSettings = defaultSettings;
		} else if (userSettings.isDefaultSettings()) {
			exceptionRegistry.throwException(ServerInitializeException.class, 1002,
					userSettings.getClass().getName());
		} else if (!userSettings.validate()) {
			// nothing to do the validation can throw the exception, if it's not
			// validated and comes here we should just give a general message, to be
			// sure

		} else {
			// we have to merge the default and user-settings
			mergedSettings = mergeSettings(defaultSettings, userSettings);
		}
	}

	/**
	 * Merges all the default- and user-settings to one
	 * <code>ServerSettings</code> instance. The default-settings are used for
	 * fall-back, i.e. if the setting isn't defined in the user-settings.
	 * 
	 * @param defaultSettings
	 *          the default settings (used for fall-back)
	 * @param userSettings
	 *          the defined user-settings, which override the default settings
	 * @return
	 */
	protected IServerSettings mergeSettings(
			final IServerSettings defaultSettings, final IServerSettings userSettings) {

		// get through the user's connectors
		if (userSettings == null) {
			return defaultSettings;
		} else {
			final Set<Integer> usedPorts = new HashSet<Integer>();
			final Set<Class<? extends IListener>> usedUserListener = new HashSet<Class<? extends IListener>>();

			// create a new DefaultServerSettings
			final DefaultServerSettings mergedSettings = new DefaultServerSettings();

			// get through all the connectors
			for (final Connector connector : userSettings.getConnectorSettings()) {
				final String listener = connector.getListener();
				final Class<? extends IListener> listenerClass = listenerFactory
						.resolve(listener);

				// add the port as used, even if it is disabled we won't use it later
				usedPorts.add(connector.getPort());
				if (listenerClass != null) {
					usedUserListener.add(listenerClass);
				}

				// add the connector if it's enabled
				if (connector.isEnable()) {
					mergedSettings.addConnectorSetting(connector);
				}
			}

			// now add the default once which are needed
			for (final Connector connector : defaultSettings.getConnectorSettings()) {
				final String listener = connector.getListener();
				final Class<? extends IListener> listenerClass = listenerFactory
						.resolve(listener);

				if (listenerClass != null && !usedPorts.contains(connector.getPort())
						&& !usedUserListener.contains(listenerClass)
						&& connector.isEnable()) {
					mergedSettings.addConnectorSetting(connector);
				}
			}

			return mergedSettings;
		}
	}

	@Override
	public IServerSettings getServerSettings() {
		return mergedSettings;
	}
}
