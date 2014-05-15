package pb.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pb.board.Edge;
import pb.proto.WallBallMessage;
import pb.proto.ConnectWallMessage;
import pb.proto.HelloMessage;
import pb.proto.Message;
import pb.proto.WelcomeMessage;
import physics.Circle;
import physics.Vect;

public class DispatcherTest {
	private static final int TEST_PORT = 4445;

	// Set by #setUp
	private ServerController controller;
	private Dispatcher dispatcher;
	private Thread dispatcherThread;
	private Thread controllerThread;
	private PipedOutputStream consoleOutput;
	private PrintWriter consoleWriter;
	
	// Set by #connectClients
	private Socket sockets[];
	private BufferedReader ins[];
	private PrintWriter outs[];
	
	@Before
	public void setUp() throws Exception {
		consoleOutput = new PipedOutputStream();
		consoleWriter = new PrintWriter(consoleOutput, true);
		PipedInputStream consoleInput = new PipedInputStream(consoleOutput);
		BoardLinks boardLinks = new BoardLinks();
		controller = new ServerController(boardLinks);
		controllerThread = new Thread(controller, "Board Links");
		controllerThread.start();
		dispatcher = new Dispatcher(controller.getRequestQueue(), TEST_PORT,
				consoleInput);
		dispatcherThread = new Thread(new Runnable() {
			@Override
			public void run() {
				dispatcher.serve();
			}
		}, "Dispatcher");
		dispatcherThread.start();
	}

	@After
	public void tearDown() throws Exception {
		consoleOutput.close();
		dispatcher.stopServing();
		dispatcherThread.join();
		controller.getRequestQueue().put(Request.EXIT);
		controllerThread.join();
	}

	@Test
	public void testRepeatedConnectDisconnect()
			throws IOException, InterruptedException {
		for (int iteration = 0; iteration < 10; ++iteration) {
			connectClients(1);
			disconnectClients();
		}
	}
	
	@Test
	public void testParallelConnectsDisconnects() throws IOException {
		int clientCount = 16;
		connectClients(clientCount);		
		disconnectClients();
	}

	@Test
	public void testRepeatedHelloDisconnect()
			throws IOException, InterruptedException {
		for (int iteration = 0; iteration < 10; ++iteration) {
			connectClients(1);
			Message hello = new HelloMessage("player" + iteration);
			outs[0].println(hello.toLine());
			String line = ins[0].readLine();
			Message welcome = Message.fromLine(line);
			assertTrue(welcome instanceof WelcomeMessage);
			disconnectClients();
		}
	}

	@Test
	public void testParallelHelloDisconnect()
			throws IOException, InterruptedException {
		int clientCount = 16;
		connectClients(clientCount);
		for (int i = 0; i < clientCount; ++i) {
			Message hello = new HelloMessage("player" + i);
			outs[i].println(hello.toLine());
		}
		for (int i = 0; i < clientCount; ++i) {
			String line = ins[i].readLine();
			Message welcome = Message.fromLine(line);
			assertTrue(welcome instanceof WelcomeMessage);
		}		
		disconnectClients();
	}
	
	@Test
	public void testRepeatedHelloBallDisconnect()
			throws IOException, InterruptedException {
		for (int iteration = 0; iteration < 10; ++iteration) {
			connectClients(1);
			Message hello = new HelloMessage("player" + iteration);
			outs[0].println(hello.toLine());
			String line = ins[0].readLine();
			Message welcome = Message.fromLine(line);
			assertTrue(welcome instanceof WelcomeMessage);
			Message ballOut = new WallBallMessage(Edge.LEFT, "ballName",
					new Circle(0.12, iteration + 2, 0.25), new Vect(-0.2, 0.3));
			outs[0].println(ballOut.toLine());
			line = ins[0].readLine();
			Message ballInMessage = Message.fromLine(line);
			assertTrue(ballInMessage instanceof WallBallMessage);
			WallBallMessage ballIn = (WallBallMessage)ballInMessage;
			assertEquals(Edge.LEFT, ballIn.getEdge());
			assertEquals("ballName", ballIn.getBallName());
			assertEquals(0.12, ballIn.getShape().getCenter().x(), 0.00001);
			assertEquals(iteration + 2 , ballIn.getShape().getCenter().y(),
					0.00001);
			assertEquals(0.2, ballIn.getVelocity().x(), 0.00001);
			assertEquals(0.3, ballIn.getVelocity().y(), 0.00001);
			disconnectClients();
		}
	}	

