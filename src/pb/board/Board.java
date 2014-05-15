package pb.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import pb.client.UserInputMessage;
import pb.gizmos.Ball;
import pb.gizmos.Portal;
import pb.gizmos.Wall;
import pb.proto.ConnectWallMessage;
import pb.proto.DisconnectWallMessage;
import pb.proto.Message;
import pb.proto.PortalBallMessage;
import pb.proto.WallBallMessage;
import pb.render.RenderManager;
import pb.render.Viewport;
import physics.Circle;
import physics.Vect;


/**
 * Ties together the aspects of managing a game board.
 * 
 * A board manages a collection of elements, represented by {@link Gizmo}
 * instances. The following methods interact with the collection:
 * {@link #add(Gizmo)}, {@link #remove(Gizmo)}, {@link #findByName(String)}, 
 * {@link #contains(Gizmo)}. Internally, the board delegates the bulk of the 
 * collection maintenance work to a {@link GizmoRegistry} instance. 
 * 
 * The board also drives the physics simulation, via the
 * {@link #simulate(double)} method. Global simulation parameters, such as
 * gravity, are specified in the {@link BoardConstants} instance associated
 * with the board at construction time. These parameters can be accessed via
 * {@link #getConstants()}.
 * 
 * The board has a logical clock that starts at 0 and advances when
 * {@link #simulate(double)} is called. This clock is read by
 * {@link #getTime()}, and is used by the to keep the rendered output in sync
 * with the player's real-time clock. To keep the synchronization simple,
 * pausing the game is implemented by skipping the physics simulation while
 * still advancing the board clock. The flag that decides whether
 * {@link #simulate(double)} skips the physics simulation can be set via
 * {@link #setPaused(boolean)}.
 * 
 * The behavior of some board elements changes depending on whether the client
 * simulating the board is connected to a server or not (see {@link Portal} for
 * an example), so the board tracks connectivity information by
 * {@link #setHasServer(boolean)} and {@link #getHasServer()}.
 * 
 * When the client simulating the board is connected to a server, {@link Ball}
 * instances are transferred between the server and the client (see {@link Wall}
 * and {@link WallBallMessage} for an example). During a
 * {@link #simulate(double)} call, board elements can queue outgoing balls via
 * methods such as {@link #addOutgoingBall(Ball, Wall)}. Queued balls are
 * converted into {@link Message} instances, which are available via
 * {@link #getAndClearOutgoingMessages()}. Incoming balls are processed via
 * {@link #onMessage(Message)} calls.
 * 
 * The {@link Message} infrastructure has been extended (abused) to represent
 * a user's keyboard input, via {@link UserInputMessage} instances. Therefore,
 * {@link #onMessage(Message)} calls also process user input. The 
 * {@link KeyBindings} instance associated with the board
 * (readable via {@link #getKeyBindings()}) triggers actions on the elements
 * associated with the user input messages.
 *
 * The board and the {@link Gizmo} instances that it manage implement the game
 * logic. The logic for displaying (rendering) the board is mostly decoupled.
 * For example, no class in the {@link pb.board} or {@link pb.gizmos} is allowed
 * to reference any UI package such as {@link java.awt} directly, and must
 * instead reference the high-level concepts in {@link pb.render}.
 * 
 * When a board is created, it fills up a {@link Viewport} instance with
 * high-level information needed to set up a rendering area. The viewport is
 * obtained by calling {@link #getViewport()}, and used to create a
 * {@link RenderManager} that directs the rendering of the entire board. The
 * manager is associated with the board by calling
 * {@link #setRenderManager(RenderManager)}. After a board obtains a
 * {@link RenderManager}, it creates {@link pb.render.Renderer} instances for
 * all its elements, so the {@link RenderManager} can be used to render the
 * board. The documentations for {@link RenderManager} and {@link Gizmo} have
 * more details on this process.
 * 
 * The board contains a {@link StyleRegistry} used to compute the {@link Style}
 * of each board element. This keeps the {@link pb.board} and {@link pb.gizmos}
 * clear of rendering concerns, and avoids repetition when specifying styles.
 * The style registry must be set via {@link #setStyleRegistry(StyleRegistry)}
 * before a board receives a {@link RenderManager}.
 * 
 * Board instances are not thread-safe and must be contained to the board thread
 * managed by {@link pb.client.ClientController}.
 */
