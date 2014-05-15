package pb.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pb.board.Board;
import pb.board.BoardConstants;
import pb.board.Gizmo;
import pb.board.StyleRegistry;
import pb.board.StyleRegistryBuilder;
import pb.gizmos.Absorber;
import pb.gizmos.Ball;
import pb.gizmos.CircleBumper;
import pb.gizmos.Flipper;
import pb.gizmos.Portal;
import pb.gizmos.SquareBumper;
import pb.gizmos.TriangleBumper;
import pb.gizmos.Flipper.Type;

/**
 * Parses board definition into built boards.
 */
public class BoardBuilder {
	/**
	 * Creates a board from the description.
	 * 
	 * @param source the path to the board file
	 * @return the board obtained by parsing the file
	 * @throws IOException
	 */
	public static Board buildBoard(File source) throws IOException {
		List<ElementDescription> elements = parse(source);
		StyleRegistryBuilder styleBuilder = new StyleRegistryBuilder();
		Board board = null;
		
		for (ElementDescription element : elements) {
			String type = element.getType();
			
			if (type.equals("board")) {
				if (board != null)
					throw new IllegalArgumentException(
							"Duplicate board definition");
				board = buildBoard(element);
			} else if (type.equals("fire")) {
				String triggerName = element.getString("trigger");
				Gizmo trigger = board.findByName(triggerName);
				if (trigger == null)
					throw new IllegalArgumentException("Trigger not found");
				String actionName = element.getString("action");
				Gizmo action = board.findByName(actionName);
				if (action == null)
					throw new IllegalArgumentException("Action not found");
				trigger.addListener(action);
			} else if (type.equals("keyup") || type.equals("keydown")) {
				boolean press = type.equals("keydown");
				String keyName = element.getString("key");
				String actionName = element.getString("action");				
				Gizmo action = board.findByName(actionName);
				if (action == null)
					throw new IllegalArgumentException("Action not found");
				board.getKeyBindings().addListener(keyName, press, action);
			} else if (type.equals("style")) {
				String styleClass = element.getString("class");
				for (Iterator<String> iterator = element.getPropertyNames();
						iterator.hasNext(); ) {
					String propertyName = iterator.next();
					String value = element.getString(propertyName);
					styleBuilder.setProperty(styleClass, propertyName, value);
				}
			} else {
				Gizmo gizmo = buildGizmo(element);
				if (board == null) {
					throw new IllegalArgumentException(
							"Board not defined first");
				}
				if (board.findByName(gizmo.name()) != null) {
					throw new IllegalArgumentException(
							"Duplicate element name");
				}
				board.add(gizmo);
			}
		}
		
		board.setStyleRegistry(styleBuilder.build());
		return board;
	}
	
	/**
	 * Creates a board element from a line description in a board file.
	 * 
	 * @param element a parsed line describing a board element
	 * @return a board element; the element is not added to any board
	 */
	static Gizmo buildGizmo(ElementDescription element) {
		String type = element.getType();
		String name = element.getString("name");
		
		Gizmo gizmo = null;
		if (type.equals("ball")) {
			double x = element.getFloat("x");
			double y = element.getFloat("y");
			double r = element.getFloat("radius", 0.25);
			double vx = element.getFloat("xVelocity");
			double vy = element.getFloat("yVelocity");
			gizmo = new Ball(name, x, y, r, vx, vy);
		} else if (type.equals("absorber")) {
			int x = element.getInteger("x");
			int y = element.getInteger("y");			
			int width = element.getInteger("width");
			int height = element.getInteger("height");
			gizmo = new Absorber(name, x, y, width, height);
		} else if (type.equals("portal")) {
			double x = element.getFloat("x");
			double y = element.getFloat("y");
			String otherBoard = element.getString("otherBoard", null);
			String otherPortal = element.getString("otherPortal");
			gizmo = new Portal(name, x, y, otherBoard, otherPortal);
		} else if (type.indexOf("Bumper") >= 0) {
			int x = element.getInteger("x");
			int y = element.getInteger("y");
			boolean isExploding =
					!element.getString("explode", "false").equals("false");
			if (type.equals("squareBumper")) {
				gizmo = new SquareBumper(name, x, y, isExploding);
			} else if (type.equals("circleBumper")) {
				gizmo = new CircleBumper(name, x, y, isExploding);
			} else if (type.equals("triangleBumper")) {
				int orientation = element.getInteger("orientation");
				gizmo = new TriangleBumper(name, x, y, orientation,
						isExploding);
			}
		} else if (type.indexOf("Flipper") >= 0) {
			int x = element.getInteger("x");
			int y = element.getInteger("y");
			int orientation = element.getInteger("orientation");
			if (type.equals("leftFlipper")) {
				gizmo = new Flipper(name, Type.LEFT, x, y, orientation);
			} else if (type.equals("rightFlipper")) {
				gizmo = new Flipper(name, Type.RIGHT, x, y, orientation);
			}
		}
		
		if (gizmo == null) {
			throw new IllegalArgumentException("Unsupported statement " +
					element.getType());
		}
		
		String styleClass = element.getString("class",
				StyleRegistry.DEFAULT_CLASS);		
		gizmo.setStyleClass(styleClass);
		return gizmo;
	}
	
	/**
	 * Creates a board from a line description in a board file.
	 * 
	 * @param element a parsed line describing a board 
	 * @return an empty board
	 */
	static Board buildBoard(ElementDescription element) {
		if (!element.getType().equals("board"))
			throw new IllegalArgumentException("The element is not a board");
		
		String name = element.getString("name", null);
		int xSize = element.getInteger("xSize", 20);
		int ySize = element.getInteger("ySize", 20);
		double gravity = element.getFloat("gravity", 25.0);
		double friction1 = element.getFloat("friction1", 0.025);
		double friction2 = element.getFloat("friction2", 0.025);
		BoardConstants constants = new BoardConstants(name, xSize, ySize,
				gravity, friction1, friction2);
		return new Board(constants);
	}

	/**
	 * Reads a board file and parses it into element descriptions.
	 * 
	 * @param file path to the board file
	 * @return a list of board element descriptions
	 * @throws IOException
	 */
	static List<ElementDescription> parse(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		List<ElementDescription> elements = new ArrayList<ElementDescription>();
		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;
			ElementDescription element = ElementDescription.fromLine(line);
			if (element != null)
				elements.add(element);
		}
		reader.close();
		return elements;
	}	
}