	@Test
	public void testParallelHelloBallDisconnect()
			throws IOException, InterruptedException {
		int clientCount = 16;
		connectClients(clientCount);
		for (int i = 0; i < clientCount; ++i) {
			Message hello = new HelloMessage("player" + i);
			outs[i].println(hello.toLine());
		}
		for (int i = 0; i < clientCount; ++i) {
			String line = ins[i].readLine();
			Message welcome = Message.fromLine(line);
			assertTrue(welcome instanceof WelcomeMessage);
		}
		for (int i = 0; i < clientCount; ++i) {		
			Message ballOut = new WallBallMessage(Edge.LEFT, "ballName",
					new Circle(0.12, i + 2, 0.25), new Vect(-0.2, 0.3));
			outs[i].println(ballOut.toLine());
		}
		for (int i = 0; i < clientCount; ++i) {
			String line = ins[i].readLine();
			Message ballInMessage = Message.fromLine(line);
			assertTrue(ballInMessage instanceof WallBallMessage);
			WallBallMessage ballIn = (WallBallMessage)ballInMessage;
			assertEquals(Edge.LEFT, ballIn.getEdge());
			assertEquals("ballName", ballIn.getBallName());
			assertEquals(0.12, ballIn.getShape().getCenter().x(), 0.00001);
			assertEquals(i + 2 , ballIn.getShape().getCenter().y(), 0.00001);
			assertEquals(0.2, ballIn.getVelocity().x(), 0.00001);
			assertEquals(0.3, ballIn.getVelocity().y(), 0.00001);
		}
		disconnectClients();
	}
	
	@Test
	public void testParallelConsoleHelloDisconnect()
			throws IOException, InterruptedException {
		int clientCount = 16;
		for (int i = 0; i < clientCount; ++i) {
			String joinLine = "h player" + i + " player" +
					((i + 1) % clientCount);
			consoleWriter.println(joinLine);
		}
		connectClients(clientCount);
		for (int i = 0; i < clientCount; ++i) {
			Message hello = new HelloMessage("player" + i);
			outs[i].println(hello.toLine());
		}
		for (int i = 0; i < clientCount; ++i) {
			String line = ins[i].readLine();
			Message welcome = Message.fromLine(line);
			assertTrue(welcome instanceof WelcomeMessage);
		}
		ConnectWallMessage[] lefts = new ConnectWallMessage[clientCount];
		ConnectWallMessage[] rights = new ConnectWallMessage[clientCount];
		for (int i = 0; i < clientCount; ++i) {
			for (int j = 0; j < 2; ++j) {
				String line = ins[i].readLine();
				Message joinMessage = Message.fromLine(line);
				assertTrue(joinMessage instanceof ConnectWallMessage);
				ConnectWallMessage join = (ConnectWallMessage)joinMessage;
				if (join.getEdge() == Edge.RIGHT) {
					assertEquals(null, rights[i]);
					rights[i] = join;
					assertEquals("player" + (i + 1) % clientCount,
							join.getNeighborName());
				} else {
					assertEquals(Edge.LEFT, join.getEdge());
					assertEquals(null, lefts[i]);
					lefts[i] = join;
					assertEquals("player" + (i + clientCount - 1) % clientCount,
							join.getNeighborName());
				}
			}
		}
		disconnectClients();
	}

	private void connectClients(int clientCount) throws IOException {
		sockets = new Socket[clientCount];
		ins = new BufferedReader[clientCount];
		outs = new PrintWriter[clientCount];
		
		for (int i = 0; i < clientCount; ++i) {
			sockets[i] = new Socket(InetAddress.getLocalHost(), TEST_PORT);
			ins[i] = new BufferedReader(new InputStreamReader(
	        		sockets[i].getInputStream()));
			outs[i] = new PrintWriter(sockets[i].getOutputStream(), true);
		}
	}
	
	private void disconnectClients() throws IOException {
		for (int i = 0; i < sockets.length; ++i) {
			ins[i].close();
			outs[i].close();
			sockets[i].close();
		}
	}
}
