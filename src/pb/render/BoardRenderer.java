package pb.render;

import java.awt.Graphics2D;
import java.util.Iterator;

import pb.board.Board;
import pb.board.Gizmo;

/**
 * Renders a {@link Board}.
 *
 * The renderer is associated with a specific board.
 */
class BoardRenderer extends Renderer {
	/** The board rendered by this renderer. */
	private final Board board;
	
	/**
	 * Creates a renderer for a board.
	 * 
	 * @param board the board rendered by this renderer
	 */
	public BoardRenderer(Board board) {
		super(board.getViewport());
		assert board != null;
		
		this.board = board;
	}
	
	@Override
	public void render(Gizmo passInNull, Graphics2D context) {
		assert passInNull == null;
		
		for (Iterator<Gizmo> iterator = board.getGizmos();
				iterator.hasNext(); ) {
			Gizmo gizmo = iterator.next(); 
			gizmo.renderer().render(gizmo, context);
		}
	}	
}
