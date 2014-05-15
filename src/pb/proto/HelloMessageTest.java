package pb.proto;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *  test for: 
 *  - construction
 *  - serialization
 */

public class HelloMessageTest {
	@Test
	public void testConstruction() {
		HelloMessage message = new HelloMessage("sampleBoard1");
		assertEquals("sampleBoard1", message.getBoardName());
	}
	
	@Test
	public void testSerialization() {
		HelloMessage message = new HelloMessage("sampleBoard1");
		String messageLine = message.toLine();
		
		Message message2 = Message.fromLine(messageLine);
		assertTrue(message2 instanceof HelloMessage);
		HelloMessage hello = (HelloMessage)message2;
		assertEquals("sampleBoard1", hello.getBoardName());
	}
}
