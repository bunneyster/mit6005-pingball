package pb.gizmos;

import pb.board.BoardConstants;
import pb.board.MobileGizmo;
import physics.Circle;
import physics.Geometry;
import physics.Geometry.VectPair;
import physics.Vect;

public class Ball extends MobileGizmo {
	/** The ball's position and radius. */
	private Circle shape;
	/** The ball's velocity. */
	private Vect velocity;
	
	/** The ball radius mandated in the project specification. */
	public static final double STANDARD_RADIUS = 0.25;
	
    // Rep invariant:
    //   shape and velocity should be non-null
	//   if the ball belongs to a board, its center should be inside the board
	//
    // AF:
    //   a ball centered at shape.getCenter() with radius shape.getRadius(),
	//   moving at "velocity" L/second
    // 
	
	/**
	 * Creates a ball with the given parameters.
	 * 
	 * @param name the ball's name
	 * @param x the X coordinate of the ball's center
	 * @param y the Y coordinate of the ball's center
	 * @param r the radius of the ball
	 * @param vx the X component of the ball's velocity
	 * @param vy the Y component of the ball's velocity
	 */
	public Ball(String name, double x, double y, double r, double vx,
			double vy) {
		super(name);
		
		this.shape = new Circle(new Vect(x, y), r);
		this.velocity = new Vect(vx, vy);
		assert checkRep();
	}

	/**
	 * Returns the center of this ball.
	 * 
	 * @return the center of this ball
	 */
	public Vect getCenter() {
		return shape.getCenter();
	}
	
	/**
	 * Sets this ball's center.
	 *  
	 * @param newCenter this ball's new center
	 */
	public void setCenter(Vect newCenter) {
		assert newCenter != null;
		this.shape = new Circle(newCenter, this.shape.getRadius());
		assert checkRep();		
	}
	
	/**
	 * Gets this ball's velocity.
	 * 
	 * @return this ball's velocity
	 */
	public Vect getVelocity() {
	    return new Vect(velocity.x(), velocity.y());
	}
	
	/**
	 * Sets the velocity of this ball.
	 * 
	 * @param newVelocity
	 */
	public void setVelocity(Vect newVelocity) {
		assert newVelocity != null;
		
	    this.velocity = newVelocity;
	    assert checkRep();
	}
	
	/**
	 * Returns this ball's position and size.
	 * 
	 * @return a circle representing the ball's position and size
	 */
	public Circle getShape() {
	    return shape;
	}
	
	@Override
	public double timeToCollision(MobileGizmo other) {
		if (other instanceof Ball) {
			Ball otherBall = (Ball)other;
			return Geometry.timeUntilBallBallCollision(this.shape,
					this.velocity, otherBall.shape, otherBall.velocity);
		}
		
		throw new UnsupportedOperationException("Unsupported MobileGizmo");
	}
	
	@Override
	public void collide(MobileGizmo other) {
		if (other instanceof Ball) {
			Ball otherBall = (Ball)other;
			VectPair newVelocities = Geometry.reflectBalls(
					shape.getCenter(), shape.getRadius(), velocity,
					otherBall.shape.getCenter(), otherBall.shape.getRadius(),
					otherBall.velocity);
			this.velocity = newVelocities.v1;
			otherBall.velocity = newVelocities.v2;
		
			assert checkRep();
			return;
		}
        throw new UnsupportedOperationException("Unsupported collision");
	}
	
	@Override
	public void advanceTime(double timeStep) {
		Vect newCenter = shape.getCenter().plus(velocity.times(timeStep));
		this.shape = new Circle(newCenter, shape.getRadius());
		
		BoardConstants constants = board().getConstants();
		double frictionMultiplier = 1 -
				constants.friction1() * timeStep -
				constants.friction2() * timeStep * velocity.length();
		if (frictionMultiplier < 0)
			frictionMultiplier = 0;
		double gravityEffect = timeStep * constants.gravity();
		this.velocity = new Vect(velocity.x() * frictionMultiplier,
				velocity.y() * frictionMultiplier + gravityEffect);
		
		assert checkRep();
	}
	
	@Override
	public void doAction() {
		// Balls aren't gadgets, so actions don't apply to them.
	}	

	/**
	 * Checks the instance's representation invariant.
	 * 
	 * @return false if the representation invariant has been violated
	 */
	@Override
	protected boolean checkRep() {
		if (super.checkRep() == false)
			return false;
		if (shape == null)
			return false;
		if (shape.getRadius() <= 0)
			return false;
		if (board() != null) {
			if (!board().hasInside(shape.getCenter()))
				return false;
		} else {
			if (shape.getCenter().x() < 0)
				return false;
			if (shape.getCenter().y() < 0)
				return false;			
		}
		if (velocity == null)
			return false;
		return true;
	}	
}