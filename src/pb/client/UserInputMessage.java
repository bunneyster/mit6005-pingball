package pb.client;

import pb.proto.Message;

/**
 * A key pressed or released by the user.
 * 
 * This class slightly violates Liskov's is-a principle. In return, we get to
 * reuse the queue that receives server messages for client UI interactions.
 * 
 * Instances of this class are immutable and therefore thread-safe. Messages
 * implement referential equality to avoid surprises when used in collections.
 */
public class UserInputMessage extends Message {
	/** Client input types. */
	public enum Type {
		/** The user pressed a key. */
		PRESS,
		/** The user released a key. */
		RELEASE
	}
	
	/** The type of user input represented by this message. */
	private final Type type;
	/** The name of the key pressed / released by the user. */
	private final String keyName;
	
	// Rep invariant:
	//   type and keyName are not null
	// Abstraction function:
	//   this represents the user having pressed/released the key "keyName"
	// Thread safety:
	//   immutable fields
	
	

	public UserInputMessage(Type type, String keyName) {
		assert type != null;
		assert keyName != null;
		
		this.type = type;
		this.keyName = UserInputMessage.normalizeKeyName(keyName);
	}
	
	/** The type of user input represented by this message. */
	public Type getType() {
		return type;
	}
	/** The key pressed/released by the user. */
	public String getKeyName() {
		return keyName;
	}
	
	/**
	 * Converts Swing key names into the canonical names used by key bindings.
	 * 
	 * @param keyName a Swing key name
	 * @return the normalized key name
	 */
	public static String normalizeKeyName(String keyName) {
		return keyName.toLowerCase().replaceAll(" ", "");
	}
	
	@Override
	protected String name() {
		throw new UnsupportedOperationException(
				"UserInputMessage instances cannnot be serialized");
	}
	
	@Override
	public String toLine() {
		throw new UnsupportedOperationException(
				"UserInputMessage instances cannnot be serialized");
	}
}