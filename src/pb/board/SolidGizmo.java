package pb.board;

/**
 * Superclass for board elements that participate in collisions. 
 *
 * Elements that change their positions should inherit from {@link MobileGizmo}.
 * {@link SolidGizmo} subclasses that do not inherit from {@link MobileGizmo}
 * are assumed to never collide with each other.
 * 
 * Many elements that participate in collisions can represent their bodies using
 * a {@link Shape} instance. This is usually more convenient than interacting
 * with {@link physics.Geometry} directly.
 * 
 * The physics simulation implemented by {@link Board} works with roughly fixed
 * discrete time steps, for consistency. Multiple discrete time steps are
 * usually taken in a {@link Board#simulate(double)} call. Each step consists of
 * a collision resolution phase, and a collision-free simulation phase.
 * Collision resolution consists of collision detection and calling 
 * {@link SolidGizmo#collide(MobileGizmo)} to simulate collisions.
 * Collision-free simulation consists of time-of-impact (TOI) computation, and
 * calling {@link Gizmo#advanceTime(double)} to simulate a time step that is
 * the minimum of the TOI result and the fixed time step size.
 * 
 * Collision detection has very simplistic broadphase and a relatively intense
 * narrowphase. The broadphase consists of tracking mobile elements (subclasses
 * of {@link MobileGizmo}) and immobile elements ({@link SolidGizmo} instances
 * that do not subclass {@link MobileGizmo}), and assuming that immobile
 * elements will never collide with each other. The narrowphase computes the
 * TOI between all pairs of potentially colliding elements (immobile vs mobile,
 * immobile vs imobile) by calling
 * {@link SolidGizmo#timeToCollision(MobileGizmo)}. If the TOI is smaller than
 * {@link Board#COLLISION_EPSILON}, the elements are assumed to be colliding.
 * 
 * Instances of this class are not thread-safe. Each instance must be contained
 * to the same thread as its owning board.
 */
public abstract class SolidGizmo extends Gizmo {
	/**
	 * Common setup for solid board elements.
	 * 
	 * @param name the element's name
	 */
	public SolidGizmo(String name) {
		super(name);
		
		// NOTE: can't call checkRep() here, because it is supposed to be
		//       overridden
	}

	/**
	 * Computes the time until this element will collide with another element.
	 *
	 * Implementations of this method must not change the element's visible
	 * state, and must not mutate the board via methods such as
	 * {@link Board#add(Gizmo)}.
	 * 
	 * @param other a board element that might collide with this element 
	 * @return the time until this element will collide with the given element;
	 *   {@link Double#MAX_VALUE} should be returned if the elements will never
	 *   collide 
	 */
	public abstract double timeToCollision(MobileGizmo other);
	
	/**
	 * Computes the effects of colliding with another element.
	 * 
	 * Implementations of this method should assume that the element is
	 * colliding with the given element. Implementations must fully resolve
	 * the collision between the two elements. If necessary, implementations can
	 * call {@link #collide(MobileGizmo)} on the other element involved in the
	 * collision. 
	 * 
	 * @param other the element that is colliding with this element
	 */
	public abstract void collide(MobileGizmo other);	
}
