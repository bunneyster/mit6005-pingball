package pb.proto;

import pb.board.Edge;

/** Sent by the server when a client's wall is connected to another board. */
public class DisconnectWallMessage extends Message {
	/** The board edge neighboring the wall that was connected. */
	private final Edge edge;
	
	// Rep invariant:
	//   everything is non-null
	// Thread safety:
	//   all fields are immutable, just like for Message
	
	/**
	 * Creates a message that tells the client that one of its neighbors left.
	 */
	public DisconnectWallMessage(Edge edge) {
		assert edge != null;
		this.edge = edge;
	}

	/**
	 * The board edge neighboring the wall that was connected.
	 * 
	 * @return the board edge neighboring the wall that was connected
	 */
	public Edge getEdge() {
		return edge;
	}

	@Override
	protected String name() {
		return NAME;
	}
	
	@Override
	public String toLine() {
		return NAME + " " + edge.name();
	}
		
	// NOTE: The stuff below is package-private on purpose.
	
	/** This message's name. */
	static final String NAME = "part";

	/**
	 * Creates a message from a line of text received from a socket.
	 * @param tokens strings that were separated by spaces on the line
	 */	
	DisconnectWallMessage(String[] tokens) {
		assert tokens[0].equals(NAME);
		assert tokens.length >= 2;
		this.edge = Edge.valueOf(tokens[1]);
	}
}