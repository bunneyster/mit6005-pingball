package pb.board;

import static org.junit.Assert.*;

import org.junit.Test;

public class BoardConstantsTest {
	@Test
	public void testConstructor() {
		BoardConstants constants = new BoardConstants("board name", 10, 20, 9.8,
				0.25, 0.75);
		assertEquals("board name", constants.name());
		assertEquals(10, constants.xSize());
		assertEquals(20, constants.ySize());
		assertEquals(9.8, constants.gravity(), 0.00001);
		assertEquals(0.25, constants.friction1(), 0.00001);
		assertEquals(0.75, constants.friction2(), 0.00001);
	}
}
