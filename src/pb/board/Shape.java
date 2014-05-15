package pb.board;

import physics.Angle;
import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

/**
 * Utility class for managing the geometry of most gizmos.
 * 
 * Shapes are immutable.
 */
public class Shape {
	/**
	 * The shape's sides, as an array of clockwise line segments.
	 * 
	 * The array's contents will never change. 
	 */
	private final LineSegment[] sides;
	/**
	 * The shape's corners, as a list of clockwise circles.
	 * 
	 * The array's contents will never change. 
	 */
	private final Circle[] corners;
	
	/**
	 * Creates a new shape.
	 * 
	 * @param corners the shape's corners, as a list of clockwise circles
	 */
	public Shape(Circle[] corners) {
		assert corners != null;
		assert corners.length > 0;
		
		this.corners = new Circle[corners.length];
		System.arraycopy(corners, 0, this.corners, 0, corners.length);
		if (corners.length == 1) {
			// Point / circle.
			this.sides = new LineSegment[0];
		} 
		else if (corners.length == 2) {
			// Line.
			this.sides = new LineSegment[] {
				new LineSegment(corners[0].getCenter(), corners[1].getCenter())
			};
		} else {
			// Polygon.
			this.sides = new LineSegment[corners.length];
			for (int i = 0; i < corners.length; ++i) {
				int nextIndex = (i + 1 == corners.length) ? 0 : i + 1;
				sides[i] = new LineSegment(corners[i].getCenter(),
						corners[nextIndex].getCenter());
			}
		}
	}
	
	/**
	 * Creates a new shape.
	 * 
	 * The shape's corners will be 0-radius circles.
	 * 
	 * @param corners the shape's corners, as a list of clockwise points
	 */
	public Shape(Vect[] corners) {
		this(Shape.cornersFor(corners));
	}
	
	/**
	 * Creates a shape wrapping a corner and a sides array.
	 * 
	 * @param corners the array of corners to be wrapped
	 * @param sides the array of sides to be wrapped
	 */	
	public Shape(Circle[] corners, LineSegment[] sides) {
		this(corners, sides, true);
	}
	
	/**
	 * Creates a shape wrapping a corner and a sides array.
	 * 
	 * @param corners the array of corners to be wrapped
	 * @param sides the array of sides to be wrapped
	 * @param copyArrays if true, the constructor does not copy the arguments
	 *   into new arrays; the caller must not use the arrays that it passes in
	 *   for any other purpose
	 */
	private Shape(Circle[] corners, LineSegment[] sides, boolean copyArrays) {
		assert corners != null;
		assert sides != null;
		
		if (copyArrays) {
			this.corners = new Circle[corners.length];
			System.arraycopy(corners, 0, this.corners, 0, corners.length);
			this.sides = new LineSegment[sides.length];
			System.arraycopy(sides, 0, this.sides, 0, sides.length);
		} else {
			this.corners = corners;
			this.sides = sides;
		}
	}
	
	/**
	 * A rotated version of the shape. The order of the corners and sides
	 * corresponds to the order for the original shape, rather than clockwise.
	 * 
	 * @param angle the angle of rotation in radians.
	 * @param origin the origin of the original shape.
	 * @return
	 */
	public Shape rotateBy(double angle, Vect origin) {
		Angle angle_ = new Angle(angle);
		Circle[] newCorners = new Circle[corners.length];
		for (int i = 0; i < corners.length; ++i) {
			Circle corner = corners[i];
			Vect center = corner.getCenter().minus(origin).rotateBy(angle_).
					plus(origin);
			newCorners[i] = new Circle(center, corner.getRadius()); 
		}
		LineSegment[] newSides = new LineSegment[sides.length];
		for (int i = 0; i < sides.length; ++i) {
			LineSegment side = sides[i];
			Vect p1 = side.p1().minus(origin).rotateBy(angle_).plus(origin);
			Vect p2 = side.p2().minus(origin).rotateBy(angle_).plus(origin);
			newSides[i] = new LineSegment(p1, p2);
		}
		return new Shape(newCorners, newSides, false);
	}
	
	/**
	 * The time until a ball collides with this shape.
	 * 
	 * @param ballShape the ball's center and radius
	 * @param ballVelocity the ball's velocity
	 * @return the time until the ball collides with this shape
	 */
	public double timeUntilBallCollision(Circle ballShape, Vect ballVelocity) {
		double minTime = Double.MAX_VALUE;
		for (LineSegment side : sides) {
			double time = Geometry.timeUntilWallCollision(side, ballShape,
					ballVelocity);
			if (time < minTime)
				minTime = time;
		}
        for (Circle corner : corners) {
			double time = Geometry.timeUntilCircleCollision(corner, ballShape,
					ballVelocity);
            if (time < minTime)
                minTime = time;
        }			
		return minTime;
	}
	
