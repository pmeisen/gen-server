package net.meisen.general.server.listener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import net.meisen.general.genmisc.types.Streams;
import net.meisen.general.sbconfigurator.api.IConfiguration;
import net.meisen.general.sbconfigurator.runners.JUnitConfigurationRunner;
import net.meisen.general.sbconfigurator.runners.annotations.ContextClass;
import net.meisen.general.sbconfigurator.runners.annotations.ContextFile;
import net.meisen.general.server.Server;
import net.meisen.general.server.listener.mock.TestPrintStream;
import net.meisen.general.server.settings.pojos.Connector;
import net.meisen.general.server.testutilities.TestHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Tests the implementation of the <code>OutputListener</code>.
 * 
 * @author pmeisen
 * 
 */
@RunWith(JUnitConfigurationRunner.class)
@ContextClass(TestOutputListener.class)
@ContextFile("testOutputListener-core.xml")
public class TestOutputListener {

	@Autowired(required = true)
	@Qualifier("coreConfiguration")
	private IConfiguration configuration;

	@Autowired(required = true)
	@Qualifier("server")
	private Server server;

	@Autowired(required = true)
	@Qualifier("testBaos")
	private ByteArrayOutputStream wiredStream;

	private final String host = "localhost";

	/**
	 * Create a <code>OutputListener</code> for the specified
	 * <code>Connector</code>.
	 * 
	 * @param conn
	 *          the <code>Connector</code> to create the
	 *          <code>OutputListener</code> for
	 * @param baos
	 *          the <code>ByteArrayOutputStream</code> to write to using
	 *          {@link OutputListener#setPrintStream(PrintStream)}, can be
	 *          <code>null</code> if nothing should be set
	 * 
	 * @return the created <code>OutputListener</code>
	 */
	public OutputListener createListener(final Connector conn,
			final ByteArrayOutputStream baos) {
		assertEquals(conn.getListener(), OutputListener.class.getName());

		// create the listener and initialize it with the connection
		final OutputListener listener = configuration
				.createInstance(OutputListener.class);
		listener.initialize(conn);

		// set the PrintStream
		if (baos != null) {
			final PrintStream out = new PrintStream(baos);
			listener.setPrintStream(out);
		}

		// return it
		assertNotNull(listener);
		return listener;
	}

	/**
	 * Tests the functionality of the <code>OutputListener</code>, i.e. the output
	 * of the message send on the <code>Socket</code>.
	 */
	@Test
	public void testOutput() {
		final int port = 10000;

		// define the connection we will use
		final Connector conn = new Connector();
		conn.setEnable(true);
		conn.setId("SimpleTestOutput");
		conn.setListener(OutputListener.class.getName());
		conn.setPort(port);

		// create the listener
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final OutputListener listener = createListener(conn, baos);

		// open the port (for the server)
		listener.open();

		// send some messages
		TestHelper.sendMessage("HELLO WORLD", host, port);
		assertEquals(baos.toString().trim(), "HELLO WORLD");
		TestHelper.sendMessage("ANOTHER WORLD SAYS HI", host, port);
		assertEquals(baos.toString().trim(),
				"HELLO WORLD" + System.getProperty("line.separator")
						+ "ANOTHER WORLD SAYS HI");

		// close the listener
		listener.close();

		// cleanup
		Streams.closeIO(baos);
	}

	/**
	 * Tests the configuration of the <code>PrintStream</code> to be used via
	 * configuration.
	 */
	@Test
	public void testSettingOfPrintStreamViaConfigClass() {
		final int port = 10000;

		// found the connector defined via Configuration
		Connector outputConn = null;
		for (final Connector conn : server.getServerSettings()
				.getConnectorSettings()) {
			if (conn.getId().equals("OutputWithTestPrintStream")) {
				outputConn = conn;
				break;
			}
		}

		// create the listener
		final OutputListener listener = createListener(outputConn, null);

		// open the port (for the server)
		listener.open();

		// send some messages
		TestHelper.sendMessage("TestPrintStream talk to me", host, port);
		assertEquals(TestPrintStream.getBaosContent(), "TestPrintStream talk to me");
		TestHelper.sendMessage("What else can I say", host, port);
		assertEquals(TestPrintStream.getBaosContent(), "TestPrintStream talk to me"
				+ System.getProperty("line.separator") + "What else can I say");

		// close the listener
		listener.close();

		// clean up
		TestPrintStream.cleanUp();
	}

	/**
	 * Tests the configuration of a <code>PrintStream</code> via a
	 * Spring-reference.
	 */
	@Test
	public void testSettingOfPrintStreamViaConfigRef() {
		final int port = 10001;

		// found the connector defined via Configuration
		Connector outputConn = null;
		for (final Connector conn : server.getServerSettings()
				.getConnectorSettings()) {
			if (conn.getId().equals("OutputWithSpringReference")) {
				outputConn = conn;
				break;
			}
		}

		// create the listener
		final OutputListener listener = createListener(outputConn, null);

		// open the port (for the server)
		listener.open();

		// send some messages
		TestHelper.sendMessage("TestPrintStream talk to me", host, port);
		assertEquals(wiredStream.toString().trim(), "TestPrintStream talk to me");
		TestHelper.sendMessage("What else can I say", host, port);
		assertEquals(wiredStream.toString().trim(), "TestPrintStream talk to me"
				+ System.getProperty("line.separator") + "What else can I say");

		// close the listener
		listener.close();

		// clean up
		TestPrintStream.cleanUp();
	}
}
