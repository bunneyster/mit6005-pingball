package pb.board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Declarative information for customizing the aspect of a board's elements.
 *
 * Declarative styles allow board elements to carry presentation information
 * without introducing rendering concerns in the board package.
 * 
 * Instances of this class are immutable, to avoid unnecessary complexity in the
 * rendering code. Therefore, this class is thread-safe.
 */
public class StyleRegistry {
	/**
	 * Styles in this registry, indexed by class name.
	 * 
	 * The contents of this map is immutable.
	 */
	private final Map<String, Style> styles;
	
	/**
	 * Creates a registry with the given styles.
	 * 
	 * @param styles the registry's styles, indexed by class names; the contents
	 *   of this map is copied, so it can be modified after the constructor
	 *   call completes
	 */
	public StyleRegistry(Map<String, Style> styles) {
		assert StyleRegistry.areValidStyles(styles);
		
		this.styles = Collections.unmodifiableMap(
				new HashMap<String, Style>(styles));
	}
	
	/**
	 * Looks up the style properties associated with a style class.
	 * 
	 * @param styleClass the name of the class
	 * @return style properties associated with the given class; if the given
	 *   class name is not in the registry, {@link Style#EMPTY} will be returned
	 */
	public Style forClass(String styleClass) {
		Style style = styles.get(styleClass);
		return (style == null) ? Style.EMPTY : style;
	}
	
	/**
	 * The style class name for elements that do not receive an explicit class.
	 */
	public static final String DEFAULT_CLASS = "default";
	
	/**
	 * Registry that returns {@link Style#EMPTY} for all queries.
	 * 
	 * This is useful for testing.
	 */
	public static final StyleRegistry EMPTY = new StyleRegistry(
			new HashMap<String, Style>());
	
	/**
	 * Checks if a string is a valid style class name.
	 * 
	 * @param name the string to be checked
	 * @return true if the given string is a valid style class name
	 */	
	public static boolean isValidClassName(String styleClass) {
		return styleClass != null && styleClass.length() != 0;
	}
	
	/**
	 * Checks if the given style map is suitable for populating a registry.
	 * 
	 * @param properties the style map to be verified
	 * @return true if the given map can be used to populate a
	 *   {@link StyleRegistry} instance
	 */
	public static boolean areValidStyles(Map<String, Style> styles) {
		if (styles == null)
			return false;
		for (Entry<String, Style> entry : styles.entrySet()) {
			String styleClass = entry.getKey();
			if (!isValidClassName(styleClass))
				return false;
			Style style = entry.getValue();
			if (style == null)
				return false;
		}
		return true;
	}
}
