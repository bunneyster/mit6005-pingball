package pb.server;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pb.board.Edge;
import pb.proto.PortalBallMessage;
import pb.proto.WallBallMessage;
import pb.proto.ConnectWallMessage;
import pb.proto.DisconnectWallMessage;
import physics.Circle;
import physics.Vect;

public class BoardLinksTest {
	private BoardLinks boardLinks;
	
	@Before
	public void setUp() throws Exception {
		boardLinks = new BoardLinks();
	}

	@Test
	public void testJoinlessConnectionsDisconnections() {
		List<BoardLinks.TargetedMessage> messages;
		messages = boardLinks.connected("player1");
		assertEquals(0, messages.size());

		messages = boardLinks.connected("player2");
		assertEquals(0, messages.size());
	
		messages = boardLinks.disconnected("player2");
		assertEquals(0, messages.size());

		messages = boardLinks.disconnected("player1");
		assertEquals(0, messages.size());
	}
	
	@Test
	public void testRepeatedDisconnections() {
		List<BoardLinks.TargetedMessage> messages;

		messages = boardLinks.connected("player1");
		assertEquals(0, messages.size());

		messages = boardLinks.disconnected("player1");
		assertEquals(0, messages.size());

		messages = boardLinks.disconnected("player1");
		assertEquals(0, messages.size());

		messages = boardLinks.disconnected("player1");
		assertEquals(0, messages.size());
	}	

	@Test
	public void testJoinBeforeHorizontalConnections() {
		List<BoardLinks.TargetedMessage> messages;
		messages = boardLinks.horizontalJoin("player1", "player2");
		assertEquals(0, messages.size());
		
		messages = boardLinks.connected("player1");
		assertEquals(0, messages.size());

		messages = boardLinks.connected("player2");
		assertEquals(2, messages.size());
		
		BoardLinks.TargetedMessage message1 = messages.get(0);
		assertEquals("player2", message1.getBoardName());
		ConnectWallMessage join1 = (ConnectWallMessage)message1.getMessage();
		assertEquals("player1", join1.getNeighborName());
		assertEquals(Edge.LEFT, join1.getEdge());

		BoardLinks.TargetedMessage message2 = messages.get(1);
		assertEquals("player1", message2.getBoardName());
		ConnectWallMessage join2 = (ConnectWallMessage)message2.getMessage();
		assertEquals("player2", join2.getNeighborName());
		assertEquals(Edge.RIGHT, join2.getEdge());
	}
	
	@Test
	public void testJoinBeforeVerticalConnections() {
		List<BoardLinks.TargetedMessage> messages;
		messages = boardLinks.verticalJoin("player1", "player2");
		assertEquals(0, messages.size());
		
		messages = boardLinks.connected("player1");
		assertEquals(0, messages.size());

		messages = boardLinks.connected("player2");
		assertEquals(2, messages.size());
		
		BoardLinks.TargetedMessage message1 = messages.get(0);
		assertEquals("player2", message1.getBoardName());
		ConnectWallMessage join1 = (ConnectWallMessage)message1.getMessage();
		assertEquals("player1", join1.getNeighborName());
		assertEquals(Edge.TOP, join1.getEdge());

		BoardLinks.TargetedMessage message2 = messages.get(1);
		assertEquals("player1", message2.getBoardName());
		ConnectWallMessage join2 = (ConnectWallMessage)message2.getMessage();
		assertEquals("player2", join2.getNeighborName());
		assertEquals(Edge.BOTTOM, join2.getEdge());
	}
	
