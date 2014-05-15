package pb.net;

import pb.proto.Message;

/**
 * Messages that are not sent between the client and the server.
 * 
 * This class slightly violates Liskov's is-a principle. In return, we get to
 * use plain Message instances in the queues that talk to sockets.
 * 
 * Instances of this class are immutable and therefore thread-safe. Messages
 * implement referential equality to avoid surprises when used in collections.
 */
public class ControlMessage extends Message {
	/** The control message's type. */
	private final Type type;
	
	// Rep invariant:
	//   type is not null
	// Abstraction function:
	//   this is a control message of the given type
	// Thread safety:
	//   immutable fields
	
	public enum Type {
		/** Sent to a socket to ask that it gets disconnected. */
		DISCONNECT,
		/** Sent by a socket that got closed. */
		CLOSED,
	}
	
	/**
	 * Creates a socket control message.
	 * 
	 * @param type the message's type
	 */
	public ControlMessage(Type type) {
		assert type != null;
		this.type = type;
	}
	
	/** The message type. */
	public Type getType() {
		return type;
	}
	
	@Override
	protected String name() {
		throw new UnsupportedOperationException(
				"ControlMessage instances cannnot be serialized");
	}
	
	@Override
	public String toLine() {
		throw new UnsupportedOperationException(
				"ControlMessage instances cannnot be serialized");
	}
}