public class Board {
	/** General board attributes. */
	private final BoardConstants constants;
	/** The elements on this board. */
	private final GizmoRegistry gizmos;
	/** The effects of user input. */
	private final KeyBindings keyBindings;
	/** Parameters used to render the board. */
	private final Viewport viewport;		
	/** 
	 * Messages that need to be sent to the server.
	 * 
	 * This holds {@link WallBallMessage} and {@link PortalBallMessage}
	 * instances describing the balls that will be teleported to other boards.
     */
	private final ArrayList<Message> outgoing;
	
	/**
	 * Manages the rendering for the entire board.
	 * 
	 * This starts out as null, and gets set to a non-null value when
	 * {@link #setRenderManager(RenderManager)} is called.
	 */
	private RenderManager renderManager;	

	/**
	 * Declarative rendering styles for this board.
	 *
	 * This starts out as null, and gets set to a non-null value when
	 * #setStyl
	 */
	private StyleRegistry styles;
	
	/** The board time. */
	private double time;
	
	/** If true, the board is connected to a server. */
	private boolean hasServer;
	
	/** If true, time advances without anything being simulated on the board. */
	private boolean paused;

	/** Caches the result of {@link GizmoRegistry#copyImmobilesToArray()}. */
	private SolidGizmo[] immobileGizmos;
	
	/** Caches the result of {@link GizmoRegistry#copyMobilesToArray()}. */
	private MobileGizmo[] mobileGizmos;
	
	/** Caches the result of {@link GizmoRegistry#copyToArray()}. */
	private Gizmo[] allGizmos;
	
	public static final double MAX_TIME_STEP = 0.0025;
	
	public static final double COLLISION_EPSILON = 0.0001;
	
	/**
	 * Fudge factor for checking when an element is inside the board.
	 */
	public static final double POSITION_EPSILON = 0.00001;
	
	/**
	 * Creates a new board with the given board constants.
	 * @param constants the constants with which to initialize the board with
	 */
	public Board(BoardConstants constants) {
		assert constants != null;
		
		this.constants = constants;
		this.time = 0;
		this.paused = false;
		this.allGizmos = null;
		this.immobileGizmos = null;
		this.mobileGizmos = null;

		this.renderManager = null;
		this.viewport = new Viewport(constants.xSize(), constants.ySize());
		this.keyBindings = new KeyBindings();
		this.gizmos = new GizmoRegistry();
		this.outgoing = new ArrayList<Message>();
		addWalls();
	}

	/**
	 * Sets the rendering styles for this board.
	 * 
	 * This method should be called exactly once.
	 * 
	 * @param styles the rendering styles for this board
	 */
	public void setStyleRegistry(StyleRegistry styles) {
		assert styles != null;
		assert this.styles == null;
		
		this.styles = styles;
	}
	
	/**
	 * Sets the rendering manager for this board.
	 * 
	 * This method should be called exactly once.
	 * 
	 * @param renderManager the manager that directs this board's rendering
	 */
	public void setRenderManager(RenderManager renderManager) {
		assert renderManager != null;
		assert renderManager.getBoard() == this;
		assert this.renderManager == null;
		assert this.styles != null;
		
		this.renderManager = renderManager;
		for (Gizmo gizmo : gizmos)
			renderManager.attachRenderer(gizmo);
	}
	
