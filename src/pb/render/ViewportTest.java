package pb.render;

import static org.junit.Assert.*;

import org.junit.Test;

public class ViewportTest {
	@Test
	public void testConstruction() {
		Viewport viewport = new Viewport(20, 10);
		assertEquals(20, viewport.xSize());
		assertEquals(10, viewport.ySize());
		assertEquals(20, viewport.xScale());
		assertEquals(20, viewport.yScale());
	}

	@Test
	public void testPixels() {
		Viewport viewport = new Viewport(20, 10);
		assertEquals(440, viewport.xPixels());
		assertEquals(240, viewport.yPixels());
	}	

	@Test
	public void testCoordinates() {
		Viewport viewport = new Viewport(20, 10);

		assertEquals(0, viewport.x(-1));
		assertEquals(20, viewport.x(0));
		assertEquals(30, viewport.x(0.5));
		assertEquals(420, viewport.x(20));
		assertEquals(440, viewport.x(21));
		
		assertEquals(0, viewport.y(-1));
		assertEquals(20, viewport.y(0));
		assertEquals(30, viewport.y(0.5));
		assertEquals(220, viewport.y(10));
		assertEquals(240, viewport.y(11));
	}	

	@Test
	public void testDeltas() {
		Viewport viewport = new Viewport(20, 10);

		assertEquals(0, viewport.dx(0));
		assertEquals(10, viewport.dx(0.5));
		assertEquals(20, viewport.dx(1));
		assertEquals(-20, viewport.dx(-1));
		assertEquals(-10, viewport.dx(-0.5));
		assertEquals(400, viewport.dx(20));
		assertEquals(-400, viewport.dx(-20));
		
		assertEquals(0, viewport.dy(0));
		assertEquals(10, viewport.dy(0.5));
		assertEquals(20, viewport.dy(1));
		assertEquals(-20, viewport.dy(-1));
		assertEquals(-10, viewport.dy(-0.5));
		assertEquals(400, viewport.dy(20));
		assertEquals(-400, viewport.dy(-20));
	}
}