	@Test
	public void testDisconnectAfterHorizontalJoin() {
		List<BoardLinks.TargetedMessage> messages;
		messages = boardLinks.horizontalJoin("player1", "player2");
		assertEquals(0, messages.size());
		
		messages = boardLinks.connected("player1");
		assertEquals(0, messages.size());

		messages = boardLinks.connected("player2");
		assertEquals(2, messages.size());
		// Actual contents tested in testJoinBeforeHorizontalConnections

		messages = boardLinks.disconnected("player2");
		assertEquals(1, messages.size());
		BoardLinks.TargetedMessage message1 = messages.get(0);
		assertEquals("player1", message1.getBoardName());
		DisconnectWallMessage part1 =
				(DisconnectWallMessage)message1.getMessage();
		assertEquals(Edge.RIGHT, part1.getEdge());

		messages = boardLinks.disconnected("player1");
		assertEquals(0, messages.size());
	}

	@Test
	public void testDisconnectAfterVerticalJoin() {
		List<BoardLinks.TargetedMessage> messages;
		messages = boardLinks.verticalJoin("player1", "player2");
		assertEquals(0, messages.size());
		
		messages = boardLinks.connected("player1");
		assertEquals(0, messages.size());

		messages = boardLinks.connected("player2");
		assertEquals(2, messages.size());
		// Actual contents tested in testJoinBeforeVerticalConnections

		messages = boardLinks.disconnected("player2");
		assertEquals(1, messages.size());
		BoardLinks.TargetedMessage message1 = messages.get(0);
		assertEquals("player1", message1.getBoardName());
		DisconnectWallMessage part1 =
				(DisconnectWallMessage)message1.getMessage();
		assertEquals(Edge.BOTTOM, part1.getEdge());

		messages = boardLinks.disconnected("player1");
		assertEquals(0, messages.size());
	}	
	
	@Test
	public void testHorizontalJoinAfterConnections() {
		List<BoardLinks.TargetedMessage> messages;
		messages = boardLinks.connected("player1");
		assertEquals(0, messages.size());
		messages = boardLinks.connected("player2");
		assertEquals(0, messages.size());
		messages = boardLinks.connected("player3");
		assertEquals(0, messages.size());
		
		messages = boardLinks.horizontalJoin("player1", "player2");
		assertEquals(2, messages.size());

		BoardLinks.TargetedMessage message1 = messages.get(0);
		assertEquals("player1", message1.getBoardName());
		ConnectWallMessage join1 = (ConnectWallMessage)message1.getMessage();
		assertEquals("player2", join1.getNeighborName());
		assertEquals(Edge.RIGHT, join1.getEdge());
		
		BoardLinks.TargetedMessage message2 = messages.get(1);
		assertEquals("player2", message2.getBoardName());
		ConnectWallMessage join2 = (ConnectWallMessage)message2.getMessage();
		assertEquals("player1", join2.getNeighborName());
		assertEquals(Edge.LEFT, join2.getEdge());
	}	

	@Test
	public void testVerticalJoinAfterConnections() {
		List<BoardLinks.TargetedMessage> messages;
		messages = boardLinks.connected("player1");
		assertEquals(0, messages.size());
		messages = boardLinks.connected("player2");
		assertEquals(0, messages.size());
		messages = boardLinks.connected("player3");
		assertEquals(0, messages.size());
		
		messages = boardLinks.verticalJoin("player1", "player2");
		assertEquals(2, messages.size());

		BoardLinks.TargetedMessage message1 = messages.get(0);
		assertEquals("player1", message1.getBoardName());
		ConnectWallMessage join1 = (ConnectWallMessage)message1.getMessage();
		assertEquals("player2", join1.getNeighborName());
		assertEquals(Edge.BOTTOM, join1.getEdge());
		
		BoardLinks.TargetedMessage message2 = messages.get(1);
		assertEquals("player2", message2.getBoardName());
		ConnectWallMessage join2 = (ConnectWallMessage)message2.getMessage();
		assertEquals("player1", join2.getNeighborName());
		assertEquals(Edge.TOP, join2.getEdge());
	}
	
