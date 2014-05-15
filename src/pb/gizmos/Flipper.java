package pb.gizmos;

import pb.board.Board;
import pb.board.MobileGizmo;
import pb.board.Shape;
import pb.board.SolidGizmo;
import physics.Angle;
import physics.Circle;
import physics.LineSegment;
import physics.Vect;

/**
 * Left and right flippers.
 * 
 * Instances of this class are not thread-safe and must be contained to the
 * thread that owns the {@link Board}.
 */

public class Flipper extends SolidGizmo {
    public enum Type {
    	/** Left flipper. */
    	LEFT {
    		@Override
    		int pivotOrientationIndex() { return 0; }
    		@Override
    		double minAngleDelta() { return -Math.PI / 2; }
    		@Override
    		double maxAngleDelta() { return 0; }    		
    	},
    	/** Right flipper. */
    	RIGHT {
    		@Override
    		int pivotOrientationIndex() { return 1; } 
    		@Override
    		double minAngleDelta() { return 0; }
    		@Override
    		double maxAngleDelta() { return Math.PI / 2; }    		
    	};
    	
    	abstract int pivotOrientationIndex();
    	abstract double minAngleDelta();
    	abstract double maxAngleDelta();
    }
    /** The flipper's type (left or right). */
    private final Type type;
    
    /**
     * The top-left corner of the flipper's bounding box.
     * 
     * This may not be the same as the flipper's pivot.
     */
	private final Vect origin;
	/** The radius of the flipper's circles, and the width of the arm. */
	private final double radius;
	/** The distance between the flipper's pivot and its other end. */
	private final double length;
	/** The flipper's orientation, in degrees. */
    private final int orientation;
    /** The coordinates of the pivot. */
    private final Vect pivot;
    /** The flipper's reflection coefficient. */
    private final double reflectionCoefficient;
    /**
     * The flipper's default angle, in radians.
     * 
     * This is the flipper's angle when the board is initialized.
     */
    private final double defaultAngle;
    /**
     * The flipper's default shape.
     */
    private final Shape defaultShape;
    
    /** Minimum and maximum values for angleDelta. */
    private final double minAngleDelta, maxAngleDelta;
    /** The delta between flipper's current angle and its default angle. */
    private double angleDelta;
    /** The flipper's current angular velocity. */
    private double angularVelocity;
    /** The flipper's current shape. */
    private Shape shape;
    
    public static final double DEFAULT_REFLECTION_COEFFICIENT = 0.95;
    
    public static final double ANGULAR_VELOCITY = 6 * Math.PI;
    
    /**
     * The default flipper radius.
     */
    public static final double DEFAULT_RADIUS = 0.125;
    
    /**
     * The default size of flippers.
     * 
     * This is from the problem set specification.
     */
    public static final double DEFAULT_LENGTH = 2;
    
    /**
     * Creates a flipper with the given parameters.
     * 
     * @param name the flipper's name
     * @param type indicates whether the flipper is a left or right flipper
     * @param x the X coordinate of the top-left corner of the flipper's
     *   bounding box
     * @param y the Y coordinate of the top-left corner of the flipper's
     *   bounding box
     * @param orientation the flipper's orientation (0 / 90 / 180 / 270)
     */
	public Flipper(String name, Type type, double x, double y,
			int orientation) {
		super(name);
		assert type != null;
    	assert 0 <= orientation && orientation < 360;
    	assert orientation % 90 == 0;
    	
    	this.type = type;
    	this.origin = new Vect(x, y);
    	this.orientation = orientation;
    	this.radius = DEFAULT_RADIUS;
    	this.length = DEFAULT_LENGTH - 2 * radius;
    	this.reflectionCoefficient = DEFAULT_REFLECTION_COEFFICIENT;
    	
    	this.pivot = origin.plus(Flipper.pivotOffset(type, orientation,
    			length, radius));
    	this.defaultAngle = Flipper.defaultAngle(orientation);
    	this.defaultShape = Flipper.defaultShape(type, orientation, origin,
    			length, radius); 
    	this.minAngleDelta = type.minAngleDelta();
    	this.maxAngleDelta = type.maxAngleDelta();
    	
    	this.angleDelta = 0;
    	this.angularVelocity = 0;
    	this.shape = defaultShape;
    	
    	assert checkRep();
    }
	
	/**
	 * The flipper's type.
	 * 
	 * @return the flipper's type (left or right)
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * The coordinates of the top-left corner of this flipper's bounding box.
	 * 
	 * @return the coordinates of the top-left corner of this flipper's bounding
	 *   box
	 */
	public Vect getOrigin() {
		return origin;
	}
	
    /**
     * This flipper's orientation, in degrees.
     * 
     * @return this flipper's orientation, in degrees
     */
    public int getOrientation() {
    	return orientation;
    } 
    
    /**
     * The coordinates of this flipper's pivot.
     * 
     * @return the coordinates of this flipper's pivot
     */
    public Vect getPivot() {
    	return pivot;
    }
    
    /**
     * This flipper's reflection coefficient.
     * 
     * @return this flipper's reflection coefficient
     */
    public double getReflectionCoefficient() {
    	return reflectionCoefficient;
    }
    
    public double getLength() {
    	return length;
    }
    
