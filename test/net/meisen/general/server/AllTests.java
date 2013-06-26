package net.meisen.general.server;

import net.meisen.general.server.settings.TestDefaultServerSettingsManager;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * All tests together as a {@link Suite}
 * 
 * @author pmeisen
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ TestDefaultServerSettingsManager.class })
public class AllTests {
	// nothing more to do here
}
