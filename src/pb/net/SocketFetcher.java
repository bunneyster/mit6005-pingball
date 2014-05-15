package pb.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import pb.net.ControlMessage.Type;
import pb.proto.Message;

/**
 * Forwards messages from a {@link Socket} to a {@link BlockingQueue}.
 * 
 * This {@link Runnable} should be used to create a {@link Thread}. While the
 * thread is running, it will continuously read lines of text from a
 * {@link Socket}, turn them into objects, and push the objects onto a
 * {@link BlockingQueue}. The conversion is done by parsing the lines of text
 * into {@link Message}s and calling {@link #convertMessage(Message)}.
 * 
 * When the {@link Socket} is closed, a final {@link ControlMessage} of type
 * {@link ControlMessage.Type#CLOSED} will be converted into an object that gets
 * pushed onto the {@link BlockingQueue}. This lets the consumer on the other
 * end of the queue know that the connection was closed. 
 * 
 * This class is intended to be used for reading from a {@link Socket} where the
 * other end is using {@link SocketPusher} to write {@link Message}s.
 * 
 * The main examples of sub-classing {@link SocketFetcher} are 
 * {@link pb.client.ClientFetcher}, which pushes {@link Message}s onto the
 * queue, exactly as they are received, and {@link pb.server.ServerFetcher},
 * which wraps the {@link Message}s into {@link pb.server.Request} instances
 * that point to the clients who sent the messages.
 *  
 * @param <QueueItemType> the type of objects that get pushed onto the
 *   {@link BlockingQueue}
 */
public abstract class SocketFetcher<QueueItemType> implements Runnable {
	/** The socket. */
	private final Socket socket;
	/** Wraps the socket's input stream. */
	private final BufferedReader reader;
	/** Messages received from the server will be queued here. */
	private final BlockingQueue<QueueItemType> recvQueue;
	/** The send queue of the {@link SocketPusher} for the same socket. */
	private final BlockingQueue<Message> pusherQueue;
	
	/**
	 * Creates a socket fetcher.
	 * 
	 * @param recvQueue messages received from the server will be queued here
	 * @param socket the socket that messages are received from
	 * @param pusherQueie the send queue connected to the {@link SocketPusher}
	 *   for the same socket; if non-null, this queue will receive a
	 *   {@link ControlMessage.Type#DISCONNECT} message when the socket is
	 *   closed, which will allow the {@link SocketPusher} thread to exit
	 * @throws IOException
	 */
	public SocketFetcher(BlockingQueue<QueueItemType> recvQueue, Socket socket,
			BlockingQueue<Message> pusherQueue) throws IOException {
		assert recvQueue != null;
		assert socket != null;
		this.socket = socket;
		this.recvQueue = recvQueue;
		this.reader = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		this.pusherQueue = pusherQueue;
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				String messageLine = reader.readLine();
				if (messageLine == null)  // The other side closed the socket.
					break;
				Message message = Message.fromLine(messageLine);
				QueueItemType queueItem = convertMessage(message);
				recvQueue.put(queueItem);
			}
		} catch(InterruptedException e) {
			// Someone else wants us to be done.
			e.printStackTrace();
		} catch (IOException e) {
			// The connection broke down. This happens when the socket is
			// closed, which is a legitimate way to get the fetcher to exit.
		} finally {
			Message closed = new ControlMessage(Type.CLOSED);
			QueueItemType queueItem = convertMessage(closed);
			try {
				recvQueue.put(queueItem);
			} catch (InterruptedException e) {
				// Someone else wants us to be done. We're almost there.
				e.printStackTrace();
			}
			try {
				reader.close();
			} catch (IOException e) {
				// Shutting down, nothing we can do about this.
				e.printStackTrace();
			}
			try {
				socket.close();
			} catch (IOException e) {
				// Shutting down, nothing we can do about this.
				e.printStackTrace();
			}
			if (pusherQueue != null) {
				try {
					pusherQueue.put(new ControlMessage(Type.DISCONNECT));
				} catch (InterruptedException e) {
					// Someone else wants us to be done. We're almost there.
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Convert a {@link Message} into the right type for the queue.
	 * @param message the message to be converted
	 * @return the right type for the queue
	 */
	protected abstract QueueItemType convertMessage(Message message);
}