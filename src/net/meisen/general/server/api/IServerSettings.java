package net.meisen.general.server.api;

import java.util.Collection;

import net.meisen.general.server.settings.pojos.Connector;

public interface IServerSettings {

	public Collection<Connector> getConnectorSettings();

	public boolean isDefaultSettings();

}
