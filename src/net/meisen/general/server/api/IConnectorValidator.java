package net.meisen.general.server.api;

import net.meisen.general.server.exceptions.ServerSettingsException;
import net.meisen.general.server.settings.pojos.Connector;

public interface IConnectorValidator {

	public boolean validate(final Connector connector)
			throws ServerSettingsException;
}
