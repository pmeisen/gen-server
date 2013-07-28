package net.meisen.general.server.exceptions;

import net.meisen.general.server.listener.control.ControlListener;

/**
 * Exception thrown by the <code>ControlListener</code>.
 * 
 * @author pmeisen
 * 
 * @see ControlListener
 * 
 */
public class ControlListenerException extends RuntimeException {
	private static final long serialVersionUID = -7552841652053639777L;

	/**
	 * Creates an exception which should been thrown whenever there is no other
	 * reason for the exception, i.e. the exception is the root.
	 * 
	 * @param message
	 *          the message of the exception
	 */
	public ControlListenerException(final String message) {
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
	public ControlListenerException(final String message, final Throwable t) {
		super(message, t);
	}
}