	/**
	 * Adds an element to the board.
	 * 
	 * @param gizmo the gizmo to be added
	 */
	public void add(Gizmo gizmo) {
		assert gizmo != null;
		assert !contains(gizmo);
		
		if (gizmo.board() == null) {
			// NOTE: balls can be added and removed from the board, so their
			//       board might have already been set
			gizmo.setBoard(this);
			if (renderManager != null) {
				// NOTE: if the renderer is null, the gizmo's renderer will be
				//       created during the Board#createRenderer() call
				renderManager.attachRenderer(gizmo);
			}
		} else {
			assert gizmo.board() == this;
		}
		gizmos.add(gizmo);
		invalidateGizmoRegistryCaches(gizmo);
	}
	
	/**
	 * Removes an element from the board.
	 * 
	 * @param gizmo the element that will be removed; the element must have been
	 *   previously added to the board by an {@link #add(Gizmo)} call
	 */
	public void remove(Gizmo gizmo) {
		assert gizmo != null;
		assert !(gizmo instanceof Wall);
		
		gizmos.remove(gizmo);
		invalidateGizmoRegistryCaches(gizmo);
	}
	
	/**
	 * Checks if an element is currently on the board.
	 * 
	 * This method is inefficient, and is only intended for testing.
	 * 
	 * @param gizmo the element that will be searched
	 * @return true if the board contains the given element, false otherwise
	 */
	public boolean contains(Gizmo gizmo) {
		assert gizmo != null;
		
		return gizmos.contains(gizmo);
	}
	
	/**
	 * Returns an element with the given name.
	 * 
	 * @param name the name to search for
	 * @return an element whose name matches the given name, or null if the
	 *   board contains no element with the given name; if multiple elements
	 *   with the same name exist, any of them may be returned
	 */
	public Gizmo findByName(String name) {
		assert name != null;
		
		return gizmos.findByName(name); 
	}
	
	/**
	 * Advances the simulation time.
	 */
	public void simulate(double time) {
		if (paused) {
			this.time += time;
			return;
		}
		
		double timeLeft = time;
		while (timeLeft >= COLLISION_EPSILON) {
			doCollisions();
			double timeStep = timeToNextCollision();
			if (timeStep > MAX_TIME_STEP)
				timeStep = MAX_TIME_STEP;
			advanceTime(timeStep);
			timeLeft -= timeStep;
		}
	}
	
	/**
	 * Handles a message received from the server.
	 * 
	 * @param message the message received from the server.
	 */
	public void onMessage(Message message) {
		if (message instanceof ConnectWallMessage) {
			ConnectWallMessage wallMessage = (ConnectWallMessage)message;
			Edge edge = wallMessage.getEdge();
			Wall wall = (Wall)gizmos.findByName(edge.wallName());
			wall.setNeighborName(wallMessage.getNeighborName());
		} else if (message instanceof DisconnectWallMessage) {
			DisconnectWallMessage wallMessage = (DisconnectWallMessage)message;
			Edge edge = wallMessage.getEdge();
			Wall wall = (Wall)gizmos.findByName(edge.wallName());
			wall.clearNeighborName();			
		} else if (message instanceof WallBallMessage) {
			WallBallMessage ballMessage = (WallBallMessage)message;
			String ballName = ballMessage.getBallName();
			Circle shape = ballMessage.getShape();
			Vect center = shape.getCenter();
			Vect velocity = ballMessage.getVelocity();
			Ball ball = new Ball(ballName, center.x(), center.y(),
					shape.getRadius(), velocity.x(), velocity.y());
			add(ball);
		} else if (message instanceof PortalBallMessage) {
			PortalBallMessage ballMessage = (PortalBallMessage)message;
			String ballName = ballMessage.getBallName();
			Circle shape = ballMessage.getShape();
			Vect velocity = ballMessage.getVelocity();
			Ball ball;
			if (ballMessage.getFromBoard().equals(constants.name())) {
				// The server returned this ball.
				Vect center = shape.getCenter();
				ball = new Ball(ballName, center.x(), center.y(),
						shape.getRadius(), velocity.x(), velocity.y());

				Gizmo source = gizmos.findByName(ballMessage.getFromPortal());
				if (source != null && source instanceof Portal) {
					((Portal)source).queueBall(ball);
				} else {
					// The portal disappeared. Drop the ball for now.
				}
			} else {
				Gizmo target = gizmos.findByName(ballMessage.getToPortal());
				if (target != null && target instanceof Portal) {
					Portal targetPortal = (Portal)target;
					Vect center = targetPortal.getShape().getCenter();
					ball = new Ball(ballName, center.x(), center.y(),
							shape.getRadius(), velocity.x(), velocity.y());
					targetPortal.queueBall(ball);
				} else {
					// The ball wants a portal that does not exist.
					// Send the ball back.
					outgoing.add(new PortalBallMessage(
							ballMessage.getFromBoard(),
							ballMessage.getFromPortal(), 
							ballMessage.getFromBoard(),
							ballMessage.getFromPortal(), 
							ballName, shape, velocity));
				}
			}
		} else if (message instanceof UserInputMessage) {
			UserInputMessage inputMessage = (UserInputMessage)message;
			keyBindings.dispatch(inputMessage);
		}
	}
	
