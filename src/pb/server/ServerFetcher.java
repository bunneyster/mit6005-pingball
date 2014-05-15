package pb.server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import pb.net.SocketFetcher;
import pb.proto.HelloMessage;
import pb.proto.Message;

/**
 * Reads lines from client sockets and turns them into {@link Request}s.
 * 
 * The {@link Request} instances are intended to be added to the
 * {@link ServerController}'s request queue.
 * 
 * This class is not thread-safe. It should be used as the {@link Runnable}
 * for a thread, just like {@link SocketFetcher}.
 */
public class ServerFetcher extends SocketFetcher<Request> {
	/** The send queue for the client's socket .*/
	private final BlockingQueue<Message> clientQueue;
	/**
	 * The name of the client's board.
	 * 
	 * This is null until the client sends {@link HelloMessage}, and is set to
	 * the client's board name afterwards.
	 */
	private String boardName;
	
	// Rep invariant:
	//   clientQueue is not null
	// Thread safety:
	//   clientQueue is final and points to a thread-safe structure
	//   boardName is only modified inside the thread associated with this
	//       Runnable
	
	/**
	 * @param requestQueue the request queue for the board links thread
	 * @param socket the client's socket
	 * @param clientQueue the send queue for the client's socket
	 * @throws IOException
	 */
	public ServerFetcher(BlockingQueue<Request> requestQueue, Socket socket,
			BlockingQueue<Message> clientQueue) throws IOException {
		super(requestQueue, socket, clientQueue);
		this.clientQueue = clientQueue;
		this.boardName = null;
	}
	
	@Override
	protected Request convertMessage(Message message) {
		if (message instanceof HelloMessage) {
			HelloMessage hello = (HelloMessage)message;
			boardName = hello.getBoardName();
			return new Request(hello, clientQueue);
		}
		return new Request(boardName, message);
	}
}