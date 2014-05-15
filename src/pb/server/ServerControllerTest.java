package pb.server;

import static org.junit.Assert.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pb.board.Edge;
import pb.net.ControlMessage;
import pb.net.ControlMessage.Type;
import pb.proto.PortalBallMessage;
import pb.proto.WallBallMessage;
import pb.proto.ConnectWallMessage;
import pb.proto.DisconnectWallMessage;
import pb.proto.HelloMessage;
import pb.proto.Message;
import pb.proto.WelcomeMessage;
import physics.Circle;
import physics.Vect;

public class ServerControllerTest {
	private BoardLinks boardLinks;
	private ServerController controller;
	private Thread controllerThread;
	private BlockingQueue<Request> requestQueue;
	
	private BlockingQueue<Message> player1, player2;
	
	@Before
	public void setUp() throws Exception {
		boardLinks = new BoardLinks();
		controller = new ServerController(boardLinks);
		controllerThread = new Thread(controller, "Board Links");
		controllerThread.start();
		requestQueue = controller.getRequestQueue();
		
		player1 = new ArrayBlockingQueue<Message>(1);
		requestQueue.put(new Request(new HelloMessage("player1"), player1));
		Message welcome1 = player1.take();
		assert welcome1 instanceof WelcomeMessage;
		assert player1.peek() == null;

		player2 = new ArrayBlockingQueue<Message>(1);
		requestQueue.put(new Request(new HelloMessage("player2"), player2));
		Message welcome2 = player2.take();
		assert welcome2 instanceof WelcomeMessage;
		assert player2.peek() == null;
	}
	
	@After
	public void tearDown() throws Exception {
		requestQueue.put(Request.EXIT);
		controllerThread.join();
	}

	@Test
	public void testHello() throws InterruptedException {
		// Tests that HelloMessage is parsed correctly.
		BlockingQueue<Message> player3 = new ArrayBlockingQueue<Message>(1);
		requestQueue.put(new Request(new HelloMessage("player3"), player3));
		Message welcome = player3.take();
		assertTrue(welcome instanceof WelcomeMessage);
		assertEquals(null, player3.peek());
	}

	@Test
	public void testHelloWithUsedBoard() throws InterruptedException {
		// Tests that clients using existing boards are disconnected.
		BlockingQueue<Message> player1too = new ArrayBlockingQueue<Message>(1);
		requestQueue.put(new Request(new HelloMessage("player1"), player1too));
		Message disconnect = player1too.take();
		assertTrue(disconnect instanceof ControlMessage);
		assertEquals(Type.DISCONNECT, ((ControlMessage)disconnect).getType());
		assertEquals(null, player1too.peek());
	}

	@Test
	public void testJoinHello() throws InterruptedException {
		// Tests that the responses to HelloMessage are dispatched correctly.
		requestQueue.put(new Request(null,
				new ServerConsoleMessage("   h     player1    player3  ")));

		BlockingQueue<Message> player3 = new ArrayBlockingQueue<Message>(1);
		requestQueue.put(new Request(new HelloMessage("player3"), player3));
		
		Message welcome = player3.take();
		assertTrue(welcome instanceof WelcomeMessage);
		Message join31Message = player3.take();
		assertTrue(join31Message instanceof ConnectWallMessage);
		ConnectWallMessage join31 = (ConnectWallMessage)join31Message;
		assertEquals(Edge.LEFT, join31.getEdge());
		assertEquals("player1", join31.getNeighborName());
		assertEquals(null, player3.peek());
		
		Message join13Message = player1.take();
		assertTrue(join13Message instanceof ConnectWallMessage);
		ConnectWallMessage join13 = (ConnectWallMessage)join13Message;
		assertEquals(Edge.RIGHT, join13.getEdge());
		assertEquals("player3", join13.getNeighborName());
		assertEquals(null, player1.peek());
	}

