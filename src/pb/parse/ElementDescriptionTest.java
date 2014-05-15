package pb.parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

public class ElementDescriptionTest {
	@Test
	public void testCommentLine() {
		ElementDescription element = ElementDescription.fromLine(
				"  # a comment  ");
		assertEquals(null, element);
	}

	@Test
	public void testBlankLine() {
		ElementDescription element = ElementDescription.fromLine("    ");
		assertEquals(null, element);
	}

	@Test
	public void testType() {
		ElementDescription element = ElementDescription.fromLine("board");
		assertEquals("board", element.getType());
	}

	@Test
	public void testNameAndTwoProperties() {
		ElementDescription element = ElementDescription.fromLine(
				"ball name=Ball xVelocity=-0.5");
		assertEquals("ball", element.getType());
		assertEquals("Ball", element.getString("name", null));
		assertEquals("-0.5", element.getString("xVelocity", null));
	}
	
	@Test
	public void testNameAndThreePropertiesWithWhitespaceNearEqual() {
		ElementDescription element = ElementDescription.fromLine(
				"ball name   =Ball xVelocity  =   -0.5  yVelocity=    6.66");
		assertEquals("ball", element.getType());
		assertEquals("Ball", element.getString("name", null));
		assertEquals("-0.5", element.getString("xVelocity", null));
		assertEquals("6.66", element.getString("yVelocity", null));
	}	

	@Test
	public void testGetStringDefault() {
		ElementDescription element = ElementDescription.fromLine("board");
		assertEquals("board", element.getType());
		assertEquals("default", element.getString("name", "default"));
	}

	@Test
	public void testGetStringMandatory() {
		ElementDescription element = ElementDescription.fromLine(
				"board name=Board");
		assertEquals("Board", element.getString("name"));
		try {
			element.getString("foo");
			fail("No exception raised");
		} catch(IllegalArgumentException e) {
			assertTrue(e.getMessage().indexOf("mandatory") > 0);
		}
	}
	
	@Test
	public void testGetInteger() {
		ElementDescription element = ElementDescription.fromLine(
				"circleBumper name=Circle x=4 y=3");
		assertEquals("circleBumper", element.getType());
		assertEquals("Circle", element.getString("name", null));
		assertEquals(4, element.getInteger("x", -1));
		assertEquals(3, element.getInteger("y", -1));
		assertEquals(-1, element.getInteger("radius", -1));
	}
	
	@Test
	public void testGetIntegerMandatory() {
		ElementDescription element = ElementDescription.fromLine(
				"circleBumper name=Circle x=4 y=3");
		assertEquals("circleBumper", element.getType());
		assertEquals("Circle", element.getString("name", null));
		assertEquals(4, element.getInteger("x"));
		assertEquals(3, element.getInteger("y"));

		try {
			element.getInteger("radius");
			fail("No exception raised");
		} catch(IllegalArgumentException e) {
			assertTrue(e.getMessage().indexOf("mandatory") > 0);
		}		
	}	

	@Test
	public void testGetFloat() {
		ElementDescription element = ElementDescription.fromLine(
				"ball name=Ball x=1.8 y=4.5 xVelocity=-3.4 yVelocity=-2.3");
		assertEquals("ball", element.getType());
		assertEquals("Ball", element.getString("name", null));
		assertEquals(1.8, element.getFloat("x", Double.NaN), 0.00001);
		assertEquals(4.5, element.getFloat("y", Double.NaN), 0.00001);
		assertEquals(-3.4, element.getFloat("xVelocity", Double.NaN), 0.00001);
		assertEquals(-2.3, element.getFloat("yVelocity", Double.NaN), 0.00001);
		assertEquals(3.14, element.getFloat("pi", 3.14), 0.00001);
	}

	@Test
	public void testGetFloatMandatory() {
		ElementDescription element = ElementDescription.fromLine(
				"ball name=Ball x=1.8 y=4.5");
		assertEquals("ball", element.getType());
		assertEquals("Ball", element.getString("name", null));
		assertEquals(1.8, element.getFloat("x"), 0.00001);
		assertEquals(4.5, element.getFloat("y"), 0.00001);
		
		try {
			element.getFloat("pi");
			fail("No exception raised");
		} catch(IllegalArgumentException e) {
			assertTrue(e.getMessage().indexOf("mandatory") > 0);
		}		
	}
	
	@Test
	public void getPropertyNames() {
		ElementDescription element = ElementDescription.fromLine(
				"ball name=Ball xVelocity=-0.5");
		
		Iterator<String> iterator = element.getPropertyNames();
		ArrayList<String> list = new ArrayList<String>();
		for (; iterator.hasNext(); ) {
			list.add(iterator.next());
			try {
				iterator.remove();
				fail("remove() did not throw UnsupportedOperationException");
			} catch(UnsupportedOperationException e) {
				// remove() is supposed to throw this exception.
			}
		}
		
		assertEquals(2, list.size());
		assertTrue(list.contains("name"));
		assertTrue(list.contains("xVelocity"));
	}
}
