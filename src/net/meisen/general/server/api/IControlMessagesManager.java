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

	/**
	 * Method to add a new {@code ControlMessage} to the manager.
	 * 
	 * @param controlMessageClazz
	 *            the class of the {@code ControlMessage} to be added
	 * @param override
	 *            {@code true} if it's expected that the adding will override
	 *            another message, otherwise {@code false}
	 */
	public void addControlMessage(
			final Class<? extends IControlMessage> controlMessageClazz,
			final boolean override);
}
