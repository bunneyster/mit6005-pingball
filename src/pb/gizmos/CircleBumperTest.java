package pb.gizmos;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pb.board.Board;
import pb.board.BoardConstants;
import pb.testing.NullGizmo;
import pb.testing.Triggerable;

public class CircleBumperTest {
	private Board board;
	private CircleBumper bumper;

	@Before
	public void setUp() throws Exception {
		board = new Board(BoardConstants.testConstants());
		bumper = new CircleBumper("circle", 10, 5, false);
		board.add(bumper);
	}

	@Test
	public void testHeadOnCollision() {
		Ball ball = new Ball("ball", 9 - Ball.STANDARD_RADIUS, 5.5,
				Ball.STANDARD_RADIUS, 1, 0);
		board.add(ball);
		
		assertEquals(1.0, bumper.timeToCollision(ball), 0.00001);
		board.simulate(2.0);
		assertEquals(9 - Ball.STANDARD_RADIUS, ball.getCenter().x(), 0.00001);
		assertEquals(5.5, ball.getCenter().y(), 0.00001);
		assertEquals(-1, ball.getVelocity().x(), 0.00001);
		assertEquals(0, ball.getVelocity().y(), 0.00001);		
	}

	@Test
	public void testSideCollision() {
		Ball ball = new Ball("ball", 9 - Ball.STANDARD_RADIUS, 4.5,
				Ball.STANDARD_RADIUS, 1, 1);
		board.add(ball);
		
		assertEquals(1.0, bumper.timeToCollision(ball), 0.00001);
		board.simulate(2.0);
		assertEquals(9 - Ball.STANDARD_RADIUS, ball.getCenter().x(), 0.00001);
		assertEquals(6.5, ball.getCenter().y(), 0.00001);
		assertEquals(-1, ball.getVelocity().x(), 0.00001);
		assertEquals(1, ball.getVelocity().y(), 0.00001);		
	}
	
	@Test
	public void testCollisionTrigger() {
		NullGizmo nullGizmo = new NullGizmo();
		bumper.addListener(nullGizmo);

		Ball ball = new Ball("ball", 9 - Ball.STANDARD_RADIUS, 5.5,
				Ball.STANDARD_RADIUS, 1, 0);
		board.add(ball);
		
		assertEquals(1.0, bumper.timeToCollision(ball), 0.00001);
		board.simulate(2.0);		
		assertEquals(1, nullGizmo.actionCount());
	}
	
	@Test
	public void testAction() {
		CircleBumper exploding = new CircleBumper("circle", 10, 5, true);
		board.add(exploding);

		Triggerable triggerable = new Triggerable();
		triggerable.addListener(bumper);
		triggerable.addListener(exploding);
		triggerable.trigger();
		assertEquals(true, board.contains(bumper));
		assertEquals(false, board.contains(exploding));
	}	
}
