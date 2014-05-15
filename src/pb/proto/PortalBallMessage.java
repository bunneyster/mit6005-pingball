package pb.proto;

import java.awt.geom.Point2D;

import physics.Circle;
import physics.Vect;

/**
 * Sent when a ball is teleported across portals.
 */
public class PortalBallMessage extends Message {
	/** The name of the board that the ball comes from. */
	private final String fromBoard;
	/**
	 * The name of the portal that the ball comes from.
	 * 
	 * If the ball arrives to a board that does not contain the target portal,
	 * it is sent back to the source board and portal. If the network is in a
	 * good mood, it is as if the ball never left the board.
	 */
	private final String fromPortal;
	/** The name of the board that the ball should be teleported to. */
	private final String toBoard;
	/** The name of the portal that the ball should be teleported to. */
	private final String toPortal;
	/** The ball's name. */
	private final String ballName;
	/** The center and radius of the ball that is teleported. */
	private final Circle shape;	
	/** The velocity of the ball that is teleported. */
	private final Vect velocity;
	
	// Rep invariant:
	//   everything is non-null
	//   all strings are non-empty
	// Thread safety:
	//   all fields are immutable, just like for Message
	
	/**
	 * Creates a message for teleporting a ball across portals.
	 * 
	 * @param fromBoard the name of the board that the ball comes from
	 * @param fromPortal the name of the portal that the ball comes from
	 * @param toBoard the name of the board that the ball should be teleported
	 * 	 to
	 * @param toPortal tre name of the portal that the ball should be teleported
	 *   to
	 * @param ballName the ball's name
	 * @param center the ball's center
	 * @param velocity the ball's velocity
	 */
	public PortalBallMessage(String fromBoard, String fromPortal,
			String toBoard, String toPortal, String ballName,
			Circle shape, Vect velocity) {
		assert fromBoard != null;
		assert fromBoard.length() != 0;
		assert fromPortal != null;
		assert fromPortal.length() != 0;
		assert toBoard != null;
		assert toBoard.length() != 0;
		assert toPortal != null;
		assert toPortal.length() != 0;
		assert ballName != null;
		assert ballName.length() != 0;
		assert shape != null;
		assert velocity != null;
		
		this.fromBoard = fromBoard;
		this.fromPortal = fromPortal;
		this.toBoard = toBoard;
		this.toPortal = toPortal;
		this.ballName = ballName;
		this.shape = shape;
		this.velocity = velocity;
	}
	
	public String getFromPortal() {
		return fromPortal;
	}
	
	public String getFromBoard() {
		return fromBoard;
	}
	
	public String getToPortal() {
		return toPortal;
	}
	
	public String getToBoard() {
		return toBoard;
	}
	
	public String getBallName() {
		return ballName;
	}	

	public Circle getShape() {
		return shape;
	}
	
	public Vect getVelocity() {
		return velocity;
	}
	
	@Override
	protected String name() {
		return NAME;
	}
	
	@Override
	public String toLine() {
		return NAME + " " + fromBoard + " " + fromPortal + " " + toBoard + " " +
				toPortal + " " + ballName + " " +
				shape.getCenter().x() + " " + shape.getCenter().y() + " " +
				shape.getRadius() + " " +
				velocity.x() + " " + velocity.y();
	}
		
	// NOTE: The stuff below is package-private on purpose.
	
	/** This message's name. */
	static final String NAME = "portalball";

	/**
	 * Creates a message from a line of text received from a socket.
	 * @param tokens strings that were separated by spaces on the line
	 */
	PortalBallMessage(String[] tokens) {
		assert tokens[0].equals(NAME);
		assert tokens.length >= 8;
		try {
			this.fromBoard = tokens[1];
			this.fromPortal = tokens[2];
			this.toBoard = tokens[3];
			this.toPortal = tokens[4];
			this.ballName = tokens[5];
			
			double cx = Double.parseDouble(tokens[6]);
			double cy = Double.parseDouble(tokens[7]);
			double radius = Double.parseDouble(tokens[8]);
			double vx = Double.parseDouble(tokens[9]);
			double vy = Double.parseDouble(tokens[10]);
			
			this.shape = new Circle(new Point2D.Double(cx, cy), radius);
			this.velocity = new Vect(vx, vy);
		} catch(NumberFormatException e) {
			throw new IllegalArgumentException("Invalid version number", e);
		}
	}
}