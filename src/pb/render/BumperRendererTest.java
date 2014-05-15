package pb.render;

import static org.junit.Assert.*;

import java.awt.Graphics2D;

import org.junit.Before;
import org.junit.Test;

import pb.board.Board;
import pb.board.BoardConstants;
import pb.board.StyleRegistryBuilder;
import pb.gizmos.BumperBase;
import pb.gizmos.CircleBumper;
import pb.gizmos.SquareBumper;
import pb.gizmos.TriangleBumper;
import pb.testing.ImageDumper;

public class BumperRendererTest {
	private Viewport viewport;
	private ImageDumper dumper;
	private Graphics2D context;
	private Board board;
	
	@Before
	public void setUp() throws Exception {
		viewport = new Viewport(3, 2);
		dumper = new ImageDumper(viewport.xPixels(), viewport.yPixels());
		context = dumper.getDrawGraphics2D();
		
		StyleRegistryBuilder stylesBuilder = new StyleRegistryBuilder();
		stylesBuilder.setProperty("yellow", "color", "ffff00");
		board = new Board(BoardConstants.testConstants());
		board.setStyleRegistry(stylesBuilder.build());		
	}

	@Test
	public void testCircleBumper() {
		BumperBase bumper = new CircleBumper("circle", 2, 1, false);
		bumper.setBoard(board);
		BumperRenderer renderer = new BumperRenderer(viewport, bumper);
		renderer.render(bumper, context);
		dumper.checkAgainst("bumperCircle");
	}

	@Test
	public void testSquareBumper() {
		BumperBase bumper = new SquareBumper("square", 2, 1, false);
		bumper.setBoard(board);
		BumperRenderer renderer = new BumperRenderer(viewport, bumper);
		renderer.render(bumper, context);
		dumper.checkAgainst("bumperSquare");
	}

	@Test
	public void testYellowBumper() {		
		BumperBase bumper = new SquareBumper("square", 2, 1, false);
		bumper.setBoard(board);
		bumper.setStyleClass("yellow");
		BumperRenderer renderer = new BumperRenderer(viewport, bumper);
		renderer.render(bumper, context);
		dumper.checkAgainst("bumperYellowSquare");
	}
	
	@Test
	public void testTriangleBumper0() {
		BumperBase bumper = new TriangleBumper("triangle", 2, 1, 0, false);
		bumper.setBoard(board);
		BumperRenderer renderer = new BumperRenderer(viewport, bumper);
		renderer.render(bumper, context);
		dumper.checkAgainst("bumperTriangle0");
	}

	@Test
	public void testTriangleBumper90() {
		BumperBase bumper = new TriangleBumper("triangle", 2, 1, 90, false);
		bumper.setBoard(board);
		BumperRenderer renderer = new BumperRenderer(viewport, bumper);
		renderer.render(bumper, context);
		dumper.checkAgainst("bumperTriangle90");
	}

	@Test
	public void testTriangleBumper180() {
		BumperBase bumper = new TriangleBumper("triangle", 2, 1, 180, false);
		bumper.setBoard(board);
		BumperRenderer renderer = new BumperRenderer(viewport, bumper);
		renderer.render(bumper, context);
		dumper.checkAgainst("bumperTriangle180");
	}

	@Test
	public void testTriangleBumper270() {
		BumperBase bumper = new TriangleBumper("triangle", 2, 1, 270, false);
		bumper.setBoard(board);
		BumperRenderer renderer = new BumperRenderer(viewport, bumper);
		renderer.render(bumper, context);
		dumper.checkAgainst("bumperTriangle270");
	}
	
	@Test
	public void testFactory() {
		BumperBase bumper = new CircleBumper("circle", 2, 1, false);
		bumper.setBoard(board);
		RendererFactory factory = new RendererFactory(viewport);
		Renderer renderer = factory.rendererFor(bumper);
		assertTrue(renderer instanceof BumperRenderer);
		renderer.render(bumper, context);
		dumper.checkAgainst("bumperFactory");
	}	
}
