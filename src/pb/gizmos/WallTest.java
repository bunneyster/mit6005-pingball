package pb.gizmos;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pb.board.Board;
import pb.board.BoardConstants;
import pb.board.Edge;
import pb.board.Gizmo;
import pb.proto.WallBallMessage;
import pb.proto.Message;
import physics.Circle;

public class WallTest {
	private Board board;
	private Wall top, bottom, left, right;
	
	@Before
	public void setUp() throws Exception {
		board = new Board(new BoardConstants("wall test", 5, 7, 0, 0, 0));
		
		for (Iterator<Gizmo> iterator = board.getGizmos();
				iterator.hasNext(); ) {
			Gizmo gizmo = iterator.next();
			if (!(gizmo instanceof Wall))
				continue;
			Wall wall = (Wall)gizmo;
			switch (wall.getEdge()) {
			case TOP:
				top = wall;
				break;
			case BOTTOM:
				bottom = wall;
				break;
			case LEFT:
				left = wall;
				break;
			case RIGHT:
				right = wall;
				break;
			}
		}
	}

	@Test
	public void testTopShape() {
		Circle[] corners = top.getShape().copyCorners();
		assertEquals(0, corners[0].getCenter().x(), 0.00001);
		assertEquals(0, corners[0].getCenter().y(), 0.00001);
		assertEquals(5, corners[1].getCenter().x(), 0.00001);
		assertEquals(0, corners[1].getCenter().y(), 0.00001);
		assertEquals(0, corners[0].getRadius(), 0.00001);
		assertEquals(0, corners[1].getRadius(), 0.00001);
	}

	@Test
	public void testBottomShape() {
		Circle[] corners = bottom.getShape().copyCorners();
		assertEquals(0, corners[0].getCenter().x(), 0.00001);
		assertEquals(7, corners[0].getCenter().y(), 0.00001);
		assertEquals(5, corners[1].getCenter().x(), 0.00001);
		assertEquals(7, corners[1].getCenter().y(), 0.00001);
		assertEquals(0, corners[0].getRadius(), 0.00001);
		assertEquals(0, corners[1].getRadius(), 0.00001);
	}

	@Test
	public void testLeftShape() {
		Circle[] corners = left.getShape().copyCorners();
		assertEquals(0, corners[0].getCenter().x(), 0.00001);
		assertEquals(0, corners[0].getCenter().y(), 0.00001);
		assertEquals(0, corners[1].getCenter().x(), 0.00001);
		assertEquals(7, corners[1].getCenter().y(), 0.00001);
		assertEquals(0, corners[0].getRadius(), 0.00001);
		assertEquals(0, corners[1].getRadius(), 0.00001);
	}

	@Test
	public void testRightShape() {
		Circle[] corners = right.getShape().copyCorners();
		assertEquals(5, corners[0].getCenter().x(), 0.00001);
		assertEquals(0, corners[0].getCenter().y(), 0.00001);
		assertEquals(5, corners[1].getCenter().x(), 0.00001);
		assertEquals(7, corners[1].getCenter().y(), 0.00001);
		assertEquals(0, corners[0].getRadius(), 0.00001);
		assertEquals(0, corners[1].getRadius(), 0.00001);
	}
	
	@Test
	public void testIsHorizontal() {
		assertEquals(true, top.getIsHorizontal());
		assertEquals(true, bottom.getIsHorizontal());
		assertEquals(false, left.getIsHorizontal());
		assertEquals(false, right.getIsHorizontal());
	}
	
	@Test
	public void testNeighborName() {
		assertEquals(false, top.hasNeighbor());
		assertEquals(null, top.getNeighborName());
		top.setNeighborName("topNeighbor");
		assertEquals(true, top.hasNeighbor());
		assertEquals("topNeighbor", top.getNeighborName());
		top.clearNeighborName();
		assertEquals(false, top.hasNeighbor());
		assertEquals(null, top.getNeighborName());
	}
	
