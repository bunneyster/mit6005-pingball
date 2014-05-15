package pb.board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Declarative information for customizing the aspect of a board element.
 * 
 * Declarative styles allow board elements to carry presentation information
 * without introducing rendering concerns in the {@link pb.board} and
 * {@link pb.gizmos} packages.
 * 
 * Instances of this class are immutable, to avoid unnecessary complexity in the
 * rendering code. Therefore, this class is thread-safe.
 */
public class Style {
	/**
	 * The properties wrapped by this style.
	 * 
	 * The contents of this map is immutable.
	 */
	private final Map<String, String> properties;
	
	/**
	 * Creates a style with the given properties.
	 * 
	 * @param properties the style's properties; the contents of this map is
	 * 	 copied, so it can be modified after the constructor call completes
	 */
	public Style(Map<String, String> properties) {
		assert Style.areValidProperties(properties);
		
		this.properties = Collections.unmodifiableMap(
				new HashMap<String, String>(properties));
	}
	
	/**
	 * Returns the value of a style property.
	 * 
	 * @param propertyName the property name
	 * @param defaultValue the value that will be returned if the property does
	 *   not have a value set
	 * @return the property's value
	 */
	public String value(String propertyName, String defaultValue) {
		assert propertyName != null;
		
		String value = properties.get(propertyName);
		return (value == null) ? defaultValue : value;
	}
	
	/**
	 * Style with no explicit property value set.
	 * 
	 * This instance is used by {@link StyleRegistry} to represent the rendering
	 * style for classes without any style information. 
	 */
	public static final Style EMPTY = new Style(new HashMap<String, String>());
	
	/**
	 * Checks if a string is a valid style property name.
	 * 
	 * @param name the string to be checked
	 * @return true if the given string is a valid style property name
	 */
	public static boolean isValidPropertyName(String name) {
		return name != null && name.length() != 0;
	}

	/**
	 * Checks if a string is a valid style property value.
	 * 
	 * @param value the string to be checked
	 * @return true if the given string is a valid style property value
	 */
	public static boolean isValidPropertyValue(String value) {
		return value != null && value.length() != 0;		
	}
	
	/**
	 * Checks if the given property map is suitable for populating a Style.
	 * 
	 * @param properties the property map to be verified
	 * @return true if the given map can be used to populate a {@link Style}
	 *   instance
	 */
	public static boolean areValidProperties(Map<String, String> properties) {
		if (properties == null)
			return false;
		for (Entry<String, String> entry : properties.entrySet()) {
			String name = entry.getKey();
			if (!isValidPropertyName(name))
				return false;
			String value = entry.getValue();
			if (!isValidPropertyValue(value))
				return false;
		}
		return true;
	}
}
