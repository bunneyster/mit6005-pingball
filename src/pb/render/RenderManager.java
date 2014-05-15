package pb.render;

import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import pb.board.Board;
import pb.board.Gizmo;


/**
 * Directs the rendering process for a {@link Board}.
 *
 * Every time a new {@link Board} is loaded, its {@link Viewport} is used to
 * resize the UI component that displays the board. This may invalidate the
 * rendering context (the object associated with video buffers), so a new
 * rendering context is obtained from the UI component, and a new
 * {@link RenderManager} is created and associated with the board via
 * {@link Board#setRenderManager(RenderManager)}.
 * 
 * In the current implementation, the rendering context is a
 * {@link BufferStrategy}, which is robust to resizing. However, the strategy
 * above works even if the code will be ported to another low-level rendering
 * API, such as OpenGL.
 *
 * The rendering manager knows how to attach the correct {@link Renderer}
 * instance to a board element. The {@link Board} keeps track of which elements
 * need renderers, and calls {@link #attachRenderer(Gizmo)}, which in turn uses
 * {@link Gizmo#setRenderer(Renderer)} to attach the renderers.
 * 
 * The main job of the rendering manager is to render the board and its
 * elements, via {@link #renderFrame()}. The manager code sets up the rendering
 * context and delegates the actual rendering to a {@link BoardRenderer}
 * instance (available via {@link #getBoardRenderer()}) that it sets up for its
 * board.
 * 
 * The rendering in {@link #renderFrame()} is done in the board thread (managed
 * by {@link pb.client.ClientController}). This lets {@link Renderer}
 * implementations access {@link Board} and {@link Gizmo} instances without any
 * thread synchronization issues, and keeps the UI thread responsive to the
 * user's input. The board and its elements are rendered into an off-screen
 * buffer that is swapped  
 * 
 * Renderer testing code uses a {@link pb.testing.ImageDumper} instance instead
 * of a {@link BufferStrategy} coming from a UI component. The
 * {@link pb.testing.ImageDumper} documentation covers test construction.
 *
 * Instances of this class are not thread-safe. Each instance must be contained
 * to the same thread as the board whose rendering it manages.
 */
public class RenderManager {
	/** The board whose rendering is directed by this manager. */
	private final Board board;
	/** Renders the board. */
	private final BoardRenderer boardRenderer;
	/** Parameters used to render the board. */
	private final Viewport viewport;
	/** Factory used to create {@link Renderer}s for {@link Gizmo}s.*/
	private final RendererFactory rendererFactory;
	/** Provides the buffers used to render the board. */
	private final BufferStrategy bufferStrategy;

	/**
	 * Creates a manager that directs a board's rendering.
	 * 
	 * @param board the board whose rendering will be directed by this manager
	 * @param bufferStrategy provides the buffers used to render the board
	 */
	public RenderManager(Board board, BufferStrategy bufferStrategy) {
		assert board != null;
		assert board.getViewport() != null;
		assert bufferStrategy != null;
		
		this.board = board;
		this.viewport = board.getViewport();
		this.bufferStrategy = bufferStrategy;
		this.rendererFactory = new RendererFactory(viewport);
		this.boardRenderer = new BoardRenderer(board);
	}
	
	/**
	 * The board whose rendering is directed by this manager.
	 * 
	 * @return the board whose rendering is directed by this manager
	 */
	public Board getBoard() {
		return board;
	}
	
	/**
	 * The {@link Renderer} used for the associated board.
	 * 
	 * @return the {@link Renderer} used for the associated board
	 */
	public BoardRenderer getBoardRenderer() {
		return boardRenderer;
	}
	
	/**
	 * Sets an element's {@link Renderer}.
	 * 
	 * This method either creates a {@link Renderer} for the given element, or
	 * fetches a shared instance used by multiple elements, and attaches it to
	 * the given element by calling {@link Gizmo#setRenderer(Renderer)}.
	 * 
	 * @param gizmo the element that will have its renderer set
	 */
	public void attachRenderer(Gizmo gizmo) {
		assert gizmo != null;
		gizmo.setRenderer(rendererFactory.rendererFor(gizmo));
	}
	
	/**
	 * Renders the managed board.
	 * 
	 * This method obtains a buffer from the associated {@link BufferStrategy}
	 * and renders the board associated with this manager to the buffer. 
	 */
	public void renderFrame() {
		Graphics2D context;
		try {
			context = (Graphics2D)bufferStrategy.getDrawGraphics();
		} catch(IllegalStateException e) {
			// This happens when shutting down the game.
			return;
		}
		viewport.clear(context);
		boardRenderer.render(null, context);
		context.dispose();
		try {
			bufferStrategy.show();
		} catch(IllegalStateException e) {
			// This happens when shutting down the game.
			return;
		}
	}
}