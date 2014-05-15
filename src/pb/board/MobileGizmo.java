package pb.board;

/**
 * Superclass for board elements that change their positions on the board.
 * 
 * {@link MobileGizmo} instances can collide with each other and with other
 * {@link SolidGizmo} instances.
 */
public abstract class MobileGizmo extends SolidGizmo {
	/**
	 * Common setup for solid board elements.
	 * 
	 * @param name the element's name
	 */
	public MobileGizmo(String name) {
		super(name);
		
		// NOTE: can't call checkRep() here, because it is supposed to be
		//       overridden
	}
}
