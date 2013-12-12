package net.meisen.general.server.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.runners.JUnitConfigurationRunner;
import net.meisen.general.sbconfigurator.runners.annotations.ContextClass;
import net.meisen.general.server.Server;
import net.meisen.general.server.control.ServerController;
import net.meisen.general.server.listener.control.ControlListener;
import net.meisen.general.server.settings.pojos.Connector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Tests the implementation of the <code>ControlListener</code>.
 * 
 * @author pmeisen
 * 
 */
@RunWith(JUnitConfigurationRunner.class)
@ContextClass(Server.class)
public class TestServerController {

	@Autowired(required = true)
	@Qualifier("coreConfiguration")
	private IConfiguration configuration;

	/**
	 * Test the sending and receiving of a message.
	 */
	@Test
	public void testSendAndReceive() {
		final int port = 10000;

		// define the connection we will use
		final Connector conn = new Connector();
		conn.setEnable(true);
		conn.setId("CONTROL");
		conn.setListener(ControlListener.class.getName());
		conn.setPort(port);

		// create the listener and initialize it with the connection
		final ControlListener listener = configuration
				.createInstance(ControlListener.class);
		listener.initialize(conn);

		// open the port (for the server)
		listener.open();

		// now create the client controller and send the shutdown
		final ServerController controller = new ServerController("localhost",
				port);
		final String answer = controller.sendMessage("rcvd");

		// close the listener
		listener.close();

		// check the result
		assertEquals("RCVD", answer);
	}

	/**
	 * Tests the parsing of arguments when using the {@code ServerController}
	 * externally
	 */
	@Test
	public void testArgumentParsing() {
		Object[] args;

		// test empty arguments
		args = ServerController.parseArguments(new String[] {}, false);
		assertEquals(0, args.length);

		// test to many arguments
		args = ServerController.parseArguments(new String[] { "localhost",
				"10000", "message", "more" }, false);
		assertEquals(0, args.length);

		// test help message
		args = ServerController.parseArguments(new String[] { "-h" }, false);
		assertEquals(0, args.length);

		// test parsing
		args = ServerController.parseArguments(new String[] { "myHost",
				"10000", "myMessage" }, false);
		assertEquals(3, args.length);
		assertTrue(args[0] instanceof String);
		assertTrue(args[1] instanceof Integer);
		assertTrue(args[2] instanceof String);
		assertEquals("myHost", args[0]);
		assertEquals(10000, args[1]);
		assertEquals("myMessage", args[2]);

		args = ServerController.parseArguments(new String[] { "15000",
				"anotherMessage" }, false);
		assertEquals(3, args.length);
		assertTrue(args[0] instanceof String);
		assertTrue(args[1] instanceof Integer);
		assertTrue(args[2] instanceof String);
		assertEquals(ServerController.DEF_HOST, args[0]);
		assertEquals(15000, args[1]);
		assertEquals("anotherMessage", args[2]);

		args = ServerController.parseArguments(new String[] { "12000" }, false);
		assertEquals(3, args.length);
		assertTrue(args[0] instanceof String);
		assertTrue(args[1] instanceof Integer);
		assertTrue(args[2] instanceof String);
		assertEquals(ServerController.DEF_HOST, args[0]);
		assertEquals(12000, args[1]);
		assertEquals(ServerController.DEF_MESSAGE, args[2]);

		args = ServerController.parseArguments(
				new String[] { "myLife", "20000" }, false);
		assertEquals(3, args.length);
		assertTrue(args[0] instanceof String);
		assertTrue(args[1] instanceof Integer);
		assertTrue(args[2] instanceof String);
		assertEquals("myLife", args[0]);
		assertEquals(20000, args[1]);
		assertEquals(ServerController.DEF_MESSAGE, args[2]);
	}
}
