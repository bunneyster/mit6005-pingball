package pb.board;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Declarative information for customizing the aspect of a board's elements.
 *
 * Declarative styles allow board elements to carry presentation information
 * without introducing rendering concerns in the board package.
 */
public class StyleRegistryBuilder {
	/** Styling information for each class. */
	private final Map<String, Map<String, String>> styles;

	/**
	 * Creates a builder with no style information.
	 * 
	 * Call {@link #setProperty(String, String, String)} to set properties on
	 * the resulting builder, then call {@link #build()} to obtain a
	 * {@link StyleRegistry} containing the styles. 
	 */
	public StyleRegistryBuilder() {
		this.styles = new HashMap<String, Map<String, String>>();
	}
	
	/**
	 * Sets a style property.
	 * 
	 * @param styleClass the style class that the property is set for
	 * @param name the name of the style property
	 * @param value the style property's value
	 */
	public void setProperty(String styleClass, String name, String value) {
		assert styleClass != null;
		assert Style.isValidPropertyName(name);
		assert Style.isValidPropertyValue(value);
		
		Map<String, String> classStyles = styles.get(styleClass);
		if (classStyles == null) {
			classStyles = new HashMap<String, String>();
			styles.put(styleClass, classStyles);
		}
		assert classStyles != null;
		classStyles.put(name, value);
	}
	
	/**
	 * Creates a {@link StyleRegistry} with the styles set on this builder.
	 * 
	 * @return a {@link StyleRegistry} wrapping the style properties currently
	 *   set on this builder; the returned instance is immutable and will not
	 *   reflect any future changes applied to the builder
	 */
	public StyleRegistry build() {
		Map<String, Style> snapshot = new HashMap<String, Style>(styles.size());
		for (Entry<String, Map<String, String>> entry : styles.entrySet()) {
			String styleClass = entry.getKey();
			Style style = new Style(entry.getValue());
			snapshot.put(styleClass, style);
		}
		return new StyleRegistry(snapshot);
	}
}
