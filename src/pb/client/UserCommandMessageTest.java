package pb.client;

import static org.junit.Assert.*;

import org.junit.Test;

import pb.client.UserCommandMessage.Type;

public class UserCommandMessageTest {
	@Test
	public void testPauseConstruction() {
		UserCommandMessage message = new UserCommandMessage(Type.PAUSE);
		assertEquals(Type.PAUSE, message.getType());
		assertEquals(null, message.getHost());
		assertEquals(0, message.getPort());
	}
	
	@Test
	public void testConnectConstruction() {
		UserCommandMessage message = new UserCommandMessage("localhost", 10987);
		assertEquals(Type.CONNECT, message.getType());
		assertEquals("localhost", message.getHost());
		assertEquals(10987, message.getPort());		
	}
}