	@Test
	public void testHorizontalBreakingJoinAfterConnections() {
		List<BoardLinks.TargetedMessage> messages;
		messages = boardLinks.connected("player1");
		assertEquals(0, messages.size());
		messages = boardLinks.connected("player2");
		assertEquals(0, messages.size());
		messages = boardLinks.connected("player3");
		assertEquals(0, messages.size());
		messages = boardLinks.connected("player4");
		assertEquals(0, messages.size());
		
		messages = boardLinks.horizontalJoin("player1", "player2");
		assertEquals(2, messages.size());
		// Actual contents tested in testHorizontalJoinAfterConnections
		messages = boardLinks.horizontalJoin("player4", "player3");
		assertEquals(2, messages.size());
		// Actual contents tested in testVerticalJoinAfterConnections
		
		messages = boardLinks.horizontalJoin("player1", "player3");
		assertEquals(4, messages.size());
		
		BoardLinks.TargetedMessage message2 = messages.get(1);
		assertEquals("player1", message2.getBoardName());
		ConnectWallMessage join1 = (ConnectWallMessage)message2.getMessage();
		assertEquals("player3", join1.getNeighborName());
		assertEquals(Edge.RIGHT, join1.getEdge());
		
		BoardLinks.TargetedMessage message4 = messages.get(3);
		assertEquals("player3", message4.getBoardName());
		ConnectWallMessage join2 = (ConnectWallMessage)message4.getMessage();
		assertEquals("player1", join2.getNeighborName());
		assertEquals(Edge.LEFT, join2.getEdge());

		BoardLinks.TargetedMessage message1 = messages.get(0);
		assertEquals("player2", message1.getBoardName());
		DisconnectWallMessage part1 =
				(DisconnectWallMessage)message1.getMessage();
		assertEquals(Edge.LEFT, part1.getEdge());
		
		BoardLinks.TargetedMessage message3 = messages.get(2);
		assertEquals("player4", message3.getBoardName());
		DisconnectWallMessage part2 =
				(DisconnectWallMessage)message3.getMessage();
		assertEquals(Edge.RIGHT, part2.getEdge());
	}

	@Test
	public void testVerticalBreakingJoinAfterConnections() {
		List<BoardLinks.TargetedMessage> messages;
		messages = boardLinks.connected("player1");
		assertEquals(0, messages.size());
		messages = boardLinks.connected("player2");
		assertEquals(0, messages.size());
		messages = boardLinks.connected("player3");
		assertEquals(0, messages.size());
		messages = boardLinks.connected("player4");
		assertEquals(0, messages.size());
		
		messages = boardLinks.verticalJoin("player1", "player2");
		assertEquals(2, messages.size());
		// Actual contents tested in testVerticalJoinAfterConnections
		messages = boardLinks.verticalJoin("player4", "player3");
		assertEquals(2, messages.size());
		// Actual contents tested in testVerticalJoinAfterConnections
		
		messages = boardLinks.verticalJoin("player1", "player3");
		assertEquals(4, messages.size());
		
		BoardLinks.TargetedMessage message2 = messages.get(1);
		assertEquals("player1", message2.getBoardName());
		ConnectWallMessage join1 = (ConnectWallMessage)message2.getMessage();
		assertEquals("player3", join1.getNeighborName());
		assertEquals(Edge.BOTTOM, join1.getEdge());
		
		BoardLinks.TargetedMessage message4 = messages.get(3);
		assertEquals("player3", message4.getBoardName());
		ConnectWallMessage join2 = (ConnectWallMessage)message4.getMessage();
		assertEquals("player1", join2.getNeighborName());
		assertEquals(Edge.TOP, join2.getEdge());

		BoardLinks.TargetedMessage message1 = messages.get(0);
		assertEquals("player2", message1.getBoardName());
		DisconnectWallMessage part1 =
				(DisconnectWallMessage)message1.getMessage();
		assertEquals(Edge.TOP, part1.getEdge());
		
		BoardLinks.TargetedMessage message3 = messages.get(2);
		assertEquals("player4", message3.getBoardName());
		DisconnectWallMessage part2 =
				(DisconnectWallMessage)message3.getMessage();
		assertEquals(Edge.BOTTOM, part2.getEdge());
	}
	
