package net.meisen.general.server.api;

import java.util.Collection;

/**
 * 
 * @author pmeisen
 * 
 */
public interface IControlMessage {

	/**
	 * The available types of a <code>ControlMessage</code>.
	 * 
	 * @author pmeisen
	 * 
	 */
	public enum MessageType {
		/**
		 * The message is data, which can be seen as the reply to a request.
		 */
		REPLY,
		/**
		 * The message is a status, which identifies the status of the received
		 * data
		 */
		REPLYSTATUS,
		/**
		 * The message is a request message only, i.e. should be used to request
		 * data
		 */
		REQUEST;
	}

	/**
	 * The identifier of a message.
	 * 
	 * @return the identifier of a message
	 */
	public String getMessageIdentifier();

	/**
	 * Get the types of a message.
	 * 
	 * @return the types of a message.
	 */
	public Collection<MessageType> getType();

	/**
	 * Executes the message (normally a received one), i.e. does whatever the
	 * message stands-for
	 * 
	 * @see MessageType#REQUEST
	 */
	public void execute();
}
