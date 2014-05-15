package pb.gizmos;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pb.board.Board;
import pb.board.BoardConstants;
import pb.testing.Triggerable;
import physics.Circle;
import physics.LineSegment;

public class AbsorberTest {
	private Board board;
	private Absorber absorber;
	private Triggerable triggerable;
	
	@Before
	public void setUp() throws Exception {
		board = new Board(BoardConstants.testConstants());
		absorber = new Absorber("absorber", 10, 12, 5, 4);
		board.add(absorber);
		triggerable = new Triggerable();
		// NOTE: Triggerables must not be added to the board, because the
		//		 collision code will not recognize them
		triggerable.addListener(absorber);
	}
	
	@Test
	public void testShape() {
		Circle[] corners = absorber.getShape().copyCorners();
		assertEquals(10, corners[0].getCenter().x(), 0.00001);
		assertEquals(12, corners[0].getCenter().y(), 0.00001);
		assertEquals(15, corners[1].getCenter().x(), 0.00001);
		assertEquals(12, corners[1].getCenter().y(), 0.00001);
		assertEquals(15, corners[2].getCenter().x(), 0.00001);
		assertEquals(16, corners[2].getCenter().y(), 0.00001);
		assertEquals(10, corners[3].getCenter().x(), 0.00001);
		assertEquals(16, corners[3].getCenter().y(), 0.00001);
		assertEquals(0, corners[0].getRadius(), 0.00001);
		assertEquals(0, corners[1].getRadius(), 0.00001);
		assertEquals(0, corners[2].getRadius(), 0.00001);

		LineSegment[] sides = absorber.getShape().copySides();
		assertEquals(10, sides[0].p1().x(), 0.00001);
		assertEquals(12, sides[0].p1().y(), 0.00001);
		assertEquals(15, sides[0].p2().x(), 0.00001);
		assertEquals(12, sides[0].p2().y(), 0.00001);
		assertEquals(15, sides[1].p1().x(), 0.00001);
		assertEquals(12, sides[1].p1().y(), 0.00001);
		assertEquals(15, sides[1].p2().x(), 0.00001);
		assertEquals(16, sides[1].p2().y(), 0.00001);
		assertEquals(15, sides[2].p1().x(), 0.00001);
		assertEquals(16, sides[2].p1().y(), 0.00001);
		assertEquals(10, sides[2].p2().x(), 0.00001);
		assertEquals(16, sides[2].p2().y(), 0.00001);
		assertEquals(10, sides[3].p1().x(), 0.00001);
		assertEquals(16, sides[3].p1().y(), 0.00001);
		assertEquals(10, sides[3].p2().x(), 0.00001);
		assertEquals(12, sides[3].p2().y(), 0.00001);
	}

	@Test
	public void testHeadOnEdgeCollision() {
		Ball ball = new Ball("ball", 12, 17 + Ball.STANDARD_RADIUS,
				Ball.STANDARD_RADIUS, 0, -1);
		board.add(ball);
		assertEquals(1.0, absorber.timeToCollision(ball), 0.00001);
		board.simulate(2.0);
		
		assertEquals(15 - Ball.STANDARD_RADIUS, ball.getCenter().x(), 0.00001);
		assertEquals(16 - Ball.STANDARD_RADIUS, ball.getCenter().y(), 0.00001);
		assertEquals(Absorber.LAUNCH_VELOCITY.x(), ball.getVelocity().x(),
				0.00001);
		assertEquals(Absorber.LAUNCH_VELOCITY.y(), ball.getVelocity().y(),
				0.00001);
		assertEquals(false, board.contains(ball));
	}

	@Test
	public void testHeadOnCornerCollision() {
		double delta = Ball.STANDARD_RADIUS / Math.sqrt(2);		
		Ball ball = new Ball("ball", 9 - delta, 11 - delta,
				Ball.STANDARD_RADIUS, 1, 1);
		board.add(ball);
		assertEquals(1.0, absorber.timeToCollision(ball), 0.00001);
		board.simulate(2.0);
		
		assertEquals(15 - Ball.STANDARD_RADIUS, ball.getCenter().x(), 0.00001);
		assertEquals(16 - Ball.STANDARD_RADIUS, ball.getCenter().y(), 0.00001);
		assertEquals(Absorber.LAUNCH_VELOCITY.x(), ball.getVelocity().x(),
				0.00001);
		assertEquals(Absorber.LAUNCH_VELOCITY.y(), ball.getVelocity().y(),
				0.00001);
		assertEquals(false, board.contains(ball));
	}
	
	@Test
	public void testAction() {
		Ball ball = new Ball("ball", 12, 17 + Ball.STANDARD_RADIUS,
				Ball.STANDARD_RADIUS, 0, -1);
		board.add(ball);
		assertEquals(1.0, absorber.timeToCollision(ball), 0.00001);
		board.simulate(2.0);
		assertEquals(false, board.contains(ball));

		triggerable.trigger();
		assertEquals(true, board.contains(ball));
		assertEquals(true, absorber.isInside(ball.getShape()));
		assertEquals(Double.MAX_VALUE, absorber.timeToCollision(ball), 1.0);
	}

	@Test
	public void testDoubleAction() {
		Ball ball1 = new Ball("ball", 12, 17 + Ball.STANDARD_RADIUS,
				Ball.STANDARD_RADIUS, 0, -1);
		board.add(ball1);
		assertEquals(1.0, absorber.timeToCollision(ball1), 0.00001);
		board.simulate(2.0);
		assertEquals(false, board.contains(ball1));

		Ball ball2 = new Ball("ball", 12, 17 + Ball.STANDARD_RADIUS,
				Ball.STANDARD_RADIUS, 0, -1);
		board.add(ball2);
		assertEquals(1.0, absorber.timeToCollision(ball2), 0.00001);
		board.simulate(2.0);
		assertEquals(false, board.contains(ball2));

		triggerable.trigger();
		assertEquals(false, board.contains(ball1));
		assertEquals(true, board.contains(ball2));
		assertEquals(true, absorber.isInside(ball2.getShape()));		

		triggerable.trigger();
		assertEquals(false, board.contains(ball1));
		assertEquals(true, board.contains(ball2));
		
		board.simulate(0.2);
		assertEquals(15 - Ball.STANDARD_RADIUS, ball2.getCenter().x(), 0.00001);
		assertEquals(6 - Ball.STANDARD_RADIUS, ball2.getCenter().y(), 0.00001);		
		assertEquals(false, absorber.isInside(ball2.getShape()));		

		triggerable.trigger();
		assertEquals(true, board.contains(ball1));
		assertEquals(true, board.contains(ball2));
	}

	@Test
	public void testSelfAction() {
		absorber.addListener(absorber);
		
		Ball ball = new Ball("ball", 12, 17 + Ball.STANDARD_RADIUS,
				Ball.STANDARD_RADIUS, 0, -1);
		board.add(ball);
		assertEquals(1.0, absorber.timeToCollision(ball), 0.00001);
		board.simulate(1.2);

		assertEquals(true, board.contains(ball));
		assertEquals(15 - Ball.STANDARD_RADIUS, ball.getCenter().x(), 0.00001);
		assertEquals(6 - Ball.STANDARD_RADIUS, ball.getCenter().y(), 0.00001);		
	}
}