    public double getDefaultAngle() {
    	return defaultAngle;
    }
    public double getMinAngleDelta() {
    	return minAngleDelta;
    }
    public double getMaxAngleDelta() {
    	return maxAngleDelta;
    }
    
    /**
     * This flipper's angular velocity, in radians.
     * 
     * @return this flipper's angular velocity, in radians
     */
    public double getAngularVelocity() {
    	return angularVelocity;
    }
    
    /**
     * This flipper's shape.
     * 
     * @return this flipper's shape
     */
    public Shape getShape() {
    	return shape;
    }
    
    public double getAngleDelta() {
    	return angleDelta;
    }
	
    /**
     * A flipper's default shape.
     * 
	 * @param type the flipper's type (left or right)
	 * @param orientation the flipper's orientation, in degrees
	 * @param origin the top-left corner of the flipper's bounding box
	 * @param length the distance between the flipper's pivot and its other end
	 * @param radius the radius of the flipper's circles, and the width of the
	 *   flipper's arm
     * @return the shape of a flipper with the given orientation, when the board
     *   is initialized
     */
    private static Shape defaultShape(Type type, int orientation, Vect origin,
    		double length, double radius) {
		assert type != null;
		assert 0 <= orientation && orientation < 360;
		assert orientation % 90 == 0;

		double angle = defaultAngle(orientation);
		Vect arm = (new Vect(length, 0)).rotateBy(new Angle(angle));

		Vect pivotCenter = origin.plus(pivotOffset(type, orientation, length,
				radius));
		Vect endCenter = pivotCenter.plus(arm);		
		Circle[] corners = new Circle[] {
			new Circle(pivotCenter, radius),
			new Circle(endCenter, radius)
		};
		
		Vect radius1 = (new Vect(radius, 0)).rotateBy(
				new Angle(angle - Math.PI / 2));
		Vect radius2 = (new Vect(radius, 0)).rotateBy(
				new Angle(angle + Math.PI / 2));
		LineSegment[] sides = new LineSegment[] {
			new LineSegment(pivotCenter.plus(radius1), endCenter.plus(radius1)),
			new LineSegment(endCenter.plus(radius1), endCenter.plus(radius2)),
			new LineSegment(endCenter.plus(radius2), pivotCenter.plus(radius2)),
			new LineSegment(pivotCenter.plus(radius2),
					pivotCenter.plus(radius1)),
		};
		
		return new Shape(corners, sides);
    }
    
	/**
	 * A flipper's default angle, in radians.
	 * 
	 * Fortunately, both left and right flippers have the same default angle at
	 * the same orientation.
	 * 
	 * @param orientation the flipper's orientation, in degrees
	 * @return the angle of a flipper with the given orientation, when the board
	 *   is initialized; the angle is in radians
	 */
	private static double defaultAngle(int orientation) {
		assert 0 <= orientation && orientation < 360;
		assert orientation % 90 == 0;
		
		int degrees = (90 + orientation) % 360;
		return degrees * Math.PI / 180.0;
	}
	
	/**
	 * The coordinates of a flipper's pivot.
	 * 
	 * @param type the flipper's type (left or right)
	 * @param orientation the flipper's orientation, in degrees
	 * @param length the distance between the flipper's pivot and its other end
	 * @param radius the radius of the flipper's circles, and the width of the
	 *   flipper's arm
	 * @return the coordinates of the flipper's pivot
	 */
	private static Vect pivotOffset(Type type, int orientation, double length,
			double radius) {
		assert type != null;
		assert 0 <= orientation && orientation < 360;
		assert orientation % 90 == 0;
		
		int index = (orientation / 90 + type.pivotOrientationIndex()) % 4;
		switch (index) {
		case 0:
			return new Vect(0 + radius, 0 + radius);
		case 1:
			return new Vect(length + radius, 0 + radius);
		case 2:
			return new Vect(length + radius, length + radius);
		case 3:
			return new Vect(0 + radius, length + radius);
		default:
			assert false;
			throw new IllegalStateException("Implementation bug");
		}
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
			ball.setVelocity(shape.reflectRotatingAgainstBall(ball.getShape(),
					ball.getVelocity(), pivot, angularVelocity,
					reflectionCoefficient));
			trigger();
			
			assert checkRep();
			return;
		}

        throw new IllegalArgumentException(
        		"Flippers can only collide with balls");
	}

	@Override
	public void advanceTime(double timeStep) {
		// NOTE: exact comparison on doubles is OK here, because we manually
		//       assign 0 to angularVelocity in the constructor and below,
		//       whenever the flipper is supposed to stop
		if (angularVelocity == 0) {
			// NOTE: the early return avoids the expensive Shape#rotateBy call
			return;
		}
		
		angleDelta = angleDelta + angularVelocity * timeStep;
		
		// Stop the flipper from rotating if it reaches a boundary.
		if (angleDelta <= minAngleDelta) {
			angleDelta = minAngleDelta;
			angularVelocity = 0;
		} else if (angleDelta >= maxAngleDelta) {
			angleDelta = maxAngleDelta;
			angularVelocity = 0;
		}
		shape = defaultShape.rotateBy(angleDelta, pivot);
	}

	@Override
	protected void doAction() {
		if (angleDelta == minAngleDelta)
			angularVelocity = ANGULAR_VELOCITY;
		if (angleDelta == maxAngleDelta)
			angularVelocity = -ANGULAR_VELOCITY;
	}	
}
