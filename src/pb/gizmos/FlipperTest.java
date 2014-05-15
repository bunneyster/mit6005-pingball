package pb.gizmos;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pb.board.Board;
import pb.board.BoardConstants;
import pb.gizmos.Flipper.Type;
import pb.testing.Triggerable;
import physics.Circle;

public class FlipperTest {
	private Board board;
	private Triggerable triggerable;
	private double r;
	
	@Before
	public void setUp() throws Exception {
		board = new Board(BoardConstants.testConstants());
		r = Flipper.DEFAULT_RADIUS;
		triggerable = new Triggerable();
		// NOTE: Triggerables must not be added to the board, because the
		//		 collision code will not recognize them
	}

	@Test
	public void testLeftCommon() {
		Flipper flipper = new Flipper("flipper", Type.LEFT, 10, 5, 0);
		assertEquals(10, flipper.getOrigin().x(), 0.00001);
		assertEquals(5, flipper.getOrigin().y(), 0.00001);
		assertEquals(Flipper.DEFAULT_LENGTH - 2 * r, flipper.getLength(),
				0.00001);
		assertEquals(Flipper.DEFAULT_REFLECTION_COEFFICIENT,
				flipper.getReflectionCoefficient(), 0.00001);		
		assertEquals(0, flipper.getAngularVelocity(), 0.00001);
		assertEquals(0, flipper.getAngleDelta(), 0.00001);
		assertEquals(-Math.PI / 2, flipper.getMinAngleDelta(), 0.00001);
		assertEquals(0, flipper.getMaxAngleDelta(), 0.00001);
	
		Circle[] corners = flipper.getShape().copyCorners();
		assertEquals(r, corners[0].getRadius(), 0.00001);
		assertEquals(r, corners[1].getRadius(), 0.00001);
	}
	
	@Test
	public void testRightCommon() {
		Flipper flipper = new Flipper("flipper", Type.RIGHT, 10, 5, 0);
		assertEquals(10, flipper.getOrigin().x(), 0.00001);
		assertEquals(5, flipper.getOrigin().y(), 0.00001);
		assertEquals(Flipper.DEFAULT_LENGTH - 2 * r, flipper.getLength(),
				0.00001);
		assertEquals(Flipper.DEFAULT_REFLECTION_COEFFICIENT,
				flipper.getReflectionCoefficient(), 0.00001);		
		assertEquals(0, flipper.getAngularVelocity(), 0.00001);
		assertEquals(0, flipper.getAngleDelta(), 0.00001);
		assertEquals(0, flipper.getMinAngleDelta(), 0.00001);
		assertEquals(Math.PI / 2, flipper.getMaxAngleDelta(), 0.00001);
		
		Circle[] corners = flipper.getShape().copyCorners();
		assertEquals(r, corners[0].getRadius(), 0.00001);
		assertEquals(r, corners[1].getRadius(), 0.00001);
	}	
	
	@Test
	public void testLeft0() {
		Flipper flipper = new Flipper("flipper", Type.LEFT, 10, 5, 0);
		assertEquals(0, flipper.getOrientation());
		assertEquals(10 + r, flipper.getPivot().x(), 0.00001);
		assertEquals(5 + r, flipper.getPivot().y(), 0.00001);
		assertEquals(Math.PI / 2, flipper.getDefaultAngle(), 0.00001);
		
		Circle[] corners = flipper.getShape().copyCorners();
		assertEquals(10 + r, corners[0].getCenter().x(), 0.00001);
		assertEquals(5 + r, corners[0].getCenter().y(), 0.00001);
		assertEquals(10 + r, corners[1].getCenter().x(), 0.00001);
		assertEquals(7 - r, corners[1].getCenter().y(), 0.00001);
	}

	@Test
	public void testLeft90() {
		Flipper flipper = new Flipper("flipper", Type.LEFT, 10, 5, 90);
		assertEquals(90, flipper.getOrientation());
		assertEquals(12 - r, flipper.getPivot().x(), 0.00001);
		assertEquals(5 + r, flipper.getPivot().y(), 0.00001);
		assertEquals(Math.PI, flipper.getDefaultAngle(), 0.00001);
		
		Circle[] corners = flipper.getShape().copyCorners();
		assertEquals(12 - r, corners[0].getCenter().x(), 0.00001);
		assertEquals(5 + r, corners[0].getCenter().y(), 0.00001);
		assertEquals(10 + r, corners[1].getCenter().x(), 0.00001);
		assertEquals(5 + r, corners[1].getCenter().y(), 0.00001);
	}

	@Test
	public void testLeft180() {
		Flipper flipper = new Flipper("flipper", Type.LEFT, 10, 5, 180);
		assertEquals(180, flipper.getOrientation());
		assertEquals(12 - r, flipper.getPivot().x(), 0.00001);
		assertEquals(7 - r, flipper.getPivot().y(), 0.00001);
		assertEquals(3 * Math.PI / 2, flipper.getDefaultAngle(), 0.00001);
		
		Circle[] corners = flipper.getShape().copyCorners();
		assertEquals(12 - r, corners[0].getCenter().x(), 0.00001);
		assertEquals(7 - r, corners[0].getCenter().y(), 0.00001);
		assertEquals(12 - r, corners[1].getCenter().x(), 0.00001);
		assertEquals(5 + r, corners[1].getCenter().y(), 0.00001);
	}

