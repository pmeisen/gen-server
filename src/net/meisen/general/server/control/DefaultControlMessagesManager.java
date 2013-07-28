package net.meisen.general.server.control;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.meisen.general.genmisc.exceptions.registry.IExceptionRegistry;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.server.api.IControlMessage;
import net.meisen.general.server.api.IControlMessagesManager;
import net.meisen.general.server.exceptions.ControlMessageException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DefaultControlMessagesManager implements IControlMessagesManager {
	private final static Logger LOG = LoggerFactory
			.getLogger(DefaultControlMessagesManager.class);

	private final Map<String, IControlMessage> controlMessages = new HashMap<String, IControlMessage>();

	@Autowired
	@Qualifier("exceptionRegistry")
	private IExceptionRegistry exceptionRegistry;

	@Autowired
	@Qualifier("coreConfiguration")
	private IConfiguration configuration;

	public void addControlMessage(
			final Class<? extends IControlMessage> controlMessageClazz) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("Adding new message of type '" + controlMessageClazz + "'");
		}

		// create the instance
		IControlMessage msg = null;
		try {
			msg = configuration.createInstance(controlMessageClazz);
		} catch (final Exception e) {
			exceptionRegistry.throwException(ControlMessageException.class, 1000, e,
					controlMessageClazz.getName());
		}

		// get the identifier of the message
		final String id = msg.getMessageIdentifier();
		if (controlMessages.containsKey(id)) {
			final IControlMessage knownMsg = controlMessages.get(id);
			if (knownMsg.getClass().equals(controlMessageClazz)) {
				// nothing to do it was just defined twice
			} else if (LOG.isWarnEnabled()) {
				LOG.warn("Another message with id '" + id + "' is already added ('"
						+ controlMessageClazz.getName() + "').");
			}
		} else {
			controlMessages.put(id, msg);
		}

	}

	public void addControlMessages(
			final Collection<Class<? extends IControlMessage>> controlMessageClazzes) {
		for (final Class<? extends IControlMessage> clazz : controlMessageClazzes) {
			addControlMessage(clazz);
		}
	}

	// NULLRECEIVED("NLRCVD"), DATARECEIVED("RCVD"), CMD_SHUTDOWN("SHUTDOWN");
	//
	// private final static Map<String, ControlMessages> commands = new
	// HashMap<String, ControlMessages>();
	//
	// private final String msg;
	//
	// private ControlMessages(final String msg) {
	//
	// // make sure that we don't have any null messages
	// if (msg == null) {
	// throw new NullPointerException("The msg cannot be null.");
	// }
	//
	// // they all should be upper case
	// this.msg = msg.toUpperCase();
	// }
	//
	// public static ControlMessages getControl(final String msg) {
	//
	// }
}
