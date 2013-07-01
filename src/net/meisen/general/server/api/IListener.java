package net.meisen.general.server.api;

import net.meisen.general.server.settings.pojos.Connector;

public interface IListener {

	public void initialize(final Connector connector);

	public void open();
	
	public void close();
}