	@Test
	public void testHorizontalRepeatedJoinAfterConnections() {
		List<BoardLinks.TargetedMessage> messages;
		messages = boardLinks.connected("player1");
		assertEquals(0, messages.size());
		messages = boardLinks.connected("player2");
		assertEquals(0, messages.size());
		
		messages = boardLinks.horizontalJoin("player1", "player2");
		assertEquals(2, messages.size());
		// Actual contents tested in testHorizontalJoinAfterConnections
		messages = boardLinks.horizontalJoin("player1", "player2");
		assertEquals(0, messages.size());
	}

	@Test
	public void testVerticalRepeatedJoinAfterConnections() {
		List<BoardLinks.TargetedMessage> messages;
		messages = boardLinks.connected("player1");
		assertEquals(0, messages.size());
		messages = boardLinks.connected("player2");
		assertEquals(0, messages.size());
		
		messages = boardLinks.horizontalJoin("player1", "player2");
		assertEquals(2, messages.size());
		// Actual contents tested in testHorizontalJoinAfterConnections
		messages = boardLinks.horizontalJoin("player1", "player2");
		assertEquals(0, messages.size());
	}

	@Test
	public void testHorizontalJoinBetweenConnections() {
		List<BoardLinks.TargetedMessage> messages;
		messages = boardLinks.connected("player1");
		assertEquals(0, messages.size());

		messages = boardLinks.horizontalJoin("player1", "player2");
		assertEquals(0, messages.size());

		messages = boardLinks.connected("player2");
		assertEquals(2, messages.size());

		BoardLinks.TargetedMessage message1 = messages.get(0);
		assertEquals("player2", message1.getBoardName());
		ConnectWallMessage join1 = (ConnectWallMessage)message1.getMessage();
		assertEquals("player1", join1.getNeighborName());
		assertEquals(Edge.LEFT, join1.getEdge());

		BoardLinks.TargetedMessage message2 = messages.get(1);
		assertEquals("player1", message2.getBoardName());
		ConnectWallMessage join2 = (ConnectWallMessage)message2.getMessage();
		assertEquals("player2", join2.getNeighborName());
		assertEquals(Edge.RIGHT, join2.getEdge());
	}	

	@Test
	public void testVerticalJoinBetweenConnections() {
		List<BoardLinks.TargetedMessage> messages;
		messages = boardLinks.connected("player1");
		assertEquals(0, messages.size());

		messages = boardLinks.verticalJoin("player1", "player2");
		assertEquals(0, messages.size());

		messages = boardLinks.connected("player2");
		assertEquals(2, messages.size());

		BoardLinks.TargetedMessage message1 = messages.get(0);
		assertEquals("player2", message1.getBoardName());
		ConnectWallMessage join1 = (ConnectWallMessage)message1.getMessage();
		assertEquals("player1", join1.getNeighborName());
		assertEquals(Edge.TOP, join1.getEdge());

		BoardLinks.TargetedMessage message2 = messages.get(1);
		assertEquals("player1", message2.getBoardName());
		ConnectWallMessage join2 = (ConnectWallMessage)message2.getMessage();
		assertEquals("player2", join2.getNeighborName());
		assertEquals(Edge.BOTTOM, join2.getEdge());
	}
	
	@Test
	public void testWallTeleportLeftToRight() {
		boardLinks.horizontalJoin("player1", "player2");
		boardLinks.connected("player1");
		boardLinks.connected("player2");
		
		WallBallMessage ball = new WallBallMessage(Edge.LEFT, "ballName",
				new Circle(0.12, 5, 0.25), new Vect(-0.2, 0.3));
		BoardLinks.TargetedMessage message = boardLinks.wallTeleport("player2",
				ball);
		assertEquals("player1", message.getBoardName());
		WallBallMessage teleported = (WallBallMessage)message.getMessage();
		assertEquals(Edge.RIGHT, teleported.getEdge());
		assertEquals("ballName", teleported.getBallName());
		assertEquals(20 - 0.12, teleported.getShape().getCenter().x(), 0.00001);
		assertEquals(5, teleported.getShape().getCenter().y(), 0.00001);
		assertEquals(0.25, teleported.getShape().getRadius(), 0.00001);
		assertEquals(-0.2, teleported.getVelocity().x(), 0.00001);
		assertEquals(0.3, teleported.getVelocity().y(), 0.00001);
	}

