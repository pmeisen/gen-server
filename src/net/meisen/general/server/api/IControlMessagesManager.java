package net.meisen.general.server.api;

/**
 * Manager to handle the different available messages.
 * 
 * @author pmeisen
 * 
 */
public interface IControlMessagesManager {

	/**
	 * Determines the message for the specified <code>msg</code>-String.
	 * 
	 * @param msg
	 *            the string to be parsed to a message
	 * 
	 * @return the <code>ControlMessage</code> represented by the passed
	 *         <code>msg</code>, or <code>null</code> if no
	 *         <code>ControlMessage</code> exists
	 */
	public IControlMessage determineMessage(final String msg);
}
