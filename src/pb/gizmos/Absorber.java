package pb.gizmos;

import java.util.ArrayList;
import java.util.List;

import pb.board.MobileGizmo;
import pb.board.Shape;
import pb.board.SolidGizmo;
import physics.Circle;
import physics.Vect;

public class Absorber extends SolidGizmo {
	/** The absorber's top-left corner. */
	private final Vect origin;
	/** The absorber's width. */
	private final double width;
	/** The absorber's height. */
	private final double height;
	/** The absorber's shape. */
	private final Shape shape;
	/** The balls absorbed by this absorber. */
	private final List<Ball> absorbed;
	/**
	 * The ball that is leaving this absorber.
	 * 
	 * This is null if the most recently launched ball has already left the
	 * absorber.
	 */
	private Ball leavingBall;
	
	/**
	 * Creates an absorber with given position and dimensions.
	 * 
	 * @param name the absorber's name
	 * @param x the X coordinate of the absorber's origin (top-left corner)
	 * @param y the Y coordinate of the absorber's origin (top-left corner)
	 * @param width the absorber's width; must be greater or equal to 1
	 * @param height the absorber's height; must be greater or equal to 1
	 */
	public Absorber(String name, double x, double y, double width,
			double height) {
		super(name);
		assert width >= 1 && height >= 1;

		this.origin = new Vect(x, y);
		this.width = width;
		this.height = height;
		
		Vect[] corners = new Vect[] {
				new Vect(x, y),
				new Vect(x + width, y),
				new Vect(x + width, y + height),
				new Vect(x, y + height)
			};
		this.shape = new Shape(corners);
		this.absorbed = new ArrayList<Ball>();
		this.leavingBall = null;
	}
	
	/**
	 * The position where a ball will be launched.
	 * 
	 * When an absorber is triggered, it launches a ball. This returns the
	 * ball's center when is launched.
	 * 
	 * @param radius the ball's radius
	 * @return the ball's center when it will be launched by the absorber
	 */
	public Vect ballLaunchCenter(double radius) {
		return new Vect(origin.x() + width - radius,
				origin.y() + height - radius);
	}
	
	/**
	 * The absorber's shape.
	 * 
	 * @return the absorber's shape
	 */
	public Shape getShape() {
		return shape;
	}
	/**
	 * The coordinates of the absorber's origin (top-left corner).
	 * 
	 * @return the coordinates of the absorber's origin (top-left corner)
	 */
	public Vect getOrigin() {
		return origin;
	}
	/**
	 * The absorber's width, in L units.
	 * 
	 * @return the absorber's width, in L units
	 */
	public double getWidth() {
		return width;
	}
	/**
	 * The absorber's height.
	 * 
	 * @return the absorber's height, in L units
	 */
	public double getHeight() {
		return height;
	}
	/**
	 * The ball that is leaving this absorber.
	 * 
	 * @return the last ball launched by the absorber, if the ball hasn't left
	 *   the absorber's bounds; null if the most recently launched ball has left
	 *   the absorber
	 */
	public Ball getLeavingBall() {
		return leavingBall;
	}
	
	/**
	 * True if the given circle overlaps the absorber.
	 * 
	 * @param circle the circle to be tested for overlaps
	 * @return true if the given circle overlaps the absorber, false if the
	 *   shapes are completely disjoint
	 */
	public boolean isInside(Circle circle) {
		Vect center = circle.getCenter();
		double x = center.x();
		double y = center.y();
		double r = circle.getRadius();
		
		if (x + r < origin.x())
			return false;
		if (y + r < origin.y())
			return false;
		if (x - r >= origin.x() + width)
			return false;
		if (y - r >= origin.y() + height)
			return false;
		return true;
	}
	
	/** The velocity of a ball launched when an absorber is triggered. */
	public static final Vect LAUNCH_VELOCITY = new Vect(0, -50);
	
    @Override
    public double timeToCollision(MobileGizmo other) {
    	if (other instanceof Ball) {
			Ball ball = (Ball)other;
			if (ball == leavingBall)
				return Double.MAX_VALUE;
			return shape.timeUntilBallCollision(ball.getShape(),
					ball.getVelocity());
    	}
    	
    	throw new UnsupportedOperationException("Unsupported MobileGizmo");
    }
    
	@Override
	public void collide(MobileGizmo other) {
		if (other instanceof Ball) {
			Ball ball = (Ball)other;

			ball.setVelocity(Absorber.LAUNCH_VELOCITY);
			ball.setCenter(ballLaunchCenter(ball.getShape().getRadius()));			
			absorbed.add(ball);
			board().remove(ball);
			trigger();
			
			assert checkRep();
			return;
		}
		
		throw new IllegalArgumentException(
				"Absorbers can only collide with balls");    	
	}

	@Override
	public void advanceTime(double timeStep) {
		// NOTE: absorbers don't move, but we take this opportunity to observe
		//		 the ball that is leaving the absorber
		if (leavingBall == null)
			return;
		
		Circle circle = leavingBall.getShape();
		if (!isInside(circle)) {
			// The ball left the absorber.
			leavingBall = null;
		}
	}
	
	@Override
	public void doAction() {
		if (leavingBall != null)
			return;
		if (absorbed.isEmpty())
			return;
		
		Ball ball = absorbed.remove(absorbed.size() - 1);
		board().add(ball);
		leavingBall = ball;
	}	
}
