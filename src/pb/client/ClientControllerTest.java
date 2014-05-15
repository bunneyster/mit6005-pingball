package pb.client;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pb.board.Edge;
import pb.proto.ConnectWallMessage;
import pb.proto.HelloMessage;
import pb.proto.Message;
import pb.proto.WallBallMessage;
import pb.proto.WelcomeMessage;
import pb.render.Viewport;
import pb.testing.ImageDumper;
import pb.testing.NullClientUi;
import physics.Circle;
import physics.Vect;

public class ClientControllerTest {
	private BlockingQueue<Message> requestQueue;
	private NullClientUi nullUi;
	private ClientController controller;
	private Thread controllerThread;
	
	@Before
	public void setUp() throws Exception {
		requestQueue = new ArrayBlockingQueue<Message>(1);
		nullUi = new NullClientUi();
		controller = new ClientController(requestQueue, nullUi);
		controllerThread = new Thread(controller);
		controllerThread.setName("Client Controller");
		controllerThread.start();
	}

	@After
	public void tearDown() throws Exception {
		if (controllerThread != null) {
			requestQueue.put(
					new UserCommandMessage(UserCommandMessage.Type.EXIT));
			controllerThread.join();
		}
	}

	@Test
	public void testExit() throws InterruptedException {
		requestQueue.put(
				new UserCommandMessage(UserCommandMessage.Type.EXIT));
		controllerThread.join();
		controllerThread = null;
	}

	@Test
	public void testLoad() throws InterruptedException {
		requestQueue.put(
				new UserCommandMessage(new File("boards/empty.pb")));
		nullUi.waitForBoadViewportChange(null);
		assertEquals(20, nullUi.getBoardViewport().xSize());
		assertEquals(20, nullUi.getBoardViewport().ySize());
		nullUi.waitForBoadNameChange(null);
		assertEquals("empty", nullUi.getBoardName());
	}

	@Test
	public void testLoadAndRender() throws InterruptedException {
		requestQueue.put(
				new UserCommandMessage(new File(
				"boards/clientControllerRenderTest.pb")));
		nullUi.waitForBoadViewportChange(null);
		Viewport viewport = nullUi.getBoardViewport();
		ImageDumper imageDumper = new ImageDumper(
				viewport.xPixels(), viewport.yPixels(), true);
		
		// Force buffer creation to happen before we start waiting.
		imageDumper.getDrawGraphics();
		imageDumper.show();
		
		requestQueue.put(new UserCommandMessage(imageDumper));
		
		// Let the simulation loop render a few frames.
		Thread.sleep(1000);
		
		requestQueue.put(
				new UserCommandMessage(UserCommandMessage.Type.EXIT));
		controllerThread.join();
		controllerThread = null;
		
		assertTrue(imageDumper.getShowCount() > 1);
		imageDumper.checkAgainst("clientControllerRenderTest");
	}
	
	@Test
	public void testConnect() throws InterruptedException, IOException {
		requestQueue.put(
				new UserCommandMessage(new File("boards/empty.pb")));
		nullUi.waitForBoadNameChange(null);
		assertEquals("empty", nullUi.getBoardName());
		
		// Set a renderer so the simulation will start.
		nullUi.waitForBoadViewportChange(null);
		Viewport viewport = nullUi.getBoardViewport();
		ImageDumper imageDumper = new ImageDumper(
				viewport.xPixels(), viewport.yPixels(), true);		
		requestQueue.put(new UserCommandMessage(imageDumper));
		
		// Connect to the server and do the Hello / Welcome sequence.
		ServerSocket listenSocket = new ServerSocket(0);
		requestQueue.put(
				new UserCommandMessage(
						InetAddress.getLocalHost().getHostAddress(),
						listenSocket.getLocalPort()));
		Socket clientSocket = listenSocket.accept();
		BufferedReader clientReader = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		Message helloMessage = Message.fromLine(clientReader.readLine());
		assertTrue(helloMessage instanceof HelloMessage);
		PrintWriter clientWriter = new PrintWriter(
				clientSocket.getOutputStream(), true);
		clientWriter.println((new WelcomeMessage()).toLine());
		nullUi.waitForHasServerChange(false);
		assertEquals(true, nullUi.getHasServer());
		
		// Tell the client that it has a neighbor and send it a ball.
		clientWriter.println(
				(new ConnectWallMessage("friend", Edge.LEFT)).toLine());
		clientWriter.println(
				(new WallBallMessage(Edge.LEFT, "someBall",
				new Circle(new Vect(1, 3), 0.25), new Vect(-2.5, 0.5))).
				toLine());
		
		// The ball should bounce into the transparent wall we just set up.
		Message ballMessage = Message.fromLine(clientReader.readLine());
		assertTrue(ballMessage instanceof WallBallMessage);
		
		// Disconnect from the server.
		requestQueue.put(
				new UserCommandMessage(UserCommandMessage.Type.DISCONNECT));
		nullUi.waitForHasServerChange(true);
		assertEquals(false, nullUi.getHasServer());
		
		clientSocket.close();
		listenSocket.close();
	}
}
