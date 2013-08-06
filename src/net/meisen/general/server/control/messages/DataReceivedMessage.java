package net.meisen.general.server.control.messages;

import java.util.Arrays;
import java.util.Collection;

import net.meisen.general.server.api.IControlMessage;

/**
 * Message send as answer if a non-<code>null</code> value was received.
 * 
 * @author pmeisen
 * 
 */
public class DataReceivedMessage implements IControlMessage {

	@Override
	public String getMessageIdentifier() {
		return "RCVD";
	}

	@Override
	public Collection<MessageType> getType() {
		return Arrays.asList(MessageType.REPLYSTATUS);
	}

	@Override
	public void execute() {
		throw new IllegalStateException("Cannot execute a DataReceivedMessage");
	}
}
