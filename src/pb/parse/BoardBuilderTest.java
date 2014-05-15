package pb.parse;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import pb.board.Board;
import pb.board.Gizmo;
import pb.board.KeyBindings;
import pb.board.Style;
import pb.board.StyleRegistry;
import pb.gizmos.Absorber;
import pb.gizmos.Ball;
import pb.gizmos.CircleBumper;
import pb.gizmos.Flipper;
import pb.gizmos.Portal;
import pb.gizmos.SquareBumper;
import pb.gizmos.TriangleBumper;
import physics.Circle;
import physics.Vect;

public class BoardBuilderTest {
	@Test
	public void testParse() throws IOException {
		List<ElementDescription> elements = 
				BoardBuilder.parse(new File("boards/parseTest.pb"));
		assertEquals(2, elements.size());
		
		ElementDescription board = elements.get(0);
		assertEquals("board", board.getType());
		assertEquals("parseTest", board.getString("name", null));
		assertEquals(9.8, board.getFloat("gravity", Double.NaN), 0.00001);
		assertEquals(0.5, board.getFloat("friction1", Double.NaN), 0.00001);
		assertEquals(0.1, board.getFloat("friction2", Double.NaN), 0.00001);

		ElementDescription ball = elements.get(1);
		assertEquals("ball", ball.getType());
		assertEquals("Ball", ball.getString("name", null));
		assertEquals(10, ball.getInteger("x", 0));
		assertEquals(11, ball.getInteger("y", 0));
		assertEquals(2, ball.getInteger("xVelocity", 0));
		assertEquals(3, ball.getInteger("yVelocity", 0));		
	}
	
	@Test
	public void testBuild() throws IOException {
		Board board = BoardBuilder.buildBoard(new File("boards/buildTest.pb"));
		
		assertEquals("buildTest", board.getConstants().name());
		assertTrue(board.findByName("Ball") instanceof Ball);
		Gizmo absorber = board.findByName("Absorber");
		assertTrue(absorber instanceof Absorber);
		Gizmo square = board.findByName("Square");
		assertTrue(square instanceof SquareBumper);
		Gizmo circle = board.findByName("Circle");
		assertTrue(circle instanceof CircleBumper);
		Gizmo portal = board.findByName("Portal");
		assertTrue(portal instanceof Portal);
		
		assertArrayEquals(new Gizmo[] { absorber }, square.copyListeners());
		assertArrayEquals(new Gizmo[] { square }, circle.copyListeners());
		
		KeyBindings bindings = board.getKeyBindings();
		assertNotNull(bindings);
		assertArrayEquals(new Gizmo[] { square },
				bindings.copyListeners("space", true));
		assertArrayEquals(new Gizmo[0], bindings.copyListeners("space", false));
		assertArrayEquals(new Gizmo[] { circle },
				bindings.copyListeners("up", false));
		assertArrayEquals(new Gizmo[0], bindings.copyListeners("up", true));
		
		StyleRegistry registry = board.getStyleRegistry();
		assertNotNull(registry);
		Style green = registry.forClass("green");
		assertEquals("00ff00", green.value("color", null));
		assertEquals("plant.png", green.value("texture", null));
	}
	
	@Test
	public void testBuildBoard() {
		ElementDescription element = ElementDescription.fromLine(
				"board name=BName gravity=9.8 friction1=0.5 friction2=0.1 " +
				"xSize=14 ySize=15");
		Board board = BoardBuilder.buildBoard(element);
		assertEquals("BName", board.getConstants().name());
		assertEquals(14, board.getConstants().xSize(), 0.00001);
		assertEquals(15, board.getConstants().ySize(), 0.00001);
		assertEquals(9.8, board.getConstants().gravity(), 0.00001);
		assertEquals(0.5, board.getConstants().friction1(), 0.00001);
		assertEquals(0.1, board.getConstants().friction2(), 0.00001);
	}

	@Test
	public void testBuildBoardDefaults() {
		ElementDescription element = ElementDescription.fromLine("board");
		Board board = BoardBuilder.buildBoard(element);
		assertEquals(null, board.getConstants().name());
		assertEquals(20, board.getConstants().xSize(), 0.00001);
		assertEquals(20, board.getConstants().ySize(), 0.00001);
		assertEquals(25, board.getConstants().gravity(), 0.00001);
		assertEquals(0.025, board.getConstants().friction1(), 0.00001);
		assertEquals(0.025, board.getConstants().friction2(), 0.00001);
	}
	
