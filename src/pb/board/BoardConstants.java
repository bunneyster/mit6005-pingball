package pb.board;

public class BoardConstants {
	private final String m_name;
	private final int m_xSize, m_ySize;
	private final double m_gravity, m_friction1, m_friction2;
	
	/**
	 * Initializes a representation of the board constants.
	 * 
	 * @param name the board's name
	 * @param xSize the board's size along the X axis, in length units (Ls)
	 * @param ySize the board's size along the Y axis, in length units (Ls)
	 * @param gravity the board's gravitational acceleration, in L^2/s
	 * @param friction1 the friction coefficient (mu), proportional to v
	 * @param friction2 the 2nd friction coefficient (mu2), proportional to v^2
	 */
	public BoardConstants(String name, int xSize, int ySize, double gravity, double friction1, double friction2) {
		assert xSize > 0;
		assert ySize > 0;
		this.m_name = name;
		this.m_xSize = xSize;
		this.m_ySize = ySize;
		this.m_gravity = gravity;
		this.m_friction1 = friction1;
		this.m_friction2 = friction2;
	}
	
	/**
	 * The board's name.
	 * @return the board's name
	 */
	public String name() { return m_name; }
	
	/**
	 * The board's size along the X axis, in length units (Ls).
	 * 
	 * @return the board's size along the X axis, in length units (Ls)
	 */
	public int xSize() { return m_xSize; }
	
	/**
	 * The board's size along the Y axis, in length units (Ls).
	 * 
	 * @return the board's size along the Y axis, in length units (Ls)
	 */
	public int ySize() { return m_ySize; }
	
	/**
	 * The board's gravitational acceleration, in L^2/s.
	 * 
	 * @return the board's gravitational acceleration, in L^2/s
	 */
	public double gravity() { return m_gravity; }
	
	/**
	 * The board's friction coefficient (mu).
	 * 
	 * @return the board's friction coefficient (mu)
	 */
	public double friction1() { return m_friction1; }
	
	/**
	 * The board's 2nd friction coefficient (mu2).
	 * 
	 * @return the board's 2nd friction coefficient (mu2)
	 */
	public double friction2() { return m_friction2; }

	/**
	 * Board constants useful for most tests.
	 * 
	 * @returns constants that describe a standard-sized board with no gravity
	 *   and no friction 
	 */
	public static BoardConstants testConstants() {
		return new BoardConstants("test", 20, 20, 0, 0, 0);
	}
	
	/**
	 * Board constants useful for gravity tests.
	 * 
	 * @param gravity the board's gravity
	 * @return constants that describe a standard-sized board with no friction
	 */
	public static BoardConstants testGravityConstants(double gravity) {
		return new BoardConstants("gravity test", 20, 20, gravity, 0, 0);
	}
	
	/**
	 * Board constants useful for gravity tests.
	 * 
	 * @param friction1 the value of friction1, proportional to v
	 * @param friction2 the value of friction2, proportional to v^2
	 * @return constants that describe a standard-sized board with no gravity
	 */
	public static BoardConstants testFrictionConstants(double friction1,
			double friction2) {
		return new BoardConstants("gravity test", 20, 20, 0, friction1,
				friction2);
	}	
}
