package pb.gizmos;

import static org.junit.Assert.*;

import org.junit.Test;

import pb.board.Board;
import pb.board.BoardConstants;

public class BallTest {
	@Test
	public void testMovement() {
		Board board = new Board(BoardConstants.testConstants());
		Ball ball = new Ball("ball", 1, 1, Ball.STANDARD_RADIUS, 1, 2);
		double t = 2;
		board.add(ball);
		board.simulate(t);
		
		assertEquals(1 + 1 * t, ball.getCenter().x(), 0.00001);
		assertEquals(1 + 2 * t, ball.getCenter().y(), 0.00001);
		assertEquals(1, ball.getVelocity().x(), 0.00001);
		assertEquals(2, ball.getVelocity().y(), 0.00001);
	}
	
	@Test
	public void testGravity() {
		Board board = new Board(BoardConstants.testGravityConstants(2));
		Ball ball = new Ball("ball", 1, 1, Ball.STANDARD_RADIUS, 0, 0);
		double t = 2;
		board.add(ball);
		board.simulate(t);
		
		assertEquals(1, ball.getCenter().x(), 0.01);
		assertEquals(1 + 0.5 * 2 * (t * t), ball.getCenter().y(), 0.01);
		assertEquals(0, ball.getVelocity().x(), 0.01);
		assertEquals(2 * t, ball.getVelocity().y(), 0.01);
	}	
}