	@Test
	public void testConsole() throws InterruptedException {
		// Tests that ConsoleMessage is parsed correctly and responses are
		// dispatched correctly.

		BlockingQueue<Message> player3 = new ArrayBlockingQueue<Message>(1);
		requestQueue.put(new Request(new HelloMessage("player3"), player3));		
		Message welcome = player3.take();
		assertTrue(welcome instanceof WelcomeMessage);
		assertEquals(null, player3.peek());

		requestQueue.put(new Request(null,
				new ServerConsoleMessage("v player1 player3")));
		
		Message join13Message = player1.take();
		assertTrue(join13Message instanceof ConnectWallMessage);
		ConnectWallMessage join13 = (ConnectWallMessage)join13Message;
		assertEquals(Edge.BOTTOM, join13.getEdge());
		assertEquals("player3", join13.getNeighborName());
		assertEquals(null, player1.peek());

		Message join31Message = player3.take();
		assertTrue(join31Message instanceof ConnectWallMessage);
		ConnectWallMessage join31 = (ConnectWallMessage)join31Message;
		assertEquals(Edge.TOP, join31.getEdge());
		assertEquals("player1", join31.getNeighborName());
		assertEquals(null, player3.peek());
	}

	@Test
	public void testWallBall() throws InterruptedException {
		// Tests that WallBallMessage is parsed correctly and the response is
		// dispatched correctly.
		
		requestQueue.put(new Request("player1", new WallBallMessage(Edge.LEFT,
				"ballName", new Circle(0.12, 5, 0.25), new Vect(-0.2, 0.3))));

		Message ballMessage = player1.take();
		assertTrue(ballMessage instanceof WallBallMessage);
		WallBallMessage ball = (WallBallMessage)ballMessage;
		assertEquals(Edge.LEFT, ball.getEdge());
		assertEquals("ballName", ball.getBallName());
		assertEquals(0.12, ball.getShape().getCenter().x(), 0.00001);
		assertEquals(5, ball.getShape().getCenter().y(), 0.00001);
		assertEquals(0.25, ball.getShape().getRadius(), 0.00001);
		assertEquals(0.2, ball.getVelocity().x(), 0.00001);
		assertEquals(0.3, ball.getVelocity().y(), 0.00001);
		assertEquals(null, player1.peek());
		assertEquals(null, player2.peek());
	}
	
	@Test
	public void testPortalBall() throws InterruptedException {
		// Tests that PortalBallMessage is parsed correctly and the response is
		// dispatched correctly.
		
		requestQueue.put(new Request("player1", new PortalBallMessage(
				"player1", "portal1", "player2", "portal2",
				"ballName", new Circle(0.12, 5, 0.25), new Vect(-0.2, 0.3))));

		Message ballMessage = player2.take();
		assertTrue(ballMessage instanceof PortalBallMessage);
		PortalBallMessage ball = (PortalBallMessage)ballMessage;
		assertEquals("player1", ball.getFromBoard());
		assertEquals("portal1", ball.getFromPortal());
		assertEquals("player2", ball.getToBoard());
		assertEquals("portal2", ball.getToPortal());
		assertEquals("ballName", ball.getBallName());
		assertEquals(0.12, ball.getShape().getCenter().x(), 0.00001);
		assertEquals(5, ball.getShape().getCenter().y(), 0.00001);
		assertEquals(0.25, ball.getShape().getRadius(), 0.00001);
		assertEquals(-0.2, ball.getVelocity().x(), 0.00001);
		assertEquals(0.3, ball.getVelocity().y(), 0.00001);
		assertEquals(null, player1.peek());
		assertEquals(null, player2.peek());
	}	

	@Test
	public void testControlClosed() throws InterruptedException {
		// Tests that ControlMessage(CLOSED) is parsed correctly and responses
		// are dispatched correctly.

		requestQueue.put(new Request(null,
				new ServerConsoleMessage(" h  player1   player2  ")));
		
		Message join12 = player1.take();
		assertTrue(join12 instanceof ConnectWallMessage);
		assertEquals(null, player1.peek());
		Message join21 = player2.take();
		assertTrue(join21 instanceof ConnectWallMessage);
		assertEquals(null, player2.peek());
		
		requestQueue.put(new Request("player1", new ControlMessage(
				Type.CLOSED)));
		Message part21 = player2.take();
		assertTrue(part21 instanceof DisconnectWallMessage);
		assertEquals(Edge.LEFT, ((DisconnectWallMessage)part21).getEdge());
		assertEquals(null, player2.peek());
	}
}
