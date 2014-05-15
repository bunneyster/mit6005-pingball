package pb.gizmos;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import pb.board.Board;
import pb.board.BoardConstants;
import pb.testing.NullGizmo;
import pb.testing.Triggerable;

public class SquareBumperTest {
	private Board board;
	private SquareBumper bumper;
	
	@Before
	public void setUp() throws Exception {
		board = new Board(BoardConstants.testConstants());
		bumper = new SquareBumper("square", 10, 5, false);
		board.add(bumper);
	}

	@Test
	public void testHeadOnEdgeCollision() {
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
	public void testSideEdgeCollision() {
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
	public void testHeadOnCornerCollision() {
		double delta = Ball.STANDARD_RADIUS / Math.sqrt(2);
		Ball ball = new Ball("ball", 9 - delta, 4 - delta,
				Ball.STANDARD_RADIUS, 1, 1);
		board.add(ball);
		
		assertEquals(1.0, bumper.timeToCollision(ball), 0.00001);
		board.simulate(2.0);
		assertEquals(9 - delta, ball.getCenter().x(), 0.00001);
		assertEquals(4 - delta, ball.getCenter().y(), 0.00001);
		assertEquals(-1, ball.getVelocity().x(), 0.00001);
		assertEquals(-1, ball.getVelocity().y(), 0.00001);
	}
	
	@Test
	public void testSideCornerCollision() {
		Ball ball = new Ball("ball", 10, 4 - Ball.STANDARD_RADIUS,
				Ball.STANDARD_RADIUS, 0, 1);
		board.add(ball);
		
		assertEquals(1.0, bumper.timeToCollision(ball), 0.00001);
		board.simulate(2.0);
		assertEquals(10, ball.getCenter().x(), 0.00001);
		assertEquals(4 - Ball.STANDARD_RADIUS, ball.getCenter().y(), 0.00001);
		assertEquals(0, ball.getVelocity().x(), 0.00001);
		assertEquals(-1, ball.getVelocity().y(), 0.00001);
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
		SquareBumper exploding = new SquareBumper("square", 10, 5, true);
		board.add(exploding);

		Triggerable triggerable = new Triggerable();
		triggerable.addListener(bumper);
		triggerable.addListener(exploding);
		triggerable.trigger();
		assertEquals(true, board.contains(bumper));
		assertEquals(false, board.contains(exploding));
	}
}