	@Test
	public void testLeft270() {
		Flipper flipper = new Flipper("flipper", Type.LEFT, 10, 5, 270);
		assertEquals(270, flipper.getOrientation());
		assertEquals(10 + r, flipper.getPivot().x(), 0.00001);
		assertEquals(7 - r, flipper.getPivot().y(), 0.00001);
		assertEquals(0, flipper.getDefaultAngle(), 0.00001);
		
		Circle[] corners = flipper.getShape().copyCorners();
		assertEquals(10 + r, corners[0].getCenter().x(), 0.00001);
		assertEquals(7 - r, corners[0].getCenter().y(), 0.00001);
		assertEquals(12 - r, corners[1].getCenter().x(), 0.00001);
		assertEquals(7 - r, corners[1].getCenter().y(), 0.00001);
	}

	@Test
	public void testRight0() {
		Flipper flipper = new Flipper("flipper", Type.RIGHT, 10, 5, 0);
		assertEquals(0, flipper.getOrientation());
		assertEquals(12 - r, flipper.getPivot().x(), 0.00001);
		assertEquals(5 + r, flipper.getPivot().y(), 0.00001);
		assertEquals(Math.PI / 2, flipper.getDefaultAngle(), 0.00001);
		
		Circle[] corners = flipper.getShape().copyCorners();
		assertEquals(12 - r, corners[0].getCenter().x(), 0.00001);
		assertEquals(5 + r, corners[0].getCenter().y(), 0.00001);
		assertEquals(12 - r, corners[1].getCenter().x(), 0.00001);
		assertEquals(7 - r, corners[1].getCenter().y(), 0.00001);
	}

	@Test
	public void testRight90() {
		Flipper flipper = new Flipper("flipper", Type.RIGHT, 10, 5, 90);
		assertEquals(90, flipper.getOrientation());
		assertEquals(12 - r, flipper.getPivot().x(), 0.00001);
		assertEquals(7 - r, flipper.getPivot().y(), 0.00001);
		assertEquals(Math.PI, flipper.getDefaultAngle(), 0.00001);
		
		Circle[] corners = flipper.getShape().copyCorners();
		assertEquals(12 - r, corners[0].getCenter().x(), 0.00001);
		assertEquals(7 - r, corners[0].getCenter().y(), 0.00001);
		assertEquals(10 + r, corners[1].getCenter().x(), 0.00001);
		assertEquals(7 - r, corners[1].getCenter().y(), 0.00001);
	}

	@Test
	public void testRight180() {
		Flipper flipper = new Flipper("flipper", Type.RIGHT, 10, 5, 180);
		assertEquals(180, flipper.getOrientation());
		assertEquals(10 + r, flipper.getPivot().x(), 0.00001);
		assertEquals(7 - r, flipper.getPivot().y(), 0.00001);
		assertEquals(3 * Math.PI / 2, flipper.getDefaultAngle(), 0.00001);
		
		Circle[] corners = flipper.getShape().copyCorners();
		assertEquals(10 + r, corners[0].getCenter().x(), 0.00001);
		assertEquals(7 - r, corners[0].getCenter().y(), 0.00001);
		assertEquals(10 + r, corners[1].getCenter().x(), 0.00001);
		assertEquals(5 + r, corners[1].getCenter().y(), 0.00001);
	}

	@Test
	public void testRight270() {
		Flipper flipper = new Flipper("flipper", Type.RIGHT, 10, 5, 270);
		assertEquals(270, flipper.getOrientation());
		assertEquals(10 + r, flipper.getPivot().x(), 0.00001);
		assertEquals(5 + r, flipper.getPivot().y(), 0.00001);
		assertEquals(0, flipper.getDefaultAngle(), 0.00001);
		
		Circle[] corners = flipper.getShape().copyCorners();
		assertEquals(10 + r, corners[0].getCenter().x(), 0.00001);
		assertEquals(5 + r, corners[0].getCenter().y(), 0.00001);
		assertEquals(12 - r, corners[1].getCenter().x(), 0.00001);
		assertEquals(5 + r, corners[1].getCenter().y(), 0.00001);
	}

