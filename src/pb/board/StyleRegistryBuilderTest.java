package pb.board;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class StyleRegistryBuilderTest {
	private StyleRegistryBuilder builder;
	
	@Before
	public void setUp() throws Exception {
		builder = new StyleRegistryBuilder();
		builder.setProperty("green", "color", "0000ff");
		builder.setProperty("green", "texture", "plant.png");
		builder.setProperty("green", "color", "00ff00");
		builder.setProperty("blue", "color", "0000ff");
	}

	@Test
	public void testBuild() {
		StyleRegistry styles = builder.build();
		assertNotNull(styles);
		
		Style green = styles.forClass("green");
		assertEquals("plant.png", green.value("texture", null));
		assertEquals("00ff00", green.value("color", null));
		Style blue = styles.forClass("blue");
		assertEquals(null, blue.value("texture", null));
		assertEquals("0000ff", blue.value("color", null));
	}

	@Test
	public void testImmutability() {
		StyleRegistry styles = builder.build();
		builder.setProperty("blue", "color", "modified");
		
		Style blue = styles.forClass("blue");
		assertEquals("0000ff", blue.value("color", null));
	}
}