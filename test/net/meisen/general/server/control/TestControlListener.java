package net.meisen.general.server.control;

import static org.junit.Assert.assertEquals;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.runners.JUnitConfigurationRunner;
import net.meisen.general.sbconfigurator.runners.annotations.ContextClass;
import net.meisen.general.server.Server;
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
public class TestControlListener {

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
		final ServerController controller = new ServerController("localhost", port);
		final String answer = controller.sendMessage("rcvd");

		// close the listener
		listener.close();

		// check the result
		assertEquals("RCVD", answer);
	}
}
