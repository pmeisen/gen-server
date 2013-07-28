package net.meisen.general.server.control.messages;

import java.util.Arrays;
import java.util.Collection;

import net.meisen.general.server.api.IControlMessage;

public class NullReceivedMessage implements IControlMessage {

	@Override
	public String getMessageIdentifier() {
		return "NLRCVD";
	}

	@Override
	public Collection<MessageType> getType() {
		return Arrays.asList(MessageType.RECEIVEDSTATUS);
	}

	@Override
	public void execute() {
		throw new IllegalStateException("Cannot execute a NullReceivedMessage");
	}
}
