package pb.board;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pb.gizmos.Ball;
import physics.Circle;
import physics.LineSegment;
import physics.Vect;

public class ShapeTest {
	private Vect[] cornersList;
	private Shape oneCorner;
	private Shape twoCorners;
	private Shape threeCorners;
	private Shape fourCorners;
	private double delta;
	
	@Before
	public void setUp() throws Exception {
		cornersList = new Vect[] {new Vect(10, 5), new Vect(11, 5), 
				new Vect(11, 6), new Vect(10, 6)};
		oneCorner = new Shape(new Vect[] {cornersList[0]});
		twoCorners = new Shape(new Vect[] {cornersList[0], cornersList[1]});
		threeCorners = new Shape(new Vect[] {cornersList[0], cornersList[1], 
				cornersList[3]});
		fourCorners = new Shape(cornersList);
		delta = Ball.STANDARD_RADIUS / Math.sqrt(2);
	}
	
	@Test
	public void testOneCornerConstruction() {
		Circle[] corners = oneCorner.copyCorners();
		assertEquals(1, corners.length);
		assertEquals(10, corners[0].getCenter().x(), 0.00001);
		assertEquals(5, corners[0].getCenter().y(), 0.00001);
	}
	
	@Test
	public void testTwoCornerConstruction() {
		Circle[] corners = twoCorners.copyCorners();
		assertEquals(2, corners.length);
		assertEquals(10, corners[0].getCenter().x(), 0.00001);
		assertEquals(5, corners[0].getCenter().y(), 0.00001);
		assertEquals(11, corners[1].getCenter().x(), 0.00001);
		assertEquals(5, corners[1].getCenter().y(), 0.00001);
		
		LineSegment[] sides = twoCorners.copySides();
		assertEquals(1, sides.length);
		assertEquals(10, sides[0].p1().x() , 0.00001);
		assertEquals(5, sides[0].p1().y(), 0.00001);		
		assertEquals(11, sides[0].p2().x() , 0.00001);
		assertEquals(5, sides[0].p2().y(), 0.00001);
	}
	
