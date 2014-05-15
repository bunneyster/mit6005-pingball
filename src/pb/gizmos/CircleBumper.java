package pb.gizmos;

import pb.board.Shape;
import physics.Circle;
import physics.Vect;

public class CircleBumper extends BumperBase {
    /**
     * Creates a circle bumper.
     * 
	 * @param name the bumper's name
     * @param x the X coordinate of the bumper's origin (top-left corner)
     * @param y the Y coordinate of the bumper's origin (top-left corner)
     * @param isExploding if true, the bumper disappears when triggered
     */
    public CircleBumper(String name, double x, double y, boolean isExploding) {
    	super(name, x, y, CircleBumper.shape(x, y), isExploding);
    	assert checkRep();
    }
    
    /**
     * The shape of a circle bumper.
     * 
     * @param x the X coordinate of the bumper's origin (top-left corner)
     * @param y the Y coordinate of the bumper's origin (top-left corner)
     * @return a shape describing the circle bumper
     */
    private static Shape shape(double x, double y) {
    	Circle circle = new Circle(new Vect(x + 0.5, y + 0.5), 0.5);
    	return new Shape(new Circle[] { circle });
    }
}