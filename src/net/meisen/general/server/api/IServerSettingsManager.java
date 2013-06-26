package net.meisen.general.server.api;

/**
 * It is possible to have two different sets of settings. The first set of
 * settings is the default setting, which enables the server to run
 * out-of-the-box. This setting (and therefore a bean associated to this id) is
 * required. The second set of settings is an optional set. It might be defined
 * by the user if needed, i.e. to override specific settings of the default
 * configuration.<br/>
 * <br/>
 * The <code>ServerSettingManager</code> is the one instance which can be used
 * to retrieve the valid settings, i.e. it retrieves the data from the
 * user-defined set of settings and if the setting is not defined, it takes the
 * one of the default set.
 * 
 * @author pmeisen
 * 
 */
public interface IServerSettingsManager {

	/**
	 * The id of the <code>defaultServerSettings</code>, which set the needed
	 * values for the server to run out of the box. The id is associated with a
	 * bean in the <code>tdServerConfigtoSpringContext.xslt</code>.
	 */
	public final static String defaultServerSettingsId = "defaultServerSettings";
	/**
	 * The id of the <code>serverSettings</code>, which can be used to override
	 * the default settings. The id is associated with a bean in the
	 * <code>tdServerConfigtoSpringContext.xslt</code>.<br/>
	 * A bean (i.e. module) with this identifier is not required to exist, it
	 * might be available and if it overrides the default-settings (i.e. if those
	 * are defined within this settings).
	 */
	public final static String serverSettingsId = "serverSettings";

	/**
	 * Method called by the loader right after the
	 * <code>ServerSettingsManager</code> is wired and able to be initialized.
	 */
	public void initialize();

	/**
	 * The final, i.e. the once merged from the default and the user-defined
	 * server-settings.
	 * 
	 * @return the merged <code>ServerSettings</code>
	 */
	public IServerSettings getServerSettings();
}
