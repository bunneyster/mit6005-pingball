package pb.server;

import pb.proto.Message;

/**
 * Lines of console input.
 * 
 * This class slightly violates Liskov's is-a principle. In return, we get to
 * treat the console as yet another client to the server.
 * 
 * Instances of this class are immutable and therefore thread-safe. Messages
 * implement referential equality to avoid surprises when used in collections.
 */
public class ServerConsoleMessage extends Message {
	/** The line typed in the server's console. */
	private final String line;
	
	// Rep invariant:
	//   line is not null
	// Abstraction function:
	//   this represents the server admin having input the line "line" at the
	//       console
	// Thread safety:
	//   immutable fields
	

	/**
	 * 
	 * @param line
	 */
	public ServerConsoleMessage(String line) {
		assert line != null;
		this.line = line;
	}
	
	/** The line typed in the server's console. */
	public String getLine() {
		return line;
	}
	
	@Override
	protected String name() {
		throw new UnsupportedOperationException(
				"ConsoleMessage instances cannnot be serialized");
	}
	
	@Override
	public String toLine() {
		throw new UnsupportedOperationException(
				"ConsoleMessage instances cannnot be serialized");
	}
}
