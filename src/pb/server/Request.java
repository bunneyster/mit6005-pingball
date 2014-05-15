package pb.server;

import java.util.concurrent.BlockingQueue;

import pb.proto.HelloMessage;
import pb.proto.Message;
import pb.proto.WelcomeMessage;

/**
 * The data needed to fulfill a client's request.
 * 
 * Requests are immutable, so they are thread-safe.
 */
public class Request {
	/** The name of the client's board. */
	private final String boardName;
	
	/** The message sent by the client. */
	private final Message message;
	
	/** The client's send queue. */
	private final BlockingQueue<Message> clientQueue;
	
	// Rep invariant:
	//   message is not null
	//   (message is a HelloMessage and clientQueue is not null) or
	//       (message is not a HelloMessage and clientQueue is null)
	// Abstraction function:
	//   message represents the request line sent by the client on its socket,
	//   such as "look\n"; responseQueue is a reference to the client thread's
	//   response queue 
	// Thread-safety argument:
	//   all fields are final, so this is an immutable data type; clientQueue
	//	 itself is mutable, but it is an instance of a thread-safe data type
	
	/**
	 * Sets up a structure for a request sent by a client.
	 * 
	 * @param message the message sent by the client
	 * @param clientQueue the queue that takes messages to the client  
	 */
	public Request(HelloMessage message, BlockingQueue<Message> clientQueue) {
		assert message != null;
		assert clientQueue != null;
		
		this.boardName = message.getBoardName();
		this.message = message;
		this.clientQueue = clientQueue;
	}

	/**
	 * Sets up a structure for a client request.
	 * 
	 * @param boardName the name of the client's board
	 * @param message the message sent by the client
	 */
	public Request(String boardName, Message message) {
		assert message != null;
		assert !(message instanceof HelloMessage);
		
		this.boardName = boardName;
		this.message = message;
		this.clientQueue = null;
	}

	/**
	 * The message sent by the client.
	 * 
	 * @return the message sent by the client; will never be null
	 */
	public Message getMessage() {
		return message;
	}
	
	/**
	 * The name of the client's board.
	 * 
	 * @return the name of the client's board; this can be null if the client
	 *   disconnects before sending a {@link HelloMessage} successfully 
	 */
	public String getBoardName() {
		return boardName;
	}
	
	/**
	 * The client's send queue.
	 * 
	 * @return the client's send queue; this is only non-null when the message
	 *   is a {@link HelloMessage} instance
	 */
	public BlockingQueue<Message> getClientQueue() {
		return clientQueue;
	}
		
	/** Singleton request that tells a links controller to terminate. */
	public static final Request EXIT = new Request(
			"\n\n", new WelcomeMessage());	
}