package pb.gizmos;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pb.board.Board;
import pb.board.BoardConstants;
import pb.gizmos.Portal;

public class PortalTest {
	private Board board;
	private Portal portal1, portal2, remotePortal;
	
	@Before public void init() {
		board = new Board(BoardConstants.testConstants());
		portal1 = new Portal("portal1", 10, 5, null, "portal2");
		board.add(portal1);
		portal2 = new Portal("portal2", 2, 2, null, "nonExistingPortal");
		board.add(portal2);
		remotePortal = new Portal("remotePortal", 18, 2, "otherBoard",
				"somePortal");
		board.add(remotePortal);
	}
	
	@Test public void testCanTeleport() {
		assertEquals(true, portal1.canTeleport());
		assertEquals(false, portal2.canTeleport());
		
		assertEquals(false, remotePortal.canTeleport());
		board.setHasServer(true);
		assertEquals(true, remotePortal.canTeleport());
	}
}
