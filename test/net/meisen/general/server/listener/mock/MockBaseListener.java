package net.meisen.general.server.listener.mock;

import net.meisen.general.server.api.impl.BaseListener;

/**
 * Mocked {@code BaseListener} which just replies the message send to it.
 */
public class MockBaseListener extends BaseListener {

	@Override
	protected String handleInput(final String input) {
		return input;
	}
}