	@Test
	public void testThreeCornerConstruction() {
		Circle[] corners = threeCorners.copyCorners();
		assertEquals(3, corners.length);
		assertEquals(10, corners[0].getCenter().x(), 0.00001);
		assertEquals(5, corners[0].getCenter().y(), 0.00001);
		assertEquals(11, corners[1].getCenter().x(), 0.00001);
		assertEquals(5, corners[1].getCenter().y(), 0.00001);
		assertEquals(10, corners[2].getCenter().x(), 0.00001);
		assertEquals(6, corners[2].getCenter().y(), 0.00001);
		
		LineSegment[] sides = threeCorners.copySides();
		assertEquals(3, sides.length);
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
	public void testFourCornerConstruction() {
		Circle[] corners = fourCorners.copyCorners();
		assertEquals(4, corners.length);
		assertEquals(10, corners[0].getCenter().x(), 0.00001);
		assertEquals(5, corners[0].getCenter().y(), 0.00001);
		assertEquals(11, corners[1].getCenter().x(), 0.00001);
		assertEquals(5, corners[1].getCenter().y(), 0.00001);
		assertEquals(11, corners[2].getCenter().x(), 0.00001);
		assertEquals(6, corners[2].getCenter().y(), 0.00001);
		assertEquals(10, corners[3].getCenter().x(), 0.00001);
		assertEquals(6, corners[3].getCenter().y(), 0.00001);
		
		LineSegment[] sides = fourCorners.copySides();
		assertEquals(4, sides.length);
		assertEquals(10, sides[0].p1().x() , 0.00001);
		assertEquals(5, sides[0].p1().y(), 0.00001);		
		assertEquals(11, sides[0].p2().x() , 0.00001);
		assertEquals(5, sides[0].p2().y(), 0.00001);
		assertEquals(11, sides[1].p1().x() , 0.00001);
		assertEquals(5, sides[1].p1().y(), 0.00001);		
		assertEquals(11, sides[1].p2().x() , 0.00001);
		assertEquals(6, sides[1].p2().y(), 0.00001);
		assertEquals(11, sides[2].p1().x() , 0.00001);
		assertEquals(6, sides[2].p1().y(), 0.00001);		
		assertEquals(10, sides[2].p2().x() , 0.00001);
		assertEquals(6, sides[2].p2().y(), 0.00001);
		assertEquals(10, sides[3].p1().x() , 0.00001);
		assertEquals(6, sides[3].p1().y(), 0.00001);		
		assertEquals(10, sides[3].p2().x() , 0.00001);
		assertEquals(5, sides[3].p2().y(), 0.00001);
	}
	
	@Test
	public void testThreeCorners90DegreeRotation() {
		Shape newShape = threeCorners.rotateBy(Math.PI / 2, 
				threeCorners.copyCorners()[0].getCenter());
		Circle[] corners = newShape.copyCorners();
		assertEquals(10, corners[0].getCenter().x(), 0.00001);
		assertEquals(5, corners[0].getCenter().y(), 0.00001);
		assertEquals(10, corners[1].getCenter().x(), 0.00001);
		assertEquals(6, corners[1].getCenter().y(), 0.00001);
		assertEquals(9, corners[2].getCenter().x(), 0.00001);
		assertEquals(5, corners[2].getCenter().y(), 0.00001);
		
		LineSegment[] sides = newShape.copySides();
		assertEquals(10, sides[0].p1().x(), 0.00001);
		assertEquals(5, sides[0].p1().y(), 0.00001);
		assertEquals(10, sides[0].p2().x(), 0.00001);
		assertEquals(6, sides[0].p2().y(), 0.00001);
		assertEquals(10, sides[1].p1().x(), 0.00001);
		assertEquals(6, sides[1].p1().y(), 0.00001);
		assertEquals(9, sides[1].p2().x(), 0.00001);
		assertEquals(5, sides[1].p2().y(), 0.00001);
		assertEquals(9, sides[2].p1().x(), 0.00001);
		assertEquals(5, sides[2].p1().y(), 0.00001);
		assertEquals(10, sides[2].p2().x(), 0.00001);
		assertEquals(5, sides[2].p2().y(), 0.00001);
	}
	
	@Test
	public void testThreeCorners180DegreeRotation() {
		Shape newShape = threeCorners.rotateBy(Math.PI, 
				threeCorners.copyCorners()[0].getCenter());
		Circle[] corners = newShape.copyCorners();
		assertEquals(10, corners[0].getCenter().x(), 0.00001);
		assertEquals(5, corners[0].getCenter().y(), 0.00001);
		assertEquals(9, corners[1].getCenter().x(), 0.00001);
		assertEquals(5, corners[1].getCenter().y(), 0.00001);
		assertEquals(10, corners[2].getCenter().x(), 0.00001);
		assertEquals(4, corners[2].getCenter().y(), 0.00001);
		
		LineSegment[] sides = newShape.copySides();
		assertEquals(10, sides[0].p1().x(), 0.00001);
		assertEquals(5, sides[0].p1().y(), 0.00001);
		assertEquals(9, sides[0].p2().x(), 0.00001);
		assertEquals(5, sides[0].p2().y(), 0.00001);
		assertEquals(9, sides[1].p1().x(), 0.00001);
		assertEquals(5, sides[1].p1().y(), 0.00001);
		assertEquals(10, sides[1].p2().x(), 0.00001);
		assertEquals(4, sides[1].p2().y(), 0.00001);
		assertEquals(10, sides[2].p1().x(), 0.00001);
		assertEquals(4, sides[2].p1().y(), 0.00001);
		assertEquals(10, sides[2].p2().x(), 0.00001);
		assertEquals(5, sides[2].p2().y(), 0.00001);
	}
	
	@Test
	public void testThreeCorners270DegreeRotation() {
		Shape newShape = threeCorners.rotateBy(3 * Math.PI / 2, 
				threeCorners.copyCorners()[0].getCenter());
		Circle[] corners = newShape.copyCorners();
		assertEquals(10, corners[0].getCenter().x(), 0.00001);
		assertEquals(5, corners[0].getCenter().y(), 0.00001);
		assertEquals(10, corners[1].getCenter().x(), 0.00001);
		assertEquals(4, corners[1].getCenter().y(), 0.00001);
		assertEquals(11, corners[2].getCenter().x(), 0.00001);
		assertEquals(5, corners[2].getCenter().y(), 0.00001);
		
		LineSegment[] sides = newShape.copySides();
		assertEquals(10, sides[0].p1().x(), 0.00001);
		assertEquals(5, sides[0].p1().y(), 0.00001);
		assertEquals(10, sides[0].p2().x(), 0.00001);
		assertEquals(4, sides[0].p2().y(), 0.00001);
		assertEquals(10, sides[1].p1().x(), 0.00001);
		assertEquals(4, sides[1].p1().y(), 0.00001);
		assertEquals(11, sides[1].p2().x(), 0.00001);
		assertEquals(5, sides[1].p2().y(), 0.00001);
		assertEquals(11, sides[2].p1().x(), 0.00001);
		assertEquals(5, sides[2].p1().y(), 0.00001);
		assertEquals(10, sides[2].p2().x(), 0.00001);
		assertEquals(5, sides[2].p2().y(), 0.00001);
	}
	
	@Test
	public void testTimeUntilBallCornerCollision() {
		Ball ball = new Ball("ball", 12 + Ball.STANDARD_RADIUS, 5,
				Ball.STANDARD_RADIUS, -1, 0);
		double time = twoCorners.timeUntilBallCollision(ball.getShape(), 
				ball.getVelocity());
		assertEquals(1, time, 0.00001);
	}
	
	@Test
	public void testTimeUntilBallSideCollision() {
		Ball ball = new Ball("ball", 10.5, 6 + Ball.STANDARD_RADIUS,
				Ball.STANDARD_RADIUS, 0, -1);
		double time = twoCorners.timeUntilBallCollision(ball.getShape(), 
				ball.getVelocity());
		assertEquals(1, time, 0.00001);
	}
	
	@Test
	public void testHeadOnTriangleSideCollision() {
		Ball ball = new Ball("ball", 10.5, 5 - Ball.STANDARD_RADIUS, 
				Ball.STANDARD_RADIUS, 0, 1);
		
		Vect velocity = threeCorners.reflectBall(ball.getShape(), 
				ball.getVelocity());
		assertEquals(0, velocity.x(), 0.00001);
		assertEquals(-1, velocity.y(), 0.00001);
	}
	
	@Test
	public void testHeadOnHypotenuseCollision() {
		Ball ball = new Ball("ball", 10.5 + delta, 5.5 + delta,
				Ball.STANDARD_RADIUS, -1, -1);
		
		Vect velocity = threeCorners.reflectBall(ball.getShape(), 
				ball.getVelocity());
		assertEquals(1, velocity.x(), 0.00001);
		assertEquals(1, velocity.y(), 0.00001);
	}
	
	@Test
	public void testAngledTriangleSideCollision() {
		Ball ball = new Ball("ball", 10 - Ball.STANDARD_RADIUS, 5.5, 
				Ball.STANDARD_RADIUS, 1, 1);
		
		Vect velocity = threeCorners.reflectBall(ball.getShape(), 
				ball.getVelocity());
		assertEquals(-1, velocity.x(), 0.00001);
		assertEquals(1, velocity.y(), 0.00001);
	}
	
	@Test
	public void testHeadOnCornerCollision() {
		Ball ball = new Ball("ball", 10 - delta, 5 - delta,
				Ball.STANDARD_RADIUS, 1, 1);
		
		Vect velocity = threeCorners.reflectBall(ball.getShape(), 
				ball.getVelocity());
		assertEquals(-1, velocity.x(), 0.00001);
		assertEquals(-1, velocity.y(), 0.00001);
	}
	
	@Test
	public void testBallCenterCornerCollision() {
		Ball ball = new Ball("ball", 10 - Ball.STANDARD_RADIUS, 6,
				Ball.STANDARD_RADIUS, 1, 0);
		
		Vect velocity = threeCorners.reflectBall(ball.getShape(), 
				ball.getVelocity());
		assertEquals(-1, velocity.x(), 0.00001);
		assertEquals(0, velocity.y(), 0.00001);
	}
	
	@Test
	public void testBallTopCornerCollision() {
		Ball ball = new Ball("ball", 10 - Ball.STANDARD_RADIUS, 
				6 + Ball.STANDARD_RADIUS, Ball.STANDARD_RADIUS, 1, 0);
		
		Vect velocity = threeCorners.reflectBall(ball.getShape(), 
				ball.getVelocity());
		assertEquals(0, velocity.x(), 0.00001);
		assertEquals(1, velocity.y(), 0.00001);
	}
	
	@Test
	public void testHeadOnRotatingCornerCollision() {
		Ball ball = new Ball("ball", 11, 5 + Ball.STANDARD_RADIUS,
				Ball.STANDARD_RADIUS, 0, -1);
		
		Vect velocity = twoCorners.reflectRotatingAgainstBall(ball.getShape(), 
				ball.getVelocity(), twoCorners.copyCorners()[0].getCenter(), 
				6 * Math.PI, 1);

		assertEquals(0, velocity.x(), 0.00001);
		assertEquals(2 * 6 * Math.PI + 1, velocity.y(), 0.00001);
	}
	
	@Test
	public void testHeadOnRotatingSideCollision() {
		Ball ball = new Ball("ball", 10.5, 5 + Ball.STANDARD_RADIUS,
				Ball.STANDARD_RADIUS, 0, -1);
		
		Vect velocity = twoCorners.reflectRotatingAgainstBall(ball.getShape(), 
				ball.getVelocity(), twoCorners.copyCorners()[0].getCenter(), 
				6 * Math.PI, 1);

		assertEquals(0, velocity.x(), 0.00001);
		assertEquals(2 * 3 * Math.PI + 1, velocity.y(), 0.00001);
	}
	
	@Test @Ignore("We don't support stationary balls vs rotating flippers")
	public void testStationaryBallRotatingCornerCollision() {
		Ball ball = new Ball("ball", 11, 5 + Ball.STANDARD_RADIUS, 
				Ball.STANDARD_RADIUS, 0, 0);
		
		Vect velocity = twoCorners.reflectRotatingAgainstBall(ball.getShape(), 
				ball.getVelocity(), twoCorners.copyCorners()[0].getCenter(), 
				6 * Math.PI, 1);
		assertEquals(0, velocity.x(), 0.00001);
		assertEquals(-6 * Math.PI, velocity.y(), 0.00001);
	}
	

}
