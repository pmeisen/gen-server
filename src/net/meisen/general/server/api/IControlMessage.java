package net.meisen.general.server.api;

import java.util.Collection;

public interface IControlMessage {
	
	public enum MessageType {
		RECEIVEDSTATUS,
		REQUEST;
	}

	public String getMessageIdentifier();

	public Collection<MessageType> getType();
	
	public void execute();
}
