package net.meisen.general.server.api.impl.exceptions;

import net.meisen.general.server.api.impl.BaseListener;

/**
 * Exception thrown by the base-implementation of a <code>BaseListener</code>.
 * 
 * @see BaseListener
 * 
 * @author pmeisen
 * 
 */
public class BaseListenerException extends RuntimeException {
	private static final long serialVersionUID = -481543552992702073L;

	/**
	 * Creates an exception which should been thrown whenever there is no other
	 * reason for the exception, i.e. the exception is the root.
	 * 
	 * @param message
	 *            the message of the exception
	 */
	public BaseListenerException(final String message) {
		super(message);
	}

	/**
	 * Creates an exception which should been thrown whenever another
	 * <code>Throwable</code> is the reason for this.
	 * 
	 * @param message
	 *            the message of the exception
	 * @param t
	 *            the reason for the exception
	 */
	public BaseListenerException(final String message, final Throwable t) {
		super(message, t);
	}
}