	@Test
	public void testBuildBall() {
		ElementDescription element = ElementDescription.fromLine(
				"ball name=ball x=1.2 y=3.4 radius=0.5 xVelocity=6.7 " +
				"yVelocity=8.9 class=green");
		Ball ball = (Ball)BoardBuilder.buildGizmo(element);
		Circle circle = ball.getShape();
		Vect velocity = ball.getVelocity();
		assertEquals("ball", ball.name());
		assertEquals("green", ball.styleClass());
		assertEquals(1.2, circle.getCenter().x(), 0.00001);
		assertEquals(3.4, circle.getCenter().y(), 0.00001);
		assertEquals(0.5, circle.getRadius(), 0.00001);
		assertEquals(6.7, velocity.x(), 0.00001);
		assertEquals(8.9, velocity.y(), 0.00001);
	}	

	@Test
	public void testBuildBallDefaults() {
		ElementDescription element = ElementDescription.fromLine(
				"ball name=ball x=1.2 y=3.4 xVelocity=6.7 yVelocity=8.9");
		Ball ball = (Ball)BoardBuilder.buildGizmo(element);
		Circle circle = ball.getShape();
		assertEquals("ball", ball.name());
		assertEquals(StyleRegistry.DEFAULT_CLASS, ball.styleClass());		
		assertEquals(0.25, circle.getRadius(), 0.00001);
	}
	
	@Test
	public void testBuildAbsorber() {
		ElementDescription element = ElementDescription.fromLine(
				"absorber name=absorber x=1 y=2 width=4 height=5 class=green");
		Absorber absorber = (Absorber)BoardBuilder.buildGizmo(element);
		assertEquals("absorber", absorber.name());
		assertEquals("green", absorber.styleClass());
		assertEquals(1, absorber.getOrigin().x(), 0.00001);
		assertEquals(2, absorber.getOrigin().y(), 0.00001);
		assertEquals(4, absorber.getWidth(), 0.00001);
		assertEquals(5, absorber.getHeight(), 0.00001);
	}
	
	@Test
	public void testBuildLeftFlipper() {
		ElementDescription element = ElementDescription.fromLine(
				"leftFlipper name=flipper x=1 y=2 orientation=90 class=green");
		Flipper flipper = (Flipper)BoardBuilder.buildGizmo(element);
		assertEquals("flipper", flipper.name());
		assertEquals("green", flipper.styleClass());		
		assertEquals(Flipper.Type.LEFT, flipper.getType());
		assertEquals(1, flipper.getOrigin().x(), 0.00001);
		assertEquals(2, flipper.getOrigin().y(), 0.00001);
		assertEquals(90, flipper.getOrientation());
	}
	
	@Test
	public void testBuildRightFlipper() {
		ElementDescription element = ElementDescription.fromLine(
				"rightFlipper name=flipper x=3 y=4 orientation=0 class=green");
		Flipper flipper = (Flipper)BoardBuilder.buildGizmo(element);
		assertEquals("flipper", flipper.name());
		assertEquals("green", flipper.styleClass());		
		assertEquals(Flipper.Type.RIGHT, flipper.getType());
		assertEquals(3, flipper.getOrigin().x(), 0.00001);
		assertEquals(4, flipper.getOrigin().y(), 0.00001);
		assertEquals(0, flipper.getOrientation());
	}	
	
	@Test
	public void testBuildSquareBumper() {
		ElementDescription element = ElementDescription.fromLine(
				"squareBumper name=square x=1 y=2 class=green");
		SquareBumper bumper = (SquareBumper)BoardBuilder.buildGizmo(element);
		assertEquals("square", bumper.name());
		assertEquals("green", bumper.styleClass());		
		assertEquals(1, bumper.getOrigin().x(), 0.00001);
		assertEquals(2, bumper.getOrigin().y(), 0.00001);
		assertEquals(false, bumper.getIsExploding());
	}
	
