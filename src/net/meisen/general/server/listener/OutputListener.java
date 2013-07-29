package net.meisen.general.server.listener;

import java.io.PrintStream;

import net.meisen.general.server.api.impl.BaseListener;

public class OutputListener extends BaseListener {

	/**
	 * The name of this <code>Listener</code> used when defined it, instead of the
	 * class-name
	 */
	public static final String NAME = "OUTPUT";

	private PrintStream out = System.out;

	public void setPrintStream(final PrintStream out) {
		this.out = out;
	}

	@Override
	protected String handleInput(final String input) {
		out.println(input);

		return "";
	}

	@Override
	public String toString() {
		return NAME + (getPort() == -1 ? "" : " (" + getPort() + ")");
	}
}
