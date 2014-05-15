package pb.board;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class StyleRegistryTest {
	private Map<String, Style> styleMap;
	private Style greenStyle, blueStyle;
	private StyleRegistry registry;
	
	@Before
	public void setUp() throws Exception {
		greenStyle = new Style(new HashMap<String, String>());
		blueStyle = new Style(new HashMap<String, String>());
		
		styleMap = new HashMap<String, Style>();
		styleMap.put("green", greenStyle);
		styleMap.put("blue", blueStyle);
		styleMap.put("alsoGreen", greenStyle);

		registry = new StyleRegistry(styleMap);
	}

	@Test
	public void testForClass() {
		assertEquals(greenStyle, registry.forClass("green"));
		assertEquals(blueStyle, registry.forClass("blue"));
		assertEquals(greenStyle, registry.forClass("alsoGreen"));
		assertEquals(Style.EMPTY, registry.forClass("red"));
	}

	@Test
	public void testImmutability() {
		styleMap.put("green", blueStyle);
		StyleRegistry otherRegistry = new StyleRegistry(styleMap);
		assertEquals(blueStyle, otherRegistry.forClass("green"));
		assertEquals(greenStyle, registry.forClass("green"));
	}

	@Test
	public void testIsValidClassName() {
		assertEquals(true, StyleRegistry.isValidClassName("green"));
		assertEquals(true, StyleRegistry.isValidClassName("0"));
		assertEquals(false, StyleRegistry.isValidClassName(""));
		assertEquals(false, StyleRegistry.isValidClassName(null));
	}
	
	@Test
	public void testAreValidStyles() {
		HashMap<String, Style> styles = new HashMap<String, Style>();
		assertEquals(true, StyleRegistry.areValidStyles(styles));
		
		styles.put("green", greenStyle);
		assertEquals(true, StyleRegistry.areValidStyles(styles));

		styles.put("blue", blueStyle);
		assertEquals(true, StyleRegistry.areValidStyles(styles));

		styles.put("green", null);
		assertEquals(false, StyleRegistry.areValidStyles(styles));
		
		styles.put("green", greenStyle);
		assertEquals(true, StyleRegistry.areValidStyles(styles));

		styles.put(null, greenStyle);
		assertEquals(false, StyleRegistry.areValidStyles(styles));

		styles.remove(null);
		assertEquals(true, StyleRegistry.areValidStyles(styles));

		styles.put("", greenStyle);
		assertEquals(false, StyleRegistry.areValidStyles(styles));

		styles.remove("");
		assertEquals(true, StyleRegistry.areValidStyles(styles));		
	}
}