	@Test
	public void testWallTeleportRightToLeft() {
		boardLinks.horizontalJoin("player1", "player2");
		boardLinks.connected("player1");
		boardLinks.connected("player2");
		
		WallBallMessage ball = new WallBallMessage(Edge.RIGHT, "ballName",
				new Circle(20 - 0.13, 7, 0.25), new Vect(0.2, 0.3));
		BoardLinks.TargetedMessage message = boardLinks.wallTeleport("player1",
				ball);
		assertEquals("player2", message.getBoardName());
		WallBallMessage teleported = (WallBallMessage)message.getMessage();
		assertEquals(Edge.LEFT, teleported.getEdge());
		assertEquals("ballName", teleported.getBallName());		
		assertEquals(0.13, teleported.getShape().getCenter().x(), 0.00001);
		assertEquals(7, teleported.getShape().getCenter().y(), 0.00001);
		assertEquals(0.25, teleported.getShape().getRadius(), 0.00001);
		assertEquals(0.2, teleported.getVelocity().x(), 0.00001);
		assertEquals(0.3, teleported.getVelocity().y(), 0.00001);
	}
	
	@Test
	public void testWallTeleportTopToBottom() {
		boardLinks.verticalJoin("player1", "player2");
		boardLinks.connected("player1");
		boardLinks.connected("player2");
		
		WallBallMessage ball = new WallBallMessage(Edge.TOP, "ballName",
				new Circle(6, 0.14, 0.25), new Vect(0.2, -0.3));
		BoardLinks.TargetedMessage message = boardLinks.wallTeleport("player2",
				ball);
		assertEquals("player1", message.getBoardName());
		WallBallMessage teleported = (WallBallMessage)message.getMessage();
		assertEquals(Edge.BOTTOM, teleported.getEdge());
		assertEquals("ballName", teleported.getBallName());		
		assertEquals(6, teleported.getShape().getCenter().x(), 0.00001);
		assertEquals(20 - 0.14, teleported.getShape().getCenter().y(), 0.00001);
		assertEquals(0.25, teleported.getShape().getRadius(), 0.00001);
		assertEquals(0.2, teleported.getVelocity().x(), 0.00001);
		assertEquals(-0.3, teleported.getVelocity().y(), 0.00001);
	}
	

	@Test
	public void testWallTeleportBottomToTop() {
		boardLinks.verticalJoin("player1", "player2");
		boardLinks.connected("player1");
		boardLinks.connected("player2");
		
		WallBallMessage ball = new WallBallMessage(Edge.BOTTOM, "ballName",
				new Circle(8, 20 - 0.15, 0.25), new Vect(0.2, 0.3));
		BoardLinks.TargetedMessage message = boardLinks.wallTeleport("player1",
				ball);
		assertEquals("player2", message.getBoardName());
		WallBallMessage teleported = (WallBallMessage)message.getMessage();
		assertEquals(Edge.TOP, teleported.getEdge());
		assertEquals("ballName", teleported.getBallName());		
		assertEquals(8, teleported.getShape().getCenter().x(), 0.00001);
		assertEquals(0.15, teleported.getShape().getCenter().y(), 0.00001);
		assertEquals(0.25, teleported.getShape().getRadius(), 0.00001);
		assertEquals(0.2, teleported.getVelocity().x(), 0.00001);
		assertEquals(0.3, teleported.getVelocity().y(), 0.00001);
	}

