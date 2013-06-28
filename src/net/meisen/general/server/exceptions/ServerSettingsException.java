package net.meisen.general.server.exceptions;

/**
 * Exception thrown when something is wrong with the defined settings.
 * 
 * @author pmeisen
 * 
 */
public class ServerSettingsException extends RuntimeException {
	private static final long serialVersionUID = 7913240737329170686L;

	/**
	 * Creates an exception which should been thrown whenever there is no other
	 * reason for the exception, i.e. the exception is the root.
	 * 
	 * @param message
	 *          the message of the exception
	 */
	public ServerSettingsException(final String message) {
		super(message);
	}

	/**
	 * Creates an exception which should been thrown whenever another
	 * <code>Throwable</code> is the reason for this.
	 * 
	 * @param message
	 *          the message of the exception
	 * @param t
	 *          the reason for the exception
	 */
	public ServerSettingsException(final String message, final Throwable t) {
		super(message, t);
	}
}
