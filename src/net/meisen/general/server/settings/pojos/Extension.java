package net.meisen.general.server.settings.pojos;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.meisen.general.sbconfigurator.helper.StringParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class Extension {
	private String id;

	private List<Extension> extensions;

	private Map<String, Object> rawProperties = new HashMap<String, Object>();
	private Map<String, Object> properties = new HashMap<String, Object>();

	@Autowired(required = false)
	@Qualifier("serverStringParser")
	private StringParser stringParser;

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public List<Extension> getExtensions() {
		return Collections.unmodifiableList(extensions);
	}

	public boolean hasExtension(final String id) {
		return getExtension(id) != null;
	}

	public void setExtensions(final List<Extension> extensions) {
		this.extensions = extensions;
	}

	public Extension getExtension(final String id) {

		if (id == null || extensions == null) {
			return null;
		} else {
			for (final Extension extension : extensions) {
				if (extension != null && id.equals(extension.getId())) {
					return extension;
				}
			}
			return null;
		}
	}

	public Map<String, Object> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	public void setProperties(final Map<String, Object> properties) {

		// if there aren't any we don't have anything to do
		if (properties == null) {
			return;
		}

		// add each and single one
		for (final Entry<String, Object> entry : properties.entrySet()) {
			setProperty(entry.getKey(), entry.getValue());
		}
	}

	public <T> void setProperty(final String property, final T value) {
		final Object parsedValue;

		// if it's a string we will parse the property prior to setting it
		if (stringParser != null && value instanceof String) {
			parsedValue = stringParser.parseString((String) value);
		} else {
			parsedValue = value;
		}

		// now add it
		properties.put(property, parsedValue);
		rawProperties.put(property, value);
	}
	
	public Object getRawProperty(final String property) {
		return rawProperties.get(property);
	}

	public <T> T getProperty(final String property) {
		@SuppressWarnings("unchecked")
		final T value = (T) properties.get(property);
		return value;
	}
}
