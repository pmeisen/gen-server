package net.meisen.general.server.control;

/**
 * Exceptions thrown by the <code>ServerController</code>.
 * 
 * @author pmeisen
 * 
 */
public class ServerControllerException extends RuntimeException {
	private static final long serialVersionUID = 1102110569844498163L;

	/**
	 * Creates an exception which should been thrown whenever there is no other
	 * reason for the exception, i.e. the exception is the root.
	 * 
	 * @param message
	 *          the message of the exception
	 */
	public ServerControllerException(final String message) {
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
	public ServerControllerException(final String message, final Throwable t) {
		super(message, t);
	}
}
