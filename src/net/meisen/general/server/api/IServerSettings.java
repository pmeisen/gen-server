package net.meisen.general.server.api;

import java.util.Collection;

import net.meisen.general.server.exceptions.ServerSettingsException;
import net.meisen.general.server.settings.pojos.Connector;

/**
 * Interface which defines the <code>ServerSettings</code>, those settings which
 * are loaded by the <code>serverConfigurationLoader</code> loader definition
 * (or the <code>serverDefaultConfigurationLoader</code>).
 * 
 * @author pmeisen
 * 
 */
public interface IServerSettings {

	/**
	 * Gets the <code>Connector</code> instances defined for <code>this</code>
	 * settings.
	 * 
	 * @return the <code>Connector</code> instances defined for <code>this</code>
	 *         settings
	 */
	public Collection<Connector> getConnectorSettings();

	/**
	 * Checks if the instance represents the default-settings, i.e.
	 * <code>true</code> is returned if the settings are the default-settings,
	 * otherwise <code>false</code>.
	 * 
	 * @return <code>true</code> if the settings are the default-settings,
	 *         otherwise <code>false</code>
	 */
	public boolean isDefaultSettings();

	/**
	 * Validates the defined <code>ServerSettings</code> instance. The
	 * implementation has to validate the defined <code>Listeners</code> with
	 * their defined <code>Connector</code>, if those implement the
	 * <code>IConnectorValidator</code> interface.
	 * 
	 * @return <code>true</code> if the validation was successful, otherwise
	 *         <code>false</code> (normally only <code>true</code> should be
	 *         returned, otherwise a <code>ServerSettingsException</code> should
	 *         be thrown)
	 * 
	 * @throws ServerSettingsException
	 *           if the validation failed
	 * 
	 * @see Connector
	 * @see IListener
	 * @see IConnectorValidator#validate(Connector)
	 */
	public boolean validate() throws ServerSettingsException;

}
