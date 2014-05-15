package pb.board;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pb.render.Viewport;
import pb.render.Renderer;
import pb.testing.NullGizmo;
import pb.testing.NullRenderer;

public class GizmoTest {
	private Board board;
	private Renderer renderer;
	private NullGizmo gizmo1, gizmo2;
	private StyleRegistry styles;
	
	@Before
	public void setUp() throws Exception {
		board = new Board(new BoardConstants("stub", 3, 2, 0, 0, 0));
		Viewport viewport = new Viewport(3, 2);
		renderer = new NullRenderer(viewport);
		gizmo1 = new NullGizmo();
		gizmo2 = new NullGizmo();
		
		StyleRegistryBuilder stylesBuilder = new StyleRegistryBuilder();
		stylesBuilder.setProperty("green", "color", "00ff00");
		styles = stylesBuilder.build();
	}

	@Test
	public void testConstruction() {
		Gizmo gizmo = new NullGizmo();
		assertEquals(null, gizmo.board());
		assertEquals(null, gizmo.renderer());
	}

	@Test
	public void testSetRenderer() {
		Gizmo gizmo = new NullGizmo();
		assertEquals(null, gizmo.renderer());
		gizmo.setRenderer(renderer);
		assertEquals(renderer, gizmo.renderer());
	}

	@Test
	public void testSetBoard() {
		Gizmo gizmo = new NullGizmo();
		assertEquals(null, gizmo.board());
		gizmo.setBoard(board);
		assertEquals(board, gizmo.board());
	}
	
	@Test
	public void testSetStyleClass() {
		Gizmo gizmo = new NullGizmo();
		assertEquals(StyleRegistry.DEFAULT_CLASS, gizmo.styleClass());
		gizmo.setStyleClass("green");
		assertEquals("green", gizmo.styleClass());
	}
	
	@Test
	public void testStyle() {
		board.setStyleRegistry(styles);

		Gizmo gizmo = new NullGizmo();
		gizmo.setBoard(board);
		assertEquals(Style.EMPTY, gizmo.getStyle());
		
		Gizmo greenGizmo = new NullGizmo();
		greenGizmo.setStyleClass("green");
		greenGizmo.setBoard(board);
		assertEquals(styles.forClass("green"), greenGizmo.getStyle());
	}
	
	@Test
	public void testEmptyTrigger() {
		NullGizmo gizmo = new NullGizmo();
		gizmo.callTrigger();
		assertEquals(0, gizmo.actionCount());
		assertEquals(0, gizmo1.actionCount());
		assertEquals(0, gizmo2.actionCount());
	}

	@Test
	public void testSingleTrigger() {
		NullGizmo gizmo = new NullGizmo();
		gizmo.addListener(gizmo1);
		gizmo.callTrigger();
		assertEquals(1, gizmo1.actionCount());
		assertEquals(0, gizmo2.actionCount());
	}

	@Test
	public void testMultipleTrigger() {
		NullGizmo gizmo = new NullGizmo();
		gizmo.addListener(gizmo1);
		gizmo.addListener(gizmo2);
		gizmo.callTrigger();
		assertEquals(1, gizmo1.actionCount());
		assertEquals(1, gizmo2.actionCount());
	}

	@Test
	public void testGetListeners() {
		NullGizmo gizmo = new NullGizmo();
		gizmo.addListener(gizmo1);
		gizmo.addListener(gizmo2);
		Gizmo[] listeners = gizmo.copyListeners();
		assertEquals(2, listeners.length);
		assertEquals(gizmo1, listeners[0]);
		assertEquals(gizmo2, listeners[1]);
		
		// Make sure a new array is created every time.
		assertNotEquals(listeners, gizmo.copyListeners());
		assertArrayEquals(listeners, gizmo.copyListeners());
	}
}
