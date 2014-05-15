package pb.gizmos;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pb.board.Board;
import pb.board.BoardConstants;
import pb.testing.NullGizmo;
import pb.testing.Triggerable;
import physics.Circle;
import physics.LineSegment;

public class TriangleBumperTest {
	private Board board;
	private TriangleBumper bumper;

	@Before
	public void setUp() throws Exception {
		board = new Board(BoardConstants.testConstants());
		bumper = new TriangleBumper("triangle", 10, 5, 0, false);
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
	public void testOrientation0() {
		TriangleBumper bumper = new TriangleBumper("triangle", 10, 5, 0, false);
		
		Circle[] corners = bumper.getShape().copyCorners();
		assertEquals(3, corners.length);
		assertEquals(10, corners[0].getCenter().x(), 0.00001);
		assertEquals(5, corners[0].getCenter().y(), 0.00001);
		assertEquals(11, corners[1].getCenter().x(), 0.00001);
		assertEquals(5, corners[1].getCenter().y(), 0.00001);
		assertEquals(10, corners[2].getCenter().x(), 0.00001);
		assertEquals(6, corners[2].getCenter().y(), 0.00001);
		assertEquals(0, corners[0].getRadius(), 0.00001);
		assertEquals(0, corners[1].getRadius(), 0.00001);
		assertEquals(0, corners[2].getRadius(), 0.00001);
		
		LineSegment[] sides = bumper.getShape().copySides();
		assertEquals(10, sides[0].p1().x() , 0.00001);
		assertEquals(5, sides[0].p1().y(), 0.00001);		
		assertEquals(11, sides[0].p2().x() , 0.00001);
		assertEquals(5, sides[0].p2().y(), 0.00001);
		assertEquals(11, sides[1].p1().x() , 0.00001);
		assertEquals(5, sides[1].p1().y(), 0.00001);		
		assertEquals(10, sides[1].p2().x() , 0.00001);
		assertEquals(6, sides[1].p2().y(), 0.00001);		
		assertEquals(10, sides[2].p1().x() , 0.00001);
		assertEquals(6, sides[2].p1().y(), 0.00001);		
		assertEquals(10, sides[2].p2().x() , 0.00001);
		assertEquals(5, sides[2].p2().y(), 0.00001);		
	}

	@Test
	public void testOrientation90() {
		TriangleBumper bumper = new TriangleBumper("triangle", 10, 5, 90,
				false);
		
		Circle[] corners = bumper.getShape().copyCorners();
		assertEquals(3, corners.length);
		assertEquals(11, corners[0].getCenter().x(), 0.00001);
		assertEquals(5, corners[0].getCenter().y(), 0.00001);
		assertEquals(11, corners[1].getCenter().x(), 0.00001);
		assertEquals(6, corners[1].getCenter().y(), 0.00001);
		assertEquals(10, corners[2].getCenter().x(), 0.00001);
		assertEquals(5, corners[2].getCenter().y(), 0.00001);
		assertEquals(0, corners[0].getRadius(), 0.00001);
		assertEquals(0, corners[1].getRadius(), 0.00001);
		assertEquals(0, corners[2].getRadius(), 0.00001);
		
		LineSegment[] sides = bumper.getShape().copySides();
		assertEquals(11, sides[0].p1().x() , 0.00001);
		assertEquals(5, sides[0].p1().y(), 0.00001);		
		assertEquals(11, sides[0].p2().x() , 0.00001);
		assertEquals(6, sides[0].p2().y(), 0.00001);		
		assertEquals(11, sides[1].p1().x() , 0.00001);
		assertEquals(6, sides[1].p1().y(), 0.00001);		
		assertEquals(10, sides[1].p2().x() , 0.00001);
		assertEquals(5, sides[1].p2().y(), 0.00001);		
		assertEquals(10, sides[2].p1().x() , 0.00001);
		assertEquals(5, sides[2].p1().y(), 0.00001);		
		assertEquals(11, sides[2].p2().x() , 0.00001);
		assertEquals(5, sides[2].p2().y(), 0.00001);
	}

	@Test
	public void testOrientation180() {
		TriangleBumper bumper = new TriangleBumper("triangle", 10, 5, 180,
				false);
		
		Circle[] corners = bumper.getShape().copyCorners();
		assertEquals(3, corners.length);
		assertEquals(11, corners[0].getCenter().x(), 0.00001);
		assertEquals(6, corners[0].getCenter().y(), 0.00001);
		assertEquals(10, corners[1].getCenter().x(), 0.00001);
		assertEquals(6, corners[1].getCenter().y(), 0.00001);
		assertEquals(11, corners[2].getCenter().x(), 0.00001);
		assertEquals(5, corners[2].getCenter().y(), 0.00001);
		assertEquals(0, corners[0].getRadius(), 0.00001);
		assertEquals(0, corners[1].getRadius(), 0.00001);
		assertEquals(0, corners[2].getRadius(), 0.00001);
		
		LineSegment[] sides = bumper.getShape().copySides();
		assertEquals(11, sides[0].p1().x() , 0.00001);
		assertEquals(6, sides[0].p1().y(), 0.00001);
		assertEquals(10, sides[0].p2().x() , 0.00001);
		assertEquals(6, sides[0].p2().y(), 0.00001);
		assertEquals(10, sides[1].p1().x() , 0.00001);
		assertEquals(6, sides[1].p1().y(), 0.00001);
		assertEquals(11, sides[1].p2().x() , 0.00001);
		assertEquals(5, sides[1].p2().y(), 0.00001);
		assertEquals(11, sides[2].p1().x() , 0.00001);
		assertEquals(5, sides[2].p1().y(), 0.00001);
		assertEquals(11, sides[2].p2().x() , 0.00001);
		assertEquals(6, sides[2].p2().y(), 0.00001);
	}
	
	@Test
	public void testOrientation270() {
		TriangleBumper bumper = new TriangleBumper("triangle", 10, 5, 270,
				false);
		
		Circle[] corners = bumper.getShape().copyCorners();
		assertEquals(3, corners.length);
		assertEquals(10, corners[0].getCenter().x(), 0.00001);
		assertEquals(6, corners[0].getCenter().y(), 0.00001);
		assertEquals(10, corners[1].getCenter().x(), 0.00001);
		assertEquals(5, corners[1].getCenter().y(), 0.00001);
		assertEquals(11, corners[2].getCenter().x(), 0.00001);
		assertEquals(6, corners[2].getCenter().y(), 0.00001);
		assertEquals(0, corners[0].getRadius(), 0.00001);
		assertEquals(0, corners[1].getRadius(), 0.00001);
		assertEquals(0, corners[2].getRadius(), 0.00001);
		
		LineSegment[] sides = bumper.getShape().copySides();
		assertEquals(10, sides[0].p1().x() , 0.00001);
		assertEquals(6, sides[0].p1().y(), 0.00001);
		assertEquals(10, sides[0].p2().x() , 0.00001);
		assertEquals(5, sides[0].p2().y(), 0.00001);
		assertEquals(10, sides[1].p1().x() , 0.00001);
		assertEquals(5, sides[1].p1().y(), 0.00001);
		assertEquals(11, sides[1].p2().x() , 0.00001);
		assertEquals(6, sides[1].p2().y(), 0.00001);
		assertEquals(11, sides[2].p1().x() , 0.00001);
		assertEquals(6, sides[2].p1().y(), 0.00001);
		assertEquals(10, sides[2].p2().x() , 0.00001);
		assertEquals(6, sides[2].p2().y(), 0.00001);
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
		TriangleBumper exploding = new TriangleBumper("triangle", 10, 5, 0,
				true);
		board.add(exploding);

		Triggerable triggerable = new Triggerable();
		triggerable.addListener(bumper);
		triggerable.addListener(exploding);
		triggerable.trigger();
		assertEquals(true, board.contains(bumper));
		assertEquals(false, board.contains(exploding));
	}	
}