	/**
	 * Returns the current simulation time.
	 * 
	 * @return the current simulation time
	 */
	public double getTime() {
		return time;
	}
	
	/**
	 * True if the board simulation is paused.
	 * 
	 * @return if true, {@link #simulate(double)} advances the board time
	 *   without performing any physics simulation
	 */
	public boolean isPaused() {
		return paused;
	}
	
	/**
	 * True if the board is connected to a server.
	 * 
	 * @return true if the board is connected to a server
	 */
	public boolean getHasServer() {
		return hasServer;
	}
	
	/**
	 * Returns board metadata.
	 * 
	 * @return the board's metadata
	 */
	public BoardConstants getConstants() {
		return constants;
	}
	
	/** The board's rendering configuration. */
	public Viewport getViewport() {
		return viewport;
	}
	
	/**
	 * Returns the board's declarative rendering styles.
	 *
	 * @return the board's declarative rendering styles; this is null before
	 *   {@link #setStyleRegistry(StyleRegistry)} is called
	 */
	public StyleRegistry getStyleRegistry() {
		return styles;
	}	

	/** 
	 * Returns the manager that directs the board's rendering.
	 * 
	 * @return the manager that directs the board's rendering; this is null
	 *   before {@link #setRenderManager(RenderManager)} is called
	 */
	public RenderManager getRenderer() {
		return renderManager;
	}
		
	/**
	 * Returns an iterator over the board's gizmos.
	 * 
	 * The iterator should be used before any other {@link Board} method is
	 * called.
	 */
	public Iterator<Gizmo> getGizmos() {
		return gizmos.iterator();
	}
	
	/**
	 * The board's key bindings.
	 * 
	 * @return the board's key bindings
	 */
	public KeyBindings getKeyBindings() {
		return keyBindings;
	}
	
	/**
	 * Checks if a point is within the board's boundaries.
	 * 
	 * @param point the point's coordinates
	 * @return false if the point is off the board
	 */
	public boolean hasInside(Vect point) {
		double x = point.x();
		if (x < 0 || x > constants.xSize())
			return false;
		double y = point.y();
		if (y < 0 || y > constants.ySize())
			return false;
		return true;
	}
	
	/**
	 * Checks if a circle is within the board's boundaries.
	 * 
	 * @param circle the circle's coordinates and radius
	 * @return false if any part of the circle is off the board
	 */
	public boolean hasInside(Circle circle) {
		Vect center = circle.getCenter();
		double r = circle.getRadius();
		double x = center.x();
		if (x - r < -POSITION_EPSILON ||
				x + r - POSITION_EPSILON > constants.xSize())
			return false;
		double y = center.y();
		if (y - r < -POSITION_EPSILON ||
				y + r - POSITION_EPSILON > constants.ySize())
			return false;
		return true;
	}
	
