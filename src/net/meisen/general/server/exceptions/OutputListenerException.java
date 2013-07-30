package net.meisen.general.server.exceptions;

/**
 * The exception thrown whenever something with the <code>OutputListener</code>
 * is wrong.
 * 
 * @author pmeisen
 * 
 */
public class OutputListenerException extends RuntimeException {
	private static final long serialVersionUID = 5805249350220895373L;

	/**
	 * Creates an exception which should been thrown whenever there is no other
	 * reason for the exception, i.e. the exception is the root.
	 * 
	 * @param message
	 *          the message of the exception
	 */
	public OutputListenerException(final String message) {
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
	public OutputListenerException(final String message, final Throwable t) {
		super(message, t);
	}
}
