package net.meisen.general.server;

import static org.junit.Assert.assertEquals;
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
	 * @throws InterruptedException 
	 */
	@Test
	public void testServerStartAndShutdown() throws InterruptedException {

		// check the amount of threads
		final int threadSize = Thread.getAllStackTraces().keySet().size();

		// get and start the server
		final Server server = Server.createServer();
		server.start();
		
		// give some time to start
		Thread.sleep(200);

		// get all running threads
		final Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

		// check if the thread was started
		assertEquals(threadSize + 1, threadSet.size());

		// search for the thread
		boolean found = false;
		for (final Thread t : threadSet) {
			if (t.getName().equals("DummyListenerThread")) {
				found = true;
				break;
			}
		}
		assertTrue(found);
		
		// shut the server down
		server.shutdown();
		
		// give some time to shutdown
		Thread.sleep(200);
		
		// now check if it was closed
		assertEquals(threadSize, Thread.getAllStackTraces().keySet().size());
	}
}
