package net.meisen.general.server.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.meisen.general.genmisc.collections.Collections;
import net.meisen.general.server.api.IServerSettings;
import net.meisen.general.server.listener.DummyListener;
import net.meisen.general.server.settings.DefaultServerSettings;
import net.meisen.general.server.settings.pojos.Connector;
import net.meisen.general.server.testutilities.TestHelper;

import org.junit.Test;

/**
 * Tests the implementation of the <code>DefaultServerSettingsManager</code>.
 * 
 * @author pmeisen
 * 
 */
public class TestDefaultServerSettingsManager {

	/**
	 * Tests the loading of the server configuration without any user-properties
	 * (i.e. just the default one)
	 */
	@Test
	public void testWithoutUserSettings() {
		final IServerSettings settings = TestHelper
				.getSettings("sbconfigurator-core-default.xml");

		assertNotNull(settings);
		assertTrue(settings instanceof DefaultServerSettings);
		assertTrue(settings.isDefaultSettings());

		// make sure that we have just the default connector
		assertEquals(1, settings.getConnectorSettings().size());

		// get the default connector and check it
		final Connector defConnector = Collections.get(0,
				settings.getConnectorSettings());
		assertEquals(6060, defConnector.getPort());
		assertEquals("DEFAULT", defConnector.getId());
		assertEquals(DummyListener.NAME, defConnector.getListener());
	}

	/**
	 * Tests the loading of the server configuration with defined user-properties
	 */
	@Test
	public void testWithUserProperties() {
		final IServerSettings settings = TestHelper
				.getSettings("sbconfigurator-core-withUserProperties.xml");

		assertNotNull(settings);
		assertTrue(settings instanceof DefaultServerSettings);
		assertFalse(settings.isDefaultSettings());

		// make sure that we have just the default connector
		assertEquals(1, settings.getConnectorSettings().size());

		// get the default connector and check it
		final Connector defConnector = Collections.get(0,
				settings.getConnectorSettings());
		assertEquals(8000, defConnector.getPort());
		assertEquals(DummyListener.NAME, defConnector.getListener());
	}

}
