package net.meisen.general.server.settings.pojos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.meisen.general.sbconfigurator.helper.StringParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The extension is the part of an XML-definition which is unknown, or better
 * defined dynamically.
 * 
 * @author pmeisen
 * 
 */
public class Extension {
	private String id;

	private List<Extension> extensions = new ArrayList<Extension>();

	private Map<String, Object> rawProperties = new HashMap<String, Object>();
	private Map<String, Object> properties = new HashMap<String, Object>();

	@Autowired(required = false)
	@Qualifier("serverStringParser")
	private StringParser stringParser;

	/**
	 * Gets the id of the extension, i.e. the tag which defined this extension.
	 * 
	 * @return the id of the extension
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of the extension (normally should be the tag of the element
	 * which specified this extension).
	 * 
	 * @param id
	 *            the id of the extension
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Gets all the <code>Extension</code> instances which are defined within
	 * this instance.
	 * 
	 * @return the <code>List</code> of all the extensions
	 */
	public List<Extension> getExtensions() {
		return Collections.unmodifiableList(extensions);
	}

	/**
	 * Gets all the extensions with the specified <code>id</code>.
	 * 
	 * @param id
	 *            the identifier to get all the extensions for
	 * 
	 * @return a list of all the <code>Extension</code> instances found with the
	 *         specified <code>id</code>
	 */
	public List<Extension> getExtensions(final String id) {
		final List<Extension> foundExtensions = new ArrayList<Extension>();

		if (id == null || extensions == null) {
			return foundExtensions;
		} else {
			for (final Extension extension : extensions) {
				if (extension != null && id.equals(extension.getId())) {
					foundExtensions.add(extension);
				}
			}

			return foundExtensions;
		}
	}

	/**
	 * Checks if <code>this</code> instance has an <code>Extension</code> with
	 * the specified <code>id</code>.
	 * 
	 * @param id
	 *            the id of the <code>Extension</code> to be checked
	 * 
	 * @return <code>true</code> if <code>this</code> has an
	 *         <code>Extension</code> with the specified id, otherwise
	 *         <code>false</code>
	 */
	public boolean hasExtension(final String id) {
		return getExtension(id) != null;
	}

	/**
	 * Sets the <code>List</code> of all the <code>Extension</code> instances
	 * for <code>this</code>. Normally those should be children within
	 * <code>this</code> definition.
	 * 
	 * @param extensions
	 *            the <code>Extension</code> instances of <code>this</code>
	 */
	public void setExtensions(final List<Extension> extensions) {
		this.extensions = extensions;
	}

	/**
	 * Gets the <code>Extension</code> instance with the specified
	 * <code>id</code> . This method returns <code>null</code> if no such
	 * <code>Extension</code> instance was defined.
	 * 
	 * @param id
	 *            the id of the <code>Extension</code> to be retrieved
	 * 
	 * @return the <code>Extension</code> instance, or <code>null</code> if none
	 *         with the specified id exists
	 */
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

	/**
	 * Gets all the defined properties for <code>this</code>. A property is
	 * normally defined via an attribute within the XML-definition.
	 * 
	 * @return all the properties of <code>this</code>
	 */
	public Map<String, Object> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	/**
	 * Sets the properties of <code>this</code>.
	 * 
	 * @param properties
	 *            the properties of <code>this</code>
	 */
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

	/**
	 * Sets the <code>value</code> of the specified <code>property</code>. If
	 * the property already exists, it will be overridden, if not it will be
	 * created.
	 * 
	 * @param property
	 *            the property to set the value for
	 * @param value
	 *            the value of the property
	 */
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

	/**
	 * Gets the raw value of the specified property (i.e. the string
	 * representation used in the XML-definition). The raw value might be equal
	 * to the final value of the property. This is the case, if the property is
	 * represented as <code>String</code> or if the property was directly set as
	 * the specified type.
	 * 
	 * @param property
	 * 
	 * @return the raw-value of the specified property
	 * 
	 * @see #setProperty(String, Object)
	 */
	public Object getRawProperty(final String property) {
		return rawProperties.get(property);
	}

	/**
	 * Gets the parsed value of the specified property. The property is parsed
	 * according to it's definition (i.e. integers are parsed to
	 * <code>Integer</code> instances, dates with a specified format
	 * {@link StringParser#getDateFormat()} to <code>Date</code> instances,
	 * ...).
	 * 
	 * @param property
	 *            the name of the property to get the parsed value for
	 * 
	 * @return the parsed value of the property
	 * 
	 * @see StringParser
	 */
	public <T> T getProperty(final String property) {
		@SuppressWarnings("unchecked")
		final T value = (T) properties.get(property);
		return value;
	}
}
