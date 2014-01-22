package net.meisen.general.server.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import net.meisen.general.genmisc.exceptions.registry.IExceptionRegistry;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.server.api.IConnectorValidator;
import net.meisen.general.server.api.IListener;
import net.meisen.general.server.api.IServerSettings;
import net.meisen.general.server.api.IServerSettingsManager;
import net.meisen.general.server.exceptions.ServerSettingsException;
import net.meisen.general.server.settings.listener.ListenerFactory;
import net.meisen.general.server.settings.pojos.Connector;

/**
 * The default implementation of the <code>ServerSettings</code> interface.
 * 
 * @author pmeisen
 * 
 * @see IServerSettings
 * 
 */
public class DefaultServerSettings implements IServerSettings {
	private final static Logger LOG = LoggerFactory
			.getLogger(DefaultServerSettings.class);

	@Autowired
	@Qualifier(IConfiguration.coreExceptionRegistryId)
	private IExceptionRegistry exceptionRegistry;

	@Autowired
	@Qualifier("listenerFactory")
	private ListenerFactory listenerFactory;

	private List<Connector> connectorSettings = new ArrayList<Connector>();

	private boolean defaultSettings = false;
	private boolean failOnUnresolvableListeners = true;

	/**
	 * Adds the settings of a <code>Connector</code> to the
	 * <code>ServerSettings</code>.
	 * 
	 * @param connectorSetting
	 *            the <code>Connector</code> to be added
	 */
	public void addConnectorSetting(final Connector connectorSetting) {
		connectorSettings.add(connectorSetting);
	}

	/**
	 * Adds a <code>Collection</code> of <code>Connector</code> instances to
	 * <code>this</code>.
	 * 
	 * @param connectorSettings
	 *            the <code>Collection</code> of <code>Connector</code>
	 *            instances to be added
	 */
	public void addConnectorSettings(
			final Collection<Connector> connectorSettings) {
		this.connectorSettings.addAll(connectorSettings);
	}

	/**
	 * Reset all other {@code connectorSettings} by the specified once.
	 * 
	 * @param connectorSettings
	 *            the {@code connectorSettings} to be used
	 */
	public void setConnectorSettings(
			final Collection<Connector> connectorSettings) {
		this.connectorSettings.clear();
		addConnectorSettings(connectorSettings);
	}

	@Override
	public Collection<Connector> getConnectorSettings() {
		return Collections.unmodifiableList(connectorSettings);
	}

	/**
	 * Defines if the instance represents the default settings of the server.
	 * There should be only one instance which is default, otherwise the
	 * <code>ServerSettingsManager</code> is unable to merge the defined
	 * settings.
	 * 
	 * @param defaultSettings
	 *            <code>true</code> if <code>this</code> represents the default
	 *            settings, otherwise <code>false</code>
	 * 
	 * @see IServerSettingsManager
	 */
	public void setDefaultSettings(final boolean defaultSettings) {
		this.defaultSettings = defaultSettings;
	}

	@Override
	public boolean isDefaultSettings() {
		return defaultSettings;
	}

	@Override
	public boolean validate() throws ServerSettingsException {
		final Set<Integer> usedPorts = new HashSet<Integer>();

		// check each connector
		for (final Connector connector : connectorSettings) {
			if (!usedPorts.add(connector.getPort())) {
				exceptionRegistry.throwException(ServerSettingsException.class,
						1000, connector.getPort(),
						(defaultSettings ? "default-" : ""));
			}

			// get the listener
			final IListener listener = listenerFactory.createListener(connector
					.getListener());
			if (listener == null) {
				System.out.println("WOULD FAIL");
				if (isFailOnUnresolvableListeners()) {
					exceptionRegistry.throwException(
							ServerSettingsException.class, 1002,
							connector.getListener(), connector.toString());
				} else {
					if (LOG.isInfoEnabled()) {
						LOG.info("The connector '" + connector.toString()
								+ "' was disabled, because the listener '"
								+ connector.getListener()
								+ "' cannot be created.");
					}

					connector.setEnable(false);
				}
			} else if (listener instanceof IConnectorValidator) {
				final IConnectorValidator validator = (IConnectorValidator) listener;

				// validate it, it can throw it's own exception but if not we
				// should
				// have one as well
				if (!validator.validate(connector)) {
					exceptionRegistry.throwException(
							ServerSettingsException.class, 1001,
							connector.toString());
				}
			}
		}

		return true;
	}

	/**
	 * Checks if the validation of <code>this</code> fails if the defined
	 * listener cannot be resolved.
	 * 
	 * @return <code>true</code> if the validation fails, otherwise
	 *         <code>false</code>
	 * 
	 * @see #validate()
	 * @see ListenerFactory#resolve(String)
	 */
	public boolean isFailOnUnresolvableListeners() {
		return failOnUnresolvableListeners;
	}

	/**
	 * Defines if <code>this</code> should fail on validation if the defined
	 * listener cannot be resolved.
	 * 
	 * @param failOnUnresolvableListeners
	 *            <code>true</code> if the validation should fail, otherwise
	 *            <code>false</code>
	 * 
	 * @see #validate()
	 * @see ListenerFactory#resolve(String)
	 */
	public void setFailOnUnresolvableListeners(
			boolean failOnUnresolvableListeners) {
		this.failOnUnresolvableListeners = failOnUnresolvableListeners;
	}
}