	/**
	 * Returns a collection of messages that should be sent to the server.
	 * 
	 * Currently, the messages are {@link WallBallMessage} instances that describe
	 * the balls that must be teleported to other clients.
	 * 
	 * @return a list of messages that should be sent to the server; may be null
	 *   if no messages should be sent
	 */
	public List<Message> getAndClearOutgoingMessages() {
		if (outgoing.isEmpty()) {
			// NOTE: optimization -- we're avoiding creating a new object here
			return Collections.emptyList();
		}
		
		List<Message> listCopy = new ArrayList<Message>(outgoing);
		outgoing.clear();
		return listCopy;
	}
	
	/**
	 * Pauses or resumes the board simulation.
	 * 
	 * @param paused if true, {@link #simulate(double)} advances the board time
	 *   without performing any physics simulation; if false, simulation works
	 *   normally
	 */
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	/**
	 * Changes the flag indicating whether the board is connected to a server.
	 * 
	 * @param hasServer true if the board is connected to a server
	 */
	public void setHasServer(boolean hasServer) {
		this.hasServer = hasServer;
		if (hasServer == false) {			
			// The walls must be notified that they lost their neighbors.
			for (Edge edge : Edge.values()) {
				Wall wall = (Wall)gizmos.findByName(edge.wallName());
				wall.clearNeighborName();
			}
		}
	}

	/**
	 * Adds a message to the board's list of outgoing messages.
	 * 
	 * This should only be called by {@link Gizmo} subclasses.
	 * 
	 * @param ball the ball that is leaving the board
	 * @param wall the wall that collided with the ball
	 */
	public void addOutgoingBall(Ball ball, Wall wall) {
		assert ball != null;
		assert wall != null;
		assert hasServer;
		
		WallBallMessage message = new WallBallMessage(wall.getEdge(), ball.name(),
				ball.getShape(), ball.getVelocity());
		outgoing.add(message);
	}
	
	/**
	 * Adds a message to the board's list of outgoing messages.
	 * 
	 * This should only be called by {@link Gizmo} subclasses.
	 * 
	 * @param ball the ball that is leaving the board
	 * @param portal the portal that collided with the ball
	 */
	public void addOutgoingBall(Ball ball, Portal portal) {
		assert ball != null;
		assert portal != null;
		assert hasServer;		
		
		PortalBallMessage message = new PortalBallMessage(constants.name(),
				portal.name(), portal.getOtherBoard(), portal.getOtherPortal(),
				ball.name(), ball.getShape(), ball.getVelocity());
		outgoing.add(message);
	}	
	
	/**
	 * Calculates the time to next collision
	 * @return the time to next collision
	 */
	private double timeToNextCollision() {
		double returnValue = Double.MAX_VALUE;
		
		buildGizmoCollisionRegistryCaches();
		assert this.immobileGizmos != null;
		assert this.mobileGizmos != null;

		// NOTE: The reference copy might be avoidable here.
		SolidGizmo[] immobileArray = this.immobileGizmos;
		MobileGizmo[] mobileArray = this.mobileGizmos;		
		
		for (int i = 0; i < immobileArray.length; ++i) {
			SolidGizmo immobile = immobileArray[i];
			assert !(immobile instanceof MobileGizmo);
			for (int j = 0; j < mobileArray.length; ++j) {
				MobileGizmo mobile = mobileArray[j];
				double time = immobile.timeToCollision(mobile);
				if (time < returnValue)
					returnValue = time;
			}
		}
		for (int i = 0; i < mobileArray.length; ++i) {
			SolidGizmo mobile = mobileArray[i];
			for (int j = 0; j < mobileArray.length; ++j) {
				MobileGizmo otherMobile = mobileArray[j];
				double time = mobile.timeToCollision(otherMobile);
				if (time < returnValue)
					returnValue = time;
			}
		}		
		return returnValue;
	}
	
