package pb.render;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Graphics2D;

import org.junit.Before;
import org.junit.Test;

import pb.board.Board;
import pb.board.BoardConstants;
import pb.gizmos.Flipper;
import pb.gizmos.Flipper.Type;
import pb.testing.ImageDumper;
import pb.testing.Triggerable;

public class FlipperRendererTest {
	private Viewport viewport;
	private Board board;
	private ImageDumper dumper;
	private Graphics2D context;
	
	@Before
	public void setUp() throws Exception {
		viewport = new Viewport(4, 4);
		board = new Board(BoardConstants.testConstants());
		dumper = new ImageDumper(viewport.xPixels(), viewport.yPixels(),
				Color.BLACK);
		context = dumper.getDrawGraphics2D();
	}

	@Test
	public void testNearCornerLeft0() {
		Flipper flipper = new Flipper("flipper", Type.LEFT, 0, 0, 0);
		FlipperRenderer renderer = new FlipperRenderer(viewport, flipper);
		renderer.render(flipper, context);
		dumper.checkAgainst("flipperNearCornerLeft0");
	}
	
	@Test
	public void testFactory() {
		Viewport buffer = new Viewport(4, 4);
		Flipper flipper = new Flipper("flipper", Type.LEFT, 0, 0, 0);
		RendererFactory factory = new RendererFactory(buffer);
		Renderer renderer = factory.rendererFor(flipper);
		assertTrue(renderer instanceof FlipperRenderer);
		renderer.render(flipper, context);
		dumper.checkAgainst("flipperFactory");
	}

	@Test
	public void testLeft90() {
		Flipper flipper = new Flipper("flipper", Type.LEFT, 1, 0, 90);
		FlipperRenderer renderer = new FlipperRenderer(viewport, flipper);
		renderer.render(flipper, context);
		dumper.checkAgainst("flipperLeft90");
	}
	
	@Test
	public void testLeft0Flipped() {
		Flipper flipper = new Flipper("flipper", Type.LEFT, 1, 0, 0);
		Triggerable triggerable = new Triggerable();
		FlipperRenderer renderer = new FlipperRenderer(viewport, flipper);
		renderer.render(flipper, context);
		dumper.checkAgainst("flipperLeft0Flipped_A");

		board.add(flipper);
		triggerable.addListener(flipper);
		triggerable.trigger();
		board.simulate(1.0);
		assertEquals(-Math.PI / 2, flipper.getAngleDelta(), 0.00001);
				
		viewport.clear(context);
		renderer.render(flipper, context);
		dumper.checkAgainst("flipperLeft0Flipped_B");
	}

	@Test
	public void testNearCornerRight0() {
		Flipper flipper = new Flipper("flipper", Type.RIGHT, 0, 0, 0);
		FlipperRenderer renderer = new FlipperRenderer(viewport, flipper);
		renderer.render(flipper, context);
		dumper.checkAgainst("flipperNearCornerRight0");
	}
}
