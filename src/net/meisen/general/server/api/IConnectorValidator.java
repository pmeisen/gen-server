package net.meisen.general.server.api;

import net.meisen.general.server.exceptions.ServerSettingsException;
import net.meisen.general.server.settings.pojos.Connector;

/**
 * The interface which marks a <code>Listener</code> to be able to be validated.
 * If a <code>Listener</code> implements this interface, the
 * <code>ServerSettings</code> will validate the <code>Listener</code>.
 * 
 * @author pmeisen
 * 
 * @see IServerSettings#validate()
 * 
 */
public interface IConnectorValidator {

	/**
	 * Called to validate the passed <code>Connector</code> for the concrete
	 * implementation to be used with.
	 * 
	 * @param connector
	 *          the <code>Connector</code> to be validated
	 * 
	 * @return <code>true</code> if the validation was successful, otherwise
	 *         <code>false</code> (or better throw an exception which explains the
	 *         exception)
	 * 
	 * @throws ServerSettingsException
	 *           if the validation failed
	 */
	public boolean validate(final Connector connector)
			throws ServerSettingsException;
}
