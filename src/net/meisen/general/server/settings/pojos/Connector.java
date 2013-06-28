package net.meisen.general.server.settings.pojos;

public class Connector extends Extension {

	private String listener;
	private int port = -1;
	private boolean enable = true;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getListener() {
		return listener;
	}

	public void setListener(final String listener) {
		this.listener = listener;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Override
	public String toString() {
		final String l = getListener();
		final String suffix = " on port " + getPort() + " ('"
				+ (l == null ? "default" : l) + "')";

		if (isEnable()) {
			return "Connector" + suffix;
		} else {
			return "Disabled connector" + suffix;
		}
	}
}
