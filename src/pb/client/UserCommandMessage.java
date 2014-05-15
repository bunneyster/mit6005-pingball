package pb.client;

import java.awt.image.BufferStrategy;
import java.io.File;

import pb.proto.Message;

/**
 * A command issued by the user of the client GUI.
 * 
 * This class slightly violates Liskov's is-a principle. In return, we get to
 * reuse the queue that receives server messages for client UI interactions.
 * 
 * Instances of this class are immutable and therefore thread-safe. Messages
 * implement referential equality to avoid surprises when used in collections.
 */
public class UserCommandMessage extends Message {
	/** Client command types. */
	public enum Type {
		/** Pause the simulation. */
		PAUSE,
		/** Resume the simulation. */
		RESUME,
		/** Restart the game with the current board. */
		RESTART,
		/** Shut down the game. */
		EXIT,
		/** Connect to a server. */
		CONNECT,
		/** Disconnect from a server. */
		DISCONNECT,
		/** Load a board from a file. */
		LOAD,
		/**
		 * Start rendering the board.
		 * 
		 * This does not represent a user interaction. The UI sends this message
		 * after a {@link ClientUi#setBoardViewport(pb.render.Viewport)} call,
		 * to pass the {@link BufferStrategy} used to create the board's
		 * rendering manager.
		 */
		SETUP_RENDERING,
	}
	
	/** The type of user command represented by this message. */
	private final Type type;
	/** The path of the board file to be loaded. */
	private final File boardFile;
	/** The hostname of the server to connect to. */
	private final String host;
	/** The port of the server to connect to. */
	private final int port;
	/** Used to create the Board's renderer. */
	private final BufferStrategy rendererBufferStrategy;
	
	// Rep invariant:
	//   type is null
	//   if the type is CONNECT, host is non-null, port is not zero
	//   if the type is not CONNECT, host is null and port is zero
	//   if the type is LOAD, boardFile is non-null
	//   if the type is not LOAD, boardFile is null
	// Abstraction function:
	//   this represents the user having issued a command from the client UI
	// Thread safety:
	//   immutable fields
	//   File is immutable; the statement is burrowed 
	
	
	public UserCommandMessage(Type type) {
		assert type != null;
		assert type != Type.CONNECT && type != Type.LOAD &&
				type != Type.SETUP_RENDERING;
		
		this.type = type;
		this.boardFile = null;
		this.host = null;
		this.port = 0;
		this.rendererBufferStrategy = null;
	}
	
	public UserCommandMessage(String host, int port) {
		assert host != null;
		
		this.type = Type.CONNECT;
		this.boardFile = null;
		this.host = host;
		this.port = port;
		this.rendererBufferStrategy = null;		
	}

	public UserCommandMessage(File boardFile) {
		this.type = Type.LOAD;
		this.boardFile = boardFile;
		this.host = null;
		this.port = 0;
		this.rendererBufferStrategy = null;		
	}
	
	public UserCommandMessage(BufferStrategy rendererBufferStrategy) {
		assert rendererBufferStrategy != null;
		
		this.type = Type.SETUP_RENDERING;
		this.boardFile = null;
		this.host = null;
		this.port = 0;
		this.rendererBufferStrategy = rendererBufferStrategy;
	}
	
	/** The type of user input represented by this message. */
	public Type getType() {
		return type;
	}
	/** The hostname of the server to connect to. */
	public String getHost() {
		return host;
	}
	
	/** The network port of the server to connect to. */
	public int getPort() {
		return port;
	}
	
	/** The path to the file that the board should be loaded from. */
	public File getBoardFile() {
		return boardFile;
	}
	
	/** The BufferStrategy used to create the board's renderer. */
	public BufferStrategy getRendererBufferStrategy() {
		return rendererBufferStrategy;
	}
		
	@Override
	protected String name() {
		throw new UnsupportedOperationException(
				"UserCommandMessage instances cannnot be serialized");
	}
	
	@Override
	public String toLine() {
		throw new UnsupportedOperationException(
				"UserCommandMessage instances cannnot be serialized");
	}
}