	@Test
	public void testHeadOnCollision() {
		Ball ball = new Ball("ball", 1 + Ball.STANDARD_RADIUS, 3,
				Ball.STANDARD_RADIUS, -1, 0);
		board.add(ball);
		assertEquals(1, left.timeToCollision(ball), 0.00001);
		board.simulate(2);
		assertEquals(1 + Ball.STANDARD_RADIUS, ball.getCenter().x(), 0.00001);
		assertEquals(3, ball.getCenter().y(), 0.00001);
		assertEquals(1, ball.getVelocity().x(), 0.00001);
		assertEquals(0, ball.getVelocity().y(), 0.00001);
		assertEquals(true, board.contains(ball));
	}

	@Test
	public void testSideCollision() {
		Ball ball = new Ball("ball", 1 + Ball.STANDARD_RADIUS, 3,
				Ball.STANDARD_RADIUS, -1, -1);
		board.add(ball);
		assertEquals(1, left.timeToCollision(ball), 0.00001);
		board.simulate(2);
		assertEquals(1 + Ball.STANDARD_RADIUS, ball.getCenter().x(), 0.00001);
		assertEquals(1, ball.getCenter().y(), 0.00001);
		assertEquals(1, ball.getVelocity().x(), 0.00001);
		assertEquals(-1, ball.getVelocity().y(), 0.00001);
		assertEquals(true, board.contains(ball));
	}
	
	@Test
	public void testTopLeftCornerCollision() {
		Ball ball = new Ball("ball", 1 + Ball.STANDARD_RADIUS,
				1 + Ball.STANDARD_RADIUS, Ball.STANDARD_RADIUS, -1, -1);
		board.add(ball);
		assertEquals(1, left.timeToCollision(ball), 0.00001);
		assertEquals(1, top.timeToCollision(ball), 0.00001);
		board.simulate(2);
		assertEquals(1 + Ball.STANDARD_RADIUS, ball.getCenter().x(), 0.00001);
		assertEquals(1 + Ball.STANDARD_RADIUS, ball.getCenter().y(), 0.00001);
		assertEquals(1, ball.getVelocity().x(), 0.00001);
		assertEquals(1, ball.getVelocity().y(), 0.00001);
		assertEquals(true, board.contains(ball));
	}
	
	@Test
	public void testBottomRightCornerCollision() {
		Ball ball = new Ball("ball", 4 - Ball.STANDARD_RADIUS,
				6 - Ball.STANDARD_RADIUS, Ball.STANDARD_RADIUS, 1, 1);
		board.add(ball);
		assertEquals(1, right.timeToCollision(ball), 0.00001);
		assertEquals(1, bottom.timeToCollision(ball), 0.00001);
		board.simulate(2);
		assertEquals(4 - Ball.STANDARD_RADIUS, ball.getCenter().x(), 0.00001);
		assertEquals(6 - Ball.STANDARD_RADIUS, ball.getCenter().y(), 0.00001);
		assertEquals(-1, ball.getVelocity().x(), 0.00001);
		assertEquals(-1, ball.getVelocity().y(), 0.00001);
		assertEquals(true, board.contains(ball));
	}
	
	@Test
	public void testTransparentCollision() {
		board.setHasServer(true);
		top.setNeighborName("top neighbor");
		
		Ball ball = new Ball("ball", 3, 1 + Ball.STANDARD_RADIUS,
				Ball.STANDARD_RADIUS, 0, -1);
		board.add(ball);
		assertEquals(1, top.timeToCollision(ball), 0.00001);
		board.simulate(2);
		
		assertEquals(false, board.contains(ball));
		List<Message> messages = board.getAndClearOutgoingMessages();
		assertEquals(1, messages.size());
		assertTrue(messages.get(0) instanceof WallBallMessage);
		WallBallMessage ballMessage = (WallBallMessage)messages.get(0);
		assertEquals(Edge.TOP, ballMessage.getEdge());
		assertEquals(3, ballMessage.getShape().getCenter().x(), 0.00001);
		assertEquals(Ball.STANDARD_RADIUS,
				ballMessage.getShape().getCenter().y(), 0.00001);
		assertEquals(Ball.STANDARD_RADIUS, ballMessage.getShape().getRadius(),
				0.00001);
		assertEquals(0, ballMessage.getVelocity().x(), 0.00001);
		assertEquals(-1, ballMessage.getVelocity().y(), 0.00001);

		messages = board.getAndClearOutgoingMessages();
		assertEquals(0, messages.size());
	}
}
