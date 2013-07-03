package net.meisen.general.server.settings.pojos;

/**
 * The <code>Connector</code> implementation is retrieved from the
 * XML-definition.
 * 
 * @author pmeisen
 * 
 */
public class Connector extends Extension {
	private String listener;
	private int port = -1;
	private boolean enable = true;

	/**
	 * Gets the defined port of the connector to be used.
	 * 
	 * @return the defined port of the connector to be used
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port to be used by the connector, must be a valid port-number.
	 * 
	 * @param port
	 *          the port to be used by the connector
	 */
	public void setPort(final int port) {
		this.port = port;
	}

	/**
	 * Gets the name or class of the listener which defines the behavior of the
	 * connector.
	 * 
	 * @return the name or class of the listener
	 */
	public String getListener() {
		return listener;
	}

	/**
	 * Defines the name or class of the listener which defines the behavior of the
	 * connector
	 * 
	 * @param listener
	 *          the name or class of the listener to be used
	 */
	public void setListener(final String listener) {
		this.listener = listener;
	}

	/**
	 * Gets the status of the connector, i.e. if it's enabled or not.
	 * 
	 * @return the status of the connector, i.e. if it's enabled (
	 *         <code>true</code>) or not (<code>false</code>).
	 */
	public boolean isEnable() {
		return enable;
	}

	/**
	 * Sets the status of the connector, i.e. if it's enabled or not.
	 * 
	 * @param enable
	 *          <code>true</code> if the connector should be enabled, otherwise
	 *          <code>false</code>
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Override
	public String toString() {
		final String l = getListener();
		final String suffix = " on port " + getPort() + " ('"
				+ (l == null ? "default" : l) + "')";

		if (isEnable()) {
			return "Connector" + suffix;
		} else {
			return "Disabled connector" + suffix;
		}
	}
}
