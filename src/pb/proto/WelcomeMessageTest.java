package pb.proto;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *  test for: 
 *  - construction
 *  - serialization
 */

public class WelcomeMessageTest {
	
	@Test
	public void testSerialization() {
		WelcomeMessage message = new WelcomeMessage();
		String messageLine = message.toLine();
		
		Message message2 = Message.fromLine(messageLine);
		assertTrue(message2 instanceof WelcomeMessage);
	}
}
