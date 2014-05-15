package pb.board;

import java.util.ArrayList;
import java.util.List;

import pb.render.Renderer;

/**
 * Superclass for all the elements on a board.
 * 
 * An element is attached to at most one {@link Board}, which can be obtained by
 * calling {@link #board()}. Elements start out as detached ({@link #board()}
 * returns null), and are eventually attached by calling
 * {@link Board#add(Gizmo)}, which in turn calls {@link #setBoard(Board)}. Once
 * an element has been attached to a board, it cannot be attached to a different
 * board. The element keeps its board attachment even when it is removed from
 * the board via {@link Board#remove(Gizmo)}, and may be re-added to the same
 * board via {@link Board#add(Gizmo)}.
 * 
 * Elements have an opportunity to update their state as the simulation time
 * advances, by implementing {@link #advanceTime(double)}.
 *  
 * An element can have at most one {@link Renderer} attached to it. The board is
 * generally responsible for creating renderers and attaching them to elements
 * by calling {@link #setRenderer(Renderer)}. Renderer testing code that wishes
 * to manually create a {@link Renderer} instance and attach it to an element
 * might also need to call {@link #setBoard(Board)} directly, and should not
 * call {@link Board#add(Gizmo)}.
 * 
 * Elements that move around the board, such as {@link pb.board.Ball}, must
 * inherit from {@link pb.board.MobileGizmo}. Elements that do not move
 * (and therefore cannot collide with each other), but do participate in
 * collisions, such as {@link pb.board.Flipper}, must inherit from
 * {@link pb.board.SolidGizmo}.
 * 
 * The rendering of individual board elements can be customized (styled). For
 * example, each bumper (see {@link pb.gizmos.BumperBase}) can be rendered using
 * a different color. To avoid polluting {@link Gizmo} with rendering concerns,
 * all the rendering customizations are encapsulated in {@link Style} instances.
 * An element's styling information can be obtained via {@link #getStyle()}.
 * 
 * In the interest of avoiding unnecessary repetition in the style declarations,
 * each board element has a style class name that is set via
 * {@link #setStyleClass(String)} and obtained via {@link #styleClass()}. Style
 * class names are looked up in the {@link StyleRegistry} associated with an
 * element's board.
 * 
 * In order to avoid having to handle styling changes in {@link Renderer}
 * implementations, {@link #setStyleClass(String)} cannot be called after the
 * element has a renderer attached. An element's style class name is typically
 * set once during board construction.
 * 
 * Each element should be tested individually. Whenever possible, tests should
 * take advantage of unattached elements. An element's trigger conditions can
 * be tested by creating a {@link pb.testing.NullGizmo} and adding it as a
 * listener. An element's action can be tested by creating a
 * {@link pb.testing.Triggerable} and setting up the element under testing as a
 * listener for the triggerable element. Collisions should be tested by setting
 * up a minimal scenario on a board and inspecting the element state before
 * and after {@link Board#simulate(double)}.
 * 
 * Instances of this class are not thread-safe. Each instance must be contained
 * to the same thread as its owning board.
 */
public abstract class Gizmo {
	/**
	 * The board that contains this element.
	 * 
	 * This starts out as null and is set to a {@link Renderer} instance
	 * exactly once, by calling {@link #setBoard(Board)}.
	 */
	private Board m_board;
	
	/**
	 * The renderer responsible for drawing this element.
	 * 
	 * This starts out as null and is set to a {@link Renderer} instance
	 * exactly once, by calling {@link #setRenderer(Renderer)}.
	 */
	private Renderer m_renderer;
	
	/**
	 * The element's render style class.
	 * 
	 * Style class names are looked up in the board's style registry to obtain
	 * the declarative style declarations used to customize this element's
	 * rendering.
	 */
	private String m_styleClass;
		
	/**
	 * The elements whose actions are hooked to this element's trigger.
	 */
	private final List<Gizmo> m_listeners;
	
	/**
	 * The element's name.
	 * 
	 * Names uniquely identify elements at board construction. However, multiple
	 * elements with the same name might end up on a board, due to
	 * teleportation.
	 */
	private final String m_name;
	
	/**
	 * Common setup for all elements.
	 * 
	 * @param name the element's name
	 */
	public Gizmo(String name) {
		assert name != null;
		assert name.length() > 0;
		
		this.m_name = name;
		this.m_board = null;
		this.m_renderer = null;
		this.m_styleClass = StyleRegistry.DEFAULT_CLASS;
		this.m_listeners = new ArrayList<Gizmo>();
		// NOTE: can't call checkRep() here, because it is supposed to be
		//       overridden
	}
	