	@Test
	public void testLeftTrigger() {
		Flipper flipper = new Flipper("flipper", Type.LEFT, 10, 5, 0);
		triggerable.addListener(flipper);
		board.add(flipper);
		assertEquals(Math.PI / 2, flipper.getDefaultAngle(), 0.00001);
		assertEquals(0, flipper.getAngularVelocity(), 0.00001);
		assertEquals(0, flipper.getAngleDelta(), 0.00001);
		
		triggerable.trigger();
		assertEquals(-Flipper.ANGULAR_VELOCITY, flipper.getAngularVelocity(),
				0.00001);
		assertEquals(0, flipper.getAngleDelta(), 0.00001);
		board.simulate(1.0);
		
		assertEquals(0, flipper.getAngularVelocity(), 0.00001);
		assertEquals(-Math.PI / 2, flipper.getAngleDelta(), 0.00001);
		
		Circle[] corners = flipper.getShape().copyCorners();
		assertEquals(10 + r, corners[0].getCenter().x(), 0.00001);
		assertEquals(5 + r, corners[0].getCenter().y(), 0.00001);
		assertEquals(12 - r, corners[1].getCenter().x(), 0.00001);
		assertEquals(5 + r, corners[1].getCenter().y(), 0.00001);

		triggerable.trigger();
		assertEquals(Flipper.ANGULAR_VELOCITY, flipper.getAngularVelocity(),
				0.00001);
		assertEquals(-Math.PI / 2, flipper.getAngleDelta(), 0.00001);
		board.simulate(1.0);
		
		assertEquals(0, flipper.getAngularVelocity(), 0.00001);
		assertEquals(0, flipper.getAngleDelta(), 0.00001);
		
		corners = flipper.getShape().copyCorners();
		assertEquals(10 + r, corners[0].getCenter().x(), 0.00001);
		assertEquals(5 + r, corners[0].getCenter().y(), 0.00001);
		assertEquals(10 + r, corners[1].getCenter().x(), 0.00001);
		assertEquals(7 - r, corners[1].getCenter().y(), 0.00001);
	}
	
	@Test
	public void testRightTrigger() {
		Flipper flipper = new Flipper("flipper", Type.RIGHT, 10, 5, 0);
		triggerable.addListener(flipper);
		board.add(flipper);
		assertEquals(Math.PI / 2, flipper.getDefaultAngle(), 0.00001);
		assertEquals(0, flipper.getAngularVelocity(), 0.00001);
		assertEquals(0, flipper.getAngleDelta(), 0.00001);
		
		triggerable.trigger();
		assertEquals(Flipper.ANGULAR_VELOCITY, flipper.getAngularVelocity(),
				0.00001);
		assertEquals(0, flipper.getAngleDelta(), 0.00001);
		board.simulate(1.0);
		
		assertEquals(0, flipper.getAngularVelocity(), 0.00001);
		assertEquals(Math.PI / 2, flipper.getAngleDelta(), 0.00001);
		
		Circle[] corners = flipper.getShape().copyCorners();
		assertEquals(12 - r, corners[0].getCenter().x(), 0.00001);
		assertEquals(5 + r, corners[0].getCenter().y(), 0.00001);
		assertEquals(10 + r, corners[1].getCenter().x(), 0.00001);
		assertEquals(5 + r, corners[1].getCenter().y(), 0.00001);

		triggerable.trigger();
		assertEquals(-Flipper.ANGULAR_VELOCITY, flipper.getAngularVelocity(),
				0.00001);
		assertEquals(Math.PI / 2, flipper.getAngleDelta(), 0.00001);
		board.simulate(1.0);
		
		assertEquals(0, flipper.getAngularVelocity(), 0.00001);
		assertEquals(0, flipper.getAngleDelta(), 0.00001);
		
		corners = flipper.getShape().copyCorners();
		assertEquals(12 - r, corners[0].getCenter().x(), 0.00001);
		assertEquals(5 + r, corners[0].getCenter().y(), 0.00001);
		assertEquals(12 - r, corners[1].getCenter().x(), 0.00001);
		assertEquals(7 - r, corners[1].getCenter().y(), 0.00001);
	}
	
	@Test
	public void testTriggerIgnoredWhileRotating() {
		Flipper flipper = new Flipper("flipper", Type.LEFT, 10, 5, 0);
		triggerable.addListener(flipper);
		board.add(flipper);
		assertEquals(Math.PI / 2, flipper.getDefaultAngle(), 0.00001);
		assertEquals(0, flipper.getAngularVelocity(), 0.00001);
		assertEquals(0, flipper.getAngleDelta(), 0.00001);
		
		triggerable.trigger();
		assertEquals(-Flipper.ANGULAR_VELOCITY, flipper.getAngularVelocity(),
				0.00001);
		assertEquals(0, flipper.getAngleDelta(), 0.00001);
		double t = (Math.PI / 4) / Flipper.ANGULAR_VELOCITY;
		board.simulate(t);
		
		assertEquals(-Flipper.ANGULAR_VELOCITY, flipper.getAngularVelocity(),
				0.00001);
		assertEquals(-Math.PI / 4, flipper.getAngleDelta(), 0.1);
		
		triggerable.trigger();
		assertEquals(-Flipper.ANGULAR_VELOCITY, flipper.getAngularVelocity(),
				0.00001);
		assertEquals(-Math.PI / 4, flipper.getAngleDelta(), 0.1);
	}	
}
