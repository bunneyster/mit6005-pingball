package pb.render;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pb.board.Board;
import pb.board.BoardConstants;
import pb.board.Gizmo;
import pb.board.StyleRegistry;
import pb.gizmos.CircleBumper;
import pb.gizmos.SquareBumper;
import pb.testing.ImageDumper;

public class RenderManagerTest {
	private Board board;
	private RenderManager renderManager;
	private ImageDumper dumper;
	
	@Before
	public void setUp() throws Exception {
		board = new Board(new BoardConstants("board", 5, 4, 0, 0, 0));
		board.setStyleRegistry(StyleRegistry.EMPTY);
		Viewport viewport = board.getViewport();
		dumper = new ImageDumper(viewport.xPixels(), viewport.yPixels());
		renderManager = new RenderManager(board, dumper); 
		board.setRenderManager(renderManager);
	}
	
	@Test
	public void testGetters() {
		assertEquals(board, renderManager.getBoard());
		assertNotNull(renderManager.getBoardRenderer());
	}
	
	@Test
	public void testCreateRenderer() {
		Gizmo square = new SquareBumper("square", 1, 3, false);
		square.setBoard(board);
		renderManager.attachRenderer(square);
		
		assertNotNull(square.renderer());
		assertTrue(square.renderer() instanceof BumperRenderer);
	}

	@Test
	public void testRenderBoardWithBumpers() {
		board.add(new SquareBumper("square", 1, 3, false));
		board.add(new CircleBumper("circle", 3, 3, false));
		renderManager.renderFrame();
		dumper.checkAgainst("renderManagerBoardWithBumpers");
	}
}
