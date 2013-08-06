package net.meisen.general.server.control.messages;

import java.util.Arrays;
import java.util.Collection;

import net.meisen.general.server.Server;
import net.meisen.general.server.api.IControlMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Message send to shut-down the server
 * 
 * @author pmeisen
 * 
 */
public class ShutdownMessage implements IControlMessage {

	@Autowired
	@Qualifier("server")
	private Server server;

	@Override
	public String getMessageIdentifier() {
		return "SHUTDOWN";
	}

	@Override
	public Collection<MessageType> getType() {
		return Arrays.asList(MessageType.REQUEST);
	}

	@Override
	public void execute() {
		server.shutdown();
	}
}
