package pb.render;

import java.awt.Graphics2D;

import pb.board.Board;
import pb.board.Gizmo;

/**
 * Superclass for all board element renderers.
 * 
 * Each renderer implementation should be tested individually. Unattached board
 * elements should be used whenever possible. An {@link pb.testing.ImageDumper}
 * instance should be set up and used to obtain a {@link Graphics2D} context
 * that can be passed to {@link #render(Gizmo, Graphics2D)}.
 * 
 * Instances of this class are not thread-safe. Each instance must be contained
 * to the same thread as the board of the element whose rendering it manages.
 */
public abstract class Renderer {
	/** Parameters used to render elements. */
	private Viewport m_viewport;
	
	/**
	 * Renders a board element.
	 * 
	 * This method must be called on the board's thread, so implementations can
	 * access the given {@link Gizmo} and its {@link Board} without any thread
	 * synchronization issues.
	 * 
	 * The given {@link Graphics2D} context must point to an off-screen buffer,
	 * so it can be used without synchronizing with the UI thread.
	 * 
	 * @param gizmo the board element to be rendered
	 * @param context interface to the off-screen buffer where the element will
	 *   be rendered
	 */
	public abstract void render(Gizmo gizmo, Graphics2D context);
	
	/**
	 * Common setup for all renderers.
	 * 
	 * @param viewport parameters used to render elements
	 */
	public Renderer(Viewport viewport) {
		assert viewport != null;
		
		this.m_viewport = viewport;
	}
	
	/**
	 * The parameters used to render elements.
	 * 
	 * @return the parameters used to render elements
	 */
	public Viewport viewport() {
		return m_viewport;
	}
}