	/**
	 * The element's name.
	 * 
	 * @return this element's name
	 */
	public String name() {
		return m_name;
	}
	
	/**
	 * The board that contains this element.
	 * 
	 * @return the board that contains this element
	 */
	public Board board() {
		return m_board;
	}
	
	/**
	 * Sets the board that contains this element.
	 * 
	 * This method must be called exactly once per instance. 
	 * 
	 * @param board the board that contains this element
	 */
	public void setBoard(Board board) {
		assert board != null;
		assert this.m_board == null;
		this.m_board = board;
		assert checkRep();
	}
		
	/**
	 * The renderer responsible for drawing this element.
	 * 
	 * @return the renderer responsible for drawing this element; null if
	 *   {@link #setRenderer(Renderer)} has not been called yet
	 */
	public Renderer renderer() {
		return m_renderer;
	}
	
	/**
	 * Sets the renderer responsible for drawing this element.
	 * 
	 * This method must be called exactly once per instance. 
	 * 
	 * @param renderer the renderer responsible for drawing this element
	 */
	public void setRenderer(Renderer renderer) {
		assert renderer != null;
		assert this.m_renderer == null;
		this.m_renderer = renderer;
		assert checkRep();
	}

	/**
	 * The element's rendering style class name.
	 * 
	 * @return this element's rendering style class name
	 */
	public String styleClass() {
		return m_styleClass;
	}
	
	/**
	 * Sets the renderer responsible for drawing this element.
	 * 
	 * This method must be called exactly once per instance. 
	 * 
	 * @param renderer the renderer responsible for drawing this element
	 */
	public void setStyleClass(String styleClass) {
		assert StyleRegistry.isValidClassName(styleClass);
		assert this.m_renderer == null;
		
		this.m_styleClass = styleClass;
		assert checkRep();
	}
	
	/**
	 * The rendering style for this element.
	 * 
	 * @return this element's rendering style
	 */
	public Style getStyle() {
		assert this.m_board != null;
		assert this.m_board.getStyleRegistry() != null;
		
		return m_board.getStyleRegistry().forClass(m_styleClass);
	}
	
	/**
	 * Connects a gizmo's action to this gizmo's trigger.
	 *  
	 * @param gizmo the gizmo whose action should be connected to this gizmo's
	 *   trigger; a gizmo can listen to its own trigger 
	 */
	public void addListener(Gizmo gizmo) {
		assert gizmo != null;
		this.m_listeners.add(gizmo);
		assert checkRep();
	}
	
	/**
	 * Creates a copy of the gizmo's listener list.
	 * 
	 * This method is intended for testing. It may be awfully slow.
	 * 
	 * @return a copy of the gizmo's listener list
	 */
	public Gizmo[] copyListeners() {
		Gizmo[] array = new Gizmo[m_listeners.size()];
		return this.m_listeners.toArray(array);
	}
	
	/**
	 * Called by subclasses when this gizmo is triggered.
	 * 
	 * This calls the actions of all the gizmos that listen to this gizmo's
	 * trigger.
	 */
	protected void trigger() {
		for (Gizmo listener : m_listeners) {
			listener.doAction();
		}
	}
	
	/**
	 * Checks this instance's representation invariant.
	 * 
	 * Subclasses that override this method should call super.checkRep() and
	 * bail if it returns false.
	 * 
	 * @return true if this instance's representation invariant holds
	 */
	protected boolean checkRep() {
		if (m_name == null || m_name.length() == 0)
			return false;
		if (m_listeners == null)
			return false;
		if (!StyleRegistry.isValidClassName(m_styleClass))
			return false;
		for (Gizmo listener : m_listeners) {
			if (listener == null)
				return false;
		}
		return true;
	}
	
	/**
	 * Simulates this element's changes over time, assuming no collisions occur.
	 * 
	 * Implementations of this method should avoid referencing the element's
	 * board (via {@link #board()}) whenever possible. This allows testing code
	 * to avoid attaching the element to a {@link Board}.
	 * 
	 * Implementations should assume that no collision will occur during the
	 * time step, and should not call {@link SolidGizmo#collide(MobileGizmo)} or
	 * {@link SolidGizmo#timeToCollision(MobileGizmo)}. The board handles this.
	 * 
	 * @param timeStep the time for which to simulate changes 
	 */
	public abstract void advanceTime(double timeStep);
	
	/**
	 * Performs this gizmo's action.
	 * 
	 * This is called when a gizmo that this gizmo is hooked up to is triggered,
	 * or when the user presses a key that is set up to trigger this gizmo.
	 */
	protected abstract void doAction();
}
