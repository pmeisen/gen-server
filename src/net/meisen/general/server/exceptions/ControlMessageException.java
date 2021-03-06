package net.meisen.general.server.exceptions;

/**
 * Exception thrown whenever a problem with <code>ControlMessages</code> occur.
 * 
 * @author pmeisen
 * 
 */
public class ControlMessageException extends RuntimeException {
	private static final long serialVersionUID = 7268719248335330247L;

	/**
	 * Creates an exception which should been thrown whenever there is no other
	 * reason for the exception, i.e. the exception is the root.
	 * 
	 * @param message
	 *          the message of the exception
	 */
	public ControlMessageException(final String message) {
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
	public ControlMessageException(final String message, final Throwable t) {
		super(message, t);
	}
}
