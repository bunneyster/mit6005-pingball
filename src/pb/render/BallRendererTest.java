package pb.render;

import static org.junit.Assert.*;

import java.awt.Graphics2D;

import org.junit.Before;
import org.junit.Test;

import pb.gizmos.Ball;
import pb.testing.ImageDumper;

public class BallRendererTest {
	private Viewport viewport;
	private BallRenderer renderer;
	private ImageDumper dumper;
	private Graphics2D context;

	@Before
	public void setUp() throws Exception {
		viewport = new Viewport(3, 2);
		renderer = new BallRenderer(viewport);
		dumper = new ImageDumper(viewport.xPixels(), viewport.yPixels());
		context = dumper.getDrawGraphics2D();
	}

	@Test
	public void testCorner() {
		Ball ball = new Ball("ball", 0.25, 0.25, 0.25, 0.2, 0.3);
		renderer.render(ball, context);
		dumper.checkAgainst("ballCorner");
	}
	
	@Test
	public void testNearWall() {
		Ball ball = new Ball("ball", 2.75, 1.75, 0.25, 0.2, 0);
		renderer.render(ball, context);
		dumper.checkAgainst("ballNearWall");
	}
	
	@Test
	public void testFactory() {
		Ball ball = new Ball("ball", 0.25, 0.25, 0.25, 0.2, 0.3);
		RendererFactory factory = new RendererFactory(viewport);
		Renderer renderer = factory.rendererFor(ball);
		assertTrue(renderer instanceof BallRenderer);
		renderer.render(ball, context);
		dumper.checkAgainst("ballFactory");
	}
}