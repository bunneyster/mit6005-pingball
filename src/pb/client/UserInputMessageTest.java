package pb.client;

import static org.junit.Assert.*;

import org.junit.Test;

import pb.client.UserInputMessage.Type;

public class UserInputMessageTest {
	@Test
	public void testConstruction() {
		UserInputMessage message = new UserInputMessage(Type.PRESS,
				"down");
		assertEquals(Type.PRESS, message.getType());
		assertEquals("down", message.getKeyName());
	}

	@Test
	public void testKeyNameNormalizationInConstruction() {
		UserInputMessage message = new UserInputMessage(Type.PRESS,
				"Back Slash");
		assertEquals(Type.PRESS, message.getType());
		assertEquals("backslash", message.getKeyName());
	}
	
	@Test
	public void testNormalizeKeyName() {
		assertEquals("backslash",
				UserInputMessage.normalizeKeyName("Back Slash"));
	}
}
