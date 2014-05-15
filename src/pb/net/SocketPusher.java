package pb.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import pb.proto.Message;

/**
 * Forwards messages from a {@link BlockingQueue} to a {@link Socket}.
 * 
 * This {@link Runnable} should be used to create a {@link Thread}. While the
 * thread is running, it will continuously get {@link Message}s from a
 * {@link BlockingQueue}, serialize them, and write them to a {@link Socket}.
 * 
 * The thread will close the {@link Socket} and terminate if it gets a
 * {@link ControlMessage} of type {@link ControlMessage.Type#DISCONNECT} from
 * the {@link BlockingQueue}.
 * 
 * This class is intended to be used for writing to a {@link Socket} where the
 * other end is using a {@link SocketFetcher} subclass to read the
 * {@link Message}s.
 */
public class SocketPusher implements Runnable {
	/** The socket that messages get pushed to. */
	private final Socket socket;
	/** Wrapper around the socket's output stream. */
	private final PrintWriter writer;
	/** Messages queued will be sent to the server. */
	private final BlockingQueue<Message> sendQueue;
	
	// Rep invariant:
	//   everything is non-null
	// Thread safety:
	//   not thread-safe; the fields are final, so they will safely make it from
	//   the thread running the constructor to the thread executing the run()
	//   method
	
	public SocketPusher(BlockingQueue<Message> sendQueue, Socket socket)
			throws IOException {
		assert sendQueue != null;
		assert socket != null;
		
		this.socket = socket;
		this.sendQueue = sendQueue;
		// NOTE: "true" enables auto-flush
		this.writer = new PrintWriter(socket.getOutputStream(), true);			
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				Message message = sendQueue.take();
				if (message instanceof ControlMessage) {
					ControlMessage control = (ControlMessage)message;
					switch(control.getType()) {
					case DISCONNECT:
						return;
					case CLOSED:
						// CLOSED should only be used by SocketFetcher, so
						// this should never happen. Someone got confused if
						// this line executes.
						assert false;
						break;
					}
				}
				String messageString = message.toLine();
				writer.println(messageString);
			}
		} catch(InterruptedException e) {
			// Someone else wants us to be done. We're almost there.
			e.printStackTrace();
		} finally {
			writer.close();
			try {
				socket.close();
			} catch(IOException e) {
				// We're already shutting down, not much to do here.
			}
		}
	}
}