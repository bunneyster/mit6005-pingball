package pb.gizmos;

import pb.board.Edge;
import pb.board.MobileGizmo;
import pb.board.Shape;
import pb.board.SolidGizmo;
import physics.LineSegment;
import physics.Vect;

/**
 * A wall, according to the Pingball specification.
 * 
 * A board has exactly 4 walls. There is a wall for each board {@link Edge}.
 */
public class Wall extends SolidGizmo {
	/** The wall's shape. */
	private final Shape shape;	
	/** False for vertical walls. */
	private final boolean isHorizontal;
	/** The board's edge that the wall is next to. */
	private final Edge position;
	/**
	 * The name of the board that this wall teleports balls to.
	 * 
	 * This is null if the wall does is not connected to another board.
	 */
	private String neighborName;
	
	/**
	 * Creates a wall between the given endpoints.
	 * 
	 * @param x1 the X coordinate of the wall's first endpoint
	 * @param y1 the Y coordinate of the wall's first endpoint
	 * @param x2 the X coordinate of the wall's second endpoint; must be greater
	 *   or equal to x1
	 * @param y2 the Y coordinate of the wall's second endpoint; must be greater
	 *   or equal to y1
	 */
	public Wall(double x1, double y1, double x2, double y2) {
		super(Wall.edge(x1, y1, x2, y2).wallName());
		
		Vect[] corners = new Vect[] { new Vect(x1, y1), new Vect(x2, y2) };
		this.shape = new Shape(corners);		
		this.isHorizontal = (y1 == y2);
		if (this.isHorizontal) {
			this.position = (y1 == 0) ? Edge.TOP : Edge.BOTTOM;
		} else {
			assert x1 == x2;  // Vertical wall.
			this.position = (x1 == 0) ? Edge.LEFT : Edge.RIGHT;
		}
		this.neighborName = null;
		
		assert checkRep();
	}
		
	/**
	 * The wall's shape.
	 * 
	 * @return the wall's shape
	 */
	public Shape getShape() {
		return shape;
	}
	/**
	 * Returns true iff this wall is horizontal.
	 * 
	 * @return true iff this wall is horizontal
	 */
	public boolean getIsHorizontal() {
		return isHorizontal;
	}
	
	/**
	 * Returns the board edge next to this wall.
	 * 
	 * @return the board edge next to this wall
	 */
	public Edge getEdge() {
		return position;
	}
	
	/**
	 * The name of the board that this wall teleports balls to.
	 * 
	 * @return the name of the board that this wall teleports balls to; null if
	 *   this wall is not connected to another board
	 */
	public String getNeighborName() {
		return neighborName;
	}
	
	/**
	 * Sets the name of the board that receives balls hitting this wall.
	 * 
	 * @param newName a board name
	 */
	public void setNeighborName(String newName) {
		assert newName != null;
		this.neighborName = newName;
	}
	
	/**
	 * Sets up this wall so it does not send balls hitting it to another board.
	 */
	public void clearNeighborName() {
		this.neighborName = null;
	}
	
	/**
	 * Checks if this wall has a neighboring board that it teleports balls to.
	 * 
	 * @return true if this wall has a neighboring board that it teleports balls
	 *     to
	 */
	public boolean hasNeighbor() {
		return this.neighborName != null;
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
			
			if (neighborName != null) {
				board().remove(ball);
				board().addOutgoingBall(ball, this);
			} else {
				ball.setVelocity(shape.reflectBall(ball.getShape(),
						ball.getVelocity()));				
			}
			
			assert checkRep();
			return;
		}
		throw new IllegalArgumentException("Walls can only collide with balls");
	}
	
	@Override
	public void advanceTime(double timeStep) {
		// Walls don't move.
	}
	
	@Override
	public void doAction() {
		// Walls don't have actions.
	}	

	/**
	 * The edge adjacent to a wall with the given endpoints.
	 * 
	 * @param x1 the X coordinate of the wall's first endpoint
	 * @param y1 the Y coordinate of the wall's first endpoint
	 * @param x2 the X coordinate of the wall's second endpoint; must be greater
	 *   or equal to x1
	 * @param y2 the Y coordinate of the wall's second endpoint; must be greater
	 *   or equal to y1
	 * @return the edge adjacent to a wall with the given endpoints
	 */
	private static Edge edge(double x1, double y1, double x2, double y2) {
		assert x1 >= 0;
		assert y1 >= 0;
		assert x1 <= x2;
		assert y1 <= y2;

		if (y1 == y2) {
			// Horizontal wall.
			return (y1 == 0) ? Edge.TOP : Edge.BOTTOM;
		} else {
			assert x1 == x2;  // Vertical wall.
			return (x1 == 0) ? Edge.LEFT : Edge.RIGHT;
		}		
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
		if (position == null)
			return false;
		if (name() != position.wallName())
			return false;
		if (board() != null) {
			if (!shape.isInsideBoard(board()))
				return false;
		}
		LineSegment[] sides = shape.copySides();
		if (sides.length != 1)
			return false;
		if (sides[0].p1().x() < 0 || sides[0].p1().y() < 0)
			return false;
		if (sides[0].p2().x() < sides[0].p1().x() ||
				sides[0].p2().y() < sides[0].p1().y())
			return false;
		return true;
	}
}