package net.meisen.general.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

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
	 */
	@Test
	public void testServerStartAndShutdown() throws InterruptedException {

		// check the amount of threads
		final int threadSize = Thread.getAllStackTraces().keySet().size();

		// get and start the server
		final Server server = Server.createServer();
		server.startAsync();

		// give some time to start
		Thread.sleep(200);

		// get all running threads
		final Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

		// check if the thread for the server and the one for the listner was
		// started
		assertEquals(threadSize + 2, threadSet.size());

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
}