	@Test
	public void testBuildExplodingSquareBumper() {
		ElementDescription element = ElementDescription.fromLine(
				"squareBumper name=square x=1 y=2 explode=true class=green");
		SquareBumper bumper = (SquareBumper)BoardBuilder.buildGizmo(element);
		assertEquals("square", bumper.name());
		assertEquals("green", bumper.styleClass());		
		assertEquals(1, bumper.getOrigin().x(), 0.00001);
		assertEquals(2, bumper.getOrigin().y(), 0.00001);
		assertEquals(true, bumper.getIsExploding());
	}

	@Test
	public void testBuildCircleBumper() {
		ElementDescription element = ElementDescription.fromLine(
				"circleBumper name=circle x=1 y=2 class=green");
		CircleBumper bumper = (CircleBumper)BoardBuilder.buildGizmo(element);
		assertEquals("circle", bumper.name());
		assertEquals("green", bumper.styleClass());		
		assertEquals(1, bumper.getOrigin().x(), 0.00001);
		assertEquals(2, bumper.getOrigin().y(), 0.00001);
		assertEquals(false, bumper.getIsExploding());
	}

	@Test
	public void testBuildExplodingCircleBumper() {
		ElementDescription element = ElementDescription.fromLine(
				"circleBumper name=circle x=1 y=2 explode=true class=green");
		CircleBumper bumper = (CircleBumper)BoardBuilder.buildGizmo(element);
		assertEquals("circle", bumper.name());
		assertEquals("green", bumper.styleClass());		
		assertEquals(1, bumper.getOrigin().x(), 0.00001);
		assertEquals(2, bumper.getOrigin().y(), 0.00001);
		assertEquals(true, bumper.getIsExploding());
	}
	
	@Test
	public void testBuildTriangleBumper() {
		ElementDescription element = ElementDescription.fromLine(
				"triangleBumper name=triangle x=1 y=2 orientation=180 " +
				"class=green");
		TriangleBumper bumper =
				(TriangleBumper)BoardBuilder.buildGizmo(element);
		assertEquals("triangle", bumper.name());
		assertEquals("green", bumper.styleClass());		
		assertEquals(1, bumper.getOrigin().x(), 0.00001);
		assertEquals(2, bumper.getOrigin().y(), 0.00001);
		assertEquals(180, bumper.getOrientation());
		assertEquals(false, bumper.getIsExploding());
	}

	@Test
	public void testBuildExplodingTriangleBumper() {
		ElementDescription element = ElementDescription.fromLine(
				"triangleBumper name=triangle x=1 y=2 orientation=180 " +
				"explode=true class=green");
		TriangleBumper bumper =
				(TriangleBumper)BoardBuilder.buildGizmo(element);
		assertEquals("triangle", bumper.name());
		assertEquals("green", bumper.styleClass());		
		assertEquals(1, bumper.getOrigin().x(), 0.00001);
		assertEquals(2, bumper.getOrigin().y(), 0.00001);
		assertEquals(180, bumper.getOrientation());
		assertEquals(true, bumper.getIsExploding());
	}
	
	@Test
	public void testBuildLocalPortal() {
		ElementDescription element = ElementDescription.fromLine(
				"portal name=Portal x=1 y=2 otherPortal=OtherPortal " +
				"class=green");
		Portal portal = 
				(Portal)BoardBuilder.buildGizmo(element);
		assertEquals("Portal", portal.name());
		assertEquals("green", portal.styleClass());		
		assertEquals(1, portal.getShape().getCenter().x(), 0.00001);
		assertEquals(2, portal.getShape().getCenter().y(), 0.00001);
		assertEquals(null, portal.getOtherBoard());
		assertEquals("OtherPortal", portal.getOtherPortal());
	}

	@Test
	public void testBuildRemotePortal() {
		ElementDescription element = ElementDescription.fromLine(
				"portal name=Portal x=1 y=2 otherBoard=Board2 " +
				"otherPortal=Portal2 class=green");
		Portal portal = 
				(Portal)BoardBuilder.buildGizmo(element);
		assertEquals("Portal", portal.name());
		assertEquals("green", portal.styleClass());		
		assertEquals(1, portal.getShape().getCenter().x(), 0.00001);
		assertEquals(2, portal.getShape().getCenter().y(), 0.00001);
		assertEquals("Board2", portal.getOtherBoard());
		assertEquals("Portal2", portal.getOtherPortal());
	}
}
