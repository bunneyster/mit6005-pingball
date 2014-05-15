package pb.gizmos;

import java.util.ArrayList;
import java.util.List;

import pb.board.Board;
import pb.board.Gizmo;
import pb.board.MobileGizmo;
import pb.board.SolidGizmo;
import physics.Circle;
import physics.Geometry;
import physics.Vect;

/**
 * Portals.
 * 
 * Instances of this class are not thread-safe and must be contained to the
 * thread that owns the {@link Board}.
 */
public class Portal extends SolidGizmo {
	/** The portal's shape. */
	private final Circle shape;
	/**
	 * The name of the target board.
	 * 
	 * This is null if the target board is the same as this portal's board.
	 */
	private final String otherBoard;
	/** The name of the target portal. */
	private final String otherPortal;
	/** The balls waiting to be launched by this portal. */
	private final List<Ball> queued;
	/**
	 * The ball that is leaving this absorber.
	 * 
	 * This is null if the most recently launched ball has already left the
	 * absorber.
	 */
	private Ball leavingBall;
	
	/** The portal radius mandated in the project specification. */
	public static final double STANDARD_RADIUS = 0.5;
	
	/**
	 * Creates a portal at the given position.
	 * 
	 * @param name the portal's name
	 * @param x the X coordinate of the absorber's center
	 * @param y the Y coordinate of the absorber's center
	 * @param otherBoard the name of the target board; null if the target board
	 *   is the same as the portal's board
	 * @param otherPortal the name of the target portal 
	 */
	public Portal(String name, double x, double y, String otherBoard,
			String otherPortal) {
		super(name);
		assert otherPortal != null;

		this.shape = new Circle(new Vect(x, y), STANDARD_RADIUS);
		this.otherBoard = otherBoard;
		this.otherPortal = otherPortal;
		
		this.queued = new ArrayList<Ball>();
		this.leavingBall = null;
		
		assert checkRep();
	}
	
	/**
	 * The absorber's shape.
	 * 
	 * @return the absorber's shape
	 */
	public Circle getShape() {
		return shape;
	}
	
	/**
	 * The name of the target board.
	 * 
	 * @return the name of this portal's target board; null if the target board
	 *   is the same as this portal's board
	 */
	public String getOtherBoard() {
		return otherBoard;
	}
	
	/**
	 * The name of the target portal.
	 * 
	 * @return the name of this portal's target portal
	 */
	public String getOtherPortal() {
		return otherPortal;
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
	 * Queues a ball to be launched from this portal.
	 * 
	 * @param ball the ball to be queued
	 */
	public void queueBall(Ball ball) {
		assert ball != null;
		assert !queued.contains(ball);
		
		queued.add(ball);
	}
	
	/**
	 * True if the given circle overlaps the portal.
	 * 
	 * @param circle the circle to be tested for overlaps
	 * @return true if the given circle overlaps the absorber, false if the
	 *   shapes are completely disjoint
	 */
	public boolean isInside(Circle circle) {
		Vect center = circle.getCenter();
		double radiusSum = shape.getRadius() + circle.getRadius();
		
		double distanceSquared =
				Geometry.distanceSquared(center, shape.getCenter());
		return distanceSquared <= radiusSum * radiusSum;
	}
	
	/**
	 * True if this portal teleports balls.
	 * 
	 * @return false if this portal does nothing
	 */
	public boolean canTeleport() {
		if (otherBoard != null)
			return board().getHasServer();
		
		Gizmo target = board().findByName(otherPortal);
		return target != null && target instanceof Portal;
	}
		
	@Override
	public double timeToCollision(MobileGizmo other) {
		if (other instanceof Ball) {
			Ball ball = (Ball)other;
			if (!canTeleport() || ball == leavingBall)
				return Double.MAX_VALUE;
			return Geometry.timeUntilCircleCollision(shape, ball.getShape(),
					ball.getVelocity());
		}
		
    	throw new UnsupportedOperationException("Unsupported MobileGizmo");
	}

	@Override
	public void collide(MobileGizmo other) {
		if (other instanceof Ball) {
			Ball ball = (Ball)other;
			
			if (otherBoard == null) {
				Gizmo target = board().findByName(otherPortal);
				if (target != null && target instanceof Portal) {
					Portal targetPortal = (Portal)target;
					board().remove(ball);
					ball.setCenter(targetPortal.shape.getCenter());
					targetPortal.queued.add(ball);
				} else {
					// NOTE: if the target portal disappeared, the ball should be
					//       left on the board; this portal will not interact with
					//       the ball anymore
					assert !canTeleport();
				}
			} else {
				board().remove(ball);
				board().addOutgoingBall(ball, this);
			}

			assert checkRep();
			return;
		}
		
		throw new IllegalArgumentException(
				"Portals can only collide with balls");    	
	}

	@Override
	public void advanceTime(double timeStep) {
		// NOTE: portals don't move, but we take this opportunity to observe
		//		 the ball that is leaving the portal, and add pending balls to
		//       the board when the opportunity arises
		if (leavingBall != null) {
			Circle circle = leavingBall.getShape();
			if (isInside(circle))
				return;
			leavingBall = null;
		}
		
		if (queued.isEmpty())
			return;
		Ball ball = queued.remove(queued.size() - 1);
		board().add(ball);
		leavingBall = ball;
	}
	
	@Override
	public void doAction() {
		// Portals have no actions.
	}	
}
