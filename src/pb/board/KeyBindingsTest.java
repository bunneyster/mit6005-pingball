package pb.board;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pb.client.UserInputMessage;
import pb.client.UserInputMessage.Type;
import pb.testing.NullGizmo;

public class KeyBindingsTest {
	private KeyBindings bindings;
	private NullGizmo gizmo1, gizmo2, gizmo3;
	
	@Before
	public void setUp() throws Exception {
		bindings = new KeyBindings();
		gizmo1 = new NullGizmo();
		gizmo2 = new NullGizmo();
		gizmo3 = new NullGizmo();
	}

	@Test
	public void testConstructor() {
		assertArrayEquals(new Gizmo[0], bindings.copyListeners("a", true));
		assertArrayEquals(new Gizmo[0], bindings.copyListeners("a", false));
		
		bindings.dispatch(new UserInputMessage(Type.PRESS, "a"));
		assertEquals(0, gizmo1.actionCount());
		assertEquals(0, gizmo2.actionCount());
		assertEquals(0, gizmo3.actionCount());
	}

	@Test
	public void testSingleListener() {
		bindings.addListener("a", true, gizmo1);
		assertArrayEquals(new Gizmo[] { gizmo1 },
				bindings.copyListeners("a", true));
		assertArrayEquals(new Gizmo[0], bindings.copyListeners("a", false));

		bindings.dispatch(new UserInputMessage(Type.PRESS, "a"));
		assertEquals(1, gizmo1.actionCount());
		assertEquals(0, gizmo2.actionCount());
		assertEquals(0, gizmo3.actionCount());
	}

	@Test
	public void testDoubleListener() {
		bindings.addListener("a", true, gizmo1);
		bindings.addListener("a", true, gizmo2);
		assertArrayEquals(new Gizmo[] { gizmo1, gizmo2 },
				bindings.copyListeners("a", true));
		assertArrayEquals(new Gizmo[0], bindings.copyListeners("a", false));

		bindings.dispatch(new UserInputMessage(Type.PRESS, "a"));
		assertEquals(1, gizmo1.actionCount());
		assertEquals(1, gizmo2.actionCount());
		assertEquals(0, gizmo3.actionCount());
	}

	@Test
	public void testPressReleaseListeners() {
		bindings.addListener("a", true, gizmo1);
		bindings.addListener("a", false, gizmo2);
		assertArrayEquals(new Gizmo[] { gizmo1 },
				bindings.copyListeners("a", true));
		assertArrayEquals(new Gizmo[] { gizmo2 },
				bindings.copyListeners("a", false));

		bindings.dispatch(new UserInputMessage(Type.PRESS, "a"));
		assertEquals(1, gizmo1.actionCount());
		assertEquals(0, gizmo2.actionCount());
		assertEquals(0, gizmo3.actionCount());

		bindings.dispatch(new UserInputMessage(Type.RELEASE, "a"));
		assertEquals(1, gizmo1.actionCount());
		assertEquals(1, gizmo2.actionCount());
		assertEquals(0, gizmo3.actionCount());
	}
}
