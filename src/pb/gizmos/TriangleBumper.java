package pb.gizmos;

import pb.board.Shape;
import physics.Vect;

public class TriangleBumper extends BumperBase {
	/** The bumper's orientation, in degrees. */
    private final int orientation;
    
    /**
     * Creates a new triangle bumper with given parameters.
     * 
	 * @param name the bumper's name
     * @param x the X coordinate of the bumper's top-left corner
     * @param y the Y coordinate of the bumper's top-left corner
     * @param orientation the bumper's orientation (0 / 90 / 180 / 270)
     * @param isExploding if true, the bumper disappears when triggered
     */
    public TriangleBumper(String name, double x, double y, int orientation,
    		boolean isExploding) {
    	// NOTE: can't use assert here because the super call has to be the
    	//		 first statement in the constructor; the shape() static method
    	//		 asserts the preconditions instead
        super(name, x, y, TriangleBumper.shape(x, y, orientation), isExploding);
    	
    	this.orientation = orientation;
    	assert checkRep();
    }
    
    /**
     * Computes the corner circles for a triangular bumper. 
     * 
     * @param x the X coordinate of the bumper's top-left corner
     * @param y the Y coordinate of the bumper's top-left corner
     * @param orientation the bumper's orientation (0 / 90 / 180 / 270)
     * @return the corner circles
     */
    static Shape shape(double x, double y, int orientation) {
    	assert 0 <= orientation && orientation < 360;
    	assert orientation % 90 == 0;

    	Vect[] boundsBox = BumperBase.boundingBox(x, y);
        int orientationIndex = orientation / 90;
    	Vect[] corners = new Vect[] {
            boundsBox[orientationIndex],
            boundsBox[(orientationIndex + 1) % 4],
            boundsBox[(4 + orientationIndex - 1) % 4]
        };
    	return new Shape(corners);
    }
    
    /**
     * Returns the orientation of this bumper.
     * 
     * @return the orientation of the triangle bumper as an integer
     */
    public int getOrientation() {
    	return orientation;
    }
    
    
}
