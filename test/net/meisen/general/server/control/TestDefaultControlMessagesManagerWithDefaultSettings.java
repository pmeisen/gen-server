package net.meisen.general.server.control;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.meisen.general.sbconfigurator.runners.JUnitConfigurationRunner;
import net.meisen.general.sbconfigurator.runners.annotations.ContextClass;
import net.meisen.general.server.Server;
import net.meisen.general.server.api.IControlMessage;
import net.meisen.general.server.control.messages.DataReceivedMessage;
import net.meisen.general.server.control.messages.NullReceivedMessage;
import net.meisen.general.server.control.messages.ShutdownMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Tests to check the implementation of the
 * <code>DefaultControlMessagesManager</code> using the default configuration,
 * i.e. the one which is delivered with the artifact.
 * 
 * @author pmeisen
 * 
 */
@RunWith(JUnitConfigurationRunner.class)
@ContextClass(Server.class)
public class TestDefaultControlMessagesManagerWithDefaultSettings {

	@Autowired
	@Qualifier("controlMessagesManager")
	private DefaultControlMessagesManager controlMessagesManager;

	/**
	 * Tests the registration of the <code>NullReceivedMessage</code>.
	 */
	@Test
	public void testNullReceivedMessage() {
		final IControlMessage msg = controlMessagesManager
				.determineMessage("NLRCVD");

		assertNotNull(msg);
		assertTrue(msg instanceof NullReceivedMessage);
	}

	/**
	 * Tests the registration of the <code>DataReceivedMessage</code>.
	 */
	@Test
	public void testDataReceivedMessage() {
		final IControlMessage msg = controlMessagesManager.determineMessage("RCVD");

		assertNotNull(msg);
		assertTrue(msg instanceof DataReceivedMessage);
	}

	/**
	 * Tests the registration of the <code>ShutdownMessage</code>.
	 */
	@Test
	public void testShutdownMessage() {
		final IControlMessage msg = controlMessagesManager
				.determineMessage("shutdown");

		assertNotNull(msg);
		assertTrue(msg instanceof ShutdownMessage);
	}
}
