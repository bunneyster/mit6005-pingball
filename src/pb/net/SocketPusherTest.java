package pb.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pb.board.Edge;
import pb.net.ControlMessage.Type;
import pb.proto.DisconnectWallMessage;
import pb.proto.Message;
import pb.proto.WelcomeMessage;

public class SocketPusherTest {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private Socket pusherSocket;
	private SocketPusher pusher;
	private Thread pusherThread;
	private BlockingQueue<Message> sendQueue;
	private BufferedReader clientReader;
	private Object lock;
	
	@Before
	public void setUp() throws Exception {
		lock = new Object();
		sendQueue = new ArrayBlockingQueue<Message>(1);
		// The lock ensures that the write goes to serverThread.		
		synchronized (lock) {
			serverSocket = new ServerSocket(0);
		}
		Thread serverThread = new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					// The lock ensures that the write goes to the main thread.
					synchronized (lock) {
						pusherSocket = serverSocket.accept();
						pusher = new SocketPusher(sendQueue, pusherSocket);
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}, "Server");
		serverThread.start();
		clientSocket = new Socket(InetAddress.getLocalHost(),
				serverSocket.getLocalPort());
		clientReader = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		serverThread.join();
		
		// The lock ensures that the writes from serverThread are received.
		synchronized (lock) {
			pusherThread = new Thread(pusher, "Socket Pusher");
			pusherThread.start();
		}
	}

	@After
	public void tearDown() throws Exception {
		if (!pusherSocket.isClosed()) {
			sendQueue.put(new ControlMessage(Type.DISCONNECT));
			pusherThread.join();
		}
		serverSocket.close();
		clientSocket.close();
	}

	@Test
	public void testSingleMessage() throws InterruptedException, IOException {
		Message sent = new WelcomeMessage();
		sendQueue.put(sent);
		String receivedLine = clientReader.readLine();
		Message received = Message.fromLine(receivedLine);
		assertTrue(received instanceof WelcomeMessage);
	}

	@Test
	public void testThreeMessages() throws InterruptedException, IOException {
		Message sent1 = new WelcomeMessage();
		Message sent2 = new DisconnectWallMessage(Edge.LEFT);
		sendQueue.put(sent1);
		sendQueue.put(sent2);
		sendQueue.put(sent1);
		String receivedLine = clientReader.readLine();
		Message received1 = Message.fromLine(receivedLine);
		assertTrue(received1 instanceof WelcomeMessage);
		receivedLine = clientReader.readLine();
		Message received2 = Message.fromLine(receivedLine);
		assertTrue(received2 instanceof DisconnectWallMessage);
		assertEquals(Edge.LEFT, ((DisconnectWallMessage)received2).getEdge());
		receivedLine = clientReader.readLine();
		Message received3 = Message.fromLine(receivedLine);
		assertTrue(received3 instanceof WelcomeMessage);
	}

	@Test
	public void testDisconnect() throws InterruptedException, IOException {
		Message disconnect = new ControlMessage(Type.DISCONNECT);
		sendQueue.put(disconnect);
		String receivedLine = clientReader.readLine();
		assertEquals(null, receivedLine);
		pusherThread.join();
		assertTrue(pusherSocket.isClosed());
	}

	@Test
	public void testMessageAndDisconnect()
			throws InterruptedException, IOException {
		Message sent = new WelcomeMessage();
		Message disconnect = new ControlMessage(Type.DISCONNECT);
		sendQueue.put(sent);
		sendQueue.put(disconnect);
		String receivedLine = clientReader.readLine();
		Message received = Message.fromLine(receivedLine);
		assertTrue(received instanceof WelcomeMessage);
		receivedLine = clientReader.readLine();
		assertEquals(null, receivedLine);
		pusherThread.join();
		assertTrue(pusherSocket.isClosed());
	}
}
