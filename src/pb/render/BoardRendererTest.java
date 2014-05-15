package pb.render;

import java.awt.Graphics2D;

import org.junit.Before;
import org.junit.Test;

import pb.board.Board;
import pb.board.BoardConstants;
import pb.board.StyleRegistry;
import pb.gizmos.CircleBumper;
import pb.gizmos.SquareBumper;
import pb.testing.ImageDumper;

public class BoardRendererTest {
	private Board board;
	private BoardRenderer renderer;
	private ImageDumper dumper;
	private Graphics2D context;
	
	
	@Before
	public void setUp() throws Exception {
		board = new Board(new BoardConstants("board", 5, 4, 0, 0, 0));
		board.setStyleRegistry(StyleRegistry.EMPTY);
		Viewport viewport = board.getViewport();
		dumper = new ImageDumper(viewport.xPixels(), viewport.yPixels());
		context = dumper.getDrawGraphics2D();
		RenderManager renderManager = new RenderManager(board, dumper); 
		board.setRenderManager(renderManager);
		renderer = renderManager.getBoardRenderer();
	}

	@Test
	public void testEmptyBoard() {
		renderer.render(null, context);
		dumper.checkAgainst("boardEmpty");
	}

	@Test
	public void testBoardWithBumpers() {
		board.add(new SquareBumper("square", 1, 3, false));
		board.add(new CircleBumper("circle", 3, 3, false));
		renderer.render(null, context);
		dumper.checkAgainst("boardWithBumpers");
	}
}
