package pb.proto;

import java.awt.geom.Point2D;

import pb.board.Edge;
import physics.Circle;
import physics.Vect;

/**
 * Sent when a ball is teleported across walls.
 */
public class WallBallMessage extends Message {
	/** The board edge neighboring the wall that was connected. */
	private final Edge edge;	
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
	 * Creates a message for teleporting a ball across walls.
	 * 
	 * @param edge the edge of the wall where the ball leaves / arrives
	 * @param ballName the ball's name
	 * @param center the ball's center
	 * @param velocity the ball's velocity
	 */
	public WallBallMessage(Edge edge, String ballName, Circle shape,
			Vect velocity) {
		assert edge != null;
		assert ballName != null;
		assert ballName.length() != 0;
		assert shape != null;
		assert velocity != null;
		
		this.edge = edge;
		this.ballName = ballName;
		this.shape = shape;
		this.velocity = velocity;
	}

	/**
	 * The board edge neighboring the wall that was connected.
	 * 
	 * @return the board edge neighboring the wall that was connected
	 */
	public Edge getEdge() {
		return edge;
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
		return NAME + " " + edge.name() + " " + ballName + " " +
				shape.getCenter().x() + " " + shape.getCenter().y() + " " +
				shape.getRadius() + " " +
				velocity.x() + " " + velocity.y();
	}
		
	// NOTE: The stuff below is package-private on purpose.
	
	/** This message's name. */
	static final String NAME = "wallball";

	/**
	 * Creates a message from a line of text received from a socket.
	 * @param tokens strings that were separated by spaces on the line
	 */
	WallBallMessage(String[] tokens) {
		assert tokens[0].equals(NAME);
		assert tokens.length >= 8;
		try {
			this.edge = Edge.valueOf(tokens[1]);
			this.ballName = tokens[2];

			double cx = Double.parseDouble(tokens[3]);
			double cy = Double.parseDouble(tokens[4]);
			double radius = Double.parseDouble(tokens[5]);
			double vx = Double.parseDouble(tokens[6]);
			double vy = Double.parseDouble(tokens[7]);
			
			this.shape = new Circle(new Point2D.Double(cx, cy), radius);
			this.velocity = new Vect(vx, vy);
		} catch(NumberFormatException e) {
			throw new IllegalArgumentException("Invalid version number", e);
		}
	}
}