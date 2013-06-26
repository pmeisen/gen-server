package net.meisen.general.server.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.meisen.general.server.api.IServerSettings;
import net.meisen.general.server.settings.pojos.Connector;

public class DefaultServerSettings implements IServerSettings {

	private List<Connector> connectorSettings = new ArrayList<Connector>();

	private boolean defaultSettings = false;

	public void addConnectorSetting(final Connector connectorSetting) {
		connectorSettings.add(connectorSetting);
	}

	public void addConnectorSettings(
			final Collection<Connector> connectorSettings) {
		this.connectorSettings.addAll(connectorSettings);
	}

	@Override
	public Collection<Connector> getConnectorSettings() {
		return Collections.unmodifiableList(connectorSettings);
	}

	public void setDefaultSettings(final boolean defaultSettings) {
		this.defaultSettings = defaultSettings;
	}

	@Override
	public boolean isDefaultSettings() {
		return defaultSettings;
	}
}
