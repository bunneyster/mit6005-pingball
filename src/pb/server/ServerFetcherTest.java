package pb.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.Test;

import pb.board.Edge;
import pb.net.ControlMessage;
import pb.net.SocketFetcher;
import pb.net.SocketFetcherTestBase;
import pb.proto.DisconnectWallMessage;
import pb.proto.HelloMessage;
import pb.proto.Message;
import pb.proto.WelcomeMessage;

public class ServerFetcherTest extends SocketFetcherTestBase<Request> {
	private BlockingQueue<Message> clientQueue;
	
	protected SocketFetcher<Request> createFetcher(
			BlockingQueue<Request> recvQueue, Socket fetcherSocket,
			BlockingQueue<Message> pusherQueue) throws IOException {
		clientQueue = new ArrayBlockingQueue<Message>(1);
		return new ServerFetcher(recvQueue, fetcherSocket, clientQueue);
	}

	@Test
	public void testSingleMessage() throws InterruptedException {
		Message sent = new HelloMessage("clientBoard");
		clientWriter.println(sent.toLine());
		Request request = recvQueue.take();
		assertEquals("clientBoard", request.getBoardName());
		assertEquals(clientQueue, request.getClientQueue());
		Message received = request.getMessage();
		assertTrue(received instanceof HelloMessage);
	}
	
	@Test
	public void testThreeMessages() throws InterruptedException {
		Message sent1 = new HelloMessage("clientBoard3");
		Message sent2 = new DisconnectWallMessage(Edge.LEFT);
		Message sent3 = new WelcomeMessage();
		clientWriter.println(sent1.toLine());
		clientWriter.println(sent2.toLine());
		clientWriter.println(sent3.toLine());
		
		Request request1 = recvQueue.take();
		assertEquals("clientBoard3", request1.getBoardName());
		assertEquals(clientQueue, request1.getClientQueue());
		Message received1 = request1.getMessage();
		assertTrue(received1 instanceof HelloMessage);
		
		Request request2 = recvQueue.take();
		assertEquals("clientBoard3", request2.getBoardName());
		assertEquals(null, request2.getClientQueue());
		Message received2 = request2.getMessage();
		assertTrue(received2 instanceof DisconnectWallMessage);
		assertEquals(Edge.LEFT, ((DisconnectWallMessage)received2).getEdge());
		
		Request request3 = recvQueue.take();
		assertEquals("clientBoard3", request3.getBoardName());
		assertEquals(null, request3.getClientQueue());
		Message received3 = request3.getMessage();
		assertTrue(received3 instanceof WelcomeMessage);		
	}

	@Test
	public void testDisconnect() throws InterruptedException, IOException {
		clientSocket.close();
		Request request = recvQueue.take();
		Message received = request.getMessage();
		assertTrue(received instanceof ControlMessage);
		assertEquals(ControlMessage.Type.CLOSED,
				((ControlMessage)received).getType());
		fetcherThread.join();
		assertTrue(fetcherSocket.isClosed());
		
		Message pusherMessage = clientQueue.take();
		assertTrue(pusherMessage instanceof ControlMessage);
		assertEquals(ControlMessage.Type.DISCONNECT,
				((ControlMessage)pusherMessage).getType());		
	}
	
	@Test
	public void testMessageAndDisconnect()
			throws InterruptedException, IOException {
		Message sent = new HelloMessage("clientBoardD");
		clientWriter.println(sent.toLine());
		clientSocket.close();
		Request request1 = recvQueue.take();
		assertEquals("clientBoardD", request1.getBoardName());
		assertEquals(clientQueue, request1.getClientQueue());
		Message received1 = request1.getMessage();
		assertTrue(received1 instanceof HelloMessage);
		Request request2 = recvQueue.take();
		assertEquals("clientBoardD", request2.getBoardName());
		assertEquals(null, request2.getClientQueue());
		Message received2 = request2.getMessage();
		assertTrue(received2 instanceof ControlMessage);
		assertEquals(ControlMessage.Type.CLOSED,
				((ControlMessage)received2).getType());
		fetcherThread.join();
		assertTrue(fetcherSocket.isClosed());
		
		Message pusherMessage = clientQueue.take();
		assertTrue(pusherMessage instanceof ControlMessage);
		assertEquals(ControlMessage.Type.DISCONNECT,
				((ControlMessage)pusherMessage).getType());
	}
}
