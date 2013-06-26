package net.meisen.general.server.settings.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.meisen.general.genmisc.types.Classes;
import net.meisen.general.server.api.IListener;

public class ListenerFactory {
	public Map<String, Class<? extends IListener>> listeners = new HashMap<String, Class<? extends IListener>>();

	private String defaultListener;

	public void registerNamedListener(final String name,
			final Class<? extends IListener> listenerClazz) {
		listeners.put(name.toUpperCase(), listenerClazz);
	}

	public void registerNamedListeners(
			final Map<String, Class<? extends IListener>> listeners) {
		for (final Entry<String, Class<? extends IListener>> entry : listeners
				.entrySet()) {
			final String name = entry.getKey();
			final Class<? extends IListener> clazz = entry.getValue();

			registerNamedListener(name, clazz);
		}
	}

	public String getDefaultListener() {
		return defaultListener;
	}

	public void setDefaultListener(final String defaultListener) {
		this.defaultListener = defaultListener;
	}

	public Class<? extends IListener> resolve(final String listener) {

		// get the listener's class
		if (listener == null && defaultListener != null) {
			return resolve(defaultListener);
		} else if (listener == null) {
			throw new IllegalStateException(
					"Please specify a defaultListener for the ListenerFactory '"
							+ getClass().getName() + "'.");
		} else if (listeners.containsKey(listener)) {
			return listeners.get(listener.toUpperCase());
		} else {
			final Class<?> clazz = Classes.getClass(listener);
			if (clazz == null) {
				return null;
			} else if (IListener.class.isAssignableFrom(clazz)) {

				@SuppressWarnings("unchecked")
				final Class<? extends IListener> listenerClazz = (Class<? extends IListener>) clazz;
				return listenerClazz;
			} else {
				return null;
			}
		}
	}

	public IListener createListener(final String listener) {
		final Class<? extends IListener> listenerClazz = resolve(listener);

		if (listenerClazz == null) {
			return null;
		} else {

			// create the listener
			try {
				return listenerClazz.newInstance();
			} catch (final Exception e) {
				return null;
			}
		}
	}
}