	@Test
	public void testWallTeleportLeftReflect() {
		boardLinks.horizontalJoin("player1", "player2");
		boardLinks.connected("player1");
		boardLinks.connected("player2");
		
		WallBallMessage ball = new WallBallMessage(Edge.LEFT, "ballName",
				new Circle(0.12, 5, 0.25), new Vect(-0.2, 0.3));
		BoardLinks.TargetedMessage message = boardLinks.wallTeleport("player1",
				ball);
		assertEquals("player1", message.getBoardName());
		WallBallMessage reflected = (WallBallMessage)message.getMessage();
		assertEquals(Edge.LEFT, reflected.getEdge());
		assertEquals("ballName", reflected.getBallName());		
		assertEquals(0.12, reflected.getShape().getCenter().x(), 0.00001);
		assertEquals(5, reflected.getShape().getCenter().y(), 0.00001);
		assertEquals(0.25, reflected.getShape().getRadius(), 0.00001);
		assertEquals(0.2, reflected.getVelocity().x(), 0.00001);
		assertEquals(0.3, reflected.getVelocity().y(), 0.00001);
	}

	@Test
	public void testWallTeleportRightReflect() {
		boardLinks.horizontalJoin("player1", "player2");
		boardLinks.connected("player1");
		boardLinks.connected("player2");
		
		WallBallMessage ball = new WallBallMessage(Edge.RIGHT, "ballName",
				new Circle(20 - 0.13, 7, 0.25), new Vect(0.2, 0.3));
		BoardLinks.TargetedMessage message = boardLinks.wallTeleport("player2",
				ball);
		assertEquals("player2", message.getBoardName());
		WallBallMessage reflected = (WallBallMessage)message.getMessage();
		assertEquals(Edge.RIGHT, reflected.getEdge());
		assertEquals("ballName", reflected.getBallName());		
		assertEquals(20 - 0.13, reflected.getShape().getCenter().x(), 0.00001);
		assertEquals(7, reflected.getShape().getCenter().y(), 0.00001);
		assertEquals(0.25, reflected.getShape().getRadius(), 0.00001);
		assertEquals(-0.2, reflected.getVelocity().x(), 0.00001);
		assertEquals(0.3, reflected.getVelocity().y(), 0.00001);
	}
	
	@Test
	public void testWallTeleportTopReflect() {
		boardLinks.verticalJoin("player1", "player2");
		boardLinks.connected("player1");
		boardLinks.connected("player2");
		
		WallBallMessage ball = new WallBallMessage(Edge.TOP, "ballName",
				new Circle(6, 0.14, 0.25), new Vect(0.2, -0.3));
		BoardLinks.TargetedMessage message = boardLinks.wallTeleport("player1",
				ball);
		assertEquals("player1", message.getBoardName());
		WallBallMessage reflected = (WallBallMessage)message.getMessage();
		assertEquals(Edge.TOP, reflected.getEdge());
		assertEquals("ballName", reflected.getBallName());		
		assertEquals(6, reflected.getShape().getCenter().x(), 0.00001);
		assertEquals(0.14, reflected.getShape().getCenter().y(), 0.00001);
		assertEquals(0.25, reflected.getShape().getRadius(), 0.00001);
		assertEquals(0.2, reflected.getVelocity().x(), 0.00001);
		assertEquals(0.3, reflected.getVelocity().y(), 0.00001);
	}
	

	@Test
	public void testWallTeleportBottomReflect() {
		boardLinks.verticalJoin("player1", "player2");
		boardLinks.connected("player1");
		boardLinks.connected("player2");
		
		WallBallMessage ball = new WallBallMessage(Edge.BOTTOM, "ballName",
				new Circle(8, 20 - 0.15, 0.25), new Vect(0.2, 0.3));
		BoardLinks.TargetedMessage message = boardLinks.wallTeleport("player2",
				ball);
		assertEquals("player2", message.getBoardName());
		WallBallMessage reflected = (WallBallMessage)message.getMessage();
		assertEquals(Edge.BOTTOM, reflected.getEdge());
		assertEquals("ballName", reflected.getBallName());		
		assertEquals(8, reflected.getShape().getCenter().x(), 0.00001);
		assertEquals(20 - 0.15, reflected.getShape().getCenter().y(), 0.00001);
		assertEquals(0.25, reflected.getShape().getRadius(), 0.00001);
		assertEquals(0.2, reflected.getVelocity().x(), 0.00001);
		assertEquals(-0.3, reflected.getVelocity().y(), 0.00001);
	}	

