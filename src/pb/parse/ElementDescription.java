package pb.parse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The description of a board element.
 * 
 * A description is a non-blank, non-comment line in a .pb file. It looks like
 *     elementName property1=value1 property2=value2 ...
 *     
 * Instances are not thread-safe. Parsing should be done in a single thread.
 */
public class ElementDescription {
	/** The statement properties (name1=value1 name2=value2 etc). */
	private final HashMap<String, String> properties;
	/** The statement type (first word). */
	private final String type; 
	
	/**
	 * Creates a new statement with no properties.
	 * 
	 * @param type the statement name
	 */
	public ElementDescription(String type) {
		assert type != null;
		this.type = type;
		this.properties = new HashMap<String, String>();
	}
	
	/**
	 * Returns the statement's type.
	 * @return the first word on the statement line
	 */
	public String getType() {
		return type;
	}

	/**
	 * Looks up a string property.
	 * 
	 * @param name the property's name
	 * @param defaultValue the value to be returned if the property does not
	 *   exist
	 * @return the property's value, as a String, or a default value if the
	 *   property does not exist
	 */
	public String getString(String name, String defaultValue) {
		assert name != null;
		
		String value = properties.get(name);
		return (value == null) ? defaultValue : value;
	}

	/**
	 * Looks up a mandatory string property.
	 * 
	 * @param name the property's name
	 * @return the property's value, as a String
	 * @throw IllegalArgumentException if the property does not exist
	 */
	public String getString(String name) {
		assert name != null;
		
		String value = properties.get(name);
		if (value == null) {
			throw new IllegalArgumentException(
					"Missing mandatory property " + name);
		}
		return value;
	}

	/**
	 * Looks up an integer property.
	 * 
	 * @param name the property's name
	 * @param defaultValue the value to be returned if the property does not
	 *   exist
	 * @return the property's value, as an integer, or a default value if the
	 *   property does not exist
	 */
	public int getInteger(String name, int defaultValue) {
		assert name != null;
		
		String stringValue = properties.get(name);
		if (stringValue == null)
			return defaultValue;

		try {
			return Integer.parseInt(stringValue);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Property value is not an integer", e);
		}
	}

	/**
	 * Looks up a mandatory integer property.
	 * 
	 * @param name the property's name
	 * @return the property's value, as an integer
	 * @throw IllegalArgumentException if the property does not exist
	 */
	public int getInteger(String name) {
		assert name != null;
		
		String stringValue = properties.get(name);
		if (stringValue == null) {
			throw new IllegalArgumentException(
					"Missing mandatory property " + name);
		}
		
		try {
			return Integer.parseInt(stringValue);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Property value is not an integer", e);
		}
	}	

	/**
	 * Looks up a floating-point number property.
	 * 
	 * @param name the property's name
	 * @param defaultValue the value to be returned if the property does not
	 *   exist
	 * @return the property's value, as a double, or a default value if the
	 *   property does not exist
	 */
	public double getFloat(String name, double defaultValue) {
		assert name != null;
		
		String stringValue = properties.get(name);
		if (stringValue == null)
			return defaultValue;

		try {
			return Double.parseDouble(stringValue);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Property value is not a float", e);
		}
	}
	
	/**
	 * Looks up a floating-point number property.
	 * 
	 * @param name the property's name
	 * @return the property's value, as a double
	 * @throw IllegalArgumentException if the property does not exist
	 */
	public double getFloat(String name) {
		assert name != null;
		
		String stringValue = properties.get(name);
		if (stringValue == null) {
			throw new IllegalArgumentException(
					"Missing mandatory property " + name);
		}

		try {
			return Double.parseDouble(stringValue);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Property value is not a float", e);
		}
	}
	
	/**
	 * An iterator over the property names in this element description.
	 * 
	 * @return an iterator over the property names in this element description
	 */
	public Iterator<String> getPropertyNames() {
		return Collections.unmodifiableSet(properties.keySet()).iterator();
	}
	
	/**
	 * Parses a line into an element description.
	 * 
	 * @param line the .pb file line to be parsed 
	 * @return the element description on the line; null if the line is blank
	 *   or a comment
	 */
	public static ElementDescription fromLine(String line) {
		// HACK: = can be surrounded by whitespace, even though that's not in
		//       the spec
		line = line.replaceAll("\\s*=\\s*", "=");
		
		String[] tokens = line.trim().split("\\s+");
		if (tokens.length == 0 || tokens[0].length() == 0 || 
				tokens[0].charAt(0) == '#') {
			return null;
		}
		
		ElementDescription statement = new ElementDescription(tokens[0]);
		for (int i = 1; i < tokens.length; ++i) {
			String[] nameValuePair = tokens[i].split("=", 2);
			if (nameValuePair.length != 2) {
				throw new IllegalArgumentException(
						"Badly formatted property: " + tokens[i]);
			}
			statement.addProperty(nameValuePair[0], nameValuePair[1]);
		}
		return statement;
	}

	/**
	 * Adds a property to a statement.
	 * 
	 * @param name the property name
	 * @param value the property's textual value
	 */
	private void addProperty(String name, String value) {
		assert name != null;
		assert value != null;		
		properties.put(name, value);
	}	
}