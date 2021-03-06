package net.meisen.general.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.Socket;
import java.util.Set;

import net.meisen.general.server.api.impl.exceptions.BaseListenerException;
import net.meisen.general.server.testutilities.TestHelper;

import org.junit.Test;

/**
 * Tests the server implementation
 * 
 * @author pmeisen
 * 
 */
public class TestServer {

	/**
	 * Tests the starting and shutting down of the <code>Server</code>.
	 * 
	 * @throws InterruptedException
	 *             if a sleep is interrupted
	 */
	@Test
	public void testServerStartAndShutdown() throws InterruptedException {

		// check the amount of threads
		final Set<Thread> startThreadSet = Thread.getAllStackTraces().keySet();
		final int threadSize = startThreadSet.size();

		// get and start the server
		final Server server = Server.createServer();
		server.startAsync();

		// give some time to start
		while (!server.isRunning()) {
			Thread.sleep(50);
		}

		// get all running threads
		final Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

		// check if the thread for the server and the one for the listner was
		// started
		threadSet.removeAll(startThreadSet);
		assertEquals(threadSet.toString(), 2, threadSet.size());

		// search for the thread
		boolean foundDummy = false, foundServer = false;
		for (final Thread t : threadSet) {
			if (t.getName().equals("DummyListenerThread")) {
				assertFalse(foundDummy);
				foundDummy = true;
			}
			if (t.getName().equals("ServerMainThread")) {
				assertFalse(foundServer);
				foundServer = true;
			}
		}
		assertTrue(foundDummy);
		assertTrue(foundServer);

		// shut the server down
		server.shutdown();

		// give some time to shutdown
		Thread.sleep(200);

		// now check if it was closed
		assertEquals(threadSize, Thread.getAllStackTraces().keySet().size());
	}

	/**
	 * Tests the closing of all open sockets when the server shuts down.
	 * 
	 * @throws Exception
	 *             if an exception occurred
	 */
	@Test
	public void testSocketClosing() throws Exception {
		System.setProperty("server.settings.selector", "mockedBaseListener.xml");

		// get and start the server
		final Server server = TestHelper
				.getServer("sbconfigurator-core-useSystemProperties.xml");
		server.startAsync();
		while (!server.isRunning()) {
			Thread.sleep(50);
		}

		// get a socket to the mocked BaseListener
		final Socket socket = new Socket("localhost", 6061);
		assertEquals("Hello World",
				TestHelper.sendMessage("Hello World", socket));
		server.shutdown();

		// the message should not receive anything
		boolean exception;
		try {
			final String msg = TestHelper.sendMessage("Hello", socket);
			exception = msg == null;
		} catch (final Exception e) {
			exception = true;
		}
		assertTrue(exception);
	}

	/**
	 * Tests the exception handling when running asynchronously.
	 * 
	 * @throws InterruptedException if the connection is interrupted
	 */
	@Test
	public void testBoundSocketException() throws InterruptedException {
		System.setProperty("server.settings.selector", "mockedBaseListener.xml");

		// get and start the server
		final Server server1 = TestHelper
				.getServer("sbconfigurator-core-useSystemProperties.xml");
		server1.startAsync();
		server1.waitForStart();
		assertTrue(server1.isRunning());
		assertFalse(server1.isFailed());

		final Server server2 = TestHelper
				.getServer("sbconfigurator-core-useSystemProperties.xml");
		server2.startAsync();

		boolean exceptionThrown = false;
		try {
			server2.waitForStart();
		} catch (final BaseListenerException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		assertFalse(server2.isRunning());
		assertTrue(server2.isFailed());
	}
}