	/**
	 * Advances time by timeStep for every gizmo
	 * @param timeStep the timestep by which to advance time
	 */
	private void advanceTime(double timeStep) {
		this.time += timeStep;
		
		buildGizmoAdvanceTimeRegistryCaches();
		assert this.allGizmos != null;

		// NOTE: New elements might be added to the board during time advances
		//       computations (e.g., the absorber might add a ball). Hanging on
		//       to the elements array prevents nasty surprises.
		Gizmo[] gizmoArray = this.allGizmos;
		for (Gizmo gizmo : gizmoArray)
			gizmo.advanceTime(timeStep);
	}
	
	/**
	 * Performs all collision calculations on every gizmo
	 */
	private void doCollisions() {
		buildGizmoCollisionRegistryCaches();
		assert this.immobileGizmos != null;
		assert this.mobileGizmos != null;
		
		// NOTE: New elements might be added to the board during the collision
		//       phase (e.g., balls can be removed for teleportation). Hanging
		//       on to the element arrays prevents nasty surprises.
		SolidGizmo[] immobileArray = this.immobileGizmos;
		MobileGizmo[] mobileArray = this.mobileGizmos;		
		
		for (int i = 0; i < immobileArray.length; ++i) {
			SolidGizmo immobile = immobileArray[i];
			assert !(immobile instanceof MobileGizmo);
			for (int j = 0; j < mobileArray.length; ++j) {
				MobileGizmo mobile = mobileArray[j];
				double time = immobile.timeToCollision(mobile);
				if (time < COLLISION_EPSILON)
					immobile.collide(mobile);
			}
		}
		for (int i = 0; i < mobileArray.length; ++i) {
			SolidGizmo mobile = mobileArray[i];
			for (int j = 0; j < mobileArray.length; ++j) {
				MobileGizmo otherMobile = mobileArray[j];
				double time = mobile.timeToCollision(otherMobile);
				if (time < COLLISION_EPSILON)
					mobile.collide(otherMobile);
			}
		}
	}
	
	/**
	 * Invalidates the members caching {@link GizmoRegistry} return values.
	 * 
	 * @param gizmo the board element that was added to or removed from the
	 *   board
	 */
	private void invalidateGizmoRegistryCaches(Gizmo gizmo) {
		allGizmos = null;
		
		if (gizmo instanceof SolidGizmo) {
			if (gizmo instanceof MobileGizmo)
				mobileGizmos = null;
			else
				immobileGizmos = null;
		}				
	}
	
	/**
	 * Ensures that the members caching {@link GizmoRegistry} are non-null.
	 */
	private void buildGizmoCollisionRegistryCaches() {
		if (mobileGizmos == null)
			mobileGizmos = gizmos.copyMobilesToArray();
		if (immobileGizmos == null)
			immobileGizmos = gizmos.copyImmobilesToArray();
	}

	/**
	 * Ensures that the members caching {@link GizmoRegistry} are non-null.
	 */
	private void buildGizmoAdvanceTimeRegistryCaches() {
		if (allGizmos == null)
			allGizmos = gizmos.copyToArray();
	}

	/** Sets up the board's walls. */
	private void addWalls() {
		double xSize = constants.xSize();
		double ySize = constants.ySize();
		Wall top = new Wall(0, 0, xSize, 0);
		assert top.getEdge() == Edge.TOP;
		add(top); 
		Wall left = new Wall(0, 0, 0, ySize);
		assert left.getEdge() == Edge.LEFT;
		add(left);
		Wall bottom = new Wall(0, ySize, xSize, ySize);
		assert bottom.getEdge() == Edge.BOTTOM;
		add(bottom);
		Wall right = new Wall(xSize, 0, xSize, ySize);
		assert right.getEdge() == Edge.RIGHT;
		add(right);
	}	
}
