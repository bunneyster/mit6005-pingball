package pb.render;

import static org.junit.Assert.assertTrue;

import java.awt.Graphics2D;

import org.junit.Before;
import org.junit.Test;

import pb.gizmos.Wall;
import pb.testing.ImageDumper;

public class WallRendererTest {
	private Viewport viewport, longViewport;
	private ImageDumper dumper, longDumper;
	private Graphics2D context, longContext;
	
	@Before
	public void setUp() throws Exception {
		viewport = new Viewport(3, 2);
		dumper = new ImageDumper(viewport.xPixels(), viewport.yPixels());
		context = dumper.getDrawGraphics2D();

		longViewport = new Viewport(9, 2);
		longDumper = new ImageDumper(longViewport.xPixels(),
				longViewport.yPixels());
		longContext = longDumper.getDrawGraphics2D();
	}
	
	@Test
	public void testHorizontalTop() {
		Wall wall = new Wall(0, 0, 3, 0);
		WallRenderer renderer = new WallRenderer(wall, viewport);
		renderer.render(wall, context);
		dumper.checkAgainst("wallHorizontalTop");
	}

	@Test
	public void testHorizontalBottom() {
		Wall wall = new Wall(0, 2, 3, 2);
		WallRenderer renderer = new WallRenderer(wall, viewport);
		renderer.render(wall, context);
		dumper.checkAgainst("wallHorizontalBottom");
	}

	@Test
	public void testVerticalLeft() {
		Wall wall = new Wall(0, 0, 0, 2);
		WallRenderer renderer = new WallRenderer(wall, viewport);
		renderer.render(wall, context);
		dumper.checkAgainst("wallVerticalLeft");
	}

	@Test
	public void testVerticalRight() {
		Wall wall = new Wall(3, 0, 3, 2);
		WallRenderer renderer = new WallRenderer(wall, viewport);
		renderer.render(wall, context);
		dumper.checkAgainst("wallVerticalRight");
	}

	@Test
	public void testFactory() {
		Wall wall = new Wall(3, 0, 3, 2);
		RendererFactory factory = new RendererFactory(viewport);
		Renderer renderer = factory.rendererFor(wall);
		assertTrue(renderer instanceof WallRenderer);
		renderer.render(wall, context);
		dumper.checkAgainst("wallFactory");
	}
	
	@Test
	public void testHorizontalTopLabel() {
		Wall wall = new Wall(0, 0, 9, 0);
		WallRenderer renderer = new WallRenderer(wall, longViewport);
		wall.setNeighborName("Jon");
		renderer.render(wall, longContext);
		longDumper.checkAgainst("wallHorizontalTopLabel");
	}	

	@Test
	public void testHorizontalBottomLabel() {
		Wall wall = new Wall(0, 2, 9, 2);
		WallRenderer renderer = new WallRenderer(wall, longViewport);
		wall.setNeighborName("Bloodstains");
		renderer.render(wall, longContext);
		longDumper.checkAgainst("wallHorizontalBottomLabel");
	}

	@Test
	public void testVerticalLeftLabel() {
		Wall wall = new Wall(0, 0, 0, 2);
		WallRenderer renderer = new WallRenderer(wall, longViewport);
		wall.setNeighborName("Clot");
		renderer.render(wall, longContext);
		longDumper.checkAgainst("wallVerticalLeftLabel");
	}

	@Test
	public void testVerticalRightLabel() {
		Wall wall = new Wall(9, 0, 9, 2);
		WallRenderer renderer = new WallRenderer(wall, longViewport);
		wall.setNeighborName("Red");
		renderer.render(wall, longContext);
		longDumper.checkAgainst("wallVerticalRightLabel");
	}
}