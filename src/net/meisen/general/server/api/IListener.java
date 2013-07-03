package net.meisen.general.server.api;

import net.meisen.general.server.settings.pojos.Connector;

/**
 * A <code>Listener</code> is an instance which can listen to a specific port.
 * The definition of a <code>Listener</code> is provided by a
 * <code>Connector</code>.<br/>
 * If the <code>Connector</code> has to be validated to be used with the
 * <code>Listener</code> additionally to this interface the
 * <code>IConnectorValidator</code> interface should be implemented as well.
 * 
 * @author pmeisen
 * 
 * @see Connector
 * @see IConnectorValidator
 * 
 */
public interface IListener {

	/**
	 * Is called when the <code>Connector</code> for the <code>Listener</code> is
	 * available and checked.
	 * 
	 * @param connector
	 *          the <code>Connector</code> which defines the <code>Listener</code>
	 */
	public void initialize(final Connector connector);

	/**
	 * Opens the <code>Listener</code>, i.e. starts the work of it
	 */
	public void open();

	/**
	 * Closes the <code>Listener</code>, i.e. stops the work of it
	 */
	public void close();
}
