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
	 * Tests the loading of the server configuration with defined
	 * user-properties
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

	/**
	 * Tests the parsing of one simple connector
	 */
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

	/**
	 * Tests the parsing of several connectors
	 */
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

	/**
	 * Tests invalid connectors, i.e. when invalid ports are used
	 */
	@Test
	public void testInvalidConnectorsByPort() {

		// we just want to load the simpleConnector
		System.setProperty("server.settings.selector",
				"server-test-invalidConnectorsByPort.xml");
		Locale.setDefault(new Locale("en"));

		// just use the system settings to ensure the correct selector
		try {
			TestHelper
					.getSettings("sbconfigurator-core-useSystemProperties.xml");
			fail("Exception not thrown");
		} catch (final Exception e) {
			assertEquals(ServerSettingsException.class, e.getClass());
			assertEquals(
					"The port 888 is used multiple times within with the settings.",
					e.getMessage());
		}
	}

	/**
	 * Tests invalid connectors, i.e. when invalid listeners are used
	 */
	@Test
	public void testInvalidConnectorsByListener() {

		// we just want to load the simpleConnector
		System.setProperty("server.settings.selector",
				"server-test-invalidConnectorsByListener.xml");
		Locale.setDefault(new Locale("en"));

		// just use the system settings to ensure the correct selector
		try {
			TestHelper
					.getSettings("sbconfigurator-core-useSystemProperties.xml");
			fail("Exception not thrown");
		} catch (final Exception e) {
			assertEquals(ServerSettingsException.class, e.getClass());
			assertEquals(
					"The defined listener 'UNKNOWN' of 'Connector on port 888 ('UNKNOWN')' cannot be created, please check the availability.",
					e.getMessage());
		}
	}

	/**
	 * Tests invalid connectors, i.e. when invalid listeners are used
	 */
	@Test
	public void testInvalidConnectorsByListenerWithoutException() {

		// we just want to load the simpleConnector
		System.setProperty("server.settings.selector",
				"server-test-invalidConnectorsByListener.xml");
		System.setProperty("server.settings.failOnUnresolvableListeners",
				"false");
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

	/**
	 * Tests the parsing of extended connectors
	 */
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
		assertEquals(5, thirdEntry.getProperties().size());
		assertEquals(2, thirdEntry.getExtensions().size());
		assertEquals("A", thirdEntry.getProperty("a"));
		assertEquals("B", thirdEntry.getProperty("b"));
		assertEquals("C", thirdEntry.getProperty("c"));
		assertEquals("D", thirdEntry.getProperty("d"));
		assertEquals("d", thirdEntry.getProperty("D"));

		// check some raw-properties as well
		assertEquals("1", firstEntry.getRawProperty("first"));
		assertEquals("true", firstEntry.getRawProperty("third"));
		assertEquals("D", thirdEntry.getRawProperty("d"));

		// check the sub-entries
		final Extension secondSubEntry = secondEntry.getExtension("subEntry");
		assertEquals("world", secondSubEntry.getRawProperty("attribute2"));
		assertEquals("hello", secondSubEntry.getProperty("attribute1"));
		assertEquals("world", secondSubEntry.getProperty("attribute2"));

		int i = 1;
		for (final Extension thirdSubEntry : thirdEntry.getExtensions()) {
			assertEquals(2, thirdSubEntry.getProperties().size());
			assertEquals("hello" + i, thirdSubEntry.getProperty("attribute1"));
			assertEquals("world" + i, thirdSubEntry.getProperty("attribute2"));

			// check the sub-sub-entries
			assertEquals(i == 1 ? 1 : 0, thirdSubEntry.getExtensions().size());
			if (i == 1) {
				final Extension thirdSubSubEntry = thirdSubEntry
						.getExtension("subSubEntry");
				assertNotNull(thirdSubSubEntry);
				assertEquals("DeepUnder", thirdSubSubEntry.getProperty(""));
				assertEquals(1, thirdSubSubEntry.getExtensions().size());
				assertNotNull(thirdSubSubEntry.getExtension("value"));
			}

			i++;
		}
	}

	/**
	 * Tests the reading of inner text, when another child or children are used
	 * within the tag.
	 */
	@Test
	public void testInnerText() {
		System.setProperty("server.settings.selector",
				"server-test-extendedConnectorsWithInnerText.xml");

		// just use the system settings to ensure the correct selector
		final IServerSettings settings = TestHelper
				.getSettings("sbconfigurator-core-useSystemProperties.xml");

		// check if there is one
		assertEquals(1, settings.getConnectorSettings().size());

		// validate the connector
		final Connector connector = Collections.get(0,
				settings.getConnectorSettings());

		// check the extensions of this one connector
		final List<Extension> extensions = connector.getExtensions();
		assertEquals(1, extensions.size());

		// get the one extension
		final Extension extension = Collections.get(0, extensions);
		assertEquals("servlet", extension.getId());
		assertEquals("aValue", extension.getProperty("value"));
		assertEquals(
				"net.meisen.general.server.http.listener.servlets.ScriptedServlet",
				extension.<String> getProperty("").trim());
		assertTrue(extension.hasExtension("script"));

		// get the script extension
		final Extension scriptExtension = extension.getExtension("script");
		assertEquals("aTest", scriptExtension.getProperty("test"));
		assertEquals("this is some script;", scriptExtension
				.<String> getProperty("").trim());
	}

	/**
	 * Tests the usage of <code>Extension</code> instances with the same id and
	 * their retrieval using {@link Extension#getExtensions(String)}.
	 */
	@Test
	public void testExtensionsWithSameId() {
		System.setProperty("server.settings.selector",
				"server-test-severalExtensionsWithSameId.xml");

		// just use the system settings to ensure the correct selector
		final IServerSettings settings = TestHelper
				.getSettings("sbconfigurator-core-useSystemProperties.xml");

		// check if there is one
		assertEquals(1, settings.getConnectorSettings().size());

		// validate the connector
		final Connector connector = Collections.get(0,
				settings.getConnectorSettings());

		// check the extensions of this one connector
		final List<Extension> extensions = connector.getExtensions("entry");
		assertEquals(3, extensions.size());

		// get the one extension
		final Extension firstExtension = Collections.get(0, extensions);
		assertEquals("entry", firstExtension.getId());
		assertEquals(new Integer(1), firstExtension.<Integer> getProperty("nr"));

		// get the script extension
		final Extension thirdExtension = Collections.get(2, extensions);
		assertEquals(new Integer(3), thirdExtension.<Integer> getProperty("nr"));
		assertEquals("Special", thirdExtension.getProperty(""));
	}
}
