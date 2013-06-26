package net.meisen.general.server.testutilities;

import net.meisen.general.server.Server;
import net.meisen.general.server.api.IServerSettings;

public class TestHelper {

	public static Server getServer(final String extension) {
		return Server.createServer(extension);
	}

	public static IServerSettings getSettings(final String extension) {
		final Server server = getServer(extension);
		return server.getServerSettings();
	}
}
