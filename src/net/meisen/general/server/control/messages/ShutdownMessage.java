package net.meisen.general.server.control.messages;

import net.meisen.general.server.api.IControlMessage;

public class ShutdownMessage implements IControlMessage {

	@Override
	public String getMessageIdentifier() {
		return "SHUTDOWN";
	}
}
