package pb.board;

/** The board edge next to this wall. */
public enum Edge {
	TOP {
		@Override
		public Edge opposite() { return BOTTOM; }
		@Override
		public boolean isHorizontal() { return true; }
		@Override
		public String wallName() { return "Top Wall _"; }
	},
	RIGHT {
		@Override
		public Edge opposite() { return LEFT; }
		@Override
		public boolean isHorizontal() { return false; }
		@Override
		public String wallName() { return "Right Wall _"; }
	},
	BOTTOM {
		@Override
		public Edge opposite() { return TOP; }
		@Override
		public boolean isHorizontal() { return true; }
		@Override
		public String wallName() { return "Bottom Wall _"; }
	},
	LEFT {
		@Override
		public Edge opposite() { return RIGHT; }
		@Override
		public boolean isHorizontal() { return false; }
		@Override
		public String wallName() { return "Left Wall _"; }
	};
	
	/**
	 * The opposite edge of the board.
	 * @return the opposite edge of the board
	 */
	public abstract Edge opposite();
	/**
	 * True for horizontal edges, false for vertical edges.
	 * @return true for horizontal edges, false for vertical edges
	 */
	public abstract boolean isHorizontal();
	/**
	 * The name of the wall adjacent to this edge.
	 * 
	 * This should be a unique name, so walls are easy to find on the board.
	 * 
	 * @return the name of the wall adjacent to this edge
	 */
	public abstract String wallName();
}