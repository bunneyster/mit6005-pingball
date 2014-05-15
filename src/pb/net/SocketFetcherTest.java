package pb.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import org.junit.Test;

import pb.board.Edge;
import pb.proto.DisconnectWallMessage;
import pb.proto.Message;
import pb.proto.WelcomeMessage;

public class SocketFetcherTest extends SocketFetcherTestBase<Message> {
	protected SocketFetcher<Message> createFetcher(
			BlockingQueue<Message> recvQueue, Socket fetcherSocket,
			BlockingQueue<Message> pusherQueue) throws IOException {
		return new NullFetcher(recvQueue, fetcherSocket, pusherQueue);
	}

	/** Returns each Message exactly as it comes from the Socket. */
	private static class NullFetcher extends SocketFetcher<Message> {
		public NullFetcher(BlockingQueue<Message> recvQueue, Socket socket,
				BlockingQueue<Message> pusherQueue) throws IOException {
			super(recvQueue, socket, pusherQueue);
		}
		
		@Override
		protected Message convertMessage(Message message) {
			return message;
		}
	}
	
	@Test
	public void testSingleMessage() throws InterruptedException {
		Message sent = new WelcomeMessage();
		clientWriter.println(sent.toLine());
		Message received = recvQueue.take();
		assertTrue(received instanceof WelcomeMessage);
	}
	
	@Test
	public void testThreeMessages() throws InterruptedException {
		Message sent1 = new WelcomeMessage();
		Message sent2 = new DisconnectWallMessage(Edge.LEFT);
		clientWriter.println(sent1.toLine());
		clientWriter.println(sent2.toLine());
		clientWriter.println(sent1.toLine());
		Message received1 = recvQueue.take();
		assertTrue(received1 instanceof WelcomeMessage);
		Message received2 = recvQueue.take();
		assertTrue(received2 instanceof DisconnectWallMessage);
		assertEquals(Edge.LEFT, ((DisconnectWallMessage)received2).getEdge());
		Message received3 = recvQueue.take();
		assertTrue(received3 instanceof WelcomeMessage);		
	}

	@Test
	public void testDisconnect() throws InterruptedException, IOException {
		clientSocket.close();
		Message received = recvQueue.take();
		assertTrue(received instanceof ControlMessage);
		assertEquals(ControlMessage.Type.CLOSED,
				((ControlMessage)received).getType());
		fetcherThread.join();
		assertTrue(fetcherSocket.isClosed());
	}

	@Test
	public void testMessageAndDisconnect()
			throws InterruptedException, IOException {
		Message sent = new WelcomeMessage();
		clientWriter.println(sent.toLine());
		clientSocket.close();
		Message received1 = recvQueue.take();
		assertTrue(received1 instanceof WelcomeMessage);
		Message received2 = recvQueue.take();
		assertTrue(received2 instanceof ControlMessage);
		assertEquals(ControlMessage.Type.CLOSED,
				((ControlMessage)received2).getType());
		fetcherThread.join();
		assertTrue(fetcherSocket.isClosed());
	}
}
