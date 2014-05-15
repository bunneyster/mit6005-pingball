package pb.board;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class StyleTest {
	private Map<String, String> propertyMap;
	private Style style;
	
	@Before
	public void setUp() throws Exception {
		propertyMap = new HashMap<String, String>();
		propertyMap.put("color", "00ff00");
		propertyMap.put("texture", "plant.png");
		style = new Style(propertyMap);
	}
	
	@Test
	public void testValue() {
		assertEquals("00ff00", style.value("color", null));
		assertEquals("plant.png", style.value("texture", null));
		assertEquals(null, style.value("blank", null));
		assertEquals("default", style.value("blank", "default"));
	}

	@Test
	public void testImmutability() {
		propertyMap.put("color", "0000ff");
		Style otherStyle = new Style(propertyMap);
		assertEquals("0000ff", otherStyle.value("color", null));
		assertEquals("00ff00", style.value("color", null));
	}	

	@Test
	public void testIsValidPropertyName() {
		assertEquals(true, Style.isValidPropertyName("color"));
		assertEquals(true, Style.isValidPropertyName("c"));
		assertEquals(false, Style.isValidPropertyName(""));
		assertEquals(false, Style.isValidPropertyName(null));
	}

	@Test
	public void testIsValidPropertyValue() {
		assertEquals(true, Style.isValidPropertyValue("green"));
		assertEquals(true, Style.isValidPropertyValue("0"));
		assertEquals(false, Style.isValidPropertyValue(""));
		assertEquals(false, Style.isValidPropertyValue(null));
	}
	
	@Test
	public void testAreValidProperties() {
		HashMap<String, String> properties = new HashMap<String, String>();
		assertEquals(true, Style.areValidProperties(properties));
		
		properties.put("color", "green");
		assertEquals(true, Style.areValidProperties(properties));

		properties.put("texture", "plant.png");
		assertEquals(true, Style.areValidProperties(properties));

		properties.put("color", null);
		assertEquals(false, Style.areValidProperties(properties));
		
		properties.put("color", "green");
		assertEquals(true, Style.areValidProperties(properties));

		properties.put("color", "");
		assertEquals(false, Style.areValidProperties(properties));
		
		properties.put("color", "green");
		assertEquals(true, Style.areValidProperties(properties));
		
		properties.put(null, "nullName");
		assertEquals(false, Style.areValidProperties(properties));

		properties.remove(null);
		assertEquals(true, Style.areValidProperties(properties));

		properties.put("", "emptyName");
		assertEquals(false, Style.areValidProperties(properties));

		properties.remove("");
		assertEquals(true, Style.areValidProperties(properties));		
	}
}