	/**
	 * Computes the new velocity of a ball reflecting off of this shape.
	 * 
	 * @param ballShape the ball's center and radius
	 * @param ballVelocity the ball's velocity
	 * @return the velocity of a ball after colliding with this shape, assuming
	 *   the ball is at the point of impact
	 */
	public Vect reflectBall(Circle ballShape, Vect ballVelocity) {
	    double minTime = Double.MAX_VALUE;
        Circle collisionCorner = null;
        for (Circle corner : corners) {
			double time = Geometry.timeUntilCircleCollision(corner, ballShape,
					ballVelocity);
            if (time < minTime) {
                collisionCorner = corner;
                minTime = time;
            }
        }
	    LineSegment collisionSide = null;
        for (LineSegment side : sides) {
			double time = Geometry.timeUntilWallCollision(side, ballShape,
					ballVelocity);
            if (time < minTime) {
            	collisionCorner = null;
                collisionSide = side;
                minTime = time;
            }
        }
        if (collisionCorner != null) {
            return Geometry.reflectCircle(collisionCorner.getCenter(),
            		ballShape.getCenter(), ballVelocity);
        }        
        if (collisionSide != null)
            return Geometry.reflectWall(collisionSide, ballVelocity);
        
        assert false;  // The ball will never collide with this shape.
        return ballVelocity;
	}
	
	/**
	 * Computes the new velocity of a ball reflecting off of a rotating shape.
	 * 
	 * @param ballShape the ball's center and radius
	 * @param ballVelocity the ball's velocity
	 * @param origin the point that this shape is rotating around
	 * @param angularVelocity this shape's angular velocity
	 * @param reflectionCoefficient this shape's reflection coefficient 
	 * @return the velocity of a ball after colliding with this shape, assuming
	 *   the ball is at the point of impact
	 */
	public Vect reflectRotatingAgainstBall(Circle ballShape,
			Vect ballVelocity, Vect origin, double angularVelocity,
			double reflectionCoefficient) {
	    double minTime = Double.MAX_VALUE;
        Circle collisionCorner = null;
        for (Circle corner : corners) {
			double time = Geometry.timeUntilCircleCollision(corner, ballShape,
					ballVelocity);
            if (time < minTime) {
                collisionCorner = corner;
                minTime = time;
            }
        }
	    LineSegment collisionSide = null;
        for (LineSegment side : sides) {
			double time = Geometry.timeUntilWallCollision(side, ballShape,
					ballVelocity);
            if (time < minTime) {
            	collisionCorner = null;
                collisionSide = side;
                minTime = time;
            }
        }
        if (collisionCorner != null) {
            return Geometry.reflectRotatingCircle(collisionCorner,
            		origin,  angularVelocity, ballShape, ballVelocity,
            		reflectionCoefficient);
        }        
        if (collisionSide != null) {
            return Geometry.reflectRotatingWall(collisionSide, origin,
            		angularVelocity, ballShape, ballVelocity,
            		reflectionCoefficient);
        }
        
        assert false;  // The ball will never collide with this shape.
        return ballVelocity;		
	}

	/**
	 * Copies the shape's corners into a new array.
	 * 
	 * This method is inefficient and is only intended for testing.
	 *  
	 * @return an array listing the shape's corners in clockwise order 
	 */
	public Circle[] copyCorners() {
		Circle[] snapshot = new Circle[corners.length];
		System.arraycopy(corners, 0, snapshot, 0, corners.length);
		return snapshot;
	}
	
	/**
	 * Copies the shape's edges into a new array.
	 * 
	 * @return an array listing the shape's edges in clockwise order 
	 */
	public LineSegment[] copySides() {
		LineSegment[] snapshot = new LineSegment[sides.length];
		System.arraycopy(sides, 0, snapshot, 0, sides.length);
		return snapshot;
	}
	
	/**
	 * Checks if a shape is entirely contained by a board.
	 * @param board the board that should contain the shape
	 * @return false if any part of the shape falls off the board
	 */
	public boolean isInsideBoard(Board board) {
		for (Circle corner : corners) {
			if (!board.hasInside(corner))
				return false;
		}
		for (LineSegment side : sides) {
			if (!board.hasInside(side.p1()) || !board.hasInside(side.p2()))
				return false;
		}
		return true;
	}
	
	/**
	 * Converts vectors to 0-radius circles.
	 * 
	 * @param corners a shape's corners
	 * @return a list of 0-radius circles centered at the given corners
	 */
	private static Circle[] cornersFor(Vect[] corners) {
		assert corners != null;
		assert corners.length > 0;
				
		Circle[] circles = new Circle[corners.length];
		for (int i = 0; i < corners.length; ++i)
			circles[i] = new Circle(corners[i], 0);
		return circles;
	}
}
