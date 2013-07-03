package net.meisen.general.server.testutilities;

import net.meisen.general.server.Server;
import net.meisen.general.server.api.IServerSettings;

/**
 * Helper class for tests.
 * 
 * @author pmeisen
 * 
 */
public class TestHelper {

	/**
	 * Helper to get the <code>Server</code> instance created by the specified
	 * <code>extension</code>.
	 * 
	 * @param extension
	 *          the spring configuration to be loaded
	 * 
	 * @return the <code>Server</code> instance created by the specified
	 *         <code>extension</code>
	 */
	public static Server getServer(final String extension) {
		return Server.createServer(extension);
	}

	/**
	 * Get the <code>IServerSettings</code> instance, created by the specified
	 * <code>extension</code>.
	 * 
	 * @param extension
	 *          the spring configuration to be loaded
	 * 
	 * @return the <code>IServerSettings</code> instance created by the specified
	 *         <code>extension</code>
	 */
	public static IServerSettings getSettings(final String extension) {
		final Server server = getServer(extension);
		return server.getServerSettings();
	}
}