	@Test
	public void testWallTeleportBottomReflectBecauseNotConnected() {
		boardLinks.verticalJoin("player2", "player1");
		boardLinks.connected("player2");
		// NOTE: player1 is to the bottom of player2, but is not yet connected
		
		WallBallMessage ball = new WallBallMessage(Edge.BOTTOM, "ballName",
				new Circle(8, 20 - 0.15, 0.25), new Vect(0.2, 0.3));
		BoardLinks.TargetedMessage message = boardLinks.wallTeleport("player2",
				ball);
		assertEquals("player2", message.getBoardName());
		WallBallMessage reflected = (WallBallMessage)message.getMessage();
		assertEquals(Edge.BOTTOM, reflected.getEdge());
		assertEquals("ballName", reflected.getBallName());		
		assertEquals(8, reflected.getShape().getCenter().x(), 0.00001);
		assertEquals(20 - 0.15, reflected.getShape().getCenter().y(), 0.00001);
		assertEquals(0.25, reflected.getShape().getRadius(), 0.00001);
		assertEquals(0.2, reflected.getVelocity().x(), 0.00001);
		assertEquals(-0.3, reflected.getVelocity().y(), 0.00001);
	}
	
	@Test
	public void testPortalTeleportToConnected() {
		boardLinks.connected("player1");
		boardLinks.connected("player2");
		
		PortalBallMessage ball = new PortalBallMessage("player1", "portal1", 
				"player2", "portal2", "ballName", new Circle(1, 2, 0.25),
				new Vect(0.2, -0.3));
		BoardLinks.TargetedMessage message = boardLinks.portalTeleport(ball);
		assertEquals("player2", message.getBoardName());
		PortalBallMessage teleported = (PortalBallMessage)message.getMessage();
		assertEquals("player1", teleported.getFromBoard());
		assertEquals("portal1", teleported.getFromPortal());
		assertEquals("player2", teleported.getToBoard());
		assertEquals("portal2", teleported.getToPortal());
		assertEquals("ballName", teleported.getBallName());
		assertEquals(1, teleported.getShape().getCenter().x(), 0.00001);
		assertEquals(2, teleported.getShape().getCenter().y(), 0.00001);
		assertEquals(0.25, teleported.getShape().getRadius(), 0.00001);
		assertEquals(0.2, teleported.getVelocity().x(), 0.00001);
		assertEquals(-0.3, teleported.getVelocity().y(), 0.00001);
	}
	
	@Test
	public void testPortalTeleportToDisconnected() {
		boardLinks.connected("player1");
		
		PortalBallMessage ball = new PortalBallMessage("player1", "portal1", 
				"player2", "portal2", "ballName", new Circle(1, 2, 0.25),
				new Vect(0.2, -0.3));
		BoardLinks.TargetedMessage message = boardLinks.portalTeleport(ball);
		assertEquals("player1", message.getBoardName());
		PortalBallMessage teleported = (PortalBallMessage)message.getMessage();
		assertEquals("player1", teleported.getFromBoard());
		assertEquals("portal1", teleported.getFromPortal());
		assertEquals("player1", teleported.getToBoard());
		assertEquals("portal1", teleported.getToPortal());
		assertEquals("ballName", teleported.getBallName());
		assertEquals(1, teleported.getShape().getCenter().x(), 0.00001);
		assertEquals(2, teleported.getShape().getCenter().y(), 0.00001);
		assertEquals(0.25, teleported.getShape().getRadius(), 0.00001);
		assertEquals(0.2, teleported.getVelocity().x(), 0.00001);
		assertEquals(-0.3, teleported.getVelocity().y(), 0.00001);
	}	
}
