package net.meisen.general.server.listener;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.runners.JUnitConfigurationRunner;
import net.meisen.general.sbconfigurator.runners.annotations.ContextClass;
import net.meisen.general.server.Server;
import net.meisen.general.server.control.ServerController;
import net.meisen.general.server.listener.control.ControlListener;
import net.meisen.general.server.settings.pojos.Connector;
import net.meisen.general.server.testutilities.TestHelper;

@RunWith(JUnitConfigurationRunner.class)
@ContextClass(Server.class)
public class TestOutputListener {

	@Autowired(required = true)
	@Qualifier("coreConfiguration")
	private IConfiguration configuration;

	@Test
	public void test() {
		final String host = "localhost";
		final int port = 10000;

		// define the connection we will use
		final Connector conn = new Connector();
		conn.setEnable(true);
		conn.setId("OUTPUT");
		conn.setListener(ControlListener.class.getName());
		conn.setPort(port);

		// create the listener and initialize it with the connection
		final OutputListener listener = configuration
				.createInstance(OutputListener.class);
		listener.initialize(conn);
//		listener.setPrintStream(out);

		// open the port (for the server)
		listener.open();
		
		// send some messages
		TestHelper.sendMessage("HELLO WORLD", host, port);
		

		// close the listener
		listener.close();
	}
}
