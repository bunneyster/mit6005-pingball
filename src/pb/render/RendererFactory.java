package pb.render;

import pb.board.Gizmo;
import pb.gizmos.Absorber;
import pb.gizmos.Ball;
import pb.gizmos.BumperBase;
import pb.gizmos.Flipper;
import pb.gizmos.Portal;
import pb.gizmos.Wall;

/**
 * Creates appropriate {@link Renderer} instances for {@link Gizmo}s.  
 *
 * This class insulates the other packages from having to know about specific
 * renderer types.
 * 
 * Instances of this class are not thread-safe and should be contained to the
 * same thread as the board being rendered.
 */
class RendererFactory {
	/** The Viewport passed to all the renderers produced by this factory. */
	private final Viewport viewport;
	/** Singleton ball renderer instance. */
	private final BallRenderer ballRenderer;
	/** Singleton absorber renderer instance. */
	private final AbsorberRenderer absorberRenderer;
	/** Singleton portal renderer instance. */
	private final PortalRenderer portalRenderer;
	
	/**
	 * Creates a renderer factory.
	 * 
	 * @param viewport the parameters used by all the renderers created by this
	 *   factory 
	 */
	public RendererFactory(Viewport viewport) {
		assert viewport != null;
		
		this.viewport = viewport;
		this.ballRenderer = new BallRenderer(viewport);
		this.absorberRenderer = new AbsorberRenderer(viewport);
		this.portalRenderer = new PortalRenderer(viewport);
	}
	
	/**
	 * Creates a renderer for a board element.
	 * 
	 * This method only creates the {@link Renderer} instance. The caller is
	 * responsible for using {@link Gizmo#setRenderer(Renderer)} to associate
	 * the renderer with the board element.
	 * 
	 * @param gizmo the board element that will have a renderer created for it
	 * @return a {@link Renderer} instance that can render the given board
	 *   element
	 */
	public Renderer rendererFor(Gizmo gizmo) {
		assert gizmo != null;
		
		if (gizmo instanceof Ball) {
			return ballRenderer;
		}
		if (gizmo instanceof Wall) {
			return new WallRenderer((Wall)gizmo, viewport);
		}
		if (gizmo instanceof BumperBase) {
			return new BumperRenderer(viewport, (BumperBase)gizmo);
		}
		if (gizmo instanceof Absorber) {
			return absorberRenderer;
		}
		if (gizmo instanceof Portal) {
			return portalRenderer;
		}
		if (gizmo instanceof Flipper) {
			return new FlipperRenderer(viewport, (Flipper)gizmo);
		}
		
		throw new UnsupportedOperationException(
				"Unimplemented renderer for " + gizmo);
	}
}
