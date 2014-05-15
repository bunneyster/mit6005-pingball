package pb.proto;

import java.net.Socket;

/**
 * Base class for messages sent between a server and a client.
 *
 * The server and client talk using a text line-based protocol. The messages
 * they exchange are subclasses of {@link Message}. Each message can serialize
 * itself into one-line String, using {@link #toLine()}. One-line strings can
 * be de-serialized back into {@link Message} instance by calling the
 * {@link Message#fromLine(String)} factory method.
 * 
 * Messages are read from / written to sockets using
 * {@link pb.net.SocketPusher} and a {@link pb.net.SocketFetcher} subclass,
 * which handle the serialization and de-serialization.
 * 
 * Each subclass of {@link Message} must have tests ensuring that serialization
 * followed by de-serialization produces a message that matches the original
 * message. A non-trivial example can be found in
 * {@link ConnectWallMessageTest}.
 * 
 * Instances of this class are immutable and therefore thread-safe.
 */
public abstract class Message {
	/**
	 * The server rejects clients whose protocol version does not match.
	 * 
	 * This number should be incremented on every protocol change.
	 */
	public static final int PROTOCOL_VERSION = 1;
	
	// Rep invariant:
	//   abstract class, does not apply 
	// Thread safety:
	//   all subclasses are expected to only use immutable fields, so instances
	//   are thread-safe
	
	/**
	 * Parses a serialized message.
	 * 
	 * @param messageLine the result of calling {@link #toLine()} on a
	 *   {@link Message}
	 * @return a {@link Message} instance that has the same contents as the
	 *   {@link Message} whose {@link #toLine()} was called to obtain the given
	 *   string
	 * @throws IllegalArgumentException if the given {@link String} is not a
	 *   valid serialized message; this indicates an error in the
	 *   {@link Message} implementation, and callers should not bother with it 
	 */
	public static Message fromLine(String messageLine) {
		String[] tokens = messageLine.split(" ");
		String name = tokens[0]; 
		
		// NOTE: the class names list below should contain all the concrete
		//       classes in pb.proto, and should be ordered by the expected
		//       frequency with which they occur
		if (name.equals(WallBallMessage.NAME)) {
			return new WallBallMessage(tokens);
		}
		if (name.equals(PortalBallMessage.NAME)) {
			return new PortalBallMessage(tokens);
		}
		if (name.equals(ConnectWallMessage.NAME)) {
			return new ConnectWallMessage(tokens);
		}
        if (name.equals(DisconnectWallMessage.NAME)) {
            return new DisconnectWallMessage(tokens);
        }
		if (name.equals(HelloMessage.NAME)) {
			return new HelloMessage(tokens);
		}
		if (name.equals(WelcomeMessage.NAME)) {
			return new WelcomeMessage(tokens);
		}
		
		throw new IllegalArgumentException("Invalid message name " + name);
	}
	
	/**
	 * Serializes this message for transmission across a {@link Socket}.
	 * 
	 * @return a string that is guaranteed to not contain any line feed (0x10)
	 *   or carriage return (0x13) 
	 */
	public abstract String toLine();
	
	/**
	 * Each message type must have an unique name.
	 * 
	 * @return the unique name for the message's type
	 */
	protected abstract String name();
}