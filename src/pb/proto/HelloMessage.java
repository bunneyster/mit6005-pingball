package pb.proto;

/** Message sent by the client when it connects to the server. */
public class HelloMessage extends Message {
	/** The protocol version spoken by the client. */
	private final int protocolVersion;
	/** The name of the client's board. */
	private final String boardName;
	
	// Rep invariant:
	//   everything is non-null
	// Thread safety:
	//   all fields are immutable, just like for Message
	
	/**
	 * Creates a message that tells the server that a new client is connecting.
	 */
	public HelloMessage(String boardName) {
		assert boardName != null;
		this.boardName = boardName;
		this.protocolVersion = Message.PROTOCOL_VERSION;
	}
	
	/**
	 * The protocol version spoken by the message's creator.
	 * 
	 * @return the protocol version spoken by the message's creator
	 */
	public int getProtocolVersion() {
		return protocolVersion;
	}
	
	/**
	 * The name of the client's board.
	 * 
	 * @return the name of the client's board
	 */
	public String getBoardName() {
		return boardName;
	}

	@Override
	protected String name() {
		return NAME;
	}
	
	@Override
	public String toLine() {
		return NAME + " " + protocolVersion + " " + boardName;
	}
		
	// NOTE: The stuff below is package-private on purpose.
	
	/** This message's name. */
	static final String NAME = "ohai";

	/**
	 * Creates a message from a line of text received from a socket.
	 * @param tokens strings that were separated by spaces on the line
	 */
	HelloMessage(String[] tokens) {
		assert tokens[0].equals(NAME);
		assert tokens.length >= 3;
		try {
			this.protocolVersion = Integer.parseInt(tokens[1]);
			this.boardName = tokens[2];
		} catch(NumberFormatException e) {
			throw new IllegalArgumentException("Invalid version number", e);
		}
	}
}