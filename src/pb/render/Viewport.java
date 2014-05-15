package pb.render;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Parameters for rendering a board into a GUI window.
 * 
 * Instances of this class are thread-safe, and are used to pass high-level
 * rendering information from the board thread managed by
 * {@link pb.client.ClientController} to {@link pb.client.ClientUiFrame}.
 */
public class Viewport {
	/** The board size along the X axis. */
	private final int m_xSize;
	/** The board size along the Y axis. */
	private final int m_ySize;
	/** Number of horizontal pixels taken up by each board length unit (L). */
	private final int m_xScale;
	/** Number of vertical pixels taken up by each board length unit (L). */
	private final int m_yScale;
	
	// Rep invariant:
	//   m_xSize, m_ySize, m_xScale and m_yScale must be positive
	// AF:
	//   a board of m_xSize x m_ySize Ls should be rendered on the screen using
	//   a box of m_xScale x m_yScale pixels for each 1L-side box on the board
	// Thread safety:
	//   all fields are final primitive values, and therefore immutable
	
	public Viewport(int xSize, int ySize) {
		assert xSize > 0;
		assert ySize > 0;
		
		this.m_xSize = xSize;
		this.m_ySize = ySize;
		this.m_xScale = 20;
		this.m_yScale = 20;
	}

	/**
	 * The size of the board along the X axis.
	 * 
	 * @return the size of the board along the X axis, in Ls (length units)
	 */
	public int xSize() {
		return m_xSize;
	}
	
	/**
	 * The size of the board along the Y axis.
	 * 
	 * @return the size of the board along the Y axis, in Ls (length units)
	 */
	public int ySize() {
		return m_ySize;
	}
	
	/**
	 * The number of horizontal pixels taken up by each board length unit (L).
	 * 
	 * @return the number of pixels used to render a 1L horizontal line
	 */
	public int xScale() {
		return m_xScale;
	}
	
	/**
	 * The number of vertical pixels taken up by each board length unit (L).
	 * 
	 * @return the number of pixels used to render a 1L vertical line
	 */
	public int yScale() {
		return m_yScale;
	}
	
	/**
	 * The number of horizontal pixels needed to render the board.
	 * 
	 * @return the horizontal size, in pixels, of the UI element that contains
	 * 	 the board's rendering
	 */
	public int xPixels() {
		return m_xScale * (m_xSize + 2);
	}
	
	/**
	 * The number of vertical pixels needed to render the board.
	 * 
	 * @return the vertical size, in pixels, of the UI element that contains
	 * 	 the board's rendering
	 */
	public int yPixels() {
		return m_yScale * (m_ySize + 2);		
	}
	
	/**
	 * Translates from board coordinates to render coordinates along the X axis.
	 * 
	 * @param x the X board coordinate, in Ls
	 * @return the corresponding coordinate in render space, in pixels
	 */
	public int x(double x) {
		assert -1 <= x && x <= m_xSize + 1;
		return m_xScale + (int)Math.round(m_xScale * x); 
	}
	
	/**
	 * Translates from board coordinates to render coordinates along the Y axis.
	 * 
	 * @param y the Y board coordinate, in Ls
	 * @return the corresponding coordinate in render space, in pixels
	 */
	public int y(double y) {
		assert -1 <= y && y <= m_xSize + 1;
		return m_yScale +  (int)Math.round(m_yScale * y);
	}

	/**
	 * Translates from board distances to render distances along the X axis.
	 * 
	 * @param dx the X board distance, in Ls
	 * @return the corresponding distance in render space, in pixels
	 */
	public int dx(double x) {
		return (int)Math.round(m_xScale * x); 
	}
	
	/**
	 * Translates from board distances to render distances along the Y axis.
	 * 
	 * @param dy the Y board distance, in Ls
	 * @return the corresponding distance in render space, in pixels
	 */
	public int dy(double y) {
		return (int)Math.round(m_yScale * y);
	}
	
	/**
	 * Convenience method for clearing a rendering buffer.
	 * 
	 * @param context drawing context for the rendering buffer to be cleared
	 */
	public void clear(Graphics2D context) {
		context.setColor(Color.BLACK);
		context.fillRect(0, 0, xPixels(), yPixels());
	}
}