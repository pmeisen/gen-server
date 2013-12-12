package net.meisen.general.server.control.messages;

import java.util.Arrays;
import java.util.Collection;

import net.meisen.general.server.api.IControlMessage;

/**
 * Message send as answer if a <code>null</code> was received.
 * 
 * @author pmeisen
 * 
 */
public class NullReceivedMessage implements IControlMessage {
	/**
	 * the identifier used for the message
	 */
	public final static String MESSAGE_ID = "NLRCVD";
	
	@Override
	public String getMessageIdentifier() {
		return MESSAGE_ID;
	}

	@Override
	public Collection<MessageType> getType() {
		return Arrays.asList(MessageType.REPLYSTATUS);
	}

	@Override
	public void execute() {
		throw new IllegalStateException("Cannot execute a NullReceivedMessage");
	}
}
