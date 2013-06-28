package net.meisen.general.server.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Locale;

import net.meisen.general.genmisc.collections.Collections;
import net.meisen.general.server.api.IServerSettings;
import net.meisen.general.server.exceptions.ServerSettingsException;
import net.meisen.general.server.listener.DummyListener;
import net.meisen.general.server.settings.pojos.Connector;
import net.meisen.general.server.settings.pojos.Extension;
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
		// we just want to load the nothing so define an invalid filename
		System.setProperty("server.settings.selector", "?");

		final IServerSettings settings = TestHelper
				.getSettings("sbconfigurator-core-useSystemProperties.xml");

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

	@Test
	public void testSimpleConnector() {

		// we just want to load the simpleConnector
		System.setProperty("server.settings.selector",
				"server-test-simpleConnector.xml");

		// just use the system settings to ensure the correct selector
		final IServerSettings settings = TestHelper
				.getSettings("sbconfigurator-core-useSystemProperties.xml");

		// check if the default connector was created
		final Connector defConnector = Collections.get(0,
				settings.getConnectorSettings());
		assertEquals(6060, defConnector.getPort());
		assertEquals(DummyListener.NAME, defConnector.getListener());
	}

	@Test
	public void testSeveralConnectors() {

		// we just want to load the simpleConnector
		System.setProperty("server.settings.selector",
				"server-test-severalConnectors.xml");

		// just use the system settings to ensure the correct selector
		final IServerSettings settings = TestHelper
				.getSettings("sbconfigurator-core-useSystemProperties.xml");

		// check if the created connectors
		assertEquals(3, settings.getConnectorSettings().size());
		final Connector firstConnector = Collections.get(0,
				settings.getConnectorSettings());
		final Connector secondConnector = Collections.get(1,
				settings.getConnectorSettings());
		final Connector thirdConnector = Collections.get(2,
				settings.getConnectorSettings());

		assertEquals(666, firstConnector.getPort());
		assertEquals(DummyListener.NAME, firstConnector.getListener());
		assertEquals(777, secondConnector.getPort());
		assertEquals(DummyListener.NAME, secondConnector.getListener());
		assertEquals(888, thirdConnector.getPort());
		assertEquals(DummyListener.NAME, thirdConnector.getListener());
	}

	@Test
	public void testInvalidConnectorsByPort() {

		// we just want to load the simpleConnector
		System.setProperty("server.settings.selector",
				"server-test-invalidConnectorsByPort.xml");
		Locale.setDefault(new Locale("en"));

		// just use the system settings to ensure the correct selector
		try {
			TestHelper.getSettings("sbconfigurator-core-useSystemProperties.xml");
			fail("Exception not thrown");
		} catch (final Exception e) {
			assertEquals(ServerSettingsException.class, e.getClass());
			assertEquals(
					"The port 888 is used multiple times within with the settings.",
					e.getMessage());
		}
	}

	@Test
	public void testInvalidConnectorsByListener() {

		// we just want to load the simpleConnector
		System.setProperty("server.settings.selector",
				"server-test-invalidConnectorsByListener.xml");
		Locale.setDefault(new Locale("en"));

		// just use the system settings to ensure the correct selector
		try {
			TestHelper.getSettings("sbconfigurator-core-useSystemProperties.xml");
			fail("Exception not thrown");
		} catch (final Exception e) {
			assertEquals(ServerSettingsException.class, e.getClass());
			assertEquals(
					"The defined listener 'UNKNOWN' of 'Connector on port 888 ('UNKNOWN')' cannot be created, please check the availability.",
					e.getMessage());
		}
	}

	@Test
	public void testInvalidConnectorsByListenerWithoutException() {

		// we just want to load the simpleConnector
		System.setProperty("server.settings.selector",
				"server-test-invalidConnectorsByListener.xml");
		System.setProperty("server.settings.failOnUnresolvableListeners", "false");
		Locale.setDefault(new Locale("en"));

		// just use the system settings to ensure the correct selector
		final IServerSettings settings = TestHelper
				.getSettings("sbconfigurator-core-useSystemProperties.xml");

		// check if the created connectors
		assertEquals(2, settings.getConnectorSettings().size());
		final Connector firstConnector = Collections.get(0,
				settings.getConnectorSettings());
		final Connector secondConnector = Collections.get(1,
				settings.getConnectorSettings());

		assertEquals(666, firstConnector.getPort());
		assertEquals(DummyListener.NAME, firstConnector.getListener());
		assertEquals(777, secondConnector.getPort());
		assertEquals(DummyListener.NAME, secondConnector.getListener());
	}

	@Test
	public void testExtendedConnectors() {

		// we just want to load the simpleConnector
		System.setProperty("server.settings.selector",
				"server-test-extendedConnectors.xml");

		// just use the system settings to ensure the correct selector
		final IServerSettings settings = TestHelper
				.getSettings("sbconfigurator-core-useSystemProperties.xml");

		// check if there is one
		assertEquals(1, settings.getConnectorSettings().size());

		// validate the connector
		final Connector connector = Collections.get(0,
				settings.getConnectorSettings());
		assertEquals(666, connector.getPort());
		assertEquals(DummyListener.NAME, connector.getListener());

		// check the extensions of this one connector
		final List<Extension> extensions = connector.getExtensions();
		assertNotNull(extensions);
		assertEquals(3, extensions.size());

		// check the existence of extensions
		assertTrue(connector.hasExtension("firstEntry"));
		assertTrue(connector.hasExtension("secondEntry"));
		assertTrue(connector.hasExtension("thirdEntry"));

		// check each extension
		final Extension firstEntry = connector.getExtension("firstEntry");
		assertEquals(4, firstEntry.getProperties().size());
		assertEquals(0, firstEntry.getExtensions().size());
		assertEquals(1, firstEntry.getProperty("first"));
		assertEquals(2, firstEntry.getProperty("second"));
		assertEquals(true, firstEntry.getProperty("third"));
		assertEquals(false, firstEntry.getProperty("fourth"));

		final Extension secondEntry = connector.getExtension("secondEntry");
		assertEquals(0, secondEntry.getProperties().size());
		assertEquals(1, secondEntry.getExtensions().size());
		
		final Extension thirdEntry = connector.getExtension("thirdEntry");
		assertEquals(4, thirdEntry.getProperties().size());
		assertEquals(2, thirdEntry.getExtensions().size());

		// check some raw-properties as well
		assertEquals("1", firstEntry.getRawProperty("first"));
		assertEquals("true", firstEntry.getRawProperty("third"));
		

		// check the sub-entries
		final Extension secondSubEntry = secondEntry.getExtension("subEntry");
		assertEquals("world", secondSubEntry.getRawProperty("attribute2"));
		assertEquals("hello", secondSubEntry.getProperty("attribute1"));
		assertEquals("world", secondSubEntry.getProperty("attribute2"));
	}
}
