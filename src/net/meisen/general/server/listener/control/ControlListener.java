package net.meisen.general.server.listener.control;

import java.net.Socket;

import net.meisen.general.server.api.IControlMessagesManager;
import net.meisen.general.server.api.impl.BaseListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Listener to control the server, e.g. shutdown.
 * 
 * @author pmeisen
 * 
 */
public class ControlListener extends BaseListener {

	/**
	 * The name of this <code>Listener</code> used when defined it, instead of the
	 * class-name
	 */
	public static final String NAME = "CONTROL";

	@Autowired
	@Qualifier("controlMessagesManager")
	private IControlMessagesManager controlMessagesManager;

	@Override
	protected Thread createWorkerThread(Socket socket) {
		return new HandleCommandsThread(socket, controlMessagesManager);
	}

	@Override
	public String toString() {
		return NAME + (getPort() == -1 ? "" : " (" + getPort() + ")");
	}
}
