package pb.proto;

import pb.board.Edge;

/** Sent by the server when a client's wall is connected to another board. */
public class ConnectWallMessage extends Message {
	/** The name of the neighboring board. */
	private final String neighborName;
	/** The board edge neighboring the wall that was connected. */
	private final Edge edge;
	
	// Rep invariant:
	//   everything is non-null
	// Thread safety:
	//   all fields are immutable, just like for Message
	
	/**
	 * Creates a message that tells the server that a new client is connecting.
	 */
	public ConnectWallMessage(String neighborName, Edge edge) {
		assert neighborName != null;
		assert edge != null;
		this.neighborName = neighborName;
		this.edge = edge;
	}

	/**
	 * The name of the neighboring board.
	 * 
	 * @return the name of the neighboring board
	 */
	public String getNeighborName() {
		return neighborName;
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
		return NAME + " " + neighborName + " " + edge.name();
	}
		
	// NOTE: The stuff below is package-private on purpose.
	
	/** This message's name. */
	static final String NAME = "join";

	/**
	 * Creates a message from a line of text received from a socket.
	 * @param tokens strings that were separated by spaces on the line
	 */
	ConnectWallMessage(String[] tokens) {
		assert tokens[0].equals(NAME);
		assert tokens.length >= 3;
		this.neighborName = tokens[1];
		this.edge = Edge.valueOf(tokens[2]);
	}
}