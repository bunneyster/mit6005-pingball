package pb.gizmos;

import pb.board.Board;
import pb.board.MobileGizmo;
import pb.board.Shape;
import pb.board.SolidGizmo;
import physics.Vect;

/** Base class for all bumpers. */
public abstract class BumperBase extends SolidGizmo {
	/** The bumper's shape. */
	private final Shape shape;
	/** The top-left corner of the bumper's bounding box. */
    private final Vect origin;
    /** If true, the bumper disappears when triggered. */
    private final boolean isExploding;

    // Rep invariant:
    //   shape and origin should be non-null
    //   if the bumper belongs to a board, its origin should be inside the board
    //   if the bumper belongs to a board, all its corners should be inside the
    //       board
    // AF:
    //    a bumper with the top-left corner of the bounding box at "origin",
    //        whose shape is "shape"
    
    /**
     * Creates a bumper.
     * 
	 * @param name the bumper's name
     * @param x the X coordinate of the bumper's origin (top-left corner)
     * @param y the Y coordinate of the bumper's origin (top-left corner)
     * @param shape the bumper's shape
     * @param isExploding if true, the bumper disappears when triggered
     */
    protected BumperBase(String name, double x, double y, Shape shape,
    		boolean isExploding) {
    	super(name);
    	assert shape != null;

    	this.shape = shape;
    	this.origin = new Vect(x, y);
    	this.isExploding = isExploding;
    	// NOTE: can't call checkRep() here because it is supposed to be
    	//       overridden
    }
    
    /**
     * Returns the top-left corner of this bumper's bounding box.
     * 
     * @return the origin (top-left corner) of the bumper's bounding box
     */
    public Vect getOrigin() {
    	return origin;
    }
    
	/**
	 * The bumper's shape.
	 * 
	 * @return the bumper's shape
	 */
	public Shape getShape() {
		return shape;
	}    
	
	/**
	 * True if this bumper disappears when triggered.
	 * 
	 * @return true if this bumper disappears when triggered, false if it does
	 *   nothing (the default behavior)
	 */
	public boolean getIsExploding() {
		return isExploding;
	}
        
    /**
     * Computes the bounding box for a bumper.
     * 
     * @param x the X coordinate of the bumper's top-left corner
     * @param y the Y coordinate of the bumper's top-left corner
     * @return the bumper's bounding box, as points listed clockwise starting
     *   from the top-left
     */
    static Vect[] boundingBox(double x, double y) {
    	return new Vect[] {
            new Vect(x, y),
            new Vect(x + 1, y),
            new Vect(x + 1, y + 1),
            new Vect(x, y + 1)
        };
    }    
    
    @Override
    public double timeToCollision(MobileGizmo other) {
    	if (other instanceof Ball) {
			Ball ball = (Ball)other;
			return shape.timeUntilBallCollision(ball.getShape(),
					ball.getVelocity());
    	}
    	
    	throw new UnsupportedOperationException("Unsupported MobileGizmo");
    }
    
    @Override
    public void collide(MobileGizmo other) {
    	if (other instanceof Ball) {
			Ball ball = (Ball)other;
			ball.setVelocity(shape.reflectBall(ball.getShape(),
					ball.getVelocity()));				
    		trigger();
    		
    		assert checkRep();
    		return;
    	}
		throw new IllegalArgumentException(
				"Bumpers can only collide with balls");    	
    }
    
    @Override
    public void advanceTime(double timeStep) {
        // Bumpers don't move.
    }
    
	@Override
	public void doAction() {
		if (isExploding)
			board().remove(this);
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
		if (origin == null)
			return false;
		if (shape == null)
			return false;
		Board board = board();
		if (board != null) {
			if (!board.hasInside(origin))
				return false;
			if (!shape.isInsideBoard(board))
				return false;
		}
		return true;
	}	
}
