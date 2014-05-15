package pb.proto;

/** Sent by the server when it accepts a client's connection. */
public class WelcomeMessage extends Message {
	// Rep invariant:
	//   everything is non-null
	// Thread safety:
	//   all fields are immutable, just like for Message
	
	/**
	 * Creates a message that tells the client that the server accepted it.
	 */
	public WelcomeMessage() {
	}
	
	@Override
	protected String name() {
		return NAME;
	}
	
	@Override
	public String toLine() {
		return NAME;
	}
		
	// NOTE: The stuff below is package-private on purpose.
	
	/** This message's name. */
	static final String NAME = "<3";

	/**
	 * Creates a message from a line of text received from a socket.
	 * @param tokens strings that were separated by spaces on the line
	 */
	WelcomeMessage(String[] tokens) {
		assert tokens[0].equals(NAME);
	}
}