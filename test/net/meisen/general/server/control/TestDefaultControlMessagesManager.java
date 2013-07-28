package net.meisen.general.server.control;

import static org.junit.Assert.assertEquals;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.runners.JUnitConfigurationRunner;
import net.meisen.general.sbconfigurator.runners.annotations.ContextClass;
import net.meisen.general.server.Server;
import net.meisen.general.server.api.IControlMessage;
import net.meisen.general.server.exceptions.ControlMessageException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Tests to check the implementation of the
 * <code>DefaultControlMessagesManager</code>. This tests do create an empty
 * manager, i.e. one without any registered <code>IControlMessage</code>
 * instances.
 * 
 * @author pmeisen
 * 
 */
@RunWith(JUnitConfigurationRunner.class)
@ContextClass(Server.class)
public class TestDefaultControlMessagesManager {

	private abstract class InvalidControlMessage implements IControlMessage {
	}

	@Autowired
	@Qualifier("coreConfiguration")
	private IConfiguration configuration;

	private DefaultControlMessagesManager controlMessagesManager;

	/**
	 * Create an auto-wired instance of the
	 * <code>DefaultControlMessagesManager</code>
	 */
	@Before
	public void initialize() {
		controlMessagesManager = configuration
				.createInstance(DefaultControlMessagesManager.class);
	}

	/**
	 * Check the adding of a <code>null</code> <code>IControlMessage</code>.
	 */
	@Test(expected = ControlMessageException.class)
	public void testInvalidNullClass() {
		controlMessagesManager.addControlMessage(null);
	}

	/**
	 * Check the adding of an unknown (or invalid) <code>IControlMessage</code>
	 * -class.
	 */
	@Test(expected = ControlMessageException.class)
	public void testUnknownClass() {
		controlMessagesManager.addControlMessage(InvalidControlMessage.class);
	}

	/**
	 * Tests the validation of an <code>null</code> identifier.
	 */
	@Test(expected = ControlMessageException.class)
	public void testNullIdValidation() {
		controlMessagesManager.validateId(null);
	}

	/**
	 * Tests the validation of an empty identifier.
	 */
	@Test(expected = ControlMessageException.class)
	public void testEmptyIdValidation() {
		controlMessagesManager.validateId("");
	}

	/**
	 * Tests the validation of an identifier, which contains spaces only.
	 */
	@Test(expected = ControlMessageException.class)
	public void testOnlySpacesIdValidation() {
		controlMessagesManager.validateId("      ");
	}

	/**
	 * Tests the unification of an identifier concerning the upper-case
	 * transformation.
	 */
	@Test
	public void testUpperCaseUnification() {
		assertEquals("MYID", controlMessagesManager.validateId("myId"));
		assertEquals("MYID", controlMessagesManager.validateId("myid"));
		assertEquals("MYID", controlMessagesManager.validateId("MYID"));
	}

	/**
	 * Tests the unification of an identifier concerning the trimming
	 * transformation.
	 */
	@Test
	public void testTrimmingUnification() {
		assertEquals("MYID", controlMessagesManager.validateId(" myId  "));
		assertEquals("MYID", controlMessagesManager.validateId("myId  "));
		assertEquals("MYID", controlMessagesManager.validateId("       myId"));
		assertEquals("MYID", controlMessagesManager.validateId("			myId				"));
	}
}
