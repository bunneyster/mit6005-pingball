package pb.render;

import static org.junit.Assert.*;

import java.awt.Graphics2D;

import org.junit.Before;
import org.junit.Test;

import pb.gizmos.Absorber;
import pb.testing.ImageDumper;

public class AbsorberRendererTest {
	private Viewport viewport;
	private AbsorberRenderer renderer;
	private ImageDumper dumper;
	private Graphics2D context;

	@Before
	public void setUp() throws Exception {
		viewport = new Viewport(4, 2);
		renderer = new AbsorberRenderer(viewport);
		dumper = new ImageDumper(viewport.xPixels(), viewport.yPixels());		
		context = dumper.getDrawGraphics2D();
	}
	
	@Test
	public void test1x1() {
		Absorber absorber = new Absorber("absorber", 0, 1, 1, 1);
		renderer.render(absorber, context);
		dumper.checkAgainst("absorber1x1");
	}

	@Test
	public void test3x2() {
		Absorber absorber = new Absorber("absorber", 1, 0, 3, 2);
		renderer.render(absorber, context);
		dumper.checkAgainst("absorber3x2");
	}

	@Test
	public void testFactory() {
		Absorber absorber = new Absorber("absorber", 0, 1, 1, 1);
		RendererFactory factory = new RendererFactory(viewport);
		Renderer renderer = factory.rendererFor(absorber);
		assertTrue(renderer instanceof AbsorberRenderer);		
		renderer.render(absorber, context);
		dumper.checkAgainst("absorberFactory");
	}
}
