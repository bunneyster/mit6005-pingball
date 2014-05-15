package pb.gizmos;

import pb.board.Shape;
import physics.Vect;

public class SquareBumper extends BumperBase {
    /**
     * Creates a square bumper with its upper left-hand corner at (x, y).
     * 
	 * @param name the bumper's name
     * @param x the x-coordinate of the upper left-hand corner
     * @param y the y-coordinate of the upper left-hand corner
     * @param isExploding if true, the bumper disappears when triggered
     */
    public SquareBumper(String name, double x, double y, boolean isExploding) {
        super(name, x, y, SquareBumper.shape(x, y), isExploding);
        assert checkRep();
    }
    
    /**
     * Computes the corner circles for a square bumper. 
     * 
     * @param x the X coordinate of the bumper's top-left corner
     * @param y the Y coordinate of the bumper's top-left corner
     * @param orientation the bumper's orientation (0 / 90 / 180 / 270)
     * @return the corner circles
     */
    private static Shape shape(double x, double y) {
    	Vect[] corners = new Vect[] {
            new Vect(x, y),
            new Vect(x + 1, y),
            new Vect(x + 1, y + 1),
            new Vect(x, y + 1)
        };
    	return new Shape(corners);
    }
}