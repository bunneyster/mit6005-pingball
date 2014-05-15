package pb.board;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import pb.gizmos.Ball;
import pb.gizmos.Wall;
import pb.render.RenderManager;
import pb.testing.NullBufferStrategy;

public class BoardTest {
	private Board board;
	private RenderManager renderManager;
	
	@Before
	public void setUp() throws Exception {
		board = new Board(BoardConstants.testConstants());
		board.setStyleRegistry(StyleRegistry.EMPTY);
		renderManager = new RenderManager(board, new NullBufferStrategy());
	}

	@Test
	public void testAddWithNoRenderManager() {
		Ball ball = new Ball("ball", 1, 1, Ball.STANDARD_RADIUS, 0, 0);
		board.add(ball);
		assertEquals(board, ball.board());
		assertEquals(null, ball.renderer());
	}

	@Test
	public void testAddWithRenderManager() {
		board.setRenderManager(renderManager);
		Ball ball = new Ball("ball", 1, 1, Ball.STANDARD_RADIUS, 0, 0);
		board.add(ball);
		assertEquals(board, ball.board());
		assertNotEquals(null, ball.renderer());
	}
	
	@Test
	public void testSetRenderManagerCreatesRenderers() {
		for (Iterator<Gizmo> iterator = board.getGizmos();
				iterator.hasNext(); ) {
			Gizmo gizmo = iterator.next();
			assertEquals(null, gizmo.renderer());
		}
		board.setRenderManager(renderManager);
		for (Iterator<Gizmo> iterator = board.getGizmos();
				iterator.hasNext(); ) {
			Gizmo gizmo = iterator.next();
			assertNotEquals(null, gizmo.renderer());
		}		
	}
	
	@Test
	public void testSimulate() {
		Ball ball = new Ball("ball", 1, 1, Ball.STANDARD_RADIUS, 1, 1);
		board.add(ball);
		assertEquals(false, board.isPaused());
		board.simulate(1);
		assertEquals(2, ball.getCenter().x(), 0.00001);
		assertEquals(2, ball.getCenter().y(), 0.00001);
	}

	@Test
	public void testSimulateAfterPause() {
		Ball ball = new Ball("ball", 1, 1, Ball.STANDARD_RADIUS, 1, 1);
		board.add(ball);
		board.setPaused(true);
		assertEquals(true, board.isPaused());
		board.simulate(1);
		assertEquals(1, ball.getCenter().x(), 0.00001);
		assertEquals(1, ball.getCenter().y(), 0.00001);
	}
	
	@Test
	public void testSimulateAfterPauseAndResume() {
		Ball ball = new Ball("ball", 1, 1, Ball.STANDARD_RADIUS, 1, 1);
		board.add(ball);
		board.setPaused(true);
		assertEquals(true, board.isPaused());
		board.simulate(1);
		assertEquals(1, ball.getCenter().x(), 0.00001);
		assertEquals(1, ball.getCenter().y(), 0.00001);
		
		board.setPaused(false);
		assertEquals(false, board.isPaused());
		board.simulate(1);
		assertEquals(2, ball.getCenter().x(), 0.00001);
		assertEquals(2, ball.getCenter().y(), 0.00001);
	}
	
	@Test
	public void testDisconnectClearsWallNeighbors() {
		board.setHasServer(true);
		Wall left = (Wall)board.findByName(Edge.LEFT.wallName());
		left.setNeighborName("someone");

		board.setHasServer(false);
		assertEquals(null, left.getNeighborName());
	}
}
