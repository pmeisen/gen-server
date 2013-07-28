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

		if (controlMessageClazz == null) {
			exceptionRegistry.throwException(ControlMessageException.class, 1002);
		}

		// create the instance
		IControlMessage msg = null;
		try {
			msg = configuration.createInstance(controlMessageClazz);
		} catch (final Exception e) {
			exceptionRegistry.throwException(ControlMessageException.class, 1000, e,
					controlMessageClazz.getName());
		}

		// validate the identifier
		final String id = validateId(msg.getMessageIdentifier());

		// check if another ControlMessage uses the id
		if (controlMessages.containsKey(id)) {
			final IControlMessage knownMsg = controlMessages.get(id);
			if (knownMsg.getClass().equals(controlMessageClazz)) {
				// nothing to do it was just defined twice
			} else if (LOG.isWarnEnabled()) {
				LOG.warn("Another message with id '" + id + "' is already added ('"
						+ controlMessageClazz.getName() + "').");
			}
		} else {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Adding new message of type '" + controlMessageClazz + "'");
			}

			controlMessages.put(id, msg);
		}
	}

	/**
	 * Validate the identifier and return a unified name.
	 * 
	 * @param id
	 *          the id to be validated and unified
	 * @param controlMessageClazz
	 *          the class of the IControlMessage, cannot be <code>null</code> and
	 *          is just used for logging purposes
	 * 
	 * @return the unified id
	 */
	protected String validateId(final String id) {
		if (id == null || "".equals(id.trim())) {
			exceptionRegistry.throwException(ControlMessageException.class, 1001);

			// never happens
			return null;
		}

		// get the unifiedId
		final String unifiedId = id.trim().toUpperCase();

		return unifiedId;
	}

	public void addControlMessages(
			final Collection<Class<? extends IControlMessage>> controlMessageClazzes) {
		for (final Class<? extends IControlMessage> clazz : controlMessageClazzes) {
			addControlMessage(clazz);
		}
	}

	@Override
	public IControlMessage determineMessage(final String msg) {

		// null is not allowed so let's get away of it right away
		if (msg == null) {
			return null;
		}

		// now trim the message and get it
		final String unified = validateId(msg);

		// get the ControlMessage associated to the unifier
		return controlMessages.get(unified);
	